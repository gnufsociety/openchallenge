package com.gnufsociety.openchallenge;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gnufsociety.openchallenge.adapters.InviteAdapter;
import com.gnufsociety.openchallenge.adapters.ParticipantAdapter;
import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteActivity extends AppCompatActivity {

    @BindView(R.id.invite_recycler) public RecyclerView mRecyclerView;
    @BindView(R.id.invite_toolbar) public Toolbar mToolbar;

    public InviteAdapter mAdapter;
    public Challenge mChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Bundle bundle = getIntent().getExtras();
        mChallenge = (Challenge) bundle.getSerializable("challenge");

        checkConnection();

        ButterKnife.bind(this);

        AsyncTask<Void, Void, ArrayList<User>> getFollowed =
                new AsyncTask<Void, Void, ArrayList<User>>() {
            @Override
            protected ArrayList<User> doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                // The organizer can invite users from his list of followed users.
                // If some user has been already invited it will not show up in the
                // invite list.
                // TODO: replace with two lists one for people already invited...
                ArrayList<User> followed = api.getFollowed(mChallenge.organizer.uid);
                ArrayList<User> alreadyInvited = api.getInvitedList(mChallenge);
                followed.removeAll(alreadyInvited);
                return followed;
            }

            @Override
            protected void onPostExecute(ArrayList<User> users) {
                mAdapter = new InviteAdapter(users, mChallenge);
                mRecyclerView.setAdapter(mAdapter);
            }
        };
        getFollowed.execute();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addItemDecoration(new DividerItemDecoration
                (mRecyclerView.getContext(),
                 DividerItemDecoration.VERTICAL));

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Invite your friends");
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

    private void checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            System.out.println(">>>>>>>>>>>>>>>> NOT CONNECTED <<<<<<<<<<<<<<<<<<");
            Intent intent = new Intent(this, NoConnectionActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }
}
