package ch.beerpro.data.repositories;

import android.util.Pair;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Entity;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class FridgeRepository {


    private static LiveData<List<FridgeBeer>> getFridgeBeersByUser(String userId) {
        return new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(FridgeBeer.COLLECTION)
                .orderBy(FridgeBeer.FIELD_ADDED_AT, Query.Direction.DESCENDING).whereEqualTo(FridgeBeer.FIELD_USER_ID, userId),
                FridgeBeer.class);
    }

    
    public LiveData<List<Pair<FridgeBeer, Beer>>> getMyFridge(LiveData<String> currentUserId,
                                                                   LiveData<List<Beer>> allBeers) {
        return map(combineLatest(getMyFridgeBeers(currentUserId), map(allBeers, Entity::entitiesById)), input -> {
            List<FridgeBeer> FridgeBeeres = input.first;
            HashMap<String, Beer> beersById = input.second;

            ArrayList<Pair<FridgeBeer, Beer>> result = new ArrayList<>();
            for (FridgeBeer FridgeBeer : FridgeBeeres) {
                Beer beer = beersById.get(FridgeBeer.getBeerId());
                result.add(Pair.create(FridgeBeer, beer));
            }
            return result;
        });
    }

    public LiveData<List<FridgeBeer>> getMyFridgeBeers(LiveData<String> currentUserId) {
        return switchMap(currentUserId, FridgeRepository::getFridgeBeersByUser);
    }
    
    public Task<Void> addFridgeBeer(String userId, String beerId) {
        return addFridgeBeer(userId, beerId, 1);
    }

    public Task<Void> addFridgeBeer(String userId, String beerId, int amount) {
        return updateAmount(userId, beerId, amount);
    }

    public Task<Void> removeFridgeBeer(String userId, String beerId) {
        return removeFridgeBeer(userId, beerId, 1);
    }

    public Task<Void> removeFridgeBeer(String userId, String beerId, int amount) {
        return updateAmount(userId, beerId, -amount);
    }

    private Task<Void> updateAmount(String userId, String beerId, int amount) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String fridgeBeerId = FridgeBeer.generateId(userId, beerId);
        DocumentReference fridgeBeerQuery = db.collection(FridgeBeer.COLLECTION).document(fridgeBeerId);

        return fridgeBeerQuery.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                long currentAmount = task.getResult().getLong(FridgeBeer.FIELD_AMOUNT);
                if (currentAmount + amount == 0) {
                    return fridgeBeerQuery.delete();
                }
                fridgeBeerQuery.update(FridgeBeer.FIELD_AMOUNT, currentAmount + amount);
                return fridgeBeerQuery.update(FridgeBeer.FIELD_ADDED_AT, new Date());
            } else if (task.isSuccessful()) {
                return fridgeBeerQuery.set(new FridgeBeer(userId, beerId,new Date(), amount));
            } else {
                throw task.getException();
            }
        });
    }



}
