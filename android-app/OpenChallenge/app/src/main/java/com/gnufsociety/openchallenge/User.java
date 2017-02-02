package com.gnufsociety.openchallenge;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Leonardo on 14/01/2017.
 */
public class User implements Serializable{
    public String email;
    public String name;
    public double rating;
    public int resPic;

    public User(String name, double rating, int resPic) {
        this.name = name;
        this.rating = rating;
        this.resPic = resPic;
    }

    public static ArrayList<User> getList(){
        ArrayList<User> users = new ArrayList<>();
        User u1 = new User("sdc",3.4,R.drawable.io1);
        User u2 = new User("spallas",3.9,R.drawable.david1);
        User u3 = new User("tiem",5,R.drawable.cina1);
        User u4 = new User("k",4.3,R.drawable.panici1);
        User u5 = new User("MaikMaglia",1,R.drawable.coke);
        User u6 = new User("zonta",3,R.drawable.hacking);
        users.add(u1);
        users.add(u2);
        users.add(u3);
        users.add(u4);
        users.add(u5);
        users.add(u6);
        users.add(u3);
        users.add(u2);
        users.add(u1);
        users.add(u5);
        users.add(u6);
        users.add(u4);

        return users;
    }


}
