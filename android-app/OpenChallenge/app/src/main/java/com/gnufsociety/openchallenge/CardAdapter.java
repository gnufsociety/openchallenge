package com.gnufsociety.openchallenge;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sdc on 1/11/17.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    List<Challenge> list;

    public CardAdapter() {
        list = Challenge.getSampleList();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Challenge c = list.get(position);

        if (c.liked)
            holder.button.colorLike();
        else
            holder.button.decolorLike();

        holder.desc.setText(c.desc);
        holder.title.setText(c.name);
        holder.org.setText(c.organizer.name);
        holder.when.setText(c.when);
        holder.where.setText(c.where);
        holder.rate.setRating((float) c.organizer.rating);
        holder.img.setImageResource(c.resImage);
        holder.user_img.setImageResource(c.organizer.resPic);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public FavoriteButton button;
        public TextView title, org, when, where, desc;
        public RatingBar rate;
        public ImageView img;

        public CircleImageView user_img;
        //Gesture detector for double tap listener
        private GestureDetector gd;

        public CardViewHolder(final View view) {

            super(view);
            button = (FavoriteButton) view.findViewById(R.id.card_favorite);

            title = (TextView) view.findViewById(R.id.card_title);
            org = (TextView) view.findViewById(R.id.card_organizer);
            when = (TextView) view.findViewById(R.id.card_when);
            where = (TextView) view.findViewById(R.id.card_where);
            desc = (TextView) view.findViewById(R.id.card_descr);

            rate = (RatingBar) view.findViewById(R.id.card_rate);

            img = (ImageView) view.findViewById(R.id.card_img);
            user_img = (CircleImageView) view.findViewById(R.id.card_user_img);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.getContext().startActivity(new Intent(view.getContext(),ChallengeActivity.class));
                }
            });

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
                public boolean onSingleTapUp(MotionEvent e) {
                    button.getContext().startActivity(new Intent(button.getContext(),ChallengeActivity.class));

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
            //listener favorite button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.likeIt();
                    list.get(getAdapterPosition()).likeIt();

                }
            });

        }
    }
}
