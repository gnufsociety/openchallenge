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
        User u5 = new User("bar tony",2);

        Challenge c1 = new Challenge("Smash the stack", R.drawable.hacking,u1,"Coding", "15 jan 2017", "under the bridge");
        Challenge c2 = new Challenge("Snowball fight", R.drawable.coke,u2,"Blowing", "23 jan 2017", "sul piatto caldo");
        Challenge c3 = new Challenge("Pull ups", R.drawable.pullups,u3,"Physical", "17 feb 2017", "Via le mani dal naso 12");
        Challenge c4 = new Challenge("Drinking contest", R.drawable.pint,u4,"Eat&Drink", "1 mar 2017", "piazza la bomba 8");
        Challenge c5 = new Challenge("Smash the stack", R.drawable.hacking,u1,"Coding", "15 jan 2017", "under the bridge");
        Challenge c6 = new Challenge("Smash the stack", R.drawable.hacking,u5,"Coding", "15 jan 2017", "under the bridge");
        Challenge c7 = new Challenge("Smash the stack", R.drawable.hacking,u3,"Coding", "15 jan 2017", "under the bridge");
        Challenge c8 = new Challenge("Smash the stack", R.drawable.hacking,u4,"Coding", "15 jan 2017", "under the bridge");
        Challenge c9 = new Challenge("Smash the stack", R.drawable.hacking,u3,"Coding", "15 jan 2017", "under the bridge");
        Challenge c10 = new Challenge("Smash the stack", R.drawable.hacking,u2,"Coding", "15 jan 2017", "under the bridge");


        arr.add(c1);
        arr.add(c2);
        arr.add(c3);
        arr.add(c4);
        arr.add(c5);
        arr.add(c6);
        arr.add(c7);
        arr.add(c8);
        arr.add(c9);
        arr.add(c10);

        return arr;
    }
}
