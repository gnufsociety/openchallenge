package com.gnufsociety.openchallenge.mainfrags;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.ApiHelper;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    //@BindView(0) public Button deleteUserBtn;
    //@BindView(0) public Button logoutBtn;

    public ProfileFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment5_profile,container,false);

        ButterKnife.bind(this,view);

        AsyncTask<String,Void,User> task = new AsyncTask<String, Void, User>() {
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

                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(user.name);

                spinner.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

            }
        };
        FirebaseAuth auth = FirebaseAuth.getInstance();
        task.execute(auth.getCurrentUser().getUid());
        return view;
    }


}
