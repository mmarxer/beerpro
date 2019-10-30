package ch.beerpro.presentation.details;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.beerpro.GlideApp;
import ch.beerpro.R;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;
import ch.beerpro.presentation.details.Price.PriceFragment;
import ch.beerpro.presentation.details.createrating.CreateRatingActivity;

import static ch.beerpro.presentation.utils.DrawableHelpers.setDrawableTint;

public class DetailsActivity extends AppCompatActivity implements OnRatingLikedListener {

    public static final String NOTE_PREFERENCE = "NotePreference";
    public static final String PRICE_PREFERENCE = "PricePreference";
    public static final String ITEM_ID = "item_id";
    private static final String TAG = "DetailsActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;

    @BindView(R.id.photo)
    ImageView photo;

    @BindView(R.id.avgRating)
    TextView avgRating;

    @BindView(R.id.numRatings)
    TextView numRatings;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.wishlist)
    ToggleButton wishlist;

    @BindView(R.id.manufacturer)
    TextView manufacturer;

    @BindView(R.id.category)
    TextView category;

    @BindView(R.id.addRatingBar)
    RatingBar addRatingBar;

    @BindView(R.id.note)
    TextView note;

    @BindView(R.id.noteView)
    CardView noteView;

    @BindView(R.id.noteText)
    EditText noteText;

    @BindView(R.id.editNote)
    Button editNote;

    @BindView(R.id.avgPrice)
    TextView avgPrice;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.shareButton)
    Button share;

    private RatingsRecyclerViewAdapter adapter;

    private DetailsViewModel model;
    private String beerId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        toolbar.setTitleTextColor(Color.alpha(0));

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            Log.i("MyApp", "Deep link clicked " + uri);
            if (Pattern.compile("https://www.beerpro.ch/beer/[a-zA-Z0-9]*$").matcher(uri).matches()) {
                beerId = uri.replaceAll("https://www.beerpro.ch/beer/", "");
            }
        } else {
            beerId = getIntent().getExtras().getString(ITEM_ID, "");
        }

        model = ViewModelProviders.of(this).get(DetailsViewModel.class);
        model.setBeerId(beerId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RatingsRecyclerViewAdapter(this, model.getCurrentUser());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        model.getBeer().observe(this, this::updateBeer);
        model.getRatings().observe(this, this::updateRatings);
        model.getWish().observe(this, this::toggleWishlistView);
        model.getMyRatings().observe(this, this::setPersonalAvgRating);

        recyclerView.setAdapter(adapter);

        SharedPreferences settings = getSharedPreferences(NOTE_PREFERENCE, MODE_PRIVATE);
        editNote.setOnClickListener(getNoteListener());
        changeVisibilityOfNoteField(settings);
        updateNote(settings);

    }

    private void setPersonalAvgRating(List<Rating> ratings) {
        float ratingSum = 0;
        int ratingCounter = 0;
        for (Rating rating: ratings) {
            if (rating.getBeerId().equals(beerId)) {
                ratingSum += rating.getRating();
                ratingCounter++;
            }
        }
        float ratingAvg = ratingSum / ratingCounter;
        setPersonalAvgRating(ratingAvg);
    }

    private void setPersonalAvgRating(float rating) {
        addRatingBar.setRating(rating);
        addRatingBar.setOnRatingBarChangeListener(this::addNewRating);
     //   share.setOnClickListener(v -> onShareListener());

    }

    private void addNewRating(RatingBar ratingBar, float v, boolean b) {
        Intent intent = new Intent(this, CreateRatingActivity.class);
        intent.putExtra(CreateRatingActivity.ITEM, model.getBeer().getValue());
        intent.putExtra(CreateRatingActivity.RATING, v);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, addRatingBar, "rating");
        startActivity(intent, options.toBundle());
    }




    @OnClick(R.id.actionsButton)
    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.single_bottom_sheet_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        Button addToFridge = dialog.findViewById(R.id.addToFridge);
        addToFridge.setOnClickListener(v -> {
            onFridgeClickedListener(v);
            Toast toast = Toast.makeText(DetailsActivity.this, "Das Bier wurde zum Kühlschrank hinzugefügt.", Toast.LENGTH_SHORT);
            toast.show();
            dialog.dismiss();
        });

        view.findViewById(R.id.addPrice).setOnClickListener(v -> {
            DialogFragment newFragment = new PriceFragment();
            newFragment.show(getSupportFragmentManager(), "missiles");

        });

        dialog.show();

        View addPrivateNote = view.findViewById(R.id.addPrivateNote);
        addPrivateNote.setOnClickListener(getNoteListener());

        View addRating = view.findViewById(R.id.addRating);
        addRating.setOnClickListener(getRatingListener());

    }


    private View.OnClickListener getRatingListener() {
        return view -> {
            showRatingActivity(view.getContext());
        };
    }

    private void showRatingActivity(Context context) {
        Intent intent = new Intent(context, CreateRatingActivity.class);
        intent.putExtra(CreateRatingActivity.ITEM, model.getBeer().getValue());
        startActivity(intent);
    }

    private View.OnClickListener getNoteListener() {
        return view -> {
            showNoteDialog(view.getContext());
        };
    }

    private void showNoteDialog(Context context) {
        SharedPreferences settings = getSharedPreferences(NOTE_PREFERENCE, MODE_PRIVATE);
        EditText noteText = new EditText(context);
        noteText.setHint("Notiz");
        noteText.setText(settings.getString(beerId, ""));
        new AlertDialog.Builder(context)
                .setTitle("Persönliche Notiz hinzufügen")
                .setView(noteText)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(beerId, noteText.getText().toString());
                    editor.commit();

                    changeVisibilityOfNoteField(settings);
                    updateNote(settings);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void changeVisibilityOfNoteField(SharedPreferences settings) {
        if (settings.contains(beerId)) {
            noteView.setVisibility(CardView.VISIBLE);
        } else {
            noteView.setVisibility(CardView.GONE);
        }
    }

    private void updateNote(SharedPreferences settings) {
        String note = settings.getString(beerId, "");
        noteText.setText(note);
    }


    private void updateBeer(Beer item) {
        name.setText(item.getName());
        manufacturer.setText(item.getManufacturer());
        category.setText(item.getCategory());
        name.setText(item.getName());
        GlideApp.with(this).load(item.getPhoto()).apply(new RequestOptions().override(120, 160).centerInside())
                .into(photo);
        ratingBar.setNumStars(5);
        ratingBar.setRating(item.getAvgRating());
        avgRating.setText(getResources().getString(R.string.fmt_avg_rating, item.getAvgRating()));
        numRatings.setText(getResources().getString(R.string.fmt_ratings, item.getNumRatings()));
        avgPrice.setText(String.valueOf(item.getAvgPrice()) + "$");
        toolbar.setTitle(item.getName());
        if (item.getNumPrices() == 0) { avgPrice.setText("-- CHF");}
        else {
            avgPrice.setText("Average: " + item.getAvgPrice() + " aus " + item.getNumPrices());
        }
    }

    private void updateRatings(List<Rating> ratings) {
        adapter.submitList(new ArrayList<>(ratings));
    }

    @Override
    public void onRatingLikedListener(Rating rating) {
        model.toggleLike(rating);
    }

    @OnClick(R.id.wishlist)
    public void onWishClickedListener(View view) {
        model.toggleItemInWishlist(model.getBeer().getValue().getId());
        /*
         * We won't get an update from firestore when the wish is removed, so we need to reset the UI state ourselves.
         * */
        if (!wishlist.isChecked()) {
            toggleWishlistView(null);
        }
    }

    private void toggleWishlistView(Wish wish) {
        if (wish != null) {
            int color = getResources().getColor(R.color.colorPrimary);
            setDrawableTint(wishlist, color);
            wishlist.setChecked(true);
        } else {
            int color = getResources().getColor(android.R.color.darker_gray);
            setDrawableTint(wishlist, color);
            wishlist.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onFridgeClickedListener(View view) {
        model.addToFridge(model.getBeer().getValue().getId());
    }

    public void onShareListener() {
        Beer beer = model.getBeer().getValue();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareText = "Schau dir mal dieses Bier an '" + beer.getName() + "'\nhttps://bieraffe.page.link/bier" + beer.getId();
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Bier mit Freunden teilen");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sharingIntent, "Sharing Option"));
    }

    @OnClick(R.id.shareButton)
    public void onShareBeerClickedListener(View view) {
        Intent sendIntent = new Intent();
        String currentBeer = model.getBeer().getValue().getId();
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")
                .authority("bieraffe.page.link")
                .appendQueryParameter("currentBeer", currentBeer);

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(builder.build())
                .setDomainUriPrefix("bieraffe.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Schau dir dieses Bier an: " + dynamicLinkUri.toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, ""));
    }

    public void updatePrice(float priceInput) {
        model.updateBeerPrice(priceInput);
    }
}


