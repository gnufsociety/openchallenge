package com.gnufsociety.openchallenge;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sdc on 1/11/17.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    List<Challenge> list;

    public CardAdapter(){
        list = Challenge.getSampleList();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Challenge c = list.get(position);

        holder.title.setText(c.name);
        holder.org.setText("Organizer: "+c.organizer.name);
        holder.type.setText("Type: "+c.type);
        holder.when.setText("When: "+c.when);
        holder.where.setText("Where: "+c.where);
        holder.rate.setRating((float) c.organizer.rating);
        holder.img.setImageResource(c.resImage);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public FavoriteButton button;
        public TextView title, org, type, when, where;
        public RatingBar rate;
        public ImageView img;
        //Gesture detector for double tap listener
        private GestureDetector gd ;
        public CardViewHolder(View view) {

            super(view);
            button = (FavoriteButton) view.findViewById(R.id.card_favorite);

            title = (TextView) view.findViewById(R.id.card_title);
            org = (TextView) view.findViewById(R.id.card_organizer);
            type = (TextView) view.findViewById(R.id.card_type);
            when = (TextView) view.findViewById(R.id.card_when);
            where = (TextView) view.findViewById(R.id.card_where);

            rate = (RatingBar) view.findViewById(R.id.card_rate);

            img = (ImageView) view.findViewById(R.id.card_img);

            //listener
            GestureDetector.SimpleOnGestureListener lis = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    System.out.println("clicked");
                    button.likeIt();
                    return true;
                }
            };
            gd = new GestureDetector(button.getContext(),lis);

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
                }
            });

        }
    }
}
