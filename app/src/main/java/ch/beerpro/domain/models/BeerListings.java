package ch.beerpro.domain.models;

import java.util.HashMap;
import java.util.List;

public class BeerListings {
    private HashMap<String, Beer> beers;
    private List<Wish> wishes;
    private List<Rating> ratings;
    private List<FridgeBeer> fridgeBeers;
    private List<BeerPrice> beerPrice;

    public BeerListings(HashMap<String, Beer> beers, List<Wish> wishes, List<Rating> ratings, List<FridgeBeer> fridgeBeers, List<BeerPrice> beerPrice) {
        this.beers = beers;
        this.wishes = wishes;
        this.ratings = ratings;
        this.fridgeBeers = fridgeBeers;
        this.beerPrice = beerPrice;
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

    public List<BeerPrice> getBeerPrice() {
        return beerPrice;
    }
}
