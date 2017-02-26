package com.gnufsociety.openchallenge.mainfrags;

import android.location.Location;
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

import com.gnufsociety.openchallenge.ApiHelper;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.adapters.CardAdapter;
import com.gnufsociety.openchallenge.model.Challenge;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sdc on 1/11/17.
 */

public class HomeFragment extends Fragment {

    public static String TAG = "fragment1_home";

    public CardAdapter adapter;
    public RecyclerView recyclerView;
    public SwipeRefreshLayout refreshLayout;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adapter = new CardAdapter();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_card_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadChall();
            }
        });

        downloadChall();


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void orderByPosition(Location currLocation){
        Collections.sort(adapter.list, new Challenge.DistanceChallangeComparator(currLocation.getLatitude(),currLocation.getLongitude()));
        adapter.notifyDataSetChanged();
    }

    public void downloadChall(){
        HomeAsync taskHome = new HomeAsync();
        taskHome.execute();
    }

    public class HomeAsync extends AsyncTask<Void, Void, ArrayList<Challenge>>{

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
            //Collections.sort(adapter.list, new Challenge.DistanceChallangeComparator(41.9149028,12.5599963));
            //LocationHelper help = new LocationHelper(getActivity());
            //help.buildGoogleApi();
            //adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);

        }
    }
}
