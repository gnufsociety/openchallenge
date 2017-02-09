package com.gnufsociety.openchallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

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

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(user.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Random random = new Random(System.currentTimeMillis());
        goldMedal.setText((random.nextInt() % 100)+"");
        silverMedal.setText((random.nextInt() % 100)+"");
        bronzeMedal.setText((random.nextInt() % 100)+"");
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
