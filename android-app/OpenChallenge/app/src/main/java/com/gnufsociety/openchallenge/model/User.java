package com.gnufsociety.openchallenge.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Leonardo on 14/01/2017.
 */
public class User implements Serializable{
    public String status;
    public String email;
    public String name;
    public String id;
    public String uid;
    public double rating;
    public int resPic;
    public String proPicLocation;
    public int silverMedals;
    public int goldMedals;
    public int bronzeMedals;


    public User(String name, double rating, int resPic) {
        this.name = name;
        this.rating = rating;
        this.resPic = resPic;
    }

    public User(JSONObject obj, int simple){
        try {
            this.name = obj.getString("username");
            this.proPicLocation = obj.getString("picture");
            this.rating = obj.getDouble("rate");
            this.uid = obj.getString("uid");
            this.id = obj.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public User(JSONObject obj){
        try {

            //Change this parameters with the one provided by the magic David1

            this.name = obj.getString("username");
            this.rating = obj.getDouble("rate");
            this.proPicLocation = obj.getString("picture");
            this.status = obj.getString("status");
            this.goldMedals = obj.getInt("gold");
            this.silverMedals = obj.getInt("silver");
            this.bronzeMedals = obj.getInt("bronze");
            this.uid = obj.getString("uid");
            this.id = obj.getString("_id");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<User> getUsersArray(String json) throws JSONException {
        ArrayList<User> users = new ArrayList<>();

        JSONArray arr = new JSONArray(json);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            users.add(new User(obj));
        }

        return users;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        return uid.equals(user.uid);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + uid.hashCode();
        return result;
    }
}
