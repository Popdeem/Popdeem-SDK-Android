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

package com.popdeem.sdk.uikit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDReward;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.popdeem.sdk.core.model.PDReward.PD_REWARD_RECURRENCE_MONTHLY;
import static java.lang.Character.toUpperCase;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUIWalletRecyclerViewAdapter extends RecyclerView.Adapter<PDUIWalletRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v);

        void onVerifyClick(int position);
    }

    private final int VIEW_TYPE_WALLET_ITEM = 0;
    private final int VIEW_TYPE_FOOTER = 1;

    private OnItemClickListener mListener;
    private ArrayList<PDReward> mItems;
    private String mAddedTextString = null;
    private int mImageDimen = -1;

    public PDUIWalletRecyclerViewAdapter(ArrayList<PDReward> mItems) {
        this.mItems = mItems;
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_WALLET_ITEM;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mAddedTextString == null) {
            mAddedTextString = parent.getContext().getString(R.string.pd_wallet_credit_reward_text);
        }
        if (mImageDimen == -1) {
            mImageDimen = (int) parent.getContext().getResources().getDimension(R.dimen.wallet_image_dimen);
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false), parent.getContext(), viewType == VIEW_TYPE_WALLET_ITEM);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            holder.brandImageView.setVisibility(View.INVISIBLE);
            holder.titleTextView.setVisibility(View.INVISIBLE);
            holder.verifyContainer.setVisibility(View.INVISIBLE);
            return;
        }

        holder.brandImageView.setVisibility(View.VISIBLE);
        holder.titleTextView.setVisibility(View.VISIBLE);

        final PDReward reward = this.mItems.get(position);

        String imageUrl = reward.getCoverImage();
        if (imageUrl.contains("default")) {
            Picasso.with(holder.context)
                    .load(R.drawable.pd_ui_star_icon)
                    .into(holder.brandImageView);
        } else {
            Picasso.with(holder.context)
                    .load(imageUrl)
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .resize(mImageDimen, 0)
                    .into(holder.brandImageView);
        }

        holder.subTitleTextView.setText(R.string.pd_wallet_redeem_text);
        holder.titleTextView.setText(reward.getDescription());

        if (reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
            String drawString = drawString(reward);
            holder.subTitleTextView.setText(drawString);
        } else if (reward.getCredit() != null && reward.getCredit().length() > 0) {
            DateFormat formatter = new SimpleDateFormat("d MMM y");
            Date claimed = new Date(reward.getClaimedAt());
            String claimedString = formatter.format(claimed);

            holder.subTitleTextView.setText(String.format(
                    Locale.getDefault(),
                    "%1s %2s %3s",
                    reward.getCredit() == null ? "Credit" : reward.getCredit(),
                    mAddedTextString,
                    claimedString));
        }

        holder.verifyContainer.setVisibility(View.INVISIBLE);
        if (reward.claimedUsingNetwork(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM)) {
            if (!reward.isInstagramVerified()) {
                holder.verifyContainer.setVisibility(View.VISIBLE);
                holder.verifyProgress.setVisibility(reward.isVerifying() ? View.VISIBLE : View.INVISIBLE);
                holder.verifyButton.setVisibility(reward.isVerifying() ? View.INVISIBLE : View.VISIBLE);
                holder.subTitleTextView.setText(reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE) ? R.string.pd_wallet_sweepstake_must_be_verified_text : R.string.pd_wallet_reward_must_be_verified_text);
            }
        }
    }

    private String drawString(PDReward reward) {
        //If it is a recurring reward, show the day/month
        //Otherwise break time left into a readable format
        if (reward.getRecurrence() != null) {
            String recur = reward.getRecurrence();
            if (recur.equals(PD_REWARD_RECURRENCE_MONTHLY)) {
                return "Draw takes place monthly.";
            } else {
                String cap = toUpperCase(recur.charAt(0)) + recur.substring(1) + ".";
                return String.format(
                        Locale.getDefault(),
                        "%1s %2s",
                        "Draw takes place on",
                        cap);
            }
        } else if (reward.getAvailableUntilInSeconds() != null) {
            long timeNow = new java.util.Date().getTime();
            long rewardAvailale = Long.valueOf(reward.getAvailableUntilInSeconds()).longValue();
            long timeAvailable = new Date(rewardAvailale).getTime();
            long dateIntervalInSecs = timeAvailable - timeNow/1000; // timeNow is in millis, need seconds

            long intervalHours =  dateIntervalInSecs/60/60;
            long intervalDays = dateIntervalInSecs/60/60/24;

            String expiresString = "";
            if (intervalDays > 1) {
                expiresString = String.format(
                                Locale.getDefault(),
                                "%1s %2s %3s",
                                "Draw takes place in",
                                intervalDays,
                                "days.");
            }
            if (intervalDays == 1) {
                expiresString = "Draw takes place in 1 day.";
            }
            if (intervalDays == 0) {

                if (intervalHours == 0) {
                    expiresString = "Draw has happened. You will be notified if you are the winner.";
                } else {
                    expiresString = String.format(
                            Locale.getDefault(),
                            "%1s %2s %3s",
                            "Draw takes place in",
                            intervalHours,
                            "hours.");
                }
            }
            return expiresString;
        }
        return "You will be notified if you are the winner.";
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (this.mItems != null && this.mItems.size() > 0) {
            count = this.mItems.size() + 1;
        }
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ImageView brandImageView;
        TextView titleTextView;
        TextView subTitleTextView;
        FrameLayout verifyContainer;
        Button verifyButton;
        ProgressBar verifyProgress;

        ViewHolder(View itemView, Context context, boolean clickable) {
            super(itemView);
            if (clickable) {
                itemView.setClickable(true);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClick(v);
                        }
                    }
                });
            }

            this.context = context;
            this.brandImageView = (ImageView) itemView.findViewById(R.id.pd_wallet_brand_image_view);
            this.titleTextView = (TextView) itemView.findViewById(R.id.pd_wallet_reward_title_text_view);
            this.subTitleTextView = (TextView) itemView.findViewById(R.id.pd_wallet_reward_sub_title_text_view);
            this.verifyContainer = (FrameLayout) itemView.findViewById(R.id.pd_wallet_verify_container);
            this.verifyButton = (Button) itemView.findViewById(R.id.pd_wallet_verify_button);
            this.verifyProgress = (ProgressBar) itemView.findViewById(R.id.pd_wallet_verify_progress_bar);
            this.verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
//                        verifyProgress.setVisibility(View.VISIBLE);
//                        verifyButton.setVisibility(View.INVISIBLE);
                        mListener.onVerifyClick(getLayoutPosition());
                    }
                }
            });
        }
    }

}
