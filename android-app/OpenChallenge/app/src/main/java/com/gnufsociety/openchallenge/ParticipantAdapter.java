package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sdc on 2/2/17.
 */

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.PartipantHolder> {

    public ArrayList<User> users;

    public ParticipantAdapter(ArrayList<User> users){
        this.users = users;
    }


    @Override
    public PartipantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_row,parent,false);
        return new PartipantHolder(view);
    }

    @Override
    public void onBindViewHolder(PartipantHolder holder, int position) {
        User u = users.get(position);

        // TODO: 2/10/17 CHANGE WHEN USER IS READY
        holder.civ.setImageResource(u.resPic);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        StorageReference userPic = sref.child("users/"+u.proPicLocation);

        Glide.with(holder.civ.getContext())
                .using(new FirebaseImageLoader())
                .load(userPic)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.civ);

        holder.user.setText(u.name);
        holder.rate.setRating((float) u.rating);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class PartipantHolder extends RecyclerView.ViewHolder{
        public CircleImageView civ;
        public TextView user;
        public RatingBar rate;

        public PartipantHolder(View itemView) {
            super(itemView);
            civ = (CircleImageView) itemView.findViewById(R.id.part_row_image);
            user = (TextView) itemView.findViewById(R.id.part_row_user);
            rate = (RatingBar) itemView.findViewById(R.id.part_row_rate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(civ.getContext(),UserActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("user",users.get(getAdapterPosition()));
                    intent.putExtras(extra);
                    civ.getContext().startActivity(intent);
                }
            });


        }

    }
}
