package com.gnufsociety.openchallenge;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

public class ParticipantsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView recyclerView;
    ParticipantAdapter adapter;
    Challenge c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);
        Bundle bundle = getIntent().getExtras();
        c = (Challenge) bundle.getSerializable("challenge");

        AsyncTask<Void, Void, ArrayList<User>> task = new AsyncTask<Void, Void, ArrayList<User>>() {
            @Override
            protected ArrayList<User> doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                return api.getParticipant(c.id);
            }

            @Override
            protected void onPostExecute(ArrayList<User> users) {
                adapter = new ParticipantAdapter(users);

                recyclerView.setAdapter(adapter);
            }
        };

        task.execute();


        mToolbar = (Toolbar) findViewById(R.id.participants_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.part_recycler);


        /**
        * Pass here the participant list with apihelper
        * */

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Participants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
