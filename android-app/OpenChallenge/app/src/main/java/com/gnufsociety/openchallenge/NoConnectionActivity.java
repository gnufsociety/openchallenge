package com.gnufsociety.openchallenge;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by davidespallaccini on 03/03/17.
 */

public class NoConnectionActivity extends AppCompatActivity {

    @BindView(R.id.no_conn_btn)
    Button retryBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.no_conn_btn)
    public void restart() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            System.out.println(">>>>>>>>>>>>>>>> CONNECTED <<<<<<<<<<<<<<<<<<");
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                // already signed in
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
