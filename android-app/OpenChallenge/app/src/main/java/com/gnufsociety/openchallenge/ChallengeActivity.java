package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChallengeActivity extends AppCompatActivity implements OnMapReadyCallback {

    public CollapsingToolbarLayout collapseToolbar;
    public Challenge c;
    public Toolbar toolbar;
    public ImageView image;
    public CircleImageView user_img;
    public TextView orgUsername;
    public RatingBar orgRate;
    public TextView where;
    public TextView when;
    public TextView desc;
    public TextView rules;
    public TextView numPart;
    private GoogleMap mMap;
    private FirebaseAuth auth;
    public boolean isOrganizer = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        c = (Challenge) getIntent().getExtras().getSerializable("challenge");
        collapseToolbar = (CollapsingToolbarLayout) findViewById(R.id.chall_collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.chall_toolbar);
        image = (ImageView) findViewById(R.id.chall_image);
        user_img = (CircleImageView) findViewById(R.id.chall_user_img);
        orgUsername = (TextView) findViewById(R.id.chall_organizer);
        orgRate = (RatingBar) findViewById(R.id.chall_rate);
        where = (TextView) findViewById(R.id.chall_where);
        when = (TextView) findViewById(R.id.chall_when);
        desc = (TextView) findViewById(R.id.chall_desc);
        rules = (TextView) findViewById(R.id.chall_rules);
        numPart = (TextView) findViewById(R.id.chall_npart);

        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                return api.numParticipant(c.id);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                numPart.setText(integer + " participants");
            }
        };



        //set num participant from web
        task.execute();



        auth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        StorageReference cImage = sref.child("challenges/" + c.imageLocation);

        if (auth.getCurrentUser().getUid().equals(c.organizer.uid)){
            ((Button) findViewById(R.id.chall_join_btn)).setText("Chooooose winner");
        }

        image.setImageResource(c.resImage);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(cImage)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(image);

        desc.setText(c.desc);
        where.setText(c.address.split(",")[0]);
        when.setText(c.when);
        rules.setText(c.rules);


        user_img.setImageResource(c.organizer.resPic);

        StorageReference userImage = sref.child("users/" + c.organizer.proPicLocation);
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
                api.addParticipant(c.id, auth.getCurrentUser().getUid());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Button btn = (Button) view;
                btn.setText("Joined!");
            }
        };
        task.execute();

    }
}
