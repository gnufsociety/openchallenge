package com.gnufsociety.openchallenge.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.ChallengeActivity;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.model.Challenge;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by davidespallaccini on 23/02/17.
 */

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeHolder> {

    public ArrayList<Challenge> challenges;

    public ChallengeAdapter(ArrayList<Challenge> challenges) {
        this.challenges = challenges;
    }

    public void swapList(List<Challenge> list){
        challenges.clear();
        challenges.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ChallengeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.challenge_item, parent, false);

        return new ChallengeHolder(view);
    }

    @Override
    public void onBindViewHolder(ChallengeHolder holder, int position) {
        Challenge c = challenges.get(position);

        holder.challengeImg.setImageResource(c.resImage);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        StorageReference cImg = reference.child("challenges/"+c.imageLocation);

        Glide.with(holder.challengeImg.getContext())
                .using(new FirebaseImageLoader())
                .load(cImg)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.challengeImg);
        holder.challengeTitle.setText(c.name);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        holder.date.setText(format.format(c.when));
    }


    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public class ChallengeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_title) TextView challengeTitle;
        @BindView(R.id.item_when) TextView date;
        @BindView(R.id.item_img) CircleImageView challengeImg;


        public ChallengeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(challengeImg.getContext(),ChallengeActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("challenge",challenges.get(getAdapterPosition()));
                    intent.putExtras(extra);
                    challengeImg.getContext().startActivity(intent);
                }
            });
        }
    }
}
