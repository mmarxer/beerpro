package ch.beerpro.presentation.profile.mybeers;

import android.widget.ImageView;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeBeer;

public interface OnMyBeerItemInteractionListener {
    void onAddNewClickedListener(Beer item);
    void onMoreClickedListener(ImageView photo, Beer item);

    void onWishClickedListener(Beer item);

    void onFridgeAddClickedListener(FridgeBeer fridgeBeer);
    void onFridgeRemoveClickedListener(FridgeBeer fridgeBeer);
}
