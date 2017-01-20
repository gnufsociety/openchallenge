package com.gnufsociety.openchallenge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by sdc on 1/16/17.
 */

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener listener;
    private FirebaseAuth auth;
    private Context context;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        context = this;
        auth = FirebaseAuth.getInstance();
        //if a user is already connected don't show login activity and jump to main acivity
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(context,MainActivity.class);
                    Bundle extras = new Bundle();
                    extras.putBoolean("new", false);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        };
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.logreg_frame,new LogInFragment(), "log").commit();
    }



}
