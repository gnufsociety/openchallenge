package com.gnufsociety.openchallenge;

import com.google.android.gms.location.places.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonardo on 14/01/2017.
 */

public class Challenge implements Serializable {
    public String name;
    public int resImage;
    public User organizer;
    public String desc;
    public String when;
    public Place place;
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
        this.when = obj.getString("date");
        JSONObject place = obj.getJSONObject("location");
        this.address = place.getString("address");
        this.lat = place.getDouble("lat");
        this.lng = place.getDouble("long");
        this.organizer = new User("marco",4,R.drawable.io1);



    }

    public Challenge(String name, int resImage, User organizer, String type, String when, String where) {
        this.name = name;
        this.resImage = resImage;
        this.organizer = organizer;
        this.desc = type;
        this.when = when;
        this.address = where;
    }

    public void likeIt() {
        liked = !liked;
    }

    public static List<Challenge> getSampleList() {
        ArrayList<Challenge> arr = new ArrayList<>();
        User u1 = new User("sdc", 3.4, R.drawable.io1);
        User u2 = new User("spallas", 3.9, R.drawable.david1);
        User u3 = new User("tiem", 5, R.drawable.cina1);
        User u4 = new User("k", 4.3, R.drawable.panici1);

        String d1 = "Distruggi lo stack con il nostro caro amico Demetrescu. Chi riesce a farlo con costo O(1) vince la vita!";
        String d2 = "Da medellin colombia, vieni a provare anche tu la nostra neve! chi ne pippa di più rifornimento omaggio a vita!";
        String d3 = "Pensi di essere gigante? partecipa alla pull ups challenge e vediamo quante fighe tiri su";
        String d4 = "Sei un tossico/ubriaco? Dimostracelo stuccando 3 pinte di birra senza fare pipì!!";
        String d5 = "Impieghi meno di 2 secondi per scrivere youporn.com? Mettiti alla prova con questa nuova sfida! pipparolo sfigato";
        String d6 = "Quando sei in chimica cucini meglio di cracco? Vediamo che sai fare, ci vediamo alle cucine di MAsterchef chi sarà il nuovo campione?";
        String d7 = "Sei forte a giocare a scacchi? Mettiti alla prova con una busta in faccia che sei un po' chess..";
        String d8 = "Porcodio e porcamadonna sono parole che usi spesso? Prova a battere i vecchi di paese a briscola, chi più ne ha più ne dica!";
        String d9 = "Sei un pozzo senza fondo perchè sei un ciccione di merda? stucca sto panizzo da 2kg e ti portiamo subito in ospedale!!!!";


        Challenge c1 = new Challenge("Smash the stack", R.drawable.hacking, u1, d1, "15 jan 2017", "under the bridge");
        Challenge c2 = new Challenge("Snowball fight", R.drawable.snow, u2, d2, "23 jan 2017", "Via bufus 117");
        Challenge c3 = new Challenge("Pull ups", R.drawable.pullups, u3, d3, "17 feb 2017", "Via le mani dal naso 12");
        Challenge c4 = new Challenge("All you can drink", R.drawable.pint, u4, d4, "1 mar 2017", "Piazza la bomba 8");
        Challenge c5 = new Challenge("Fast typing", R.drawable.typing, u1, d5, "25 dec 2017", "Via dotto 23 ");
        Challenge c6 = new Challenge("MasterChef", R.drawable.cooking, u4, d6, "20 may 2017", "Cracco ce fa na p..");
        Challenge c7 = new Challenge("Chess", R.drawable.chess, u3, d7, "13 mar 2017", "Via pyrex 777");
        Challenge c8 = new Challenge("Briscola", R.drawable.briscola, u4, d8, "12 may 2017", "Bar del paese");
        Challenge c9 = new Challenge("Panizzo 2KG", R.drawable.panizzeri, u4, d9, "31 jan 2017", "Via L. Bragaglia 71");


        arr.add(c1);
        arr.add(c2);
        arr.add(c3);
        arr.add(c9);
        arr.add(c4);
        arr.add(c5);
        arr.add(c6);
        arr.add(c7);
        arr.add(c8);

        return arr;
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
