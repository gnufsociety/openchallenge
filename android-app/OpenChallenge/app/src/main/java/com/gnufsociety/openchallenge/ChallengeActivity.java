package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChallengeActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static int WINNER_CODE = 1;

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
    public Podium podium;
    private FirebaseAuth auth;
    public SwipeRefreshLayout refresh;
    public Button join;
    public boolean isOrganizer = false;
    public boolean joined = false;


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
        join = (Button) findViewById(R.id.chall_join_btn);
        podium = (Podium) findViewById(R.id.chall_podium);
        /*refresh = (SwipeRefreshLayout) findViewById(R.id.chall_refresh);*/

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
                    boolean find = integer.getBoolean("you");
                    int num = integer.getInt("participants");
                    numPart.setText(num + " participants");
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
        task.execute();


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        StorageReference cImage = sref.child("challenges/" + c.imageLocation);

        if (auth.getCurrentUser().getUid().equals(c.organizer.uid)) {
            join.setText("Chooooose winner");
            isOrganizer = true;
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

        /*refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.black);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(refresh.getContext(),"",Toast.LENGTH_LONG).show();
                refresh.setRefreshing(false);
            }
        });*/

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
                Button btn = (Button) view;
                String n = numPart.getText().toString();
                int i = Integer.parseInt(n.split(" ")[0]);
                if (joined) {
                    numPart.setText(--i + " participants");
                    btn.setText("Join Challenge");
                } else {
                    numPart.setText(++i + " participants");
                    btn.setText("Joined!");
                }
                joined = !joined;
            }
        };

        if (isOrganizer) {
            Intent intent = new Intent(this,WinnerActivity.class);
            Bundle extra = new Bundle();
            extra.putString("chall_id",c.id);
            intent.putExtras(extra);
            startActivityForResult(intent,WINNER_CODE);

        } else
            task.execute();

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
            }
        }
    }
}
