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

public class Fragment2 extends Fragment {

    public static String TAG = "fragment2_unused";

    public Fragment2(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //search();
        return inflater.inflate(R.layout.fragment2_unused,container,false);

    }
}
