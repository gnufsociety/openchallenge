package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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

import com.gnufsociety.openchallenge.adapters.WinnerAdapter;
import com.gnufsociety.openchallenge.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WinnerActivity extends AppCompatActivity {

    @BindView(R.id.winner_lay) public RelativeLayout layout;
    @BindView(R.id.winner_recycler) public RecyclerView recyclerView;
    @BindView(R.id.winner_progress) public ProgressBar progressBar;
    @BindView(R.id.winner_done_btn) public FloatingActionButton doneBtn;
    @BindView(R.id.winner_toolbar)  public Toolbar toolbar;

    public User[] winners;
    public WinnerActivity win;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        winners = new User[3];
        win = this;

        Bundle extra = getIntent().getExtras();
        String chall_id = extra.getString("chall_id");

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose winners");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AsyncTask<String, Void, List<User>> task = new AsyncTask<String, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(String... params) {
                ApiHelper api = new ApiHelper();
                return api.getParticipant(params[0]);
            }

            @Override
            protected void onPostExecute(List<User> users) {
                WinnerAdapter adapter = new WinnerAdapter(users,win);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(doneBtn.getContext(),"Choose the first",Toast.LENGTH_SHORT).show();
            }
        };

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        task.execute(chall_id);

    }

    @OnClick(R.id.winner_done_btn)
    public void onDone() {

        //AlertDialog dialog = new AlertDialog();

        Intent intent = new Intent();
        intent.putExtra("winners",winners);
        setResult(-1,intent);
        finish();
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
