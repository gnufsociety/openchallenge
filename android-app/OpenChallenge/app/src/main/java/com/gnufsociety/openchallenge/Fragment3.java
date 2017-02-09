package com.gnufsociety.openchallenge;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sdc on 1/11/17.
 */

public class Fragment3 extends Fragment {
    public static int PLACE_AUTOCOMPLETE_INTENT = 1;


    public static String TAG = "fragment3";

    public TextView dateText, placeText;

    public Fragment3(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3,container,false);

        placeText = (TextView) view.findViewById(R.id.organize_place_text);
        dateText = (TextView) view.findViewById(R.id.organize_date_text);
        view.findViewById(R.id.organize_find_place).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace();
            }
        });
        view.findViewById(R.id.organize_date_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();

            }
        });

        return view;
    }

    private void showDateDialog() {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth+"/"+month+"/"+year;
                dateText.setText(date);
            }
        };
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getContext(),mDateListener,year,month,day);
        dpd.show();
    }

    public void findPlace(){
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent,PLACE_AUTOCOMPLETE_INTENT);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_INTENT){
            if (resultCode == RESULT_OK){
                Place place = PlaceAutocomplete.getPlace(getContext(),data);
                placeText.setText(place.getAddress());
                System.out.println(place.getAddress());
            }
        }
    }


}
