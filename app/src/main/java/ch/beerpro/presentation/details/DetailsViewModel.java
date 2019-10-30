package ch.beerpro.presentation.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import ch.beerpro.data.repositories.BeersRepository;
import ch.beerpro.data.repositories.CurrentUser;
import ch.beerpro.data.repositories.LikesRepository;
import ch.beerpro.data.repositories.RatingsRepository;
import ch.beerpro.data.repositories.WishlistRepository;
import ch.beerpro.data.repositories.*;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.BeerPrice;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;
import ch.beerpro.presentation.utils.EntityClassSnapshotParser;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class DetailsViewModel extends ViewModel implements CurrentUser {

    private final MutableLiveData<String> beerId = new MutableLiveData<>();
    private final MutableLiveData<String> currentUserId = new MutableLiveData<>();
    private final LiveData<Beer> beer;
    private final LiveData<List<Rating>> ratings;
    private final LiveData<List<Rating>> myRatings;
    private final LiveData<Wish> wish;

    private final LikesRepository likesRepository;
    private final WishlistRepository wishlistRepository;
    private final RatingsRepository ratingsRepository;
    private EntityClassSnapshotParser<Rating> parser = new EntityClassSnapshotParser<>(Rating.class);

    private final FridgeRepository fridgeRepository;

    public DetailsViewModel() {
        // TODO We should really be injecting these!
        BeersRepository beersRepository = new BeersRepository();
        ratingsRepository = new RatingsRepository();
        likesRepository = new LikesRepository();
        wishlistRepository = new WishlistRepository();
        fridgeRepository = new FridgeRepository();

//        MutableLiveData<String> currentUserId = new MutableLiveData<>();
        beer = beersRepository.getBeer(beerId);
        wish = wishlistRepository.getMyWishForBeer(currentUserId, getBeer());
        ratings = ratingsRepository.getRatingsForBeer(beerId);
        currentUserId.setValue(getCurrentUser().getUid());
        myRatings = ratingsRepository.getMyRatings(currentUserId);
    }

    public LiveData<Beer> getBeer() {
        return beer;
    }

    public LiveData<Wish> getWish() {
        return wish;
    }

    public LiveData<List<Rating>> getRatings() {
        return ratings;
    }

    public LiveData<List<Rating>> getMyRatings() { return myRatings; }

    public void setBeerId(String beerId) {
        this.beerId.setValue(beerId);
    }

    public void toggleLike(Rating rating) {
        likesRepository.toggleLike(rating);
    }

    public Task<Void> toggleItemInWishlist(String itemId) {
        return wishlistRepository.toggleUserWishlistItem(getCurrentUser().getUid(), itemId);
    }

    public Task<Rating> savePrice(String beerId, float price) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        BeerPrice newPrice = new BeerPrice(null, beerId, user.getUid(), user.getDisplayName(), price, new Date());
        CollectionReference prices = FirebaseFirestore.getInstance().collection(BeerPrice.COLLECTION);
        return prices.add(newPrice)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().get();
                    } else {
                        throw task.getException();
                    }
                }).continueWithTask(task -> {

                    if (task.isSuccessful()) {
                        return Tasks.forResult(parser.parseSnapshot(task.getResult()));
                    } else {
                        throw task.getException();
                    }
                });
    }

    public void addToFridge(String beerId) {
        fridgeRepository.addFridgeBeer(getCurrentUser().getUid(), beerId);
    }

    public void updateBeerPrice(float inputPrice) {
        if (inputPrice <= 0) {return;} // Throws away really nonsensical negative values
        Beer b = beer.getValue();

        int numPrices = b.getNumPrices();
        float averagePrice = b.getAvgPrice();
        if (numPrices == 0) {
            b.setAvgPrice(inputPrice);
            b.setNumPrices(1);
        }
        else {
            float newPrice = averagePrice * ((float)numPrices / (numPrices + 1f));
            newPrice = newPrice + inputPrice * (1f / ((float)numPrices + 1f));
            b.setAvgPrice(newPrice);
            b.setNumPrices(numPrices + 1);

        }

        BeersRepository.updatePrice(b);
    }
}