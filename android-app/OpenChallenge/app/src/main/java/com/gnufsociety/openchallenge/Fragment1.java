package com.gnufsociety.openchallenge;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnufsociety.openchallenge.adapters.CardAdapter;
import com.gnufsociety.openchallenge.model.Challenge;

import java.util.ArrayList;

/**
 * Created by sdc on 1/11/17.
 */

public class Fragment1 extends Fragment {

    public static String TAG = "fragment1";

    public CardAdapter adapter;
    public RecyclerView recyclerView;
    public SwipeRefreshLayout refreshLayout;

    public Fragment1(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adapter = new CardAdapter();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_card_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        final AsyncTask<Void,Void,ArrayList<Challenge>> task = new AsyncTask<Void, Void, ArrayList<Challenge>>() {
            @Override
            protected ArrayList<Challenge> doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                return api.getHomeChallenge();
            }

            @Override
            protected void onPostExecute(ArrayList<Challenge> challenges) {
                super.onPostExecute(challenges);
                adapter = new CardAdapter(challenges);
                recyclerView.setAdapter(adapter);
                //refreshLayout.setRefreshing(false);

            }
        };

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTask<Void,Void,ArrayList<Challenge>> tas = new AsyncTask<Void, Void, ArrayList<Challenge>>() {
                    @Override
                    protected ArrayList<Challenge> doInBackground(Void... params) {
                        ApiHelper api = new ApiHelper();
                        return api.getHomeChallenge();
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Challenge> challenges) {
                        super.onPostExecute(challenges);
                        adapter = new CardAdapter(challenges);
                        recyclerView.setAdapter(adapter);
                        refreshLayout.setRefreshing(false);

                    }
                };
                tas.execute();
            }
        });

        task.execute();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }
}
