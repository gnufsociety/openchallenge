package com.gnufsociety.openchallenge;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;

/**
 * Created by Leonardo on 14/01/2017.
 */

/**
 * New view that extends ImageButton, with like animation and image changing
 * **/
public class FavoriteButton extends ImageButton {
    public boolean liked = false;

    public FavoriteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //if button is liked set to false and change
    public void likeIt(){
        likeAnimation();
    }

    private void likeAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this.getContext(),R.anim.anim_scale);
        //bounce effect
        anim.setInterpolator(new BounceInterpolator());

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //change favorite image when animation start
                setLike();

            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(anim);
    }



    private void setLike(){
        if (liked){
            setImageResource(R.drawable.favorite_border_16);
            setColorFilter(ContextCompat.getColor(getContext(),R.color.white));
            liked = false;
        }
        else {
            setImageResource(R.drawable.favorite_16);
            setColorFilter(Color.RED);
            liked = true;
        }
    }
    public void colorLike(){

            setImageResource(R.drawable.favorite_16);
            setColorFilter(Color.RED);
    }
    public void decolorLike(){
            setImageResource(R.drawable.favorite_border_16);
            setColorFilter(ContextCompat.getColor(getContext(),R.color.white));
    }
}
