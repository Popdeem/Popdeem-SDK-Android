package com.niallquinn.onezinedemo;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.utils.PDSocialUtils;

import io.fabric.sdk.android.Fabric;

/**
 * Created by niall on 06/02/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics(), PDSocialUtils.getTwitterKitForFabric(this));

        // Initialise Popdeem SDK
        PopdeemSDK.initializeSDK(this);
        PopdeemSDK.enableSocialLogin(MainActivity.class, 5);
    }
}
