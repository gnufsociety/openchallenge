package com.gnufsociety.openchallenge;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sdc on 1/11/17.
 */

public class Fragment3 extends Fragment {
    public static int PLACE_AUTOCOMPLETE_INTENT = 1;
    public static int PICK_GALLERY_INTENT = 2;


    public static String TAG = "fragment3";

    @BindView(R.id.organize_date_text)  TextView dateText;
    @BindView(R.id.organize_place_text) TextView placeText;
    @BindView(R.id.organize_name_edit) EditText nameEdit;
    @BindView(R.id.organize_desc_edit) EditText descEdit;
    @BindView(R.id.organize_rules_edit) EditText rulesEdit;
    @BindView(R.id.organize_image_view) ImageView image;

    public Uri uriImage;
    public Place place;
    public MainActivity main;

    public Fragment3(){}

    public void setMainActivity(MainActivity main){
        this.main = main;
    }


    public void setToHomePage(){
        main.clickBottomBar(main.findViewById(R.id.home_bottom));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3,container,false);

        placeText = (TextView) view.findViewById(R.id.organize_place_text);
        dateText = (TextView) view.findViewById(R.id.organize_date_text);
        image = (ImageView) view.findViewById(R.id.organize_image_view);
        nameEdit = (EditText) view.findViewById(R.id.organize_name_edit);
        descEdit = (EditText) view.findViewById(R.id.organize_desc_edit);
        rulesEdit = (EditText) view.findViewById(R.id.organize_rules_edit);

        //image.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorAccent));

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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromGallerry();
            }
        });
        view.findViewById(R.id.organize_create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createChallenge();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void showDateDialog() {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth+"/"+(month+1)+"/"+year;
                dateText.setText(date);
                dateText.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
            }
        };
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getContext(),mDateListener,year,month,day);
        dpd.show();
    }

    public void createChallenge() throws JSONException, IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        String name = nameEdit.getText().toString().toLowerCase().replace(" ","_");
        Random rand = new Random(System.currentTimeMillis());
        name += (rand.nextInt(1000) +1);
        StorageReference challangesRef = storageRef.child("challenges/"+name);
        UploadTask uploadTask = challangesRef.putFile(uriImage);

        final String nameC = nameEdit.getText().toString();
        final String descC = descEdit.getText().toString();
        final String ruleC = rulesEdit.getText().toString();
        final String locat = name;
        final String date = dateText.getText().toString();
        AsyncTask<Void,Void,String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                ApiHelper api = new ApiHelper();
                return api.createChallenge(user.getUid(),nameC,descC,ruleC,locat,date,place);
            }
        };

        System.out.println(task.execute());

        uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(),"Upload successfull",Toast.LENGTH_LONG).show();
                setToHomePage();

            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Errore nel caricareeeeee");
            }
        });


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

    public void chooseFromGallerry(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select from gallery"),PICK_GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_INTENT){
            if (resultCode == RESULT_OK){
                place = PlaceAutocomplete.getPlace(getContext(),data);
                String s = (String) place.getAddress();
                placeText.setText(s.split(",")[0]);
                placeText.setTextColor(ContextCompat.getColor(getContext(),R.color.black));

                System.out.println(place.getAddress());
            }
        }
        else if (requestCode == PICK_GALLERY_INTENT){
            if (resultCode == RESULT_OK){
                uriImage = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uriImage);
                    image.clearColorFilter();
                    image.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    image.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                    image.requestLayout();
                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    image.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }


    }


}
