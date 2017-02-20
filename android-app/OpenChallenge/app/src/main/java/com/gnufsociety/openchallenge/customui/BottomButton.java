package com.gnufsociety.openchallenge.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gnufsociety.openchallenge.R;

/**
 * Created by Leonardo on 15/01/2017.
 */

public class BottomButton extends LinearLayout {
    public boolean current = false;
    int clickedColor, nonclickedColor;
    public ImageView imgBtn;
    public TextView title;


    public BottomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        clickedColor = ContextCompat.getColor(context, R.color.colorAccent);
        nonclickedColor = ContextCompat.getColor(context,R.color.white);

        View root = inflate(context, R.layout.bottom_button, this);
        title = (TextView) root.findViewById(R.id.bottom_title);
        imgBtn = (ImageView) root.findViewById(R.id.bottom_image);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.BottomButton, 0, 0);

        int img = arr.getResourceId(R.styleable.BottomButton_imageButton, R.drawable.add_box_24);
        String tit = arr.getString(R.styleable.BottomButton_title);
        imgBtn.setImageResource(img);
        imgBtn.setColorFilter(nonclickedColor);
        title.setText(tit);
        arr.recycle();
    }

    public void clickIt() {
        if (current){
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            title.setTextColor(nonclickedColor);
            imgBtn.setColorFilter(nonclickedColor);
            current = false;
        }
        else{
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            title.setTextColor(clickedColor);
            imgBtn.setColorFilter(clickedColor);
            current = true;
        }
    }




}
