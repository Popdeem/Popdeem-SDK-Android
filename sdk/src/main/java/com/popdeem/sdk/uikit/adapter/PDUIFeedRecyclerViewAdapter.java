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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDFeed;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.uikit.fragment.PDUIFeedFragment;
import com.popdeem.sdk.uikit.widget.PDSquareImageView;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;

import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK;
import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM;
import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIFeedRecyclerViewAdapter extends RecyclerView.Adapter<PDUIFeedRecyclerViewAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    private final PDUIFeedFragment context;
    private OnItemClickListener mListener;
    private ArrayList<PDFeed> mItems;
    private int mSharedImageDimen = -1;
    private int mProfileImageDimen = -1;
    private String mCurrentUserName = "";

    public PDUIFeedRecyclerViewAdapter(ArrayList<PDFeed> mItems, PDUIFeedFragment context) {
        this.mItems = mItems;
        this.context = context;

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
        if (mSharedImageDimen == -1) {
            mSharedImageDimen = (int) parent.getContext().getResources().getDimension(R.dimen.pd_feed_shared_image_dimen);
        }
        if (mProfileImageDimen == -1) {
            mProfileImageDimen = (int) parent.getContext().getResources().getDimension(R.dimen.pd_feed_profile_image_dimen);
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_v2, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PDFeed item = this.mItems.get(position);

        boolean isCheckin = item.getImageUrlString().contains("default");
        if (isCheckin) {
            holder.sharedImageView.setVisibility(View.GONE);
            holder.sharedImageView.setImageDrawable(null);
        } else {


            holder.sharedImageView.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(item.getImageUrlString())
                    .error(null)
                    .dontAnimate()
                    .into(holder.sharedImageView);

        }

        if (item.getUserProfilePicUrlString().isEmpty()) {

            Glide.with(context)
                    .load(R.drawable.pduikit_default_user)
                    .dontAnimate()
                    .dontAnimate()
                    .fitCenter()
                    .into(holder.profileImageView);
        } else {
            Glide.with(context)
                    .load(item.getUserProfilePicUrlString())
                    .placeholder(R.drawable.pduikit_default_user)
                    .error(R.drawable.pduikit_default_user)
                    .dontAnimate()
                    .fitCenter()
                    .into(holder.profileImageView);
        }

//        Spannable actionText = getRedemptionText(holder.context, item.getUserFirstName(), item.getUserLastName(), item.getDescriptionString(), item.getBrandName(), isCheckin);
        holder.userNameTextView.setText(getNameForItem(item.getUserFirstName(), item.getUserLastName()));
        SpannableStringBuilder str = new SpannableStringBuilder(getNameForItem(item.getUserFirstName(), item.getUserLastName()) + " " + item.getCaption());
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, getNameForItem(item.getUserFirstName(), item.getUserLastName()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.userCommentTextView.setText(str);

        holder.timeTextView.setText(item.getTimeAgo());
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    private String getNameForItem(String firstName, String secondName) {
        String fullName = String.format(Locale.getDefault(), "%1s %2s", firstName, secondName);
        if (fullName.equalsIgnoreCase(mCurrentUserName)) {
            return "You";
        } else {
            return fullName;
        }
    }

    @Deprecated
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
        PDSquareImageView sharedImageView;
        TextView userNameTextView;
        TextView userCommentTextView;
        TextView timeTextView;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
//            itemView.setClickable(true);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mListener != null) {
//                        mListener.onItemClick(v);
//                    }
//                }
//            });

            this.context = context;
            this.profileImageView = (PDUIBezelImageView) itemView.findViewById(R.id.pd_feed_profile_image_view);
            this.sharedImageView = (PDSquareImageView) itemView.findViewById(R.id.pd_feed_shared_image_view);
            this.userNameTextView = (TextView) itemView.findViewById(R.id.pd_feed_user_name_text_view);
            this.userCommentTextView = (TextView) itemView.findViewById(R.id.pd_feed_user_comment_text_view);
            this.timeTextView = (TextView) itemView.findViewById(R.id.pd_feed_time_text_view);
        }

    }

    // *********************************************************************************************************************************
    // *********************************************** Bind ViewHolder Functions *******************************************************
    // *********************************************************************************************************************************

    public String getTimeSting(String time){
        String ret = "⌚︎"; // Note: watch icon is invisible in editor
        long interval = PDNumberUtils.toLong(time, -1) - (Calendar.getInstance().getTimeInMillis()/1000);
//        String convertedTimeString = PDUIUtils.convertTimeToDayAndMonth(timeInSecs);
//        if (!convertedTimeString.isEmpty()) {
//            ret = ret + (String.format(Locale.getDefault(), " Exp %1s", convertedTimeString));
//        }

        long intervalHours = (interval/60)/60;
        long intervalDays = ((interval/60)/60)/24;
        long intervalWeeks = (((interval/60)/60)/24)/7;
        long intervalMonths = (((interval/60)/60)/24)/28;

        if (intervalMonths > 0) {
            if (intervalMonths > 1) {
                ret = ret + " " + intervalMonths + " months left to claim";
            } else {
                ret = ret + " " + intervalMonths + " month left to claim";
            }
        } else if (intervalDays > 6) {
            ret = ret + " " + intervalWeeks + " weeks left to claim";
        } else if (intervalDays < 7 && intervalHours > 23) {
            ret = ret + " " + intervalDays + " days left to claim";
        } else {
            ret = ret + " " + intervalHours + " left to claim";
        }


        return ret;
    }

}
