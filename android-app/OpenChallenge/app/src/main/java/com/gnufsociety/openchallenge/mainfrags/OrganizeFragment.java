package com.gnufsociety.openchallenge.mainfrags;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gnufsociety.openchallenge.ApiHelper;
import com.gnufsociety.openchallenge.InviteActivity;
import com.gnufsociety.openchallenge.MainActivity;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.model.Challenge;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sdc on 1/11/17.
 */

public class OrganizeFragment extends Fragment {
    public static int PLACE_AUTOCOMPLETE_INTENT = 1;
    public static int PICK_GALLERY_INTENT = 2;


    public static String TAG = "fragment3_organize";

    @BindView(R.id.organize_date_text)
    public TextView dateText;

    @BindView(R.id.organize_place_text)
    public TextView placeText;

    @BindView(R.id.organize_name_edit)
    public EditText nameEdit;

    @BindView(R.id.organize_desc_edit)
    public EditText descEdit;

    @BindView(R.id.organize_rules_edit)
    public EditText rulesEdit;

    @BindView(R.id.organize_image_view)
    public ImageView image;

    @BindView(R.id.organize_date_btn)
    public ImageButton dateBtn;

    @BindView(R.id.organize_find_place)
    public ImageButton placeBtn;

    @BindView(R.id.organize_create_btn)
    public Button createBtn;


    public Uri uriImage;
    public Place place;
    public MainActivity main;

    public OrganizeFragment() {
    }

    public void setMainActivity(MainActivity main) {
        this.main = main;
    }


    public void setToHomePage() {
        main.clickBottomBar(main.findViewById(R.id.home_bottom));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3_organize, container, false);

        ButterKnife.bind(this, view);

        return view;
    }


    @OnClick(R.id.organize_date_btn)
    public void showDateDialog() {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateText.setText(date);
                dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        };
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getContext(), mDateListener, year, month, day);
        dpd.show();
    }

    @OnClick(R.id.organize_create_btn)
    public void createChallenge() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");

        String name = nameEdit.getText().toString().toLowerCase().replace(" ", "_");
        Random rand = new Random(System.currentTimeMillis());
        name += (rand.nextInt(1000) + 1);

        final String nameC = nameEdit.getText().toString();
        final String descC = descEdit.getText().toString();
        final String ruleC = rulesEdit.getText().toString();
        final String locat = name;
        final String date = dateText.getText().toString();

        //Check if you put correct value
        if (nameC.equals("")) {
            Toast.makeText(getContext(), R.string.choose_name, Toast.LENGTH_LONG).show();
            return;
        }
        if (descC.equals("")) {
            Toast.makeText(getContext(), R.string.choose_description, Toast.LENGTH_LONG).show();
            return;
        }
        if (ruleC.equals("")) {
            Toast.makeText(getContext(), R.string.insert_rules, Toast.LENGTH_LONG).show();
            return;
        }
        if (date.equals(R.string.choose_date)) {
            Toast.makeText(getContext(), R.string.choose_date, Toast.LENGTH_LONG).show();
            return;
        }
        if (placeText.getText().toString().equals(R.string.choose_location)) {
            Toast.makeText(getContext(), R.string.choose_location, Toast.LENGTH_LONG).show();
            return;
        }
        if (uriImage == null){
            Toast.makeText(getContext(), R.string.choose_picture, Toast.LENGTH_LONG).show();
            return;
        }

        //disable button preventing two challenges
        createBtn.setEnabled(false);

        //get current image
        File toCompress = null;
        try {
            toCompress = FileUtil.from(getContext(),uriImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), R.string.uploading_challenge, Toast.LENGTH_LONG).show();

        //return compressed image PLAY WITH THIS
        final File compressedFile = new Compressor.Builder(getContext())
                .setMaxHeight(1024)
                .setMaxWidth(1024)
                .setQuality(90)
                .build().compressToFile(toCompress);

        /* = Compressor.getDefault(getContext()).compressToFile(toCompress);*/
        System.out.println(compressedFile.getTotalSpace());

        StorageReference challengesRef = storageRef.child("challenges/" + name);
        UploadTask uploadTask = challengesRef.putFile(Uri.fromFile(compressedFile));

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                    ApiHelper api = new ApiHelper();
                    return api.createChallenge(user.getUid(), nameC, descC, ruleC, locat, date, place);
            }
        };

        System.out.println(task.execute());

        uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //enable the button after the upload
                createBtn.setEnabled(true);
                //delete compressed image
                System.out.println("File deleted: "+compressedFile.delete());
                Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                setToHomePage();

            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handle on failure photo upload
                Toast.makeText(getContext(), "Failed Uploading", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void invite(Challenge challenge) {
        Intent intent = new Intent(this.getContext(), InviteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("challenge", challenge);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @OnClick(R.id.organize_find_place)
    public void findPlace() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            getActivity().startActivityForResult(intent, PLACE_AUTOCOMPLETE_INTENT);
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.organize_image_view)
    public void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select from gallery"), PICK_GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_INTENT) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(getContext(), data);
                String s = (String) place.getAddress();
                placeText.setText(s.split(",")[0]);
                placeText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            }
        } else if (requestCode == PICK_GALLERY_INTENT) {
            if (resultCode == RESULT_OK) {
                uriImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriImage);
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
