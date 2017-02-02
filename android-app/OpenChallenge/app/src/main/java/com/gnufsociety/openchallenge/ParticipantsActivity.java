package com.gnufsociety.openchallenge;

import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ParticipantsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView recyclerView;
    ParticipantAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        mToolbar = (Toolbar) findViewById(R.id.participants_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.part_recycler);

        adapter = new ParticipantAdapter(User.getList());

        recyclerView.setAdapter(adapter);
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
