/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Popdeem
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popdeem.sdk.uikit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;

/**
 * Created by mikenolan on 09/08/16.
 */
public class PDUIInstagramShareFragment extends Fragment implements View.OnClickListener {

    public interface PDInstagramShareCallback {
        void onShareClick();

        void onCancel();
    }

    public static PDUIInstagramShareFragment newInstance(@NonNull PDInstagramShareCallback callback) {
        Bundle args = new Bundle();

        PDUIInstagramShareFragment fragment = new PDUIInstagramShareFragment();
        fragment.setCallback(callback);
        fragment.setArguments(args);
        return fragment;
    }

    private View mView;
    private PDInstagramShareCallback mCallback;

    public PDUIInstagramShareFragment() {
    }

    public void setCallback(@NonNull PDInstagramShareCallback callback) {
        this.mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pd_instagram_share, container, false);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCancel();
            }
        });
        mView.findViewById(R.id.pd_instagram_share_next_button).setOnClickListener(this);
        mView.findViewById(R.id.pd_instagram_share_okay_button).setOnClickListener(this);
        PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_TUTORIAL_MODULE_ONE)
                .create());
        return mView;
    }

    @Override
    public void onClick(View v) {
        final int ID = v.getId();
        if (ID == R.id.pd_instagram_share_next_button) {
            PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                    .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_TUTORIAL_MODULE_TWO)
                    .create());

            final LinearLayout firstView = (LinearLayout) mView.findViewById(R.id.pd_instagram_share_first_view);
            final LinearLayout secondView = (LinearLayout) mView.findViewById(R.id.pd_instagram_share_second_view);

            firstView.setVisibility(View.INVISIBLE);
            secondView.setVisibility(View.VISIBLE);

//            ObjectAnimator fAnim = ObjectAnimator.ofFloat(firstView, "translationX", -1.0f);
//            ObjectAnimator sAnim = ObjectAnimator.ofFloat(secondView, "translationX", 2.0f, 1.0f);
//            AnimatorSet set = new AnimatorSet();
//            set.setDuration(1000);
//            set.setInterpolator(new LinearInterpolator());
//            set.playTogether(fAnim, sAnim);
//            set.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    secondView.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    firstView.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                }
//            });
//            set.start();
        } else if (ID == R.id.pd_instagram_share_okay_button) {
            PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CLICKED_NEXT_INSTAGRAM_TUTORIAL, null);
            mCallback.onShareClick();
        }
    }

    public static String getName() {
        return PDUIInstagramShareFragment.class.getSimpleName();
    }
}
