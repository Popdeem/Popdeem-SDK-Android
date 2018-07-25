package com.popdeem.sdk.uikit.fragment.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.realm.PDRealmCustomer;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.uikit.fragment.PDUIRewardsFragment;
import com.popdeem.sdk.uikit.widget.PDAmbassadorView;

import io.realm.Realm;

/**
 * Created by colm on 27/02/2018.
 */

public class PDUIGratitudeDialog extends Dialog {


    private static String mType = "";
    Context context;

    PDReward pdReward;



    public PDUIGratitudeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
//        if(context instanceof )
    }

    public static PDUIGratitudeDialog showGratitudeDialog(Context context, String type){
        return showGratitudeDialog(context,type, null);
    }
    public static PDUIGratitudeDialog showGratitudeDialog(final Context context, String type, PDReward reward){

        mType = type;
        Realm  mRealm = Realm.getDefaultInstance();
        PDRealmUserDetails mUser = mRealm.where(PDRealmUserDetails.class).findFirst();
        if(mUser.getAdvocacyScore()>30 && type.equalsIgnoreCase("logged_in")){
            return null;
        }
        final PDUIGratitudeDialog dialog = new PDUIGratitudeDialog(context, R.style.FullScreenDialogStyle);
        dialog.setPdReward(reward);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pduigratitude);
//        dialog.setOnCancelListener(onCancelListener);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        PDAmbassadorView ambassadorView = (PDAmbassadorView) dialog.findViewById(R.id.pd_gratitude_ambassador_view);

        PDRealmCustomer mCustomer = mRealm.where(PDRealmCustomer.class).findFirst();
        if(mCustomer!=null && mCustomer.usesAmbassadorFeatures()){
            int increment = 0;
            if(mCustomer!=null){
                increment = mCustomer.getIncrement_advocacy_points();
            }
            if (mUser != null) {
                ambassadorView.setLevel((int) mUser.getAdvocacyScore() + increment, ((int) mUser.getAdvocacyScore() < 90));
                mRealm.beginTransaction();
                mUser.setAdvocacyScore(mUser.getAdvocacyScore() + increment);
                mRealm.commitTransaction();
            }

        }else{
            ambassadorView.setVisibility(View.GONE);
        }

        Button profileButton = (Button) dialog.findViewById(R.id.pd_gratitude_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        setIcon(context, mCustomer, reward, type, dialog);
        setTitleAndsBody(context, mCustomer, reward, type, dialog);

        mRealm.close();
        return dialog;
    }

    public static void setIcon(Context context, PDRealmCustomer customer, PDReward reward, String type, PDUIGratitudeDialog dialog){

        SharedPreferences sp = context.getSharedPreferences("popdeem_prefs", Activity.MODE_PRIVATE);
        int variationNumImages = 0;

        ImageView icon = dialog.findViewById(R.id.pd_iv_ambassador_icon);

        TypedArray imagesArray = context.getResources().obtainTypedArray(R.array.pd_login_images);

        if(type.equalsIgnoreCase("logged_in")){
            imagesArray = context.getResources().obtainTypedArray(R.array.pd_connect_images);
            variationNumImages = sp.getInt("variation_num_images_login", 0);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_COUPON)) {
            imagesArray = context.getResources().obtainTypedArray(R.array.pd_coupon_images);
            variationNumImages = sp.getInt("variation_num_images_coupon", 0);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_CREDIT)) {
            imagesArray = context.getResources().obtainTypedArray(R.array.pd_credit_images);
            variationNumImages = sp.getInt("variation_num_images_credit", 0);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
            imagesArray = context.getResources().obtainTypedArray(R.array.pd_sweepstake_images);
            variationNumImages = sp.getInt("variation_num_images_sweepstake", 0);
        }else{
            imagesArray = context.getResources().obtainTypedArray(R.array.pd_connect_images);
            variationNumImages = sp.getInt("variation_num_images", 0);
        }

        if(imagesArray.length()==0) {
            icon.setVisibility(View.INVISIBLE);
        }else if(imagesArray.length()==1){
            icon.setVisibility(View.VISIBLE);
            int resourceId = imagesArray.getResourceId(0,-1);
            icon.setImageResource(resourceId);
        }else if(imagesArray.length()>1){
            int showNum = variationNumImages%imagesArray.length();
            icon.setImageResource(imagesArray.getResourceId(showNum,-1));
            icon.setVisibility(View.VISIBLE);
        }


        variationNumImages++;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("variation_num_images", variationNumImages);

        if(type.equalsIgnoreCase("logged_in")){
            editor.putInt("variation_num_images_login", variationNumImages);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_COUPON)) {
            editor.putInt("variation_num_images_coupon", variationNumImages);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_CREDIT)) {
            editor.putInt("variation_num_images_credit", variationNumImages);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
            editor.putInt("variation_num_images_sweepstake", variationNumImages);
        }else{
            editor.putInt("variation_num_images", variationNumImages);
        }

        editor.commit();


    }

    public static void setTitleAndsBody(Context context, PDRealmCustomer customer, PDReward reward, String type, PDUIGratitudeDialog dialog){
        String title = "";
        String body = "";

        String[] stringsArrayTitle;
        String[] stringsArrayBody;
        if(type.equalsIgnoreCase("logged_in")){
            stringsArrayTitle = context.getResources().getStringArray(R.array.pd_gratuity_strings_login_title);
            stringsArrayBody = context.getResources().getStringArray(R.array.pd_gratuity_strings_login_body);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_COUPON)) {
            stringsArrayTitle = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_coupon_title);
            stringsArrayBody = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_coupon_body);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_CREDIT)) {
            stringsArrayTitle = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_credit_title);
            stringsArrayBody = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_credit_body);
        }else if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
            stringsArrayTitle = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_sweepstake_title);
            stringsArrayBody = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_sweepstake_body);
        }else{
            stringsArrayTitle = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_coupon_title);
            stringsArrayBody = context.getResources().getStringArray(R.array.pd_gratuity_strings_share_coupon_title);
        }

        int numVar = stringsArrayBody.length;

        SharedPreferences sp = context.getSharedPreferences("popdeem_prefs", Activity.MODE_PRIVATE);
        int variationNum = sp.getInt("variation_num", 0);

        if(numVar == 0){
            if(type.equalsIgnoreCase("logged_in")){
                title = "Welcome!";
                body = "Thanks for connecting, start sharing to earn more rewards and enter amazing competitions.";

            }else{
                if(reward != null && reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_COUPON)){
                    if(reward.getCredit()!=null && reward.getCredit().length()>0){
                        title = "You're Brilliant!";
                        body = String.format("Thanks for sharing. %s has been added to your account. Enjoy!", "" + reward.getCredit());
                    }else{
                        title = "Great Job!";
                        body = "Thanks for sharing, your reward has been added to your profile. Enjoy!";
                    }
                }else{
                    title = "Awesome!";
                    body = "Thanks for sharing, you’ve been entered into the competition.";
                }
            }
        }else if(numVar == 1) {
            title = stringsArrayTitle[0];
            if (reward != null && reward.getCredit() != null && reward.getCredit().length() > 0) {
                body = String.format(stringsArrayBody[0], reward.getCredit());
            } else {
                body = stringsArrayBody[0];
            }
        }else{
            int showNum = variationNum%numVar;
            title = stringsArrayTitle[showNum];
            if (reward != null && reward.getCredit() != null && reward.getCredit().length() > 0) {
                body = String.format(stringsArrayBody[showNum], reward.getCredit());
            } else {
                body = stringsArrayBody[showNum];
            }
        }

        ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(title);
        ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(body);
        ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText("");

        variationNum++;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("variation_num", variationNum);
        editor.commit();

    }

    public void setPdReward(PDReward pdReward) {
        this.pdReward = pdReward;
    }


    @Override
    public void dismiss() {
        PopdeemSDK.showHome = true;
        if(mType.equalsIgnoreCase("logged_in")) {
            getContext().sendBroadcast(new Intent(PDUIRewardsFragment.PD_LOGGED_IN_RECEIVER_FILTER));
        }
        super.dismiss();

    }
}
