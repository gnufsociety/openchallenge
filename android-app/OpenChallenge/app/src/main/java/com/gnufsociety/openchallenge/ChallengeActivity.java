package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChallengeActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static int WINNER_CODE = 1;
    Challenge c;
    private GoogleMap mMap;
    private FirebaseAuth auth;

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
    @BindView(R.id.chall_podium)
    Podium podium;
    @BindView(R.id.chall_join_btn) Button join;
    @BindView(R.id.chall_refresh) SwipeRefreshLayout refreshLayout;

    public boolean isOrganizer = false;
    public boolean joined = false;

    public StorageReference store;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        c = (Challenge) getIntent().getExtras().getSerializable("challenge");
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();

        AsyncTask<Void, Void, JSONObject> task = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                return api.numParticipant(c.id, auth.getCurrentUser().getUid());
            }

            @Override
            protected void onPostExecute(JSONObject integer) {
                try {
                    boolean find = integer.getBoolean("joined");
                    int num = integer.getInt("numParticipants");
                    numPart.setText("" + num + " participants");
                    if (find) {
                        join.setText("Joined");
                        joined = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //set num participant from web
        //task.execute();
        if (auth.getCurrentUser().getUid().equals(c.organizer.uid)) {
            join.setText(R.string.choose_winner);
            isOrganizer = true;
        }

        ChallengeAsync ca = new ChallengeAsync();
        ca.execute();
        //task.execute();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ChallengeAsync ca = new ChallengeAsync();
                ca.execute();
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        store = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");




        /*switch (c.simplePart.size()){
            case 1:
                StorageReference p1 = sref.child("users/" + c.simplePart.get(0).proPicLocation);
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(p1)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(part1);
                part1.setVisibility(View.VISIBLE);
                break;
            case 2:
                StorageReference p11 = sref.child("users/" + c.simplePart.get(0).proPicLocation);
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(p11)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(part1);
                StorageReference p2 = sref.child("users/" + c.simplePart.get(1).proPicLocation);
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(p2)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(part2);
                part1.setVisibility(View.VISIBLE);
                part2.setVisibility(View.VISIBLE);

                break;
            case 3:
                StorageReference p111 = sref.child("users/" + c.simplePart.get(0).proPicLocation);
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(p111)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(part1);
                StorageReference p22 = sref.child("users/" + c.simplePart.get(1).proPicLocation);
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(p22)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(part2);
                StorageReference p3 = sref.child("users/" + c.simplePart.get(2).proPicLocation);
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(p3)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(part3);
                part1.setVisibility(View.VISIBLE);
                part2.setVisibility(View.VISIBLE);
                part3.setVisibility(View.VISIBLE);

                break;
        }*/
        StorageReference cImage = store.child("challenges/" + c.imageLocation);
        image.setImageResource(c.resImage);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(cImage)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(image);

        desc.setText(c.desc);
        where.setText(c.address.split(",")[0]);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        when.setText(format.format(c.when));
        rules.setText(c.rules);


        user_img.setImageResource(c.organizer.resPic);

        StorageReference userImage = store.child("users/" + c.organizer.proPicLocation);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(userImage)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(user_img);

        orgUsername.setText(c.organizer.name);
        orgRate.setRating((float) c.organizer.rating);


        collapseToolbar.setTitle(c.name);
        //collapseToolbar.setExpandedTitleColor(ContextCompat.getColor(this,R.color.white));
        collapseToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));

        //Edit this two to change color and text appearance
        collapseToolbar.setCollapsedTitleTextAppearance(R.style.collapsedText);
        collapseToolbar.setExpandedTitleTextAppearance(R.style.expandedText);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.chall_map_fra);

        mapFragment.getMapAsync(this);

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
        bundle.putSerializable("challenge", c);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng marker = new LatLng(c.lat, c.lng);
        mMap.addMarker(new MarkerOptions().position(marker).title(c.name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    public void showUser(View view) {
        Intent user = new Intent(this, UserActivity.class);
        Bundle extra = new Bundle();
        extra.putSerializable("user", c.organizer);
        user.putExtras(extra);
        startActivity(user);
    }

    public void joinChallenge(final View view) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                if (joined)
                    api.removeParticipant(c.id, auth.getCurrentUser().getUid());
                else
                    api.addParticipant(c.id, auth.getCurrentUser().getUid());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                /*Button doneBtn = (Button) view;
                String n = numPart.getText().toString();
                int i = Integer.parseInt(n.split(" ")[0]);
                if (joined) {
                    numPart.setText((--i)+"" + R.string.participants);
                    doneBtn.setText(R.string.join_challenge);
                } else {
                    numPart.setText((++i)+"" + R.string.participants);
                    doneBtn.setText(R.string.joined);
                }
                joined = !joined;*/
                ChallengeAsync ac = new ChallengeAsync();
                ac.execute();
            }
        };

        if (isOrganizer) {
            Intent intent = new Intent(this,WinnerActivity.class);
            Bundle extra = new Bundle();
            extra.putString("chall_id",c.id);
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
                //Toast.makeText(this,"The winners are "+winners[0]+" "+winners[1]+" "+winners[2],Toast.LENGTH_LONG).show();
                podium.setVisibility(View.VISIBLE);
                podium.setWinners(winners);
                join.setVisibility(View.GONE);
                AsyncTask<User[],Void,Void> task = new AsyncTask<User[], Void, Void>() {
                    @Override
                    protected Void doInBackground(User[]... params) {
                        ApiHelper api = new ApiHelper();
                        api.setWinners(params[0]);
                        return null;
                    }
                };
                task.execute(winners);
            }
        }
    }

    public class ChallengeAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            ApiHelper api = new ApiHelper();

            return api.numParticipant(c.id,auth.getCurrentUser().getUid());
        }

        @Override
        protected void onPostExecute(JSONObject integer) {
            boolean youJoin;
            try {
                refreshLayout.setRefreshing(false);
                youJoin = integer.getBoolean("joined");

                int num = integer.getInt("numParticipants");

                JSONArray participants = integer.getJSONArray("participants");

                numPart.setText(num + " participants");
                if (youJoin) {
                    join.setText("Joined");
                    joined = true;
                }
                else {
                    join.setText("Join Challenge");
                    joined = false;
                }
                if (isOrganizer){
                    joined = false;
                    join.setText("Choose the winners!");
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
