package ch.beerpro.domain.models;

import java.util.Date;

public interface MyBeer {
    String getBeerId();

    Beer getBeer();

    Date getDate();

    FridgeBeer getFridgeBeer();
    void setFridgeItem(FridgeBeer fridgeBeer);
}
