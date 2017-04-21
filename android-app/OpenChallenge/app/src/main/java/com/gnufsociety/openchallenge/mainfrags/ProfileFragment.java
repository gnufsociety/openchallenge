package com.gnufsociety.openchallenge.mainfrags;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.ApiHelper;
import com.gnufsociety.openchallenge.NoConnectionActivity;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.RegistrationActivity;
import com.gnufsociety.openchallenge.adapters.ChallengeAdapter;
import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sdc on 1/11/17.
 */

public class ProfileFragment extends Fragment{

    public static String TAG = "fragment5_profile";

    private static final int PICK_GALLERY_INTENT = 42;

    @BindView(R.id.user_pro_pic) public CircleImageView profilePic;
    @BindView(R.id.user_number_gold) public TextView gold;
    @BindView(R.id.user_number_silver) public TextView silver;
    @BindView(R.id.user_number_bronze) public TextView bronze;
    @BindView(R.id.profile_status) public TextView status;
    @BindView(R.id.user_layout) public LinearLayout layout;
    @BindView(R.id.profile_progress_bar) public ProgressBar spinner;
    @BindView(R.id.edit_status_btn) public ImageButton editStatusBtn;

    @BindView(R.id.show_organized) public RelativeLayout orgList;
    @BindView(R.id.show_joined) public RelativeLayout joinedList;
    @BindView(R.id.show_hide_org) public ImageView showHideOrg;
    @BindView(R.id.show_hide_joined) public ImageView showHideJoined;

    @BindView(R.id.profile_org_recycler) public RecyclerView orgRecycler;
    @BindView(R.id.profile_refresh) public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.profile_join_recycler) public RecyclerView joinedRecycler;

    public ChallengeAdapter orgCardAdapter;
    public ChallengeAdapter joinedCardAdapter;

    public User currentUser;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageRef=storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
    private Uri uriImage;

    public ProfileFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            // already signed in
            Intent intent = new Intent(getActivity(), NoConnectionActivity.class);
            startActivity(intent);
            return null;
        }

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

                // IMPORTANT: the currentUser must be loaded before we can execute
                // the tasks for downloading organized/joined challenges.
                populateChallenges(false);

            }
        };

        orgRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        joinedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        orgRecycler.setNestedScrollingEnabled(false);
        orgRecycler.addItemDecoration(dividerItemDecoration);

        joinedRecycler.setNestedScrollingEnabled(false);
        joinedRecycler.addItemDecoration(dividerItemDecoration);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserTask.execute(auth.getCurrentUser().getUid());

        // is all this code duplication really needed?
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateChallenges(true);

            }
        });

        return view;
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


    @OnClick(R.id.show_organized)
    public void revealOrganized() {
        if(orgRecycler.getVisibility() == View.VISIBLE) {
            orgRecycler.setVisibility(View.GONE);
            showHideOrg.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        } else {
            orgRecycler.setVisibility(View.VISIBLE);
            showHideOrg.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }
    }

    @OnClick(R.id.show_joined)
    public void revealJoined() {
        if(joinedRecycler.getVisibility() == View.VISIBLE) {
            joinedRecycler.setVisibility(View.GONE);
            showHideJoined.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        } else {
            joinedRecycler.setVisibility(View.VISIBLE);
            showHideJoined.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }
    }


    @OnClick(R.id.edit_status_btn)
    public void editStatus() {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.insert_new_status)
                .setView(getActivity().getLayoutInflater()
                                      .inflate(R.layout.dialog_status, null))
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        status.setText(((EditText) ((AlertDialog) dialog).findViewById(R.id.new_status_text))
                                .getText().toString());
                        uploadNewStatus();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }


    public void uploadNewStatus() {
        final String new_status = status.getText().toString();
        AsyncTask<Void, Void, Void> uploadStatus = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                new ApiHelper().setStatus(currentUser, new_status);
                return null;
            }
        };
        uploadStatus.execute();
    }

    @OnClick(R.id.user_pro_pic)
    public void editProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select from gallery"), PICK_GALLERY_INTENT);
    }


    public void uploadProfilePicture() {
        try {
            Bitmap bitmap = MediaStore.Images.Media
                    .getBitmap(getContext().getContentResolver(), uriImage);
            profilePic.setImageBitmap(bitmap);
            File toCompress = FileUtil.from(getActivity(), uriImage);

            File compressedFile = new Compressor.Builder(getActivity())
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setQuality(90)
                    .build().compressToFile(toCompress);


            String pic_title = currentUser.proPicLocation;

            StorageReference oldRef = storageRef.child("users/" + pic_title);

            oldRef.delete().addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Error deleting old profile picture");
                }
            });

            UploadTask uploadTask = oldRef.putFile(Uri.fromFile(compressedFile));

            uploadTask.addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), R.string.error_profpic_upload,Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), R.string.success_profpic_upload, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_GALLERY_INTENT){
            if (resultCode == RESULT_OK){
                uriImage = data.getData();
                uploadProfilePicture();
            }
        }
    }


    public void deleteUser() {
        // TODO: call api to delete currentUser (not implemented yet)
        /* Uncomment when api implemented
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Deletion succeeded: delete from database through api call
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
                        // currentUser is now signed out
                        startActivity(new Intent(getContext(),RegistrationActivity.class));
                        getActivity().finish();
                    }
                });
    }


}
