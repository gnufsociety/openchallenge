package com.gnufsociety.openchallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by sdc on 1/11/17.
 */

public class Fragment2 extends Fragment {

    public static String TAG = "fragment2";
    public FirebaseDatabase database;

    public Fragment2(){

    }

    public void search(){
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("users");
        Query searchUser = ref.orderByChild("name").startAt("sc");
        searchUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println(dataSnapshot);
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    System.out.println("username "+ds.child("name").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //search();
        return inflater.inflate(R.layout.fragment2,container,false);

    }
}
