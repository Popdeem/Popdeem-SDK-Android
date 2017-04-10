package com.popdeem.sdk.uikit.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.popdeem.sdk.R;

/**
 * Created by dave on 10/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDUISelectNetworkActivity extends PDBaseActivity {

    private static String TAG = PDUISelectNetworkActivity.class.getSimpleName();
    private String scannableHashTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_select_network);
        setTitle(R.string.pd_scan_title);

        scannableHashTag = getIntent().getStringExtra("forcedTag");
        Log.i(TAG, "onCreate: " + scannableHashTag);


        populateUIWithTag();

    }

    private void populateUIWithTag()
    {
        TextView titleLabel = (TextView) findViewById(R.id.title_label);
        titleLabel.setText(String.format(getString(R.string.pd_scan_title_label), scannableHashTag));

        TextView noteLabel = (TextView) findViewById(R.id.note_label);
        noteLabel.setText(String.format(getString(R.string.pd_scan_note), scannableHashTag));
    }
}
