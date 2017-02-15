package com.gnufsociety.openchallenge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Leonardo on 15/02/2017.
 */

public class Podium extends RelativeLayout {

    public CircleImageView first, second, third;

    public Podium(Context context, AttributeSet attrs) {
        super(context, attrs);
        View root = inflate(context,R.layout.podium_layout,this);
        first = (CircleImageView) root.findViewById(R.id.first_podium);
        second = (CircleImageView) root.findViewById(R.id.second_podium);
        third = (CircleImageView) root.findViewById(R.id.third_podium);
    }

    public void setWinners(User[] u){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        StorageReference firstRef = sref.child("users/" + u[0].proPicLocation);
        StorageReference secondRef = sref.child("users/" + u[1].proPicLocation);
        StorageReference thirdRef = sref.child("users/" + u[2].proPicLocation);

        Glide.with(first.getContext())
                .using(new FirebaseImageLoader())
                .load(firstRef)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(first);

        Glide.with(first.getContext())
                .using(new FirebaseImageLoader())
                .load(secondRef)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(second);

        Glide.with(first.getContext())
                .using(new FirebaseImageLoader())
                .load(thirdRef)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(third);

    }
}
