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

package com.popdeem.sdk.uikit.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.uikit.utils.PDUICountDownTimer;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by mikenolan on 25/02/16.
 */
public class PDUIRedeemActivity extends PDBaseActivity {

    private PDUICountDownTimer mCountDownTimer;
    private boolean timerFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_redeem);
        setTitle(R.string.pd_redeem_string);

        findViewById(R.id.pd_redeem_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfRedeemed();
            }
        });


        String imageUrl = getIntent().getStringExtra("imageUrl");
        ImageView imageView = (ImageView) findViewById(R.id.pd_redeem_brand_image_view);
        if (imageUrl == null || imageUrl.contains("default") || imageUrl.isEmpty()) {
            Picasso.with(this)
                    .load(R.drawable.pd_ui_star_icon)
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(imageView);
        } else {
            Picasso.with(this)
                    .load(imageUrl)
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(imageView);
        }

        String reward = getIntent().getStringExtra("reward");
        TextView textView = (TextView) findViewById(R.id.pd_redeem_reward_title_text_view);
        textView.setText(reward == null ? "" : reward);

        String rules = getIntent().getStringExtra("rules");
        textView = (TextView) findViewById(R.id.pd_redeem_reward_rules_text_view);
        textView.setText(rules == null ? "" : rules);

        boolean isSweepstakes = getIntent().getBooleanExtra("isSweepstakes", false);

        final TextView countdownTextView = (TextView) findViewById(R.id.pd_redeem_countdown_timer_text_view);
        if (isSweepstakes) {
            timerFinished = true;
            TextView instructionsTextView = (TextView) findViewById(R.id.pd_redeem_instructions_text_view);
            instructionsTextView.setText(R.string.pd_draw_takes_place_in_string);

            long timeInSeconds = PDNumberUtils.toLong(getIntent().getStringExtra("time"), 0);
            countdownTextView.setText(PDUIUtils.timeUntil(timeInSeconds, false, true));
        } else {
            final long REDEMPTION_TIMER = 1000 * 60 * 10 + 500;
            final long COUNTDOWN_INTERVAL_IN_MILLIS = 1000;

            countdownTextView.setText(PDUIUtils.millisecondsToTimer(REDEMPTION_TIMER));
            mCountDownTimer = new PDUICountDownTimer(REDEMPTION_TIMER, COUNTDOWN_INTERVAL_IN_MILLIS) {
                @Override
                public void onTick(long millisUntilFinished) {
                    countdownTextView.setText(PDUIUtils.millisecondsToTimer(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    timerFinished = true;
                    countdownTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    countdownTextView.setText(R.string.pd_timer_finished_string);
                }
            };
            mCountDownTimer.start();
        }
    }

    private void checkIfRedeemed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.pd_have_you_redeemed_your_reward_string)
                .setMessage(R.string.pd_redeem_reward_first_warning_string)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!timerFinished && mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                        }
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            checkIfRedeemed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!timerFinished) {
            checkIfRedeemed();
        } else {
            super.onBackPressed();
        }
    }
}
