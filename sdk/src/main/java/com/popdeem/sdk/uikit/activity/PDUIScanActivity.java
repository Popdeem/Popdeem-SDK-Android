package com.popdeem.sdk.uikit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.model.PDBGScanResponseModel;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDSocialUtils;

import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.twitter.sdk.android.core.TwitterCore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import io.realm.Realm;

import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK;
import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM;
import static com.popdeem.sdk.core.model.PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER;

/**
 * Created by dave on 11/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDUIScanActivity extends PDBaseActivity implements View.OnClickListener {

    private static String TAG = PDUIScanActivity.class.getSimpleName();
    private PDReward mReward;
    private String mNetwork;

    private LinearLayout PDScanView, PDScanSuccess, PDScanFailure;
    private AVLoadingIndicatorView indicatorView;
    private PDBGScanResponseModel pdbgScanResponseModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_scan);
        setTitle(R.string.pd_scan_title);

        PDScanView = (LinearLayout) findViewById(R.id.pd_scan);
        PDScanSuccess = (LinearLayout) findViewById(R.id.pd_scan_success);
        PDScanFailure = (LinearLayout) findViewById(R.id.pd_scan_failure);

        mReward = new Gson().fromJson(getIntent().getStringExtra("reward"), PDReward.class);
        mNetwork = getIntent().getStringExtra("network");


        populateUIWithTag();


        indicatorView = (AVLoadingIndicatorView) PDScanView.findViewById(R.id.av_indicator);
        indicatorView.smoothToShow();

        scan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            if (!popBackStackIfNeeded()) {
                finish();
//            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateUIWithTag() {
        TextView topLabel = (TextView) findViewById(R.id.scan_top_label);
        topLabel.setText(String.format(getString(R.string.pd_scan_top_label), mReward.getGlobalHashtag()));
    }

    private void scan() {
        PDAPIClient.instance().scanSocialNetwork(mReward.getId(), mNetwork, new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                Log.i(TAG, "success: " + jsonObject.toString());
                indicatorView.hide();
                parseResponse(jsonObject);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                Log.e(TAG, e.toString());
                indicatorView.hide();
            }
        });
    }

    private void parseResponse(JsonObject response) {
        pdbgScanResponseModel = new Gson().fromJson(response, PDBGScanResponseModel.class);

        if (pdbgScanResponseModel.isValidated()) {
            Log.i(TAG, "parseResponse: User has shared, show success view");
            showSuccessView();
        } else {
            Log.i(TAG, "parseResponse: User has not shared, show failure view");
            showFailView();
        }
    }

    /**
     * Show Success View
     */
    private void showSuccessView() {
        PDScanView.setVisibility(View.GONE);
        PDScanSuccess.setVisibility(View.VISIBLE);

        TextView topLabel = (TextView) PDScanSuccess.findViewById(R.id.pd_success_title);
        topLabel.setText(String.format(getString(R.string.pd_scan_success_label), pdbgScanResponseModel.getSocialName(), mReward.getGlobalHashtag()));


        //profile photo
        PDUIBezelImageView userProfilePicture = (PDUIBezelImageView) PDScanSuccess.findViewById(R.id.pd_scan_success_user_image_view);
        Glide.with(this)
                .load(pdbgScanResponseModel.getProfilePictureUrl())
                .centerCrop()
                .placeholder(R.drawable.pd_ui_default_user)
                .error(R.drawable.pd_ui_default_user)
                .dontAnimate()
                .into(userProfilePicture);


        //user name
        TextView userSocialName = (TextView) PDScanSuccess.findViewById(R.id.pd_social_user_name);
        userSocialName.setText(pdbgScanResponseModel.getSocialName());


        //media image
        ImageView mediaImage = (ImageView) PDScanSuccess.findViewById(R.id.image_media_url);
        Glide.with(this)
                .load(pdbgScanResponseModel.getMediaUrl())
                .fitCenter()
                .dontAnimate()
                .into(mediaImage);


        Button claimButton = (Button) PDScanSuccess.findViewById(R.id.btn_claim);
        claimButton.setOnClickListener(this);

        Button returnButton = (Button) PDScanSuccess.findViewById(R.id.btn_return);
        returnButton.setOnClickListener(this);
    }

    /**
     * Failure View
     */
    private void showFailView() {
        PDScanView.setVisibility(View.GONE);
        PDScanFailure.setVisibility(View.VISIBLE);

        TextView topLabel = (TextView) PDScanFailure.findViewById(R.id.pd_scan_label_fail);
        topLabel.setText(String.format(getString(R.string.pd_scan_fail_label), pdbgScanResponseModel.getSocialName(), mNetwork, mReward.getGlobalHashtag()));


        Button returnButton = (Button) PDScanFailure.findViewById(R.id.btn_return_fail);
        returnButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        final int ID = v.getId();

        if (ID == R.id.btn_claim) {
            //claim reward
            Log.i(TAG, "onClick: Claiming Reward");
            claimReward();
        } else if (ID == R.id.btn_return || ID == R.id.btn_return_fail) {
            //return to reward screen
            finish();
        }
    }

    private void claimReward(){
        String lat = "", lng = "", id = "";

        Realm realm = Realm.getDefaultInstance();
        PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();
        if (userLocation != null) {
            lat = String.valueOf(userLocation.getLatitude());
            lng = String.valueOf(userLocation.getLongitude());
            id = String.valueOf(userLocation.getId());
        }
        realm.close();

        String facebookAccessToken = null, twitterAccessToken = null, twitterAccessSecret = null, instagramAccessToken = null;

        if (pdbgScanResponseModel.getNetwork().equalsIgnoreCase(PD_SOCIAL_MEDIA_TYPE_FACEBOOK)){
            facebookAccessToken = AccessToken.getCurrentAccessToken().getToken();
        } else if (pdbgScanResponseModel.getNetwork().equalsIgnoreCase(PD_SOCIAL_MEDIA_TYPE_TWITTER)){
            if (PDSocialUtils.isTwitterLoggedIn() && TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().token != null
                    && TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().secret != null){
                twitterAccessToken = TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().token;
                twitterAccessSecret = TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().secret;
            }
        } else if (pdbgScanResponseModel.getNetwork().equalsIgnoreCase(PD_SOCIAL_MEDIA_TYPE_INSTAGRAM)){
            Realm realm1 = Realm.getDefaultInstance();
            PDRealmUserDetails userDetails = realm1.where(PDRealmUserDetails.class).findFirst();
            if (userDetails.getUserInstagram() != null && userDetails.getUserInstagram().getAccessToken() != null && !userDetails.getUserInstagram().getAccessToken().isEmpty()){
                instagramAccessToken = userDetails.getUserInstagram().getAccessToken();
            }
            realm1.close();
        }

        PDAPIClient.instance().claimDiscovery(pdbgScanResponseModel, facebookAccessToken, twitterAccessToken, twitterAccessSecret, instagramAccessToken, lat, lng, id, mReward.getId(), this, new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                Log.i(TAG, "success: " + jsonObject.toString());
                if (jsonObject.has("error")) {
                    new AlertDialog.Builder(PDUIScanActivity.this)
                            .setTitle(R.string.pd_common_sorry_text)
                            .setMessage(jsonObject.get("error").getAsString())
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                } else {
                    //reward claimed
                    new AlertDialog.Builder(PDUIScanActivity.this)
                            .setTitle(R.string.pd_claim_reward_claimed_text)
                            .setMessage(mReward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE) ? R.string.pd_claim_sweepstakes_claimed_success_text : R.string.pd_claim_reward_claimed_success_text)
                            .setPositiveButton(R.string.pd_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "onClick: Back to Rewards");
                                    Intent intent = new Intent(PDUIScanActivity.this, PDUIHomeFlowActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .create()
                            .show();
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                Log.e(TAG, e.toString());
                new AlertDialog.Builder(PDUIScanActivity.this)
                        .setTitle(R.string.pd_common_sorry_text)
                        .setMessage(R.string.pd_common_something_wrong_text)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });
    }


}
