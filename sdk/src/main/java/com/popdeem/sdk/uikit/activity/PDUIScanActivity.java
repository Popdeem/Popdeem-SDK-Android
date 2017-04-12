package com.popdeem.sdk.uikit.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

/**
 * Created by dave on 11/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDUIScanActivity extends PDBaseActivity {

    private static String TAG = PDUIScanActivity.class.getSimpleName();
    private PDReward mReward;
    private String mNetwork;

    private LinearLayout PDScanView, PDScanSuccess, PDScanFailure;
    private AVLoadingIndicatorView indicatorView;


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

    private void populateUIWithTag() {
        TextView topLabel = (TextView) findViewById(R.id.scan_top_label);
        topLabel.setText(String.format(getString(R.string.pd_scan_top_label), mReward.getInstagramOptions().getForcedTag()));
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

    private void parseResponse(JsonObject response){
        boolean isValidated = response.get("validated").getAsBoolean();
        if (isValidated){
            Log.i(TAG, "parseResponse: User has shared, show success view");
            showSuccessView(response);
        } else {
            Log.i(TAG, "parseResponse: User has not shared, show failure view");
        }
    }

    /**
     * Show Success View
     */
    private void showSuccessView(JsonObject response){
        PDScanView.setVisibility(View.GONE);
        PDScanSuccess.setVisibility(View.VISIBLE);

        String socialName = response.get("social_name").getAsString();
        String mediaUrl = response.get("media_url").getAsString();
        String profilePicUrl = response.get("profile_picture_url").getAsString();

        TextView topLabel = (TextView) PDScanSuccess.findViewById(R.id.pd_success_title);
        topLabel.setText(String.format(getString(R.string.pd_scan_success_label), socialName, mReward.getInstagramOptions().getForcedTag()));


        //profile photo
        PDUIBezelImageView userProfilePicture = (PDUIBezelImageView) PDScanSuccess.findViewById(R.id.pd_scan_success_user_image_view);
        Picasso.with(this)
                .load(profilePicUrl)
                .centerCrop()
                .placeholder(R.drawable.pd_ui_default_user)
                .error(R.drawable.pd_ui_default_user)
                .resizeDimen(R.dimen.pd_scan_profile_image_dimen, R.dimen.pd_scan_profile_image_dimen)
                .into(userProfilePicture);


        //user name
        TextView userSocialName = (TextView) PDScanSuccess.findViewById(R.id.pd_social_user_name);
        userSocialName.setText(socialName);


        //media image
        ImageView mediaImage = (ImageView) PDScanSuccess.findViewById(R.id.image_media_url);
        Picasso.with(this)
                .load(mediaUrl)
                .fit()
                .into(mediaImage);
    }

}
