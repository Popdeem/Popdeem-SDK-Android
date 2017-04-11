package com.popdeem.sdk.uikit.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.model.PDReward;

/**
 * Created by dave on 11/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDUIScanActivity extends PDBaseActivity {

    private static String TAG = PDUIScanActivity.class.getSimpleName();
    private PDReward mReward;
    private String mNetwork;

    private LinearLayout PDScanView, PDScanSuccess, PDScanFailure;


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
            }

            @Override
            public void failure(int statusCode, Exception e) {
                Log.e(TAG, e.toString());
            }
        });
    }
}
