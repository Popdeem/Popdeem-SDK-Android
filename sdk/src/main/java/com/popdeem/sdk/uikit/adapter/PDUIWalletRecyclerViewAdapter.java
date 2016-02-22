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
import android.widget.ImageView;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUIWalletRecyclerViewAdapter extends RecyclerView.Adapter<PDUIWalletRecyclerViewAdapter.ViewHolder> {

    private ArrayList<PDReward> mItems;

    public PDUIWalletRecyclerViewAdapter(ArrayList<PDReward> mItems) {
        this.mItems = mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PDReward reward = this.mItems.get(position);
        boolean isSweepstakes = reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE);

        Picasso.with(holder.context).load(isSweepstakes ? R.drawable.trophy : R.drawable.tag).into(holder.rewardTypeImageView);
        String imageUrl = reward.getCoverImage();
        if (imageUrl.contains("default")) {
            Picasso.with(holder.context)
                    .load(R.drawable.star)
                    .error(R.drawable.star)
                    .placeholder(R.drawable.star)
                    .into(holder.brandImageView);
        } else {
            Picasso.with(holder.context)
                    .load(imageUrl)
                    .error(R.drawable.star)
                    .placeholder(R.drawable.star)
                    .into(holder.brandImageView);
        }

        // Set reward type Drawable
        holder.titleTextView.setText(reward.getDescription());

        if (isSweepstakes) {
            holder.actionTextView.setText(R.string.pd_you_will_be_notified_label);
        } else {
            holder.actionTextView.setText(R.string.pd_redeem_at_stores_label);
        }

        try {
            long timeInMillis = Long.valueOf(reward.getAvailableUntilInSeconds());
            String expiryString = (isSweepstakes ? "Draw in " : "") + PDUIUtils.timeUntil(timeInMillis, false, isSweepstakes);
            holder.expiryTextView.setText(expiryString);
        } catch (NumberFormatException e) {
            holder.expiryTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ImageView brandImageView;
        ImageView rewardTypeImageView;
        TextView titleTextView;
        TextView expiryTextView;
        TextView actionTextView;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            brandImageView = (ImageView) itemView.findViewById(R.id.pd_wallet_brand_image_view);
            rewardTypeImageView = (ImageView) itemView.findViewById(R.id.pd_wallet_reward_type_image_view);
            titleTextView = (TextView) itemView.findViewById(R.id.pd_wallet_reward_title_text_view);
            expiryTextView = (TextView) itemView.findViewById(R.id.pd_wallet_reward_expiry_date_text_view);
            actionTextView = (TextView) itemView.findViewById(R.id.pd_wallet_reward_redeem_at_text_view);
        }
    }

}
