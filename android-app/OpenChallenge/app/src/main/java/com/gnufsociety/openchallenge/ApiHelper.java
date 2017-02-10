package com.gnufsociety.openchallenge;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    public String url = "http://10.0.2.2:3000/api/";

    public ApiHelper(){
        client = new OkHttpClient();
    }

    public String createChallenge(String org,String name, String desc, String rules, String image, String date, Place place){
        JSONObject json ;
        try {
            json = new JSONObject()
                    .put("organizer",org)
                    .put("name",name)
                    .put("description", desc)
                    .put("rules", rules)
                    .put("image", image)
                    .put("date",date)
                    .put("location", new JSONObject()
                            .put("address",place.getAddress())
                            .put("lat",place.getLatLng().latitude)
                            .put("long",place.getLatLng().longitude));


        RequestBody body = RequestBody.create(JSON,json.toString());
        Request request = new Request.Builder()
                .url(url +"newChallenge")
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

}
