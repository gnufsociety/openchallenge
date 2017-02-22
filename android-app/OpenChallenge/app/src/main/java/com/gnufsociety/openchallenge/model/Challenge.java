package com.gnufsociety.openchallenge.model;

import com.gnufsociety.openchallenge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Leonardo on 14/01/2017.
 */

public class Challenge implements Serializable {
    public String name;
    public int resImage;
    public User organizer;
    public String desc;
    public Date when;
    public String id;
    //public List<User> simplePart;
    public String imageLocation;
    public String rules;
    public String address;
    public double lat;
    public double lng;
    public boolean liked = false;

    public Challenge(JSONObject obj) throws JSONException {
        this.name = obj.getString("name");
        this.desc = obj.getString("description");
        this.rules = obj.getString("rules");
        this.imageLocation = obj.getString("image");


        String date = obj.getString("date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        try {
            when = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject place = obj.getJSONObject("location");
        this.address = place.getString("address");
        this.lat = place.getDouble("lat");
        this.lng = place.getDouble("long");
        this.id = obj.getString("_id");

        // UNCOMMENT WHEN IT'S READY
        JSONObject userObj = obj.getJSONObject("organizer");
        this.organizer = new User(userObj,0);

        /*JSONArray part = obj.getJSONArray("participants");
        simplePart = new ArrayList<>();

        for (int i = 0; i < part.length(); i++) {
            simplePart.add(new User(part.getJSONObject(i),1));
        }*/




    }

    public Challenge(String name, int resImage, User organizer,
                     String type, String when, String where) {
        this.name = name;
        this.resImage = resImage;
        this.organizer = organizer;
        this.desc = type;
        //this.when = when;
        this.address = where;
    }

    public void likeIt() {
        liked = !liked;
    }


    public static ArrayList<Challenge> getArrayList(String json) {

        ArrayList<Challenge> arr = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                arr.add(new Challenge(obj));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr;
    }
}
