package com.popdeem.sdk.core.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

/**
 * Created by colm on 06/07/2018.
 */

public class PDTwitterBroadcastReceiver extends BroadcastReceiver {
    String TAG = "PDTwitterBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
            // success
            Log.i(TAG, "onReceive: UPLOAD_SUCCESS");
        } else if (TweetUploadService.UPLOAD_FAILURE.equals(intent.getAction())) {
            // failure
            Log.i(TAG, "onReceive: UPLOAD_FAILURE");
        } else if (TweetUploadService.TWEET_COMPOSE_CANCEL.equals(intent.getAction())) {
            // cancel
            Log.i(TAG, "onReceive: TWEET_COMPOSE_CANCEL");
        }
    }

}
