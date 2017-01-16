package com.gnufsociety.openchallenge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sdc on 1/16/17.
 */

public class RegistrationActivity extends AppCompatActivity {

    private Button regBtn;
    private TextView emailView, passView, userView;

    private FirebaseAuth auth;

    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        //regBtn = (Button) findViewById(R.id.reg_btn);
        userView = (TextView) findViewById(R.id.user_reg);
        emailView = (TextView) findViewById(R.id.email_reg);
        passView = (TextView) findViewById(R.id.pass_reg);
    }

    public void regUser(View view){
        String email = emailView.getText().toString();
        String pass = passView.getText().toString();
        String user = userView.getText().toString();

        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Snackbar.make(emailView.getRootView(), "Register: "+task.isSuccessful(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public void logUser(View view){
        String email = emailView.getText().toString();
        String pass = passView.getText().toString();
        auth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Snackbar.make(emailView.getRootView(), "Logged: "+task.isSuccessful(), Snackbar.LENGTH_LONG).show();

                    }
                });
    }
}
