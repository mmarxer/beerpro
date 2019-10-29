package ch.beerpro.domain.models;

import java.util.HashMap;
import java.util.List;

public class BeerListings {
    private HashMap<String, Beer> beers;
    private List<Wish> wishes;
    private List<Rating> ratings;
    private List<FridgeBeer> fridgeBeers;

    public BeerListings(HashMap<String, Beer> beers, List<Wish> wishes, List<Rating> ratings, List<FridgeBeer> fridgeBeers) {
        this.beers = beers;
        this.wishes = wishes;
        this.ratings = ratings;
        this.fridgeBeers = fridgeBeers;
    }

    public HashMap<String, Beer> getBeers() {
        return beers;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public List<FridgeBeer> getFridgeBeers() {
        return fridgeBeers;
    }
}
