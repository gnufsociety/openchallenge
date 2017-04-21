package com.gnufsociety.openchallenge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.adapters.ChallengeAdapter;
import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    public User currentUser;

    @BindView(R.id.user_pro_pic) public CircleImageView userPic;
    @BindView(R.id.user_toolbar) public Toolbar toolbar;
    @BindView(R.id.user_status)  public TextView userStatus;

    @BindView(R.id.user_number_gold)   public TextView goldMedal;
    @BindView(R.id.user_number_silver) public TextView silverMedal;
    @BindView(R.id.user_number_bronze) public TextView bronzeMedal;

    @BindView(R.id.user_progress_bar) public ProgressBar spinner;

    @BindView(R.id.user_show_organized) public RelativeLayout orgList;
    @BindView(R.id.user_show_joined) public RelativeLayout joinedList;
    @BindView(R.id.user_show_hide_org) public ImageView showHideOrg;
    @BindView(R.id.user_show_hide_joined) public ImageView showHideJoined;

    @BindView(R.id.user_org_recycler) public RecyclerView orgRecycler;
    @BindView(R.id.user_refresh) public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.user_join_recycler) public RecyclerView joinedRecycler;

    @BindView(R.id.follow_button) Button fButton;

    public ChallengeAdapter orgCardAdapter;
    public ChallengeAdapter joinedCardAdapter;
    public boolean isFollowed = false;
    private FirebaseAuth auth;

    StorageReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        auth = FirebaseAuth.getInstance();

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
        Bundle extra = getIntent().getExtras();
        currentUser = (User) extra.getSerializable("currentUser");
        userPic.setImageResource(currentUser.resPic);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        userRef = sref.child("users/"+ currentUser.proPicLocation);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(userRef)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(userPic);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(currentUser.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        goldMedal.setText(currentUser.goldMedals+"");
        silverMedal.setText(currentUser.silverMedals+"");
        bronzeMedal.setText(currentUser.bronzeMedals+"");
        userStatus.setText(currentUser.status);

        orgRecycler.setLayoutManager(new LinearLayoutManager(this));
        joinedRecycler.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        orgRecycler.setNestedScrollingEnabled(false);
        orgRecycler.addItemDecoration(dividerItemDecoration);

        joinedRecycler.setNestedScrollingEnabled(false);
        joinedRecycler.addItemDecoration(dividerItemDecoration);

        checkFollow();
        populateChallenges(false);

        // is all this code duplication really needed?
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateChallenges(true);
            }
        });
    }


    /**
     * Executes two async tasks querying the joined/organized challenges
     * from server and displaying them into the recycler list
     *
     * @param isRefresh is true if the function is called refreshing the layout
     */
    public void populateChallenges(final boolean isRefresh) {
        AsyncTask<User,Void,ArrayList<Challenge>> organizedTask = new AsyncTask<User, Void, ArrayList<Challenge>>() {
            @Override
            protected ArrayList<Challenge> doInBackground(User... users) {
                return new ApiHelper().getOrganizedChallenges(users[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Challenge> challenges) {
                super.onPostExecute(challenges);
                orgCardAdapter = new ChallengeAdapter(challenges);
                orgRecycler.setAdapter(orgCardAdapter);
                if(isRefresh) refreshLayout.setRefreshing(false);
                spinner.setVisibility(View.GONE);
            }
        };

        AsyncTask<User,Void,ArrayList<Challenge>> joinedTask = new AsyncTask<User, Void, ArrayList<Challenge>>() {
            @Override
            protected ArrayList<Challenge> doInBackground(User... users) {
                return new ApiHelper().getJoinedChallenges(users[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Challenge> challenges) {
                super.onPostExecute(challenges);
                joinedCardAdapter = new ChallengeAdapter(challenges);
                joinedRecycler.setAdapter(joinedCardAdapter);
                if(isRefresh) refreshLayout.setRefreshing(false);
            }
        };

        organizedTask.execute(currentUser);
        joinedTask.execute(currentUser);
    }


    @OnClick(R.id.user_show_organized)
    public void revealOrganized() {
        if(orgRecycler.getVisibility() == View.VISIBLE) {
            orgRecycler.setVisibility(View.GONE);
            showHideOrg.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        } else {
            orgRecycler.setVisibility(View.VISIBLE);
            showHideOrg.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }
    }


    @OnClick(R.id.user_show_joined)
    public void revealJoined() {
        if(joinedRecycler.getVisibility() == View.VISIBLE) {
            joinedRecycler.setVisibility(View.GONE);
            showHideJoined.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        } else {
            joinedRecycler.setVisibility(View.VISIBLE);
            showHideJoined.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }
    }


    @OnClick(R.id.user_pro_pic)
    public void showPicDialog() {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = this.getLayoutInflater().inflate(R.layout.dialog_profile_pic, null);


        ImageView dialogImage = (ImageView) view.findViewById(R.id.dialog_img);

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(userRef)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(dialogImage);

        builder.setView(view);
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    public void followUser(final View view){
        isFollowed = !isFollowed; // click on button changes status.
        if(isFollowed)
            fButton.setText(R.string.followed);
        else
            fButton.setText(R.string.follow);

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                if(!isFollowed) // make coherent status of database
                    api.unfollow(auth.getCurrentUser().getUid(), currentUser.id);
                else
                    api.follow(auth.getCurrentUser().getUid(), currentUser.id);
                return isFollowed;
            }
        };
        task.execute();
    }

    public void checkFollow(){
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                ArrayList<User> followedList = api.getFollowed(auth.getCurrentUser().getUid());
                for (User u: followedList) {
                    if(u.uid.equals(currentUser.uid)) return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }

            @Override
            protected void onPostExecute(Boolean alreadyFollowed) {
                if(alreadyFollowed) {
                    fButton.setText(R.string.followed);
                    isFollowed = true;
                } else {
                    fButton.setText(R.string.follow);
                    isFollowed = false;
                }
                if(auth.getCurrentUser().getUid().equals(currentUser.uid))
                    fButton.setVisibility(View.GONE);
            }
        };
        task.execute();
    }
}
