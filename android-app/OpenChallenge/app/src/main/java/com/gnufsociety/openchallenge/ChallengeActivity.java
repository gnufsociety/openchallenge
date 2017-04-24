package com.gnufsociety.openchallenge;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.customui.Podium;
import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChallengeActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static int WINNER_CODE = 1;
    Challenge challenge;
    private GoogleMap mMap;
    private FirebaseAuth auth;
    private ShareActionProvider miShareAction;


    @BindView(R.id.chall_collapsing_toolbar) CollapsingToolbarLayout collapseToolbar;
    @BindView(R.id.chall_toolbar) Toolbar toolbar;
    @BindView(R.id.chall_image) ImageView image;
    @BindView(R.id.chall_user_img) CircleImageView user_img;
    @BindView(R.id.chall_part1) CircleImageView part1;
    @BindView(R.id.chall_part2) CircleImageView part2;
    @BindView(R.id.chall_part3) CircleImageView part3;
    @BindView(R.id.chall_organizer) TextView orgUsername;
    @BindView(R.id.chall_rate) RatingBar orgRate;
    @BindView(R.id.chall_where) TextView where;
    @BindView(R.id.chall_when) TextView when;
    @BindView(R.id.chall_desc) TextView desc;
    @BindView(R.id.chall_rules) TextView rules;
    @BindView(R.id.chall_npart) TextView numPart;
    @BindView(R.id.chall_podium) Podium podium;
    @BindView(R.id.chall_join_btn) Button join;
    @BindView(R.id.chall_refresh) SwipeRefreshLayout refreshLayout;

    public boolean isOrganizer = false;
    public boolean joined = false;

    public StorageReference store;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            // already signed in
            System.out.println(">>>>>>>>>>>>>>>> NOT CONNECTED <<<<<<<<<<<<<<<<<<");
            Intent intent = new Intent(this, NoConnectionActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        ButterKnife.bind(this);

        challenge = (Challenge) getIntent().getExtras().getSerializable("challenge");

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser().getUid().equals(challenge.organizer.uid)) {
            join.setText(R.string.choose_winner);
            isOrganizer = true;
        }

        ChallengeAsync ca = new ChallengeAsync();
        ca.execute();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ChallengeAsync ca = new ChallengeAsync();
                ca.execute();
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        store = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");

        StorageReference cImage = store.child("challenges/" + challenge.imageLocation);
        image.setImageResource(challenge.resImage);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(cImage)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(image);

        desc.setText(challenge.desc);
        where.setText(challenge.address.split(",")[0]);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        when.setText(format.format(challenge.when));
        rules.setText(challenge.rules);


        user_img.setImageResource(challenge.organizer.resPic);

        StorageReference userImage = store.child("users/" + challenge.organizer.proPicLocation);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(userImage)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(user_img);

        orgUsername.setText(challenge.organizer.name);
        orgRate.setRating((float) challenge.organizer.rating);


        collapseToolbar.setTitle(challenge.name);
        collapseToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));

        //Edit this two to change color and text appearance
        collapseToolbar.setCollapsedTitleTextAppearance(R.style.collapsedText);
        collapseToolbar.setExpandedTitleTextAppearance(R.style.expandedText);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.chall_map_fra);

        mapFragment.getMapAsync(this);

        if(!challenge.isTerminated) join.setVisibility(View.VISIBLE);

        // check if challenge is terminated
        checkIsTerminated();
    }


    public void checkIsTerminated() {
        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                ArrayList<User> winners = new ArrayList<>(3);
                if(challenge.isTerminated) {
                    winners = new ApiHelper().getWinners(challenge);
                }
                return winners;
            }

            @Override
            protected void onPostExecute(List<User> winners) {
                super.onPostExecute(winners);
                if(challenge.isTerminated) {
                    podium.setVisibility(View.VISIBLE);
                    User[] onPodium = new User[winners.size()];
                    winners.toArray(onPodium);
                    podium.setWinners(onPodium);
                    join.setVisibility(View.GONE);
                }
            }
        };
        task.execute();
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.challenge_menu, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
        // Return true to display menu
        return true;
    }*/


    private void setShareIntent(Intent shareIntent) {
        if (miShareAction != null) {
            miShareAction.setShareIntent(shareIntent);
        }
    }
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Come and join this challenge: '"+ challenge.name +"'");
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    public void showParticipants(View view) {
        Intent intent = new Intent(this, ParticipantsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("challenge", challenge);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng marker = new LatLng(challenge.lat, challenge.lng);
        mMap.addMarker(new MarkerOptions().position(marker).title(challenge.name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    public void showUser(View view) {
        Intent user = new Intent(this, UserActivity.class);
        Bundle extra = new Bundle();
        extra.putSerializable("currentUser", challenge.organizer);
        user.putExtras(extra);
        startActivity(user);
    }

    public void joinChallenge(final View view) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                if (joined)
                    api.removeParticipant(challenge.id, auth.getCurrentUser().getUid());
                else
                    api.addParticipant(challenge.id, auth.getCurrentUser().getUid());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ChallengeAsync ac = new ChallengeAsync();
                ac.execute();
            }
        };

        if (isOrganizer) {
            Intent intent = new Intent(this,WinnerActivity.class);
            Bundle extra = new Bundle();
            extra.putString("chall_id", challenge.id);
            intent.putExtras(extra);
            startActivityForResult(intent,WINNER_CODE);

        } else {
            task.execute();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WINNER_CODE){
            if (resultCode == RESULT_OK){
                User[] winners = (User[]) data.getSerializableExtra("winners");
                podium.setVisibility(View.VISIBLE);
                podium.setWinners(winners);
                join.setVisibility(View.GONE);
            }
        }
    }

    public class ChallengeAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            ApiHelper api = new ApiHelper();

            return api.numParticipant(challenge.id,auth.getCurrentUser().getUid());
        }

        @Override
        protected void onPostExecute(JSONObject integer) {
            boolean youJoin;
            try {
                refreshLayout.setRefreshing(false);
                youJoin = integer.getBoolean("joined");

                int num = integer.getInt("numParticipants");

                JSONArray participants = integer.getJSONArray("participants");
                Resources res = getResources();
                if (num == 1)
                    numPart.setText(res.getString(R.string.single_participant,num));
                else
                    numPart.setText(res.getString(R.string.participants,num));
                if (youJoin) {
                    join.setText(R.string.joined);
                    joined = true;
                }
                else {
                    join.setText(R.string.join_challenge);
                    joined = false;
                }
                if (isOrganizer){
                    joined = false;
                    join.setText(R.string.choose_winner);
                }

                if (num > 0){
                    String image = participants.getJSONObject(0).getString("picture");
                    StorageReference p1 = store.child("users/" + image);
                    Glide.with(numPart.getContext())
                            .using(new FirebaseImageLoader())
                            .load(p1)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(part1);
                    part1.setVisibility(View.VISIBLE);
                }
                if (num > 1){
                    String image = participants.getJSONObject(1).getString("picture");
                    StorageReference p2 = store.child("users/" + image);
                    Glide.with(numPart.getContext())
                            .using(new FirebaseImageLoader())
                            .load(p2)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(part2);
                    part2.setVisibility(View.VISIBLE);
                }
                if (num > 2){
                    String image = participants.getJSONObject(2).getString("picture");
                    StorageReference p3 = store.child("users/" + image);
                    Glide.with(numPart.getContext())
                            .using(new FirebaseImageLoader())
                            .load(p3)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(part3);
                    part3.setVisibility(View.VISIBLE);
                }

                if (num < 3) part3.setVisibility(View.GONE);
                if (num < 2) part2.setVisibility(View.GONE);
                if (num < 1) part1.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
