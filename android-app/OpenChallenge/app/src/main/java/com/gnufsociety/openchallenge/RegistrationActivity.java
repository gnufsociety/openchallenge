package com.gnufsociety.openchallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by sdc on 1/16/17.
 */

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        //regBtn = (Button) findViewById(R.id.reg_btn);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.logreg_frame,new LogInFragment(), "reg").commit();
    }



}
