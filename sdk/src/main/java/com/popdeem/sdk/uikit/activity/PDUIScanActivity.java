package com.popdeem.sdk.uikit.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDReward;

/**
 * Created by dave on 11/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDUIScanActivity extends PDBaseActivity {

    private PDReward mReward;
    private String mNetwork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_scan);
        setTitle(R.string.pd_scan_title);

        mReward = new Gson().fromJson(getIntent().getStringExtra("reward"), PDReward.class);
        mNetwork = getIntent().getStringExtra("network");


        populateUIWithTag();
        scan();
    }

    private void populateUIWithTag()
    {
        TextView topLabel = (TextView) findViewById(R.id.scan_top_label);
        topLabel.setText(String.format(getString(R.string.pd_scan_top_label), mReward.getInstagramOptions().getForcedTag()));
    }

    private void scan(){

    }
}
