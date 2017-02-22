package com.gnufsociety.openchallenge.mainfrags;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.ApiHelper;
import com.gnufsociety.openchallenge.MainActivity;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.RegistrationActivity;
import com.gnufsociety.openchallenge.adapters.CardAdapter;
import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sdc on 1/11/17.
 */

public class ProfileFragment extends Fragment {

    public static String TAG = "fragment5_profile";

    @BindView(R.id.user_pro_pic) public CircleImageView profilePic;
    @BindView(R.id.user_number_gold) public TextView gold;
    @BindView(R.id.user_number_silver) public TextView silver;
    @BindView(R.id.user_number_bronze) public TextView bronze;
    @BindView(R.id.user_status) public TextView status;
    @BindView(R.id.user_layout) public LinearLayout layout;
    @BindView(R.id.user_progress_bar) public ProgressBar spinner;

    @BindView(R.id.profile_org_recycler) public RecyclerView orgRecycler;
    @BindView(R.id.profile_refresh) public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.profile_join_recycler) public RecyclerView joinedRecycler;

    public CardAdapter orgCardAdapter;
    public CardAdapter joinedCardAdapter;

    public User currentUser;

    public ProfileFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment5_profile,container,false);

        ButterKnife.bind(this,view);

        AsyncTask<String,Void,User> currentUserTask = new AsyncTask<String, Void, User>() {
            @Override
            protected User doInBackground(String... params) {
                ApiHelper api = new ApiHelper();
                return api.getCurrentUser(params[0]);
            }

            @Override
            protected void onPostExecute(User user) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
                StorageReference userRef = sref.child("users/"+user.proPicLocation);

                Glide.with(profilePic.getContext())
                        .using(new FirebaseImageLoader())
                        .load(userRef)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(profilePic);

                status.setText(user.status);
                gold.setText(user.goldMedals+"");
                silver.setText(user.silverMedals+"");
                bronze.setText(user.bronzeMedals+"");

                currentUser = user;

                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(user.name);

                spinner.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

            }
        };

        AsyncTask<User,Void,ArrayList<Challenge>> organizedTask = new AsyncTask<User, Void, ArrayList<Challenge>>() {
            @Override
            protected ArrayList<Challenge> doInBackground(User... users) {
                return null;
            }
        };

        AsyncTask<User,Void,ArrayList<Challenge>> joinedTask = new AsyncTask<User, Void, ArrayList<Challenge>>() {
            @Override
            protected ArrayList<Challenge> doInBackground(User... users) {
                return null;
            }
        };



        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserTask.execute(auth.getCurrentUser().getUid());
        return view;
    }


    public void deleteUser() {
        // TODO: call api to delete user (not implemented yet)
        /* Uncomment when api implemented
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Deletion succeeded
                        } else {
                            // Deletion failed
                        }
                    }
                });
        */
    }


    public void logout() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(getContext(),RegistrationActivity.class));
                        getActivity().finish();
                    }
                });
    }


}
