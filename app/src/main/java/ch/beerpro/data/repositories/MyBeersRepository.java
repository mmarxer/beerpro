package ch.beerpro.data.repositories;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.BeerPrice;
import ch.beerpro.domain.models.BeerListings;
import ch.beerpro.domain.models.Entity;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.models.MyBeer;
import ch.beerpro.domain.models.BeerFromPreis;
import ch.beerpro.domain.models.MyBeerFromFridge;
import ch.beerpro.domain.models.MyBeerFromRating;
import ch.beerpro.domain.models.MyBeerFromWishlist;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;
import ch.beerpro.domain.utils.BeerCart;

import static androidx.lifecycle.Transformations.map;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatestData;

public class MyBeersRepository {


    private static List<MyBeer> getMyBeers(BeerListings input) {
        List<Wish> wishes = input.getWishes();
        List<Rating> ratings = input.getRatings();
        HashMap<String, Beer> beers = input.getBeers();
        List<FridgeBeer> fridgeBeers = input.getFridgeBeers();
        HashMap<String, MyBeer> fridgeHashMap = new HashMap<>();
        HashMap<String, MyBeer> resultHashMap = new HashMap<>();

        Set<String> beersAlreadyOnTheList = new HashSet<>();
        for (Wish wish : wishes) {
            String beerId = wish.getBeerId();
            resultHashMap.put(beerId, new MyBeerFromWishlist(wish, beers.get(beerId)));
            beersAlreadyOnTheList.add(beerId);
        }

        for (Rating rating : ratings) {
            String beerId = rating.getBeerId();
            if (beersAlreadyOnTheList.contains(beerId)) {
                // if the beer is already on the wish list, don't add it again
            } else {
                resultHashMap.put(beerId, new MyBeerFromRating(rating, beers.get(beerId)));
                // we also don't want to see a rated beer twice
                beersAlreadyOnTheList.add(beerId);
            }
        }


        for (FridgeBeer fridgeItem : fridgeBeers) {
            String beerId = fridgeItem.getBeerId();
            if (beersAlreadyOnTheList.contains(beerId)) {
                MyBeer myBeer = resultHashMap.get(beerId);
                myBeer.setFridgeItem(fridgeItem);
                resultHashMap.put(beerId, myBeer);
            } else {
                resultHashMap.put(beerId, new MyBeerFromFridge(fridgeItem, beers.get(beerId)));
                beersAlreadyOnTheList.add(beerId);
            }
        }
        ArrayList<MyBeer> result = new ArrayList<>(resultHashMap.values());
        Collections.sort(result, (r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return result;
    }

    public LiveData<List<MyBeer>> getMyBeers(LiveData<List<Beer>> allBeers, LiveData<List<Wish>> myWishlist, LiveData<List<Rating>> myRatings, LiveData<List<FridgeBeer>> myFridgeBeers, LiveData<List<BeerPrice>> myPrices) {
        return map(combineLatest(myWishlist, myRatings, myFridgeBeers, map(allBeers, Entity::entitiesById), myPrices),
                MyBeersRepository::getMyBeers);
    }
}