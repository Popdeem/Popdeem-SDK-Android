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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUIWalletRecyclerViewAdapter extends RecyclerView.Adapter<PDUIWalletRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v);
    }

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mAddedTextString == null) {
            mAddedTextString = parent.getContext().getString(R.string.pd_credit_added_text);
        }
        if (mImageDimen == -1) {
            mImageDimen = (int) parent.getContext().getResources().getDimension(R.dimen.wallet_image_dimen);
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

        if (reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_CREDIT)) {
            holder.titleTextView.setText(String.format(
                    Locale.getDefault(),
                    "%1s %2s",
                    reward.getCredit() == null ? "Credit" : reward.getCredit(),
                    mAddedTextString));
        } else {
            holder.titleTextView.setText(reward.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return this.mItems == null ? 0 : this.mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ImageView brandImageView;
        TextView titleTextView;

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
            this.brandImageView = (ImageView) itemView.findViewById(R.id.pd_wallet_brand_image_view);
            this.titleTextView = (TextView) itemView.findViewById(R.id.pd_wallet_reward_title_text_view);
        }
    }

}
