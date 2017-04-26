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
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.uikit.fragment.multilogin.PDUISocialMultiLoginFragment;
import com.twitter.sdk.android.core.TwitterAuthConfig;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            //issue: fragment is NULL when it gets here, so we'll never be able to continue the Twitter Login Process
//            Fragment fragment = getSupportFragmentManager().findFragmentByTag(PDUISocialMultiLoginFragment.getName());
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("PDUISocialMultiLoginFragment");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.e(TAG, "onActivityResult: Fragment is NULL");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: In on Pause");
    }
}
