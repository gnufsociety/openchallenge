package com.gnufsociety.openchallenge;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnufsociety.openchallenge.adapters.ParticipantAdapter;
import com.gnufsociety.openchallenge.model.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public ArrayList<User> users;
    public RecyclerView userList;
    public ParticipantAdapter adapter;
    public Context context;
    public SearchFragment() {
        // Required empty public constructor
        users = new ArrayList<>();

    }
    public void setContext(Context c){
        this.context = c;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        users = new ArrayList<>();
        userList = (RecyclerView) view.findViewById(R.id.search_list);
        adapter = new ParticipantAdapter(users);
        userList.setLayoutManager(new LinearLayoutManager(getContext()));
        userList.setAdapter(adapter);

        return view;
    }

}
