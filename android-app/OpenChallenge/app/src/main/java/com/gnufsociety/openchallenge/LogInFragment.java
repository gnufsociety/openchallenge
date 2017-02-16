package com.gnufsociety.openchallenge;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LogInFragment extends Fragment {

    private Button logBtn;

    private EditText emailEdit, passEdit;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public LogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        emailEdit = (EditText) view.findViewById(R.id.email_log);
        passEdit = (EditText) view.findViewById(R.id.pass_log);
        logBtn = (Button) view.findViewById(R.id.log_btn);
        //if clicked on the register here text, show register fragment
        view.findViewById(R.id.link_register_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logreg_frame,new RegistrationFragment()).commit();
            }
        });
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUser();
            }
        });

        return view;
    }

    public void logUser(){
        String email = emailEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();

        auth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent in = new Intent(getContext(), MainActivity.class);
                            Bundle extra = new Bundle();
                            extra.putBoolean("new", false);
                            in.putExtras(extra);
                            startActivity(in);
                        }
                            //checkIfUserExists(auth.getCurrentUser(),passEdit.getContext());
                        else {
                            System.out.println(task.getException());
                            Toast.makeText(getContext(), "Non sei registrato furbetto", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //theoretically this is unnecessary because if a user just login, he already have configured his account
    private void checkIfUserExists(final FirebaseUser user, final Context context) {
        database.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent in = new Intent(context, MainActivity.class);
                Bundle extra = new Bundle();
                if (!dataSnapshot.hasChild(user.getUid()))
                    extra.putBoolean("new",true);

                else
                    extra.putBoolean("new", false);
                in.putExtras(extra);
                startActivity(in);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
