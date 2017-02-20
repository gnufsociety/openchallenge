package com.gnufsociety.openchallenge.mainfrags;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sdc on 1/11/17.
 */

public class ProfileFragment extends Fragment {

    public static String TAG = "fragment5_profile";
    public CircleImageView civ;
    public TextView gold, silver, bronze;
    public TextView status;
    public LinearLayout layout;
    public ProgressBar spinner;

    public ProfileFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment5_profile,container,false);
        civ = (CircleImageView) view.findViewById(R.id.user_pro_pic);
        gold = (TextView) view.findViewById(R.id.user_number_gold);
        silver = (TextView) view.findViewById(R.id.user_number_silver);
        bronze = (TextView) view.findViewById(R.id.user_number_bronze);
        status = (TextView) view.findViewById(R.id.user_status);
        spinner = (ProgressBar) view.findViewById(R.id.user_progress_bar);
        layout = (LinearLayout) view.findViewById(R.id.user_layout);

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

                Glide.with(civ.getContext())
                        .using(new FirebaseImageLoader())
                        .load(userRef)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(civ);

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
