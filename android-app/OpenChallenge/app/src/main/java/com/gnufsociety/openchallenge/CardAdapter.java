package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sdc on 1/11/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    List<Challenge> list;
    public FirebaseStorage fstorage;
    public StorageReference storage;

    public CardAdapter(List<Challenge> list) {
        this.list = list;
        fstorage = FirebaseStorage.getInstance();
        storage = fstorage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Challenge c = list.get(position);

        StorageReference img = storage.child("challenges/"+c.imageLocation);
        StorageReference userImg = storage.child("users/"+c.organizer.proPicLocation);


        if (c.liked)
            holder.button.colorLike();
        else
            holder.button.decolorLike();

        holder.desc.setText(c.desc);
        holder.title.setText(c.name);
        holder.org.setText(c.organizer.name);
        holder.when.setText(c.when);
        holder.where.setText(c.address.split(",")[0]);
        holder.rate.setRating((float) c.organizer.rating);

        Glide.with(holder.desc.getContext())
                .using(new FirebaseImageLoader())
                .load(img)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.img);

        Glide.with(holder.desc.getContext())
                .using(new FirebaseImageLoader())
                .load(userImg)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.user_img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_title) TextView title;
        @BindView(R.id.card_when)  TextView when;
        @BindView(R.id.card_where) TextView where;
        @BindView(R.id.card_descr) TextView desc;
        @BindView(R.id.card_rate)  RatingBar rate;
        @BindView(R.id.card_img)   ImageView img;
        @BindView(R.id.card_organizer) TextView org;
        @BindView(R.id.card_favorite)  FavoriteButton button;
        @BindView(R.id.card_user_img)  CircleImageView user_img;
        //Gesture detector for double tap listener
        private GestureDetector gd;

        @OnClick(R.id.card_user_img)
        public void openUserActivity() {
            Intent user = new Intent(user_img.getContext(), UserActivity.class);
            Bundle extra = new Bundle();
            extra.putSerializable("user", list.get(getAdapterPosition()).organizer);
            user.putExtras(extra);
            user_img.getContext().startActivity(user);
        }

        @OnClick(R.id.card_favorite)
        public void like() {
            button.likeIt();
            list.get(getAdapterPosition()).likeIt();
        }

        public CardViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);

            //listener
            final GestureDetector.SimpleOnGestureListener lis = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    System.out.println("clicked");
                    button.likeIt();
                    list.get(getAdapterPosition()).likeIt();
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("challenge", list.get(getAdapterPosition()));
                    Intent i = new Intent(button.getContext(), ChallengeActivity.class);
                    i.putExtras(bundle);
                    button.getContext().startActivity(i);

                    return true;
                }
            };

            gd = new GestureDetector(button.getContext(), lis);

            //set listener on touch event
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gd.onTouchEvent(event);
                    return true;

                }
            });
        }
    }
}
