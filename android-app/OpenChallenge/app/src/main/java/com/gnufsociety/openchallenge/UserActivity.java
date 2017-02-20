package com.gnufsociety.openchallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    public User user;

    @BindView(R.id.user_pro_pic) public CircleImageView userPic;
    @BindView(R.id.user_toolbar) public Toolbar toolbar;
    @BindView(R.id.user_status)  public TextView userStatus;

    @BindView(R.id.user_number_gold)   public TextView goldMedal;
    @BindView(R.id.user_number_silver) public TextView silverMedal;
    @BindView(R.id.user_number_bronze) public TextView bronzeMedal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);
        Bundle extra = getIntent().getExtras();
        user = (User) extra.getSerializable("user");
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
