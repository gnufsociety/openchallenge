package com.gnufsociety.openchallenge;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sdc on 2/10/17.
 */

public class ApiHelper {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public OkHttpClient client;
    public String url = "http://139.59.131.72:3000/api/";

    public ApiHelper() {
        client = new OkHttpClient();
    }

    public String createChallenge(String org, String name, String desc, String rules, String image, String date, Place place) {
        JSONObject json;
        try {
            json = new JSONObject()
                    .put("organizer", org)
                    .put("name", name)
                    .put("description", desc)
                    .put("rules", rules)
                    .put("image", image)
                    .put("date", date)
                    .put("location", new JSONObject()
                            .put("address", place.getAddress())
                            .put("lat", place.getLatLng().latitude)
                            .put("long", place.getLatLng().longitude));


            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(url + "newChallenge")
                    .post(body)
                    .build();
            Response resp = client.newCall(request).execute();

            return resp.body().string();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "errore";
    }

    public ArrayList<Challenge> getHomeChallenge() {

        Request request = new Request.Builder()
                .url(url + "allChallenges")
                .build();
        try {
            Response resp = client.newCall(request).execute();

            return Challenge.getArrayList(resp.body().string());


        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ArrayList<>();
    }

    public void createUser(String username, String status, String imgLocation, String uid) {
        JSONObject json = null;
        try {
            json = new JSONObject()
                    .put("username", username)
                    .put("status", status)
                    .put("uid", uid)
                    .put("picture", imgLocation);


            RequestBody body = RequestBody.create(JSON, json.toString());

            Request req = new Request.Builder()
                    .url(url + "newUser")
                    .post(body)
                    .build();
            Response resp = client.newCall(req).execute();
            System.out.println(resp.body().string());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
