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
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIRewardsRecyclerViewAdapter extends RecyclerView.Adapter<PDUIRewardsRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    private OnItemClickListener mListener;
    private ArrayList<PDReward> mItems;

    public PDUIRewardsRecyclerViewAdapter(ArrayList<PDReward> mItems) {
        this.mItems = mItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PDReward reward = this.mItems.get(position);

        holder.offerTextView.setText(reward.getDescription());

        final boolean TWITTER_ACTION_REQUIRED = twitterActionRequired(reward.getSocialMediaTypes());
        if (reward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
            holder.actionTextView.setText(String.format(Locale.getDefault(), "%1s Required", TWITTER_ACTION_REQUIRED ? "Tweet with Photo" : "Photo"));
        } else if (reward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_CHECKIN)) {
            holder.actionTextView.setText(String.format(Locale.getDefault(), "%1s Required", TWITTER_ACTION_REQUIRED ? "Tweet" : "Check-in"));
        } else {
            holder.actionTextView.setText(R.string.pd_instant_coupon_label);
        }

        try {
            long timeInMillis = Long.valueOf(reward.getAvailableUntilInSeconds());
            holder.remainingTextView.setText(PDUIUtils.timeUntil(timeInMillis, true, false));
        } catch (NumberFormatException e) {
            holder.remainingTextView.setText("");
//            Crashlytics.getInstance().core.setString("remaining_reward_time_in_millis", reward.getId() + "_" + reward.getAvailableUntilInSeconds());
//            Crashlytics.getInstance().core.logException(e);
        }

        String imageUrl = reward.getCoverImage();
        if (imageUrl.contains("default")) {
            Picasso.with(holder.context)
                    .load(R.drawable.pd_ui_star_icon)
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(holder.imageView);
        } else {
            Picasso.with(holder.context)
                    .load(imageUrl)
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    private boolean twitterActionRequired(String[] socialMediaTypes) {
        return socialMediaTypes != null && socialMediaTypes.length == 1 && socialMediaTypes[0].equalsIgnoreCase(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        PDUIBezelImageView imageView;
        TextView offerTextView;
        TextView remainingTextView;
        TextView actionTextView;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(v);
                    }
                }
            });
            this.context = context;
            this.imageView = (PDUIBezelImageView) itemView.findViewById(R.id.pd_reward_star_imgae_view);
            this.offerTextView = (TextView) itemView.findViewById(R.id.pd_reward_offer_text_view);
            this.remainingTextView = (TextView) itemView.findViewById(R.id.pd_reward_time_remaining_text_view);
            this.actionTextView = (TextView) itemView.findViewById(R.id.pd_reward_request_text_view);
        }
    }

}
