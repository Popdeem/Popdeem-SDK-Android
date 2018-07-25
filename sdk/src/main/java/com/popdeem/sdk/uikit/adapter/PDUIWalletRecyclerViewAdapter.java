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
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.popdeem.sdk.BuildConfig;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDEvent;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.realm.PDRealmCustomer;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.uikit.activity.PDUIInboxActivity;
import com.popdeem.sdk.uikit.activity.PDUISettingsActivity;
import com.popdeem.sdk.uikit.widget.PDAmbassadorView;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

import static com.popdeem.sdk.core.model.PDReward.PD_REWARD_RECURRENCE_MONTHLY;
import static java.lang.Character.toUpperCase;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUIWalletRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private int messagesCount = 0;
    private PDRealmCustomer mCustomer;

    public interface OnItemClickListener {
        void onItemClick(View v);

        void onVerifyClick(int position);
    }

    private final int VIEW_TYPE_WALLET_ITEM = 0;
    private final int VIEW_TYPE_FOOTER = 1;
    private final int VIEW_TYPE_HEADER = 2;
    private final int VIEW_TYPE_EVENT = 3;
    private Realm mRealm;
    private PDRealmUserDetails mUser;

    private OnItemClickListener mListener;
    private ArrayList<Object> mItems;
    private String mAddedTextString = null;

    private int mImageDimen = -1;


    public PDUIWalletRecyclerViewAdapter(ArrayList<Object> mItems) {
        this.mItems = mItems;
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public void setMessagesCount(int messagesCount){
        this.messagesCount = messagesCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
//        else if (position == getItemCount()) {
//            return VIEW_TYPE_FOOTER;
//        }
        if(mItems.get(position-1) instanceof PDReward)
            return VIEW_TYPE_WALLET_ITEM;
        else
            return VIEW_TYPE_EVENT;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (mAddedTextString == null) {
            mAddedTextString = parent.getContext().getString(R.string.pd_wallet_credit_reward_text);
        }
        if (mImageDimen == -1) {
            mImageDimen = (int) parent.getContext().getResources().getDimension(R.dimen.wallet_image_dimen);
        }
        if(viewType == VIEW_TYPE_WALLET_ITEM) {
            viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false), parent.getContext(), true);
        }else if(viewType == VIEW_TYPE_EVENT){
            viewHolder = new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false), parent.getContext(), false);
        }else{
            viewHolder = new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_cell, parent, false), parent.getContext(), false);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        if (position == getItemCount() - 1) {
//            holder.brandImageView.setVisibility(View.INVISIBLE);
//            holder.titleTextView.setVisibility(View.INVISIBLE);
//            holder.verifyContainer.setVisibility(View.INVISIBLE);
//            return;
//        }
        if(position == 0){
            HeaderViewHolder holder = (HeaderViewHolder)viewHolder;

            setupHeader(holder);
            return;
        }
        if(this.mItems.get(position - 1) instanceof PDReward) {
            ViewHolder holder = (ViewHolder)viewHolder;

            holder.brandImageView.setVisibility(View.VISIBLE);
            holder.titleTextView.setVisibility(View.VISIBLE);
            final PDReward reward = (PDReward) this.mItems.get(position - 1);

            String imageUrl = reward.getCoverImage();
            Log.i("IMAGEURL", "onBindViewHolder: " + reward.toString());
            Log.i("IMAGEURL", "onBindViewHolder: " + imageUrl);
            if (imageUrl.contains("default")) {
                Glide.with(holder.context)
                        .load(R.drawable.pd_ui_star_icon)
                        .into(holder.brandImageView)
                ;
            } else {
                Glide.with(holder.context)
                        .load(imageUrl)
                        .error(R.drawable.pd_ui_star_icon)
                        .placeholder(R.drawable.pd_ui_star_icon)
//                        .override(mImageDimen, 1)
                        .into(holder.brandImageView);
            }

            holder.subTitleTextView.setText(R.string.pd_wallet_redeem_text);
            holder.chevronImageView.setVisibility(View.VISIBLE);
            holder.titleTextView.setText(reward.getDescription());

            if (reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
                String drawString = drawString(reward);
                holder.subTitleTextView.setText(drawString);
            } else if (reward.getCredit() != null && reward.getCredit().length() > 0) {

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(reward.getClaimedAt()*1000);

                DateFormat formatter = new SimpleDateFormat("d MMM");
                Date claimed = c.getTime();
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
        }else{
            EventViewHolder holder = (EventViewHolder)viewHolder;

            holder.medalTextView.setVisibility(View.VISIBLE);
            holder.detailsTextView.setVisibility(View.VISIBLE);

            final PDEvent event = (PDEvent) this.mItems.get(position - 1);

            switch (event.getToTier()){
                case 0:
                    holder.medalTextView.setText(holder.context.getResources().getString(R.string.pd_event_emoji_0));
                    break;
                case 1:
                    holder.medalTextView.setText(holder.context.getResources().getString(R.string.pd_event_emoji_1));
                    break;
                case 2:
                    holder.medalTextView.setText(holder.context.getResources().getString(R.string.pd_event_emoji_2));
                    break;
                case 3:
                    holder.medalTextView.setText(holder.context.getResources().getString(R.string.pd_event_emoji_3));
                    break;
                default:
                    holder.medalTextView.setText(holder.context.getResources().getString(R.string.pd_event_emoji_0));
                    break;
            }

            holder.detailsTextView.setText(getEventText(holder.context, event));

        }
    }

    private String getEventText(Context context, PDEvent event){
        String level1Name = context.getResources().getString(R.string.pd_tier_1_name);
        String level2Name = context.getResources().getString(R.string.pd_tier_2_name);
        String level3Name = context.getResources().getString(R.string.pd_tier_3_name);

        String ret = "";
        if(event.getFromTier() < event.getToTier()) {

            ret = context.getResources().getString(R.string.pd_event_congrats_1);

            switch (event.getToTier()) {
                case 1:
                    ret = ret + " " + level1Name + " " + context.getString(R.string.pd_event_congrats_2);
                    break;
                case 2:
                    ret = ret + " " + level2Name + " " + context.getString(R.string.pd_event_congrats_2);
                    break;
                case 3:
                    ret = ret + " " + level3Name + " " + context.getString(R.string.pd_event_congrats_2);
                    break;
                default:
                    ret = ret + " " + level1Name + " " + context.getString(R.string.pd_event_congrats_2);
                    break;
            }
        }else{
            ret = context.getString(R.string.pd_event_now);

            switch (event.getToTier()) {
                case 1:
                    ret = ret + " " + level1Name + " " + context.getString(R.string.pd_event_now_2);
                    break;
                case 2:
                    ret = ret + " " + level2Name + " " + context.getString(R.string.pd_event_now_2);
                    break;
                default:
                    ret = ret + " " + level1Name + " " + context.getString(R.string.pd_event_now_2);
                    break;
            }
        }

        return ret;
    }

    public void setupHeader(HeaderViewHolder header) {
        if (mRealm == null) {
            mRealm = Realm.getDefaultInstance();
        }

        mUser = mRealm.where(PDRealmUserDetails.class).findFirst();
        if (mUser != null) {
            String profileUrl = "";
            if (mUser.getUserFacebook() != null && mUser.getUserFacebook().getProfilePictureUrl() != null && !mUser.getUserFacebook().getProfilePictureUrl().isEmpty()) {
                profileUrl = mUser.getUserFacebook().getProfilePictureUrl();
            } else if (mUser.getUserTwitter() != null && mUser.getUserTwitter().getProfilePictureUrl() != null && !mUser.getUserTwitter().getProfilePictureUrl().isEmpty()) {
                profileUrl = mUser.getUserTwitter().getProfilePictureUrl();
            } else if (mUser.getUserInstagram() != null && mUser.getUserInstagram().getProfilePictureUrl() != null && !mUser.getUserInstagram().getProfilePictureUrl().isEmpty()) {
                profileUrl = mUser.getUserInstagram().getProfilePictureUrl();
            }
            if (profileUrl.length() > 0) {
                Glide.with(header.context)
                        .load(profileUrl)
                        .placeholder(R.drawable.pd_ui_default_user)
                        .error(R.drawable.pd_ui_default_user)
                        .into(header.profilePic);
            } else {
                displayDefaultUserImage(header.context, header.profilePic);
            }

            if (mUser.getFirstName() != null && mUser.getLastName() != null) {
                header.profileName.setText(String.format(Locale.getDefault(), "%1s %2s", mUser.getFirstName(), mUser.getLastName()));
            }else if (mUser.getFirstName() !=null){
                header.profileName.setText(mUser.getFirstName());
            }else if (mUser.getLastName() !=null){
                header.profileName.setText(mUser.getFirstName());
            }

            mCustomer = mRealm.where(PDRealmCustomer.class).findFirst();
            if(mCustomer!=null){
                if(!mCustomer.usesAmbassadorFeatures()){
                    header.ambassadorView.setVisibility(View.GONE);
                }
            }
            header.ambassadorView.setLevel((int)mUser.getAdvocacyScore(), false);
            header.loggedInLinearLayout.setVisibility(View.VISIBLE);
        }else{
            displayDefaultUserImage(header.context, header.profilePic);
            header.loggedInLinearLayout.setVisibility(View.GONE);
        }

        header.messagesBadgeTextView.setText(""+messagesCount);
        if(messagesCount>0){
            header.messagesBadgeTextView.setVisibility(View.VISIBLE);
        }else{
            header.messagesBadgeTextView.setVisibility(View.GONE);
        }
        if(mItems.size() < 1){
            header.noHistory.setVisibility(View.VISIBLE);
        }else{
            header.noHistory.setVisibility(View.GONE);
        }
    }

    private void displayDefaultUserImage(Context context, PDUIBezelImageView imageview) {
        Glide.with(context)
                .load(R.drawable.pd_ui_default_user)
                .into(imageview);
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
        int count = 1;
        if (this.mItems != null && this.mItems.size() > 0) {
            count = this.mItems.size() + 1;
        }
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ImageView brandImageView;
        ImageView chevronImageView;
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
            this.chevronImageView = (ImageView) itemView.findViewById(R.id.chevron);
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
                        mListener.onVerifyClick(getLayoutPosition()-1);
                    }
                }
            });
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        Context context;
        TextView medalTextView;
        TextView detailsTextView;


        EventViewHolder(View itemView, Context context, boolean clickable) {
            super(itemView);

            this.context = context;
            this.medalTextView = (TextView) itemView.findViewById(R.id.pd_event_medal);
            this.detailsTextView = (TextView) itemView.findViewById(R.id.pd_event_text_view);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        Context context;
        LinearLayout loggedInLinearLayout;
        LinearLayout profilesLinearLayout;
        LinearLayout messagesLinearLayout;
        TextView messagesTextView;
        TextView messagesBadgeTextView;
        TextView versionTextView;
        LinearLayout noHistory;


        PDUIBezelImageView profilePic;
        TextView profileName;
        PDAmbassadorView ambassadorView;


        HeaderViewHolder(View itemView, final Context context, boolean clickable) {
            super(itemView);

            this.context = context;

            loggedInLinearLayout = (LinearLayout)itemView.findViewById(R.id.pd_logged_in_view);
            profilesLinearLayout = (LinearLayout)itemView.findViewById(R.id.pd_connect_layout);
            profilesLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context.getApplicationContext(), PDUISettingsActivity.class));
                }
            });
            messagesLinearLayout = (LinearLayout)itemView.findViewById(R.id.pd_messages_layout);
            messagesLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context.getApplicationContext(), PDUIInboxActivity.class));

                }
            });
            ambassadorView = (PDAmbassadorView)itemView.findViewById(R.id.pd_profile_ambassador_view);

            messagesTextView = (TextView) itemView.findViewById(R.id.pd_messages_text);
            messagesBadgeTextView = (TextView) itemView.findViewById(R.id.pd_badge_text);

            profilePic = (PDUIBezelImageView)itemView.findViewById(R.id.pd_feed_profile_image_view);
            profileName = (TextView)itemView.findViewById(R.id.pd_feed_user_name_text_view);
            ambassadorView = (PDAmbassadorView)itemView.findViewById(R.id.pd_profile_ambassador_view);
            noHistory =(LinearLayout)itemView.findViewById(R.id.pd_wallet_no_items_view);
            versionTextView =(TextView) itemView.findViewById(R.id.version_text);
            versionTextView.setText("V"+BuildConfig.VERSION_NAME);
            versionTextView.setVisibility(View.GONE);

        }
    }

}
