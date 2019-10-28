package ch.beerpro.presentation.profile.myfridge;

import android.widget.ImageView;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.presentation.profile.mybeers.OnMyBeerItemInteractionListener;

public interface OnFridgeItemInteractionListener {
    void onMoreClickedListener(ImageView photo, Beer beer);
    void onFridgeAddClickedListener(FridgeBeer fridgeBeer);
    void onFridgeRemoveClickedListener(FridgeBeer fridgeBeer);
}
