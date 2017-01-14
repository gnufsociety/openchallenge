package com.gnufsociety.openchallenge;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by sdc on 1/11/17.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {


    public CardAdapter(){

    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public FavoriteButton button;
        //Gesture detector for double tap listener
        private GestureDetector gd ;
        public CardViewHolder(View view) {

            super(view);
            button = (FavoriteButton) view.findViewById(R.id.card_favorite);

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
