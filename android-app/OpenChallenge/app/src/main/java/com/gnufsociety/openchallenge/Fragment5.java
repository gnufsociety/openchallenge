package com.gnufsociety.openchallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sdc on 1/11/17.
 */

public class Fragment5 extends Fragment {

    public static String TAG = "fragment5";

    public Fragment5(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment5,container,false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_card_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new CardAdapter());

        return view;
    }
}
