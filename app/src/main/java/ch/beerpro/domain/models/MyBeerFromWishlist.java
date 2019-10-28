package ch.beerpro.domain.models;

import androidx.annotation.NonNull;

import java.util.Date;

public class MyBeerFromWishlist implements MyBeer {
    private Wish wish;
    private Beer beer;
    private FridgeBeer fridgeBeer;

    public MyBeerFromWishlist(Wish wish, Beer beer) {
        this.wish = wish;
        this.beer = beer;
    }

    @Override
    public String getBeerId() {
        return wish.getBeerId();
    }

    @Override
    public Date getDate() {
        return wish.getAddedAt();
    }

    public Wish getWish() {
        return this.wish;
    }

    public Beer getBeer() {
        return this.beer;
    }

    public void setWish(Wish wish) {
        this.wish = wish;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public FridgeBeer getFridgeBeer() {
        return this.fridgeBeer;
    }

    @Override
    public void setFridgeItem(FridgeBeer fridgeBeer) {
        this.fridgeBeer = fridgeBeer;
    }


    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MyBeerFromWishlist)) return false;
        final MyBeerFromWishlist other = (MyBeerFromWishlist) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$wish = this.getWish();
        final Object other$wish = other.getWish();
        if (this$wish == null ? other$wish != null : !this$wish.equals(other$wish)) return false;
        final Object this$beer = this.getBeer();
        final Object other$beer = other.getBeer();
        return this$beer == null ? other$beer == null : this$beer.equals(other$beer);
    }

    private boolean canEqual(final Object other) {
        return other instanceof MyBeerFromWishlist;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $wish = this.getWish();
        result = result * PRIME + ($wish == null ? 43 : $wish.hashCode());
        final Object $beer = this.getBeer();
        result = result * PRIME + ($beer == null ? 43 : $beer.hashCode());
        return result;
    }

    @NonNull
    public String toString() {
        return "MyBeerFromWishlist(wish=" + this.getWish() + ", beer=" + this.getBeer() + ")";
    }
}
