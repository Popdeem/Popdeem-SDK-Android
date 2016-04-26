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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDFeed;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIFeedRecyclerViewAdapter extends RecyclerView.Adapter<PDUIFeedRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    private OnItemClickListener mListener;
    private ArrayList<PDFeed> mItems;
    private int imageDimen = -1;
    private String mCurrentUserName = "";

    public PDUIFeedRecyclerViewAdapter(ArrayList<PDFeed> mItems) {
        this.mItems = mItems;

        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        if (userDetails != null) {
            mCurrentUserName = String.format(Locale.getDefault(), "%1s %2s", userDetails.getFirstName(), userDetails.getLastName());
            PDLog.d(getClass(), "name: " + mCurrentUserName);
        }
        realm.close();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (imageDimen == -1) {
            imageDimen = (int) parent.getContext().getResources().getDimension(R.dimen.pd_feed_shared_image_dimen);
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PDFeed item = this.mItems.get(position);

        boolean isCheckin = item.getImageUrlString().contains("default");
        if (isCheckin) {
            holder.sharedImageView.setVisibility(View.GONE);
            holder.sharedImageView.setImageDrawable(null);
        } else {
            holder.sharedImageView.setVisibility(View.VISIBLE);
            Picasso.with(holder.context)
                    .load(item.getImageUrlString())
                    .resize(imageDimen, imageDimen)
                    .centerCrop()
                    .into(holder.sharedImageView);
        }

        if (item.getUserProfilePicUrlString().isEmpty()) {

        } else {
            Picasso.with(holder.context)
                    .load(item.getUserProfilePicUrlString())
                    .resize(imageDimen, imageDimen)
                    .centerCrop()
                    .into(holder.profileImageView);
        }

        Spannable actionText = getRedemptionText(holder.context, item.getUserFirstName(), item.getUserLastName(), item.getDescriptionString(), item.getBrandName(), isCheckin);
        holder.actionTextView.setText(actionText);
        holder.timeTextView.setText(item.getTimeAgoString());
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    private Spannable getRedemptionText(Context context, String firstName, String secondName, String reward, String brandName, boolean isCheckin) {
        String fullName = firstName + " " + secondName;
        String redemptionName;
        if (fullName.equalsIgnoreCase(mCurrentUserName)) {
            redemptionName = "You";
        } else {
            redemptionName = fullName;
        }

        String redemptionAction;
        if (isCheckin) {
            redemptionAction = "checked in and redeemed";
        } else {
            redemptionAction = "shared an image and redeemed";
        }

        Spannable spannable = new SpannableString(redemptionName + " " + redemptionAction + " " + reward + " at " + brandName);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.pd_feed_item_name_text_color)), 0, redemptionName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.pd_feed_item_title_text_color)), (redemptionName + " " + redemptionAction).length(), (redemptionName + " " + redemptionAction + " ").length() + reward.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.pd_feed_item_title_text_color)), (redemptionName + " " + redemptionAction + " " + reward + " at").length(), (redemptionName + " " + redemptionAction + " " + reward + " at ").length() + brandName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        PDUIBezelImageView profileImageView;
        ImageView sharedImageView;
        TextView actionTextView;
        TextView timeTextView;

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
            this.profileImageView = (PDUIBezelImageView) itemView.findViewById(R.id.pd_feed_profile_image_view);
            this.sharedImageView = (ImageView) itemView.findViewById(R.id.pd_feed_shared_image_view);
            this.actionTextView = (TextView) itemView.findViewById(R.id.pd_feed_action_text_view);
            this.timeTextView = (TextView) itemView.findViewById(R.id.pd_feed_time_ago_text_view);
        }

    }

}
