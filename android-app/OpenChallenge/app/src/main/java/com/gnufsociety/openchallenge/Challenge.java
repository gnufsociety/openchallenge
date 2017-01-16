package com.gnufsociety.openchallenge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonardo on 14/01/2017.
 */

public class Challenge {
    public String name;
    public int resImage;
    public User organizer;
    public String type;
    public String when;
    public String where;
    public boolean liked = false;

    public Challenge(String name, int resImage, User organizer, String type, String when, String where) {
        this.name = name;
        this.resImage = resImage;
        this.organizer = organizer;
        this.type = type;
        this.when = when;
        this.where = where;
    }
    public void likeIt(){
        liked = !liked;
    }
    public static List<Challenge> getSampleList(){
        ArrayList<Challenge> arr = new ArrayList<>();
        User u1 = new User("sdc",3.4);
        User u2 = new User("spallas",3.9);
        User u3 = new User("tiem",5);
        User u4 = new User("k",4.3);
        User u5 = new User("Bastianich",2);

        Challenge c1 = new Challenge("Smash the stack", R.drawable.hacking,u1,"Coding", "15 jan 2017", "under the bridge");
        Challenge c2 = new Challenge("Snowball fight", R.drawable.snow,u2,"Blowing", "23 jan 2017", "Via bufus 117");
        Challenge c3 = new Challenge("Pull ups", R.drawable.pullups,u3,"Physical", "17 feb 2017", "Via le mani dal naso 12");
        Challenge c4 = new Challenge("All you can drink", R.drawable.pint,u4,"Eat&Drink", "1 mar 2017", "Piazza la bomba 8");
        Challenge c5 = new Challenge("Fast typing", R.drawable.typing,u1,"Coding", "25 dec 2017", "Via dotto 23 ");
        Challenge c6 = new Challenge("MasterChef", R.drawable.cooking,u5,"Cooking", "20 may 2017", "Cracco ce fa na p..");
        Challenge c7 = new Challenge("Chess", R.drawable.chess,u3,"Game", "7 feb 2017", "Via pyrex 777");
        Challenge c8 = new Challenge("Briscola", R.drawable.briscola,u4,"Card", "12 may 2017", "Bar del paese");
        Challenge c9 = new Challenge("Panizzo 2KG", R.drawable.panizzeri,u4,"Eat&Food", "31 jan 2017", "Via L. Bragaglia 71");


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
}
