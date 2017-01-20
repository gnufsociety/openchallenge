package com.gnufsociety.openchallenge;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class LogInFragment extends Fragment {

    private Button logBtn;

    private EditText emailEdit, passEdit;
    private FirebaseAuth auth;

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
        view.findViewById(R.id.link_register_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logreg_frame,new RegistrationFragment()).commit();
            }
        });
        auth = FirebaseAuth.getInstance();
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
                            startActivity(in);
                        }
                        else
                            Toast.makeText(getContext(),"Non sei registrato furbetto",Toast.LENGTH_LONG).show();
                    }
                });
    }

}
