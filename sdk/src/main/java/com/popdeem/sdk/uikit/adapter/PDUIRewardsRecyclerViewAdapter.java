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
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.popdeem.sdk.core.model.PDReward.PD_REWARD_ACTION_CHECKIN;
import static com.popdeem.sdk.core.model.PDReward.PD_REWARD_ACTION_NONE;
import static com.popdeem.sdk.core.model.PDReward.PD_REWARD_ACTION_PHOTO;
import static com.popdeem.sdk.core.model.PDReward.PD_REWARD_ACTION_SOCIAL_LOGIN;
import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK;
import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM;
import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER;

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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_v2, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PDReward reward = this.mItems.get(position);

        holder.offerTextView.setText(reward.getDescription());
        setIcons(reward, holder);
        if (reward.getRules() == null || reward.getRules().isEmpty()) {
            holder.rulesTextView.setText("");
            holder.rulesTextView.setVisibility(View.GONE);
        } else {
            holder.rulesTextView.setText(reward.getRules());
            holder.rulesTextView.setVisibility(View.VISIBLE);
        }


        StringBuilder actionStringBuilder = new StringBuilder("");

        //TODO  📸 Photo Required camera image here (invisible in the editor but it is there.)
//        final boolean TWITTER_ACTION_REQUIRED = twitterActionRequired(reward.getSocialMediaTypes());
//        if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_PHOTO)) {
//            actionStringBuilder.append(holder.context.getString(TWITTER_ACTION_REQUIRED ? R.string.pd_claim_action_tweet_photo : R.string.pd_claim_action_photo_camera));
//        } else if (reward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_CHECKIN)) {
//            actionStringBuilder.append(holder.context.getString(TWITTER_ACTION_REQUIRED ? R.string.pd_claim_action_tweet_checkin : R.string.pd_claim_action_checkin));
//        } else {
//            actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
//        }
        if(reward.getSocialMediaTypes().size() > 0) {
            if (reward.getSocialMediaTypes().size() > 1) { // both networks
                if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_CHECKIN)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_tweet_checkin));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_PHOTO)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_photo_camera));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_SOCIAL_LOGIN)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_social_login));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_NONE)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
                } else {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
                }
            } else if (reward.getSocialMediaTypes().get(0).equalsIgnoreCase("Facebook")) {
                if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_CHECKIN)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_checkin));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_PHOTO)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_photo_camera));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_SOCIAL_LOGIN)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_social_login));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_NONE)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
                } else {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
                }
            } else if (reward.getSocialMediaTypes().get(0).equalsIgnoreCase("Twitter")) {
                if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_CHECKIN)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_tweet));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_PHOTO)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_photo_camera));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_SOCIAL_LOGIN)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_social_login));
                } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_NONE)) {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
                } else {
                    actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
                }
            } else if (reward.getSocialMediaTypes().get(0).equalsIgnoreCase("Instagram")) {
                actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_photo_camera));
            }
        }else{
            if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_CHECKIN)) {
                actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_tweet_checkin));
            } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_PHOTO)) {
                actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_photo_camera));
            } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_SOCIAL_LOGIN)) {
                actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_social_login));
            } else if (reward.getAction().equalsIgnoreCase(PD_REWARD_ACTION_NONE)) {
                actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
            } else {
                actionStringBuilder.append(holder.context.getString(R.string.pd_claim_action_none));
            }
        }

        String textTest = actionStringBuilder.toString();

        if(textTest.equalsIgnoreCase("Photo required")){
            textTest = "\uD83D\uDCF8 Photo Required";
        }

        holder.actionTextView.setText(textTest);
        if(!reward.isUnlimitedAvailability()) {
            if(shouldShowTime(reward.getAvailableUntilInSeconds())){
                String text = getTimeSting(reward.getAvailableUntilInSeconds());
                holder.timeTextView.setText(text);
                holder.timeTextView.setVisibility(View.VISIBLE);
            }else{
                holder.timeTextView.setVisibility(View.GONE);
            }
        }else{
            holder.timeTextView.setVisibility(View.GONE);
        }


//        if (reward.getDisableLocationVerification().equalsIgnoreCase(PDReward.PD_FALSE) && reward.getDistanceFromUser() > 0) {
//            actionStringBuilder.append(String.format(Locale.getDefault(), " | %1s", PDUIUtils.formatDistance(reward.getDistanceFromUser())));
//        }


        String imageUrl = reward.getCoverImage();
        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.contains("default")) {
            Glide.with(holder.context)
                    .load(R.drawable.pd_ui_star_icon)
                    .dontAnimate()
                    .error(R.drawable.pd_ui_star_icon)
                    .dontAnimate()
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(holder.imageView);
        } else {


            Glide.with(holder.context)
                    .load(imageUrl)
                    .dontAnimate()
                    .error(R.drawable.pd_ui_star_icon)
                    .dontAnimate()
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
        ImageView imageView;
        TextView offerTextView;
        TextView rulesTextView;
        TextView timeTextView;
        TextView actionTextView;
        ImageView[] imageViewsSocial = new ImageView[3];

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
            this.imageView = (ImageView) itemView.findViewById(R.id.pd_reward_star_image_view);
            this.offerTextView = (TextView) itemView.findViewById(R.id.pd_reward_offer_text_view);
            this.rulesTextView = (TextView) itemView.findViewById(R.id.pd_reward_item_rules_text_view);
            this.timeTextView = (TextView) itemView.findViewById(R.id.pd_reward_item_time_text_view);
            this.actionTextView = (TextView) itemView.findViewById(R.id.pd_reward_request_text_view);


            this.imageViewsSocial[0] = (ImageView) itemView.findViewById(R.id.pd_reward_item_social_icon_view_1);
            this.imageViewsSocial[1] = (ImageView) itemView.findViewById(R.id.pd_reward_item_social_icon_view_2);
            this.imageViewsSocial[2] = (ImageView) itemView.findViewById(R.id.pd_reward_item_social_icon_view_3);
        }
    }

    // *********************************************************************************************************************************
    // *********************************************** Bind ViewHolder Functions *******************************************************
    // *********************************************************************************************************************************

    private void setIcons(PDReward reward, ViewHolder holder){
        holder.imageViewsSocial[0].setVisibility(View.GONE);
        holder.imageViewsSocial[1].setVisibility(View.GONE);
        holder.imageViewsSocial[2].setVisibility(View.GONE);

        for (int i = 0; i < reward.getSocialMediaTypes().size(); i++) {
            if (reward.getSocialMediaTypes().get(i).equalsIgnoreCase(PD_SOCIAL_MEDIA_TYPE_FACEBOOK)){ //Facebook
                holder.imageViewsSocial[i].setImageResource(R.drawable.pduirewardfacebookicon);
                holder.imageViewsSocial[i].setVisibility(View.VISIBLE);
            }else if (reward.getSocialMediaTypes().get(i).equalsIgnoreCase(PD_SOCIAL_MEDIA_TYPE_INSTAGRAM)){ //Instagram
                holder.imageViewsSocial[i].setImageResource(R.drawable.pduirewardinstagramicon);
                holder.imageViewsSocial[i].setVisibility(View.VISIBLE);
            }else if (reward.getSocialMediaTypes().get(i).equalsIgnoreCase(PD_SOCIAL_MEDIA_TYPE_TWITTER)){ //Twitter
                holder.imageViewsSocial[i].setImageResource(R.drawable.pduirewardtwittericon);
                holder.imageViewsSocial[i].setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean shouldShowTime(String time){
        long interval = PDNumberUtils.toLong(time, -1) - (Calendar.getInstance().getTimeInMillis()/1000);
        long intervalDays = ((interval/60)/60)/24;

        if(intervalDays > 6){
            return false;
        }
        return true;
    }

    public String getTimeSting(String time){
        String ret = "︎";
        long interval = PDNumberUtils.toLong(time, -1) - (Calendar.getInstance().getTimeInMillis()/1000);
//        String convertedTimeString = PDUIUtils.convertTimeToDayAndMonth(timeInSecs);
//        if (!convertedTimeString.isEmpty()) {
//            ret = ret + (String.format(Locale.getDefault(), " Exp %1s", convertedTimeString));
//        }

        long intervalHours = (interval/60)/60;
        long intervalDays = ((interval/60)/60)/24;
        long intervalWeeks = (((interval/60)/60)/24)/7;
        long intervalMonths = (((interval/60)/60)/24)/28;

        if(intervalDays > 6){
            return ret;
        }


        // Note: watch icon is invisible in editor
        if (intervalMonths > 0) {
            if (intervalMonths > 1) {
                ret = ret + "⌚︎ " + intervalMonths + " months left to claim";
            } else {
                ret = ret + "⌚︎ " + intervalMonths + " month left to claim";
            }
        } else if (intervalDays > 6) {
            ret = ret + "⌚︎ " + intervalWeeks + " weeks left to claim";
        } else if (intervalDays < 7 && intervalHours > 23) {
            if(intervalDays == 1) {
                ret = ret + "⌚︎ " + intervalDays + " day left to claim";
            }else{
                ret = ret + "⌚︎ " + intervalDays + " days left to claim";
            }
        } else {
            if(intervalHours == 1) {
                ret = ret + "⌚︎ " + intervalHours + " hour left to claim";
            }else{
                ret = ret + "⌚︎ " + intervalHours + " hours left to claim";
            }
        }


        return ret;
    }

}
