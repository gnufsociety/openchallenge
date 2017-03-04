package com.gnufsociety.openchallenge;

import android.content.DialogInterface;
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
import android.widget.EditText;
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
    private String challenge_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        winners = new User[3];
        win = this;

        Bundle extra = getIntent().getExtras();
        challenge_id = extra.getString("chall_id");

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.choose_winners);
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
                Toast.makeText(doneBtn.getContext(), R.string.choose_first,Toast.LENGTH_SHORT).show();
            }
        };
        task.execute(challenge_id);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @OnClick(R.id.winner_done_btn)
    public void onDone() {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirm_termination)
                .setMessage(R.string.terminate_challenge_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        terminateChallenge(challenge_id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    public void terminateChallenge(String ch_id) {
        AsyncTask<String, Void, Void> taskTermination = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                new ApiHelper().terminate(strings[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                AsyncTask<User[],Void,Void> task = new AsyncTask<User[], Void, Void>() {
                    @Override
                    protected Void doInBackground(User[]... params) {
                        ApiHelper api = new ApiHelper();
                        api.setWinners(params[0], challenge_id);
                        return null;
                    }
                };
                task.execute(winners);
                Intent intent = new Intent();
                intent.putExtra("winners",winners);
                setResult(-1,intent);
                finish();
            }
        };
        taskTermination.execute(ch_id);
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
