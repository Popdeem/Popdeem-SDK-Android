package com.popdeem.sdk.uikit.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popdeem.sdk.R;

/**
 * Created by colm on 26/02/2018.
 */

public class PDAmbassadorView extends LinearLayout {

    public int setLevel = 0;
    int oldLevel = 0;

    float bronzePoint;
    float silverPoint;
    float goldPoint;

    FrameLayout bar;
    private boolean animated = true;

    public PDAmbassadorView(Context context) {
        super(context);
        init(context);
    }

    public PDAmbassadorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PDAmbassadorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PDAmbassadorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.widget_ambassador_view, this);


        bar = (FrameLayout)findViewById(R.id.fl_filler);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(bronzePoint<=0) {
            bronzePoint = (findViewById(R.id.tv_bronze).getWidth()/2) + findViewById(R.id.tv_bronze).getX();
            silverPoint = (findViewById(R.id.tv_silver).getWidth()/2) + findViewById(R.id.tv_silver).getX();
            goldPoint = (findViewById(R.id.tv_gold).getWidth()/2) + findViewById(R.id.tv_gold).getX();
            setLevel(-1,animated);
        }
    }

    /**
     * Set the progress level of the ambassador view
     *
     * @param amount amount input to set the view
     * @param animated animate the progress view from 0 to the set level
     */

    public void setLevel(int amount, boolean animated){

        int level = 0;

        if (amount < 30) {
            level = 0;
        }
        if (amount >= 30 && amount <= 60) {
            level = 1;
        }
        if (amount >= 60 && amount <= 90) {
            level = 2;
        }

        if (amount >= 90){
            level = 3;
        }

        if(amount>=0) {
            oldLevel = setLevel;
            setLevel = level;
        }
        this.animated = animated;
        float setWidth = 1;

//        bronzePoint = (findViewById(R.id.tv_bronze).getWidth()/2) + findViewById(R.id.tv_bronze).getX();
//        silverPoint = (findViewById(R.id.tv_silver).getWidth()/2) + findViewById(R.id.tv_silver).getX();
//        goldPoint = findViewById(R.id.fl_frame).getWidth();

        findViewById(R.id.tv_bronze).setAlpha(0.3f);
        findViewById(R.id.tv_silver).setAlpha(0.3f);
        findViewById(R.id.tv_gold).setAlpha(0.3f);
        if(goldPoint<=0){
            return;
        }
        switch(setLevel){
            case 0:
                setWidth = 1;
                break;
            case 1:
                setWidth = bronzePoint;
                findViewById(R.id.tv_bronze).setAlpha(1f);
                findViewById(R.id.tv_silver).setAlpha(0.3f);
                findViewById(R.id.tv_gold).setAlpha(0.3f);

                break;
            case 2:
                findViewById(R.id.tv_bronze).setAlpha(1f);
                findViewById(R.id.tv_silver).setAlpha(1f);
                findViewById(R.id.tv_gold).setAlpha(0.3f);
                setWidth = silverPoint;
                break;
            case 3:
                findViewById(R.id.tv_bronze).setAlpha(1f);
                findViewById(R.id.tv_silver).setAlpha(1f);
                findViewById(R.id.tv_gold).setAlpha(1f);
                setWidth = goldPoint;
                break;
        }

        if(animated){
            bar.requestLayout();
            BarAnimation barAnimation = new BarAnimation(bar, (int)setWidth, (int)(bar.getWidth()+bar.getX()));
            barAnimation.setStartOffset(1000);

            bar.startAnimation(barAnimation);
        }else{
            bar.getLayoutParams().width = (int)setWidth;
            bar.requestLayout();

        }
    }

    public class BarAnimation extends Animation {
        private final int startWidth;
        private final int targetWidth;
        private final View view;

        public BarAnimation(View view, int targetWidth, int startWidth) {
            this.view = view;
            this.targetWidth = targetWidth;
            this.startWidth = startWidth;
            this.setDuration(1000);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth;
            newWidth = (int) (startWidth + ((targetWidth - startWidth) * (interpolatedTime)));

            view.getLayoutParams().width = newWidth;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
