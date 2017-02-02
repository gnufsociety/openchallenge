package com.gnufsociety.openchallenge;

import android.icu.text.MessagePattern;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

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
        holder.civ.setImageResource(u.resPic);
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


        }

    }
}
