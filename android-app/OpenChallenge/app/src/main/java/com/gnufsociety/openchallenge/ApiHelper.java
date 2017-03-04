package com.gnufsociety.openchallenge;

import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;
import com.google.android.gms.location.places.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.gnufsociety.openchallenge.R.drawable.user;

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

    public String createChallenge(String org, String name, String desc, String rules,
                                  String image, String date, Place place) {
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
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "error";
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
            JSONArray arr = new JSONArray(response.body().string());
            return arr.getJSONObject(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * API:
     * router.get('/terminateChallenge/:challenge_id', function (req, res) {
     *      Challenge.findByIdAndUpdate(req.params.challenge_id,
     *      {isTerminated: true},
     */
    public void terminate(String challenge_id) {
        Request request = new Request.Builder()
                .url(url + "terminateChallenge/" + challenge_id)
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String createUser(String username, String status, String imgLocation, String uid) {
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
            return resp.body().string();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error404";


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


    public boolean isPresent(String uid) {
        Request req = new Request.Builder()
                .url(url + "findUserByUid/" + uid)
                .build();
        try {
            Response resp = client.newCall(req).execute();
            if(resp.body().string().length()>15) return true;
            else return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    public void setWinners(User[] users, String challenge_id) {
        try {
            JSONObject json = new JSONObject()
                    .put("first", users[0].id)
                    .put("second", users[1].id)
                    .put("third", users[2].id);
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request req = new Request.Builder()
                    .url(url+"setWinners/"+ challenge_id)
                    .post(body)
                    .build();
            Response resp = client.newCall(req).execute();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<User> getWinners(Challenge challenge) {
        ArrayList<User> winners = new ArrayList<>();
        Request req = new Request.Builder()
                .url(url+"winners/" + challenge.id)
                .build();

        Response res = null;
        try {
            res = client.newCall(req).execute();
            winners = User.getUsersArray(res.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return winners;
    }


    public ArrayList<Challenge> getOrganizedChallenges(User user) {
        ArrayList<Challenge> organized = new ArrayList<>();
        try {
            Request req = new Request.Builder()
                    .url(url+"organizedChallenges/"+user.id)
                    .build();

            Response res = client.newCall(req).execute();
            organized = Challenge.getArrayList(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return organized;
    }


    public ArrayList<Challenge> getJoinedChallenges(User user) {
        ArrayList<Challenge> joined = new ArrayList<>();
        try {
            Request req = new Request.Builder()
                    .url(url+"joinedChallenges/"+user.id)
                    .build();

            Response res = client.newCall(req).execute();
            joined = Challenge.getArrayList(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return joined;
    }

    public void setStatus(User user, String new_status) {
        try {
            RequestBody body = RequestBody.create(JSON,
                    "{ \"new_status\" : \"" + new_status + "\"}");
            Request req = new Request.Builder()
                    .url(url+"setStatus/"+user.id)
                    .post(body)
                    .build();

            Response res = client.newCall(req).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void follow(String user, String followed){
        try{
            Request req = new Request.Builder()
                    .url(url+"follow/"+user+"/"+followed)
                    .build();

            Response res = client.newCall(req).execute();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<User> getFollowed(String user_uid) {
        ArrayList<User> followed = new ArrayList<>();
        Request req = new Request.Builder()
                .url(url+"following/" + user_uid)
                .build();

        Response res = null;
        try {
            res = client.newCall(req).execute();
            followed = User.getUsersArray(res.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return followed;
    }
}
