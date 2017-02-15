package com.gnufsociety.openchallenge;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.List;

public class WinnerActivity extends AppCompatActivity {

    public RecyclerView rec;
    public ProgressBar progressBar;
    public RelativeLayout layout;
    public FloatingActionButton btn;

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


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rec.setLayoutManager(new LinearLayoutManager(this));
        AsyncTask<String, Void, List<User>> task = new AsyncTask<String, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(String... params) {
                ApiHelper api = new ApiHelper();
                int i = 0;
                return api.getParticipant(params[0]);
            }

            @Override
            protected void onPostExecute(List<User> users) {
                WinnerAdapter adapter = new WinnerAdapter(users);
                rec.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                Snackbar.make(layout,"Choose the first",Snackbar.LENGTH_SHORT).show();
            }
        };

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rec.getContext(), DividerItemDecoration.VERTICAL);
        rec.addItemDecoration(dividerItemDecoration);

        task.execute(chall_id);


    }
}
