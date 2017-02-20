package com.gnufsociety.openchallenge.mainfrags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnufsociety.openchallenge.R;

/**
 * Created by sdc on 1/11/17.
 */

public class FavoriteFragment extends Fragment {

    public static String TAG = "fragment4_favorites";


    public FavoriteFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment4_favorites,container,false);
    }
}
