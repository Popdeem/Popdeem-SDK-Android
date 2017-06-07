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

package com.popdeem.navigationsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.response.PDBasicResponse;
import com.popdeem.sdk.core.interfaces.FragmentCommunicator;
import com.popdeem.sdk.core.utils.PDLog;
import com.twitter.sdk.android.core.TwitterAuthConfig;

public class MainActivity extends AppCompatActivity implements FragmentCommunicator {

    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Process a referral if one is present
        PopdeemSDK.processReferral(this, getIntent());
    }

    public void pushPopdeemClick(View view) {
        PopdeemSDK.showHomeFlow(this);
    }

    public void triggerActionClick(View view) {
        PopdeemSDK.logMoment("post_payment", new PDAPICallback<PDBasicResponse>() {
            @Override
            public void success(PDBasicResponse response) {
                PDLog.d(PDAPIClient.class, response.toString());
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.w(PDAPIClient.class, "code=" + statusCode + ", message=" + e.getMessage());
            }
        });
    }

    public void thirdPartyTokenClick(View view) {
        PopdeemSDK.setThirdPartyToken("thirdPartyTokenTest");
    }

    /**
     * Used to complete the Twitter Login Flow, seen in SocialMultiLoginFragment
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("PDUISocialMultiLoginFragment");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.e(TAG, "onActivityResult: Fragment is NULL");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * The client must implement the Activity's onAttachFragment and do a simple check for one of the SDK's login fragments, in order to allow for custom functionality
     * @param fragment
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.i(TAG, "onAttachFragment: Fragment Attached " + fragment.getClass().getSimpleName());
        if (fragment.getClass().getSimpleName().equalsIgnoreCase("PDUISocialLoginFragment") || fragment.getClass().getSimpleName().equalsIgnoreCase("PDUISocialMultiLoginFragment"))
        {
            //clients can do custom stuff here (hide action bars)
        }
    }

    /**
     * fragmentDetached is called when PDUISocialLoginFragment or PDUISocialMultiLoginFragment are no longer attached to the Activity. This allows the client to perform custom func
     */

    @Override
    public void fragmentDetached() {
        Log.i(TAG, "fragmentDetached: Fragment is Detached");
        //clients can do custom stuff here when the social login fragment disappears
    }
}
