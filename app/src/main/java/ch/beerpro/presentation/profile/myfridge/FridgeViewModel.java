package ch.beerpro.presentation.profile.myfridge;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import java.util.List;

import ch.beerpro.data.repositories.BeersRepository;
import ch.beerpro.data.repositories.CurrentUser;
import ch.beerpro.data.repositories.FridgeRepository;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.models.Wish;

public class FridgeViewModel extends ViewModel implements CurrentUser {

    private static final String TAG = "FridgeViewModel";

    private final MutableLiveData<String> currentUserId = new MutableLiveData<>();
    private final FridgeRepository FridgeRepository;
    private final BeersRepository beersRepository;

    public FridgeViewModel() {
        FridgeRepository = new FridgeRepository();
        beersRepository = new BeersRepository();

        currentUserId.setValue(getCurrentUser().getUid());
    }

    public LiveData<List<Pair<FridgeBeer, Beer>>> getMyFridgeWithBeers() {
        return FridgeRepository.getMyFridge(currentUserId, beersRepository.getAllBeers());
    }

    public void addToFridge(FridgeBeer fridgeItem) {
        FridgeRepository.addFridgeBeer(currentUserId.getValue(), fridgeItem.getBeerId());
    }

    public void removeFromFridge(FridgeBeer fridgeItem) {
        FridgeRepository.removeFridgeBeer(currentUserId.getValue(), fridgeItem.getBeerId());
    }

    public void addToFridge(Beer item) {
        FridgeRepository.addFridgeBeer(currentUserId.getValue(), item.getId());
    }

}