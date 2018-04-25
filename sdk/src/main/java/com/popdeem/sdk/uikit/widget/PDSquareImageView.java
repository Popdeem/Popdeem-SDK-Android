package com.popdeem.sdk.uikit.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by colm on 15/03/2018.
 */

public class PDSquareImageView extends AppCompatImageView {

    public PDSquareImageView(Context context) {
        super(context);
    }

    public PDSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PDSquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

}