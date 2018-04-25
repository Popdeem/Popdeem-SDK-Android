package com.popdeem.sdk.uikit.fragment.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.realm.PDRealmCustomer;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.uikit.widget.PDAmbassadorView;

import io.realm.Realm;

/**
 * Created by colm on 27/02/2018.
 */

public class PDUIGratitudeDialog extends Dialog {



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

        Realm  mRealm = Realm.getDefaultInstance();
        PDRealmCustomer mCustomer = mRealm.where(PDRealmCustomer.class).findFirst();
        if(mCustomer!=null){
            if(!mCustomer.usesAmbassadorFeatures()){
                ambassadorView.setVisibility(View.GONE);
            }
        }

        Button profileButton = (Button) dialog.findViewById(R.id.pd_gratitude_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        PDRealmUserDetails mUser = mRealm.where(PDRealmUserDetails.class).findFirst();

        int increment = 0;
        if(mCustomer!=null){
            increment = mCustomer.getIncrement_advocacy_points();
        }

        ImageView icon = dialog.findViewById(R.id.pd_iv_ambassador_icon);


        SharedPreferences sp = context.getSharedPreferences("popdeem_prefs", Activity.MODE_PRIVATE);
        int variationNum = sp.getInt("variation_num", 0);
        int variationNumImages = sp.getInt("variation_num_images", 0);


        if(type.equalsIgnoreCase("logged_in")) {

            if (mUser != null && mCustomer.usesAmbassadorFeatures()) {
                ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(context.getString(R.string.pd_gratitude_thanks_for_connecting));
                ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(String.format(context.getString(R.string.pd_gratitude_connect_body_text), "" + mCustomer.getIncrement_advocacy_points()));
                ambassadorView.setLevel((int) mUser.getAdvocacyScore(), true);

            }else{


//                int[] imagesArray = context.getResources().getIntArray(R.array.pd_login_images);
                TypedArray imagesArray = context.getResources().obtainTypedArray(R.array.pd_login_images);

                if(imagesArray.length()==0) {
                    icon.setVisibility(View.INVISIBLE);
                }else if(imagesArray.length()==1){
                    icon.setVisibility(View.VISIBLE);
                    int showNum = variationNumImages%imagesArray.length();
                    icon.setImageResource(imagesArray.getResourceId(showNum,-1));
                }else if(imagesArray.length()>1){
                    int showNum = variationNumImages%imagesArray.length();
                    icon.setImageResource(imagesArray.getResourceId(showNum,-1));
                    icon.setVisibility(View.VISIBLE);
                }

                imagesArray.recycle();

                String[] stringsArrayTitle = context.getResources().getStringArray(R.array.pd_gratuity_strings_login_title);
                String[] stringsArrayBody = context.getResources().getStringArray(R.array.pd_gratuity_strings_login_title);
                if(stringsArrayBody.length==0) {
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(context.getString(R.string.pd_gratitude_thanks_for_connecting));
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(String.format(context.getString(R.string.pd_gratitude_connect_body_text), "" + mCustomer.getIncrement_advocacy_points()));
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText("");
                }else if(stringsArrayBody.length==1) {
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(stringsArrayTitle[0]);
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(stringsArrayBody[0]);
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText("");

                }else if(stringsArrayBody.length>1) {
                    int showNum = variationNum%stringsArrayBody.length;
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(stringsArrayTitle[showNum]);
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(stringsArrayBody[showNum]);
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText("");
                }


            }

        }else{
            if(mCustomer!=null && mCustomer.usesAmbassadorFeatures()) {
                ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(context.getString(R.string.pd_gratitude_title));
                ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText(String.format(context.getString(R.string.pd_gratitude_share_body_text), "" + mCustomer.getIncrement_advocacy_points()));

                if (mUser != null) {
                    ambassadorView.setLevel((int) mUser.getAdvocacyScore() + increment, ((int) mUser.getAdvocacyScore() < 90));
                    mRealm.beginTransaction();
                    mUser.setAdvocacyScore(mUser.getAdvocacyScore() + increment);
                    mRealm.commitTransaction();
                }

            }else{
//                int[] imagesArray = context.getResources().getIntArray(R.array.pd_share_images);
                TypedArray imagesArray = context.getResources().obtainTypedArray(R.array.pd_share_images);

                if(imagesArray.length()==0) {
                    icon.setVisibility(View.INVISIBLE);
                }else if(imagesArray.length()==1){
                    int showNum = variationNumImages%imagesArray.length();
                    icon.setImageResource(imagesArray.getResourceId(showNum,-1));
                    icon.setVisibility(View.VISIBLE);
                }else if(imagesArray.length()>1){
                    int showNum = variationNum%imagesArray.length();
                    icon.setImageResource(imagesArray.getResourceId(showNum,-1));
                    icon.setVisibility(View.VISIBLE);
                }

                imagesArray.recycle();

                String[] stringsArrayTitle;
                String[] stringsArrayBody;
                if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_COUPON)) {
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

                if(stringsArrayBody.length==0) {
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(context.getString(R.string.pd_gratitude_title));
                    if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_CREDIT)) {
                        ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(String.format(context.getString(R.string.pd_gratitude_share_text_coupon), reward.getCredit()));
                    }else {
                        ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(context.getString(R.string.pd_gratitude_share_text));
                    }
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText("");
                }else if(stringsArrayBody.length==1) {
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(stringsArrayTitle[0]);
                    if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_CREDIT)) {
                        ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(String.format(stringsArrayBody[0], reward.getCredit()));
                    }else {
                        ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(stringsArrayBody[0]);
                    }
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText("");
                }else if(stringsArrayBody.length>1) {
                    int showNum = variationNum%stringsArrayBody.length;
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_title)).setText(stringsArrayTitle[showNum]);
                    if(reward.getRewardType().equals(PDReward.PD_REWARD_TYPE_CREDIT)) {
                        ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(String.format(stringsArrayBody[showNum], reward.getCredit()));
                    }else {
                        ((TextView) dialog.findViewById(R.id.pd_gratitude_description)).setText(stringsArrayBody[showNum]);
                    }
                    ((TextView) dialog.findViewById(R.id.pd_gratitude_description_2)).setText("");
                }




            }
        }

        variationNum++;
        variationNumImages++;

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("variation_num", variationNum);
        editor.putInt("variation_num_images", variationNumImages);
        editor.commit();



        mRealm.close();
        return dialog;
    }

    public void setPdReward(PDReward pdReward) {
        this.pdReward = pdReward;
    }

}
