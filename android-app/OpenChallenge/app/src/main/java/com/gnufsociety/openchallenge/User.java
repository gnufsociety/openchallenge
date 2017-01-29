package com.gnufsociety.openchallenge;

/**
 * Created by Leonardo on 14/01/2017.
 */
public class User {
    public String email;
    public String name;
    public double rating;
    public int resPic;

    public User(String name, double rating, int resPic) {
        this.name = name;
        this.rating = rating;
        this.resPic = resPic;
    }


}
