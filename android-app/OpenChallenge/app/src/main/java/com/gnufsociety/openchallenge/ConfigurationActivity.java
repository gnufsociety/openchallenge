package com.gnufsociety.openchallenge;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class ConfigurationActivity extends AppCompatActivity {

    private EditText usernameEdit;
    private Button doneBtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_configuration);
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        usernameEdit = (EditText) findViewById(R.id.username_first);

        doneBtn = (Button) findViewById(R.id.done_btn);

        context = this;

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null){
                    if (writeNewUser(user)){
                        Toast.makeText(context,"Congratulation",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        Toast.makeText(context,"Nope!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    //add user to database
    private boolean writeNewUser(FirebaseUser user) {
        String username = usernameEdit.getText().toString();
        if (username.equals(""))
            return false;
        User u = new User(username,0.0);
        u.email = user.getEmail();
        database.getReference().child("users").child(user.getUid()).setValue(u);

        return true;
    }

}
