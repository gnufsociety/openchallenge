package com.gnufsociety.openchallenge;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public ArrayList<CharSequence> users;
    public ListView userList;
    public ArrayAdapter<CharSequence> adapter;
    public Context context;
    public SearchFragment() {
        // Required empty public constructor
        users = new ArrayList<>();

    }
    public void setContext(Context c){
        this.context = c;
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,users);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        userList = (ListView) view.findViewById(R.id.search_list);
        userList.setAdapter(adapter);

        return view;
    }

}
