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
import ch.beerpro.domain.models.Entity;
import ch.beerpro.domain.models.MyBeer;
import ch.beerpro.domain.models.BeerFromPreis;
import ch.beerpro.domain.models.MyBeerFromRating;
import ch.beerpro.domain.models.MyBeerFromWishlist;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;
import ch.beerpro.domain.utils.quadrupel;

import static androidx.lifecycle.Transformations.map;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class MyBeersRepository {

    private static List<MyBeer> getMyBeers(quadrupel<List<Wish>, List<Rating>, List<BeerPrice>, HashMap<String, Beer>> input) {
        List<Wish> wishlist = input.getLastA();
        List<Rating> ratings = input.getLastB();
        List<BeerPrice> prices = input.getLastC();
        HashMap<String, Beer> beers = input.getLastD();

        ArrayList<MyBeer> result = new ArrayList<>();
        Set<String> beersAlreadyOnTheList = new HashSet<>();
        for (Wish wish : wishlist) {
            String beerId = wish.getBeerId();
            result.add(new MyBeerFromWishlist(wish, beers.get(beerId)));
            beersAlreadyOnTheList.add(beerId);
        }

        for (Rating rating : ratings) {
            String beerId = rating.getBeerId();
            if (beersAlreadyOnTheList.contains(beerId)) {
                // if the beer is already on the wish list, don't add it again
            } else {
                result.add(new MyBeerFromRating(rating, beers.get(beerId)));
                // we also don't want to see a rated beer twice
                beersAlreadyOnTheList.add(beerId);
            }
        }

        for (BeerPrice price : prices) {
            String beerId = price.getBeerId();
            if (!beersAlreadyOnTheList.contains(beerId)) {
                result.add(new BeerFromPreis(price, beers.get(beerId)));
                beersAlreadyOnTheList.add(beerId);
            }
        }

        Collections.sort(result, (r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return result;
    }

    public LiveData<List<MyBeer>> getMyBeers(LiveData<List<Beer>> allBeers, LiveData<List<Wish>> myWishlist,
                                             LiveData<List<Rating>> myRatings, LiveData<List<BeerPrice>> myPrices) {
        return map(combineLatest(myWishlist, myRatings, myPrices, map(allBeers, Entity::entitiesById)),
                MyBeersRepository::getMyBeers);
    }

}