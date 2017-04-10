package com.popdeem.sdk.uikit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.uikit.fragment.PDUIConnectSocialAccountFragment;

/**
 * Created by dave on 10/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDUISelectNetworkActivity extends PDBaseActivity implements View.OnClickListener {

    private static String TAG = PDUISelectNetworkActivity.class.getSimpleName();
    private String scannableHashTag = "";
    private boolean isFacebookLoggedIn, isTwitterLoggedIn, isInstagramLoggedIn;

    private FragmentManager mFragmentManager;


    private Button btnFacebook;
    private Button btnTwitter;
    private Button btnInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_select_network);
        setTitle(R.string.pd_scan_title);

        mFragmentManager = getSupportFragmentManager();

        scannableHashTag = getIntent().getStringExtra("forcedTag");
        Log.i(TAG, "onCreate: " + scannableHashTag);


        populateUIWithTag();
        checkSocialAccounts();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!popBackStackIfNeeded()) {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateUIWithTag() {
        TextView titleLabel = (TextView) findViewById(R.id.title_label);
        titleLabel.setText(String.format(getString(R.string.pd_scan_title_label), scannableHashTag));

        TextView noteLabel = (TextView) findViewById(R.id.note_label);
        noteLabel.setText(String.format(getString(R.string.pd_scan_note), scannableHashTag));
    }

    private void checkSocialAccounts() {

        btnFacebook = (Button) findViewById(R.id.btn_facebook);
        btnTwitter = (Button) findViewById(R.id.btn_twitter);
        btnInstagram = (Button) findViewById(R.id.btn_instagram);

        //update Facebook Button
        if (PDSocialUtils.isLoggedInToFacebook()) {
            btnFacebook.setText(R.string.pd_scan_facebook);
            isFacebookLoggedIn = true;
        } else {
            btnFacebook.setText(R.string.pd_connect_facebook);
            isFacebookLoggedIn = false;
        }

        //update Twitter Button
        if (PDSocialUtils.isTwitterLoggedIn()) {
            btnTwitter.setText(R.string.pd_scan_twitter);
            isTwitterLoggedIn = true;
        } else {
            btnTwitter.setText(R.string.pd_connect_twitter);
            isTwitterLoggedIn = false;
        }

        //update Instagram
        PDSocialUtils.isInstagramLoggedIn(new PDAPICallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                if (aBoolean) {
                    btnInstagram.setText(R.string.pd_scan_instagram);
                    isInstagramLoggedIn = true;
                } else {
                    btnInstagram.setText(R.string.pd_connect_instagram);
                    isInstagramLoggedIn = false;
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                Log.e(TAG, e.toString());
            }
        });


        //set clicks
        btnFacebook.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int ID = v.getId();

        if (ID == R.id.btn_facebook) {
            Log.i(TAG, "onClick: Scan Facebook");
            if (isFacebookLoggedIn) {
                scan(ID);
            } else {
                connect(ID);
            }

        } else if (ID == R.id.btn_twitter) {
            Log.i(TAG, "onClick: Scan Twitter");
            if (isTwitterLoggedIn) {
                scan(ID);
            } else {
                connect(ID);
            }

        } else if (ID == R.id.btn_instagram) {
            Log.i(TAG, "onClick: Scan Instagram");
            if (isInstagramLoggedIn) {
                scan(ID);
            } else {
                connect(ID);
            }
        }
    }

    private void scan(int ID) {
        Log.i(TAG, "scan: Jumping to Scan");
        // TODO: 10/04/2017 Jump to Scan Activity with hashTag
    }

    private void connect(int ID) {
        Log.i(TAG, "connect: Connecting Social Account");

        if (ID == R.id.btn_facebook) {
            Log.i(TAG, "connect: Connecting Facebook");
            showConnectAccountDialog(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_FACEBOOK);
        } else if (ID == R.id.btn_twitter) {
            Log.i(TAG, "connect: Connecting Twitter");
            showConnectAccountDialog(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_TWITTER);
        } else if (ID == R.id.btn_instagram) {
            Log.i(TAG, "connect: Connecting Instagram");
            showConnectAccountDialog(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_INSTAGRAM);
        }
    }

    private void showConnectAccountDialog(@PDUIConnectSocialAccountFragment.PDConnectSocialAccountType int type) {
        PDUIConnectSocialAccountFragment fragment = PDUIConnectSocialAccountFragment.newInstance(type, new PDUIConnectSocialAccountFragment.PDUIConnectSocialAccountCallback() {
            @Override
            public void onAccountConnected(@PDUIConnectSocialAccountFragment.PDConnectSocialAccountType int type) {

                switch (type) {
                    case PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_FACEBOOK:
                        Log.i(TAG, "onAccountConnected: Facebook Connected");
                        btnFacebook.setText(R.string.pd_scan_facebook);
                        isFacebookLoggedIn = true;
                        break;
                    case PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_TWITTER:
                        Log.i(TAG, "onAccountConnected: Twitter Connected");
                        btnTwitter.setText(R.string.pd_scan_twitter);
                        isTwitterLoggedIn = true;
                        break;
                    case PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_INSTAGRAM:
                        Log.i(TAG, "onAccountConnected: Instagram Connected");
                        btnInstagram.setText(R.string.pd_scan_instagram);
                        isInstagramLoggedIn = true;
                        break;
                }
            }
        });

        mFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment, PDUIConnectSocialAccountFragment.getName())
                .addToBackStack(PDUIConnectSocialAccountFragment.getName())
                .commit();
    }

    private boolean popBackStackIfNeeded() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            String name = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
            mFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }
        return false;
    }
}
