package com.gnufsociety.openchallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    public User user;
    public CircleImageView userPic;
    public Toolbar toolbar;
    public TextView userStatus;
    public TextView goldMedal, silverMedal, bronzeMedal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle extra = getIntent().getExtras();
        user = (User) extra.getSerializable("user");


        userStatus = (TextView) findViewById(R.id.user_status);
        userPic = (CircleImageView) findViewById(R.id.user_pro_pic);
        goldMedal = (TextView) findViewById(R.id.user_number_gold);
        silverMedal = (TextView) findViewById(R.id.user_number_silver);
        bronzeMedal = (TextView) findViewById(R.id.user_number_bronze);
        toolbar = (Toolbar) findViewById(R.id.user_toolbar);

        userPic.setImageResource(user.resPic);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        StorageReference userRef = sref.child("users/"+user.proPicLocation);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(userRef)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(userPic);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(user.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        goldMedal.setText(user.goldMedals+"");
        silverMedal.setText(user.silverMedals+"");
        bronzeMedal.setText(user.bronzeMedals+"");
        userStatus.setText(user.status);
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
}
