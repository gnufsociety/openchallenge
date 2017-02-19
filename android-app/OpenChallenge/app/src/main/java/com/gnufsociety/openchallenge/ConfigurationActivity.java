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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class ConfigurationActivity extends AppCompatActivity {

    private static final int PICK_GALLERY_INTENT = 1;

    @BindView(R.id.username_first)   EditText usernameEdit;
    @BindView(R.id.configure_status) EditText statusEdit;
    @BindView(R.id.done_btn)         Button doneBtn;
    @BindView(R.id.configure_image)  CircleImageView civ;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Activity activity;
    private Uri uriImage;


    @OnClick(R.id.done_btn)
    void tryToCreateUser(){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            createUser();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_configuration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        activity = this;
        ButterKnife.bind(this);
    }

    //add user to database
    /*private boolean writeNewUser(FirebaseUser user) {
        String username = usernameEdit.getText().toString();
        if (username.equals(""))
            return false;
        User u = new User(username,0.0,1);
        u.email = user.getEmail();
        database.getReference().child("users").child(user.getUid()).setValue(u);

        return true;
    }*/

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
                    StorageReference userRef = storageRef.child("users/"+usPic);
                    UploadTask uploadTask = userRef.putFile(uriImage);


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
                    civ.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
