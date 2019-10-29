package ch.beerpro.data.repositories;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import ch.beerpro.domain.models.BeerPrice;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;

import static androidx.lifecycle.Transformations.switchMap;

public class PriceRepository {

    private final FirestoreQueryLiveDataArray<BeerPrice> allPrices = new FirestoreQueryLiveDataArray<>(
            FirebaseFirestore.getInstance().collection(BeerPrice.COLLECTION)
                    .orderBy(BeerPrice.FIELD_CREATION_DATE, Query.Direction.DESCENDING), BeerPrice.class);

    public static LiveData<List<BeerPrice>> getPricesByUser(String userId) {
        return new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(BeerPrice.COLLECTION)
                .orderBy(BeerPrice.FIELD_CREATION_DATE, Query.Direction.DESCENDING)
                .whereEqualTo(BeerPrice.FIELD_USER_ID, userId), BeerPrice.class);
    }


    public static LiveData<List<BeerPrice>> getPricesByBeer(String beerId) {
        return new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(BeerPrice.COLLECTION)
                .orderBy(BeerPrice.FIELD_CREATION_DATE, Query.Direction.DESCENDING)
                .whereEqualTo(BeerPrice.FIELD_BEER_ID, beerId), BeerPrice.class);
    }

    public LiveData<List<BeerPrice>> getAllPrices() {
        return allPrices;
    }

    public LiveData<List<BeerPrice>> getMyPrices(LiveData<String> currentUserId) {
        return switchMap(currentUserId, PriceRepository::getPricesByUser);
    }

    public LiveData<List<BeerPrice>> getPricesForBeer(LiveData<String> beerId) {
        return switchMap(beerId, PriceRepository::getPricesByBeer);
    }

}