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

    public ArrayList<User> getParticipant(String id_chall) {
        Request request = new Request.Builder()
                .url(url + "getParticipants/" + id_chall)
                .build();
        try {
            Response resp = client.newCall(request).execute();
            return User.getUsersArray(resp.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public void addParticipant(String id_chall, String uid) {
        Request request = new Request.Builder()
                .url(url + "/addParticipant/" + id_chall + "/" + uid)
                .build();
        try {
            Response resp = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeParticipant(String id_chall, String uid) {
        Request request = new Request.Builder()
                .url(url + "/removeParticipant/" + id_chall + "/" + uid)
                .build();
        try {
            Response resp = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject numParticipant(String chall_id, String uid) {
        Request request = new Request.Builder()
                .url(url + "getNumParticipants/" + chall_id + "/" + uid)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    public ArrayList<User> searchUsers(String prefix) {
        Request request = new Request.Builder()
                .url(url + "findUsers/" + prefix)
                .build();
        try {
            Response resp = client.newCall(request).execute();
            return User.getUsersArray(resp.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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

    public User getCurrentUser(String uid) {
        Request req = new Request.Builder()
                .url(url + "findUserByUid/" + uid)
                .build();
        try {
            Response resp = client.newCall(req).execute();
            return new User(new JSONObject(resp.body().string()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setWinners(User[] users) {
        try {
            JSONObject json = new JSONObject()
                    .put("first", users[0].id)
                    .put("second", users[1].id)
                    .put("third", users[2].id);
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request req = new Request.Builder()
                    .url(url+"setWinners")
                    .post(body)
                    .build();
            Response resp = client.newCall(req).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
