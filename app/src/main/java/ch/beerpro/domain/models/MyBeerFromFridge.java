package ch.beerpro.domain.models;

import androidx.annotation.NonNull;

import java.util.Date;

public class MyBeerFromFridge implements MyBeer {
    private FridgeBeer fridgeBeer;
    private Beer beer;

    public MyBeerFromFridge(FridgeBeer fridgeBeer, Beer beer) {
        this.fridgeBeer = fridgeBeer;
        this.beer = beer;
    }

    @Override
    public String getBeerId() {
        return fridgeBeer.getBeerId();
    }

    @Override
    public Date getDate() {
        return fridgeBeer.getAddedAt();
    }

    public FridgeBeer getFridgeBeer() {
        return this.fridgeBeer;
    }

    @Override
    public void setFridgeItem(FridgeBeer fridgeBeer) {
        this.fridgeBeer = fridgeBeer;
    }

    public Beer getBeer() {
        return this.beer;
    }

    public void setFridgeBeer(FridgeBeer fridgeBeer) {
        this.fridgeBeer = fridgeBeer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MyBeerFromFridge)) return false;
        final MyBeerFromFridge other = (MyBeerFromFridge) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$fridgeBeer = this.getFridgeBeer();
        final Object other$fridgeBeer = other.getFridgeBeer();
        if (this$fridgeBeer == null ? other$fridgeBeer != null : !this$fridgeBeer.equals(other$fridgeBeer)) return false;
        final Object this$beer = this.getBeer();
        final Object other$beer = other.getBeer();
        return this$beer == null ? other$beer == null : this$beer.equals(other$beer);
    }

    private boolean canEqual(final Object other) {
        return other instanceof MyBeerFromFridge;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $fridgeBeer = this.getFridgeBeer();
        result = result * PRIME + ($fridgeBeer == null ? 43 : $fridgeBeer.hashCode());
        final Object $beer = this.getBeer();
        result = result * PRIME + ($beer == null ? 43 : $beer.hashCode());
        return result;
    }

    @NonNull
    public String toString() {
        return "MyBeerFromFridge(fridgeBeer=" + this.getFridgeBeer() + ", beer=" + this.getBeer() + ")";
    }
}
