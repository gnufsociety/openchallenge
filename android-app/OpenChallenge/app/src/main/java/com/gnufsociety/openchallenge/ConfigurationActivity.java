package com.gnufsociety.openchallenge;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;


public class ConfigurationActivity extends AppCompatActivity {

    private static final int PICK_GALLERY_INTENT = 1;

    @BindView(R.id.choose_username) EditText usernameEdit;
    @BindView(R.id.choose_status)   EditText statusEdit;
    @BindView(R.id.done_btn)        Button doneBtn;
    @BindView(R.id.logout_btn)      Button logoutBtn;
    @BindView(R.id.chosen_profile_pic) CircleImageView profilePic;
    @BindView(R.id.choose_profile_pic) ImageView chooseProfilePic;

    private FirebaseAuth auth;
    private Activity activity;
    private Uri uriImage;


    @OnClick(R.id.done_btn)
    void tryToCreateUser(){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            createUser();
        }
    }

    @OnClick(R.id.logout_btn)
    public void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(ConfigurationActivity.this, RegistrationActivity.class));
                        finish();
                    }
                });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_configuration);
        auth = FirebaseAuth.getInstance();
        activity = this;
        ButterKnife.bind(this);
    }

    /**
     * Verify that all information is filled then upload everything to server
     */
    public void createUser(){

        final String username = usernameEdit.getText().toString();
        final String usPic = username.toLowerCase().replace(" ","_");
        final String status = statusEdit.getText().toString();

        if (username.equals("")){
            Toast.makeText(activity,"Choose an username!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (uriImage == null){
            Toast.makeText(activity,"Choose a profile picture!",Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncTask<Void,Void,String> task = new AsyncTask<Void, Void, String>() {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");

            @Override
            protected String doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                return api.createUser(username,status,usPic,user.getUid());

            }

            @Override
            protected void onPostExecute(String s) {
                if (s.equals("already exist")){
                    Toast.makeText(activity,"Username already exist!", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(activity,"Uploading profile photos",Toast.LENGTH_LONG).show();
                    File toCompress = null;
                    try {
                        toCompress = FileUtil.from(activity,uriImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(activity, "Uploading challenge..", Toast.LENGTH_LONG).show();

                    //return compressed image PLAY WITH THIS
                    final File compressedFile = new Compressor.Builder(activity)
                            .setMaxHeight(800)
                            .setMaxWidth(800)
                            .setQuality(90)
                            .build().compressToFile(toCompress);

                    StorageReference userRef = storageRef.child("users/"+usPic);

                    UploadTask uploadTask = userRef.putFile(Uri.fromFile(compressedFile));

                    uploadTask.addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity,"Error uploading profile picture!",Toast.LENGTH_LONG).show();
                        }
                    });
                    uploadTask.addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(activity, "Congratulations!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        };
        task.execute();
    }


    public void chooseProPic(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select from gallery"),PICK_GALLERY_INTENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_GALLERY_INTENT){
            if (resultCode == RESULT_OK){
                uriImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriImage);
                    profilePic.setImageBitmap(bitmap);
                    chooseProfilePic.setVisibility(View.GONE);
                    profilePic.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(activity, "You can't escape from the configuration!", Toast.LENGTH_LONG)
                .show();
    }
}
