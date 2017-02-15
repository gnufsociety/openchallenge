package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import static com.gnufsociety.openchallenge.ChallengeActivity.WINNER_CODE;

public class WinnerActivity extends AppCompatActivity {

    public RecyclerView rec;
    public ProgressBar progressBar;
    public RelativeLayout layout;
    public FloatingActionButton btn;
    public User[] winners;
    public WinnerActivity win;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        Bundle extra = getIntent().getExtras();
        String chall_id = extra.getString("chall_id");

        rec = (RecyclerView) findViewById(R.id.winner_recycler);
        layout = (RelativeLayout) findViewById(R.id.winner_lay);
        progressBar = (ProgressBar) findViewById(R.id.winner_progress);
        btn = (FloatingActionButton) findViewById(R.id.winner_done_btn);
        toolbar = (Toolbar) findViewById(R.id.winner_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose winners");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        winners = new User[3];
        win = this;


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("winners",winners);
                setResult(-1,intent);
                finish();
            }
        });

        rec.setLayoutManager(new LinearLayoutManager(this));
        AsyncTask<String, Void, List<User>> task = new AsyncTask<String, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(String... params) {
                ApiHelper api = new ApiHelper();
                return api.getParticipant(params[0]);
            }

            @Override
            protected void onPostExecute(List<User> users) {
                WinnerAdapter adapter = new WinnerAdapter(users,win);
                rec.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                //Snackbar.make(findViewById(R.id.activity_winner),"Choose the first",Snackbar.LENGTH_INDEFINITE).show();
                Toast.makeText(btn.getContext(),"Choose the first",Toast.LENGTH_SHORT).show();
            }
        };

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rec.getContext(), DividerItemDecoration.VERTICAL);
        rec.addItemDecoration(dividerItemDecoration);

        task.execute(chall_id);


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
