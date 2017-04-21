package com.gnufsociety.openchallenge;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DonateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, RegistrationActivity.class);
        return in;
    }
}
