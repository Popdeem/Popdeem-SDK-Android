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

package com.popdeem.sdk.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.response.PDBasicResponse;
import com.popdeem.sdk.core.exception.PopdeemSDKNotInitializedException;
import com.popdeem.sdk.core.gcm.PDGCMUtils;
import com.popdeem.sdk.core.model.PDNonSocialUID;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.realm.PDRealmNonSocialUID;
import com.popdeem.sdk.core.realm.PDRealmReferral;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.realm.PDRealmUtils;
import com.popdeem.sdk.core.utils.PDPreferencesUtils;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUniqueIdentifierUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.activity.PDUIHomeFlowActivity;
import com.popdeem.sdk.uikit.fragment.PDUISocialLoginFragment;

import bolts.AppLinks;
import io.realm.Realm;

/**
 * Created by mikenolan on 15/02/16.
 */
public final class PopdeemSDK {

    private static Application sApplication;
    private static Activity sCurrentActivity;
    private static String sPopdeemAPIKey = null;
    private static boolean sdkInitialized = false;


    private PopdeemSDK() {
    }

    /**
     * Initialize Popdeem SDK
     *
     * @param application Application context
     */
    public static void initializeSDK(Application application) {
        sApplication = application;

        // Register Activity Lifecycle Callbacks
        application.registerActivityLifecycleCallbacks(PD_ACTIVITY_LIFECYCLE_CALLBACKS);

        // Init ACRA

        // Init Realm
        PDRealmUtils.initRealmDB(application);

        // Get Popdeem API Key
        getPopdeemAPIKey();

        // Get UID for Non Social login
        if (PDUniqueIdentifierUtils.getUID() == null) {
            PDUniqueIdentifierUtils.createUID(application, new PDUniqueIdentifierUtils.PDUIDCallback() {
                @Override
                public void success(String uid) {
                    PDRealmNonSocialUID uidReam = new PDRealmNonSocialUID();
                    uidReam.setId(0);
                    uidReam.setRegistered(false);
                    uidReam.setUid(uid);

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(uidReam);
                    realm.commitTransaction();
                    realm.close();

                    registerNonSocialUser();
                }

                @Override
                public void failure(String message) {
                    Log.d(PDUniqueIdentifierUtils.class.getSimpleName(), "failed to create uid: " + message);
                }
            });
        }

        // Init GCM
        PDGCMUtils.initGCM(application, new PDGCMUtils.PDGCMRegistrationCallback() {
            @Override
            public void success(String registrationToken) {
                Log.d(PDGCMUtils.class.getSimpleName(), "Init GCM success. Registration token: " + registrationToken);
                registerNonSocialUser();
            }

            @Override
            public void failure(String message) {
                Log.d(PDGCMUtils.class.getSimpleName(), "Init GCM failure: " + message);
            }
        });

        // Init Facebook
        FacebookSdk.sdkInitialize(application);

        // Init Twitter
        PDSocialUtils.initTwitter(application);

        sdkInitialized = true;
    }


    /**
     * Process a referral if one is present.
     *
     * @param context Application context
     * @param intent  Incoming intent
     */
    public static void processReferral(Context context, Intent intent) {
        if (intent != null) {
            Uri targetUri = AppLinks.getTargetUrlFromInboundIntent(context, intent);
            if (targetUri != null) {
                Log.d(PopdeemSDK.class.getSimpleName(), "targetUri: " + targetUri.toString());

                Realm realm = Realm.getDefaultInstance();

                // Save PDReferral
                PDRealmReferral referral = new PDRealmReferral();
                referral.setId(0);
                referral.setType("");
                referral.setRequestId(0);
                referral.setSenderAppName("");
                referral.setSenderId(0);
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(referral);
                realm.commitTransaction();

                PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
                if (userDetails == null) {
                    return;
                }

                PDRealmGCM gcm = realm.where(PDRealmGCM.class).findFirst();
                String deviceToken = gcm == null ? "" : gcm.getRegistrationToken();

                PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();
                String lat = "", lng = "";
                if (userLocation != null) {
                    lat = String.valueOf(userLocation.getLatitude());
                    lng = String.valueOf(userLocation.getLongitude());
                }

                PDAPIClient.instance().updateUserLocationAndDeviceToken(userDetails.getId(), deviceToken, lat, lng, new PDAPICallback<PDUser>() {
                    @Override
                    public void success(PDUser user) {

                    }

                    @Override
                    public void failure(int statusCode, Exception e) {

                    }
                });

                realm.close();
            }
        }
    }


    /**
     * Register Non Social user if UID and GCM Token are present
     */
    private static synchronized void registerNonSocialUser() {
        final PDNonSocialUID uid = PDUniqueIdentifierUtils.getUID();
        final String token = PDGCMUtils.getRegistrationToken(sApplication);
        if (uid != null && !uid.isRegistered()) {
            PDAPIClient.instance().createNonSocialUser(uid.getUid(), token.isEmpty() ? null : token, new PDAPICallback<PDBasicResponse>() {
                @Override
                public void success(PDBasicResponse response) {
                    Log.d("Popdeem", "registerNonSocialUser: " + response.toString());
                    if (response.isSuccess() && !token.isEmpty()) {
                        PDRealmNonSocialUID uidReam = new PDRealmNonSocialUID();
                        uidReam.setId(0);
                        uidReam.setRegistered(true);

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(uidReam);
                        realm.commitTransaction();
                        realm.close();
                    }
                }

                @Override
                public void failure(int statusCode, Exception e) {
                    Log.d(PDAPIClient.class.getSimpleName(), "Register non social user failed. code:" + statusCode + ", message: " + e.getMessage());
                }
            });
        }
    }


    /**
     * Show Popdeem Home Flow
     */
    public static void showHomeFlow() {
        if (!isPopdeemSDKInitialized()) {
            throw new PopdeemSDKNotInitializedException("Popdeem SDK is not initialized. Be sure to call PopdeemSDK.initializeSDK(Application application) in your Application class before using the SDK.");
        }

        Intent intent = new Intent(currentActivity(), PDUIHomeFlowActivity.class);
        currentActivity().startActivity(intent);
    }

    /**
     * Show social login flow.
     *
     * @param activity AppCompatActivity initiating te social login flow
     */
    public static void showSocialLogin(final AppCompatActivity activity) {
        if (!isPopdeemSDKInitialized()) {
            throw new PopdeemSDKNotInitializedException("Popdeem SDK is not initialized. Be sure to call PopdeemSDK.initializeSDK(Application application) in your Application class before using the SDK.");
        }
        pushSocialLoginFragmentToActivity(activity);
    }

    /**
     * Enable social login flow for a given Activity.
     * <p>
     * When this activity is presented to the user, the Popdeem Social Login Flow will be displayed if they are not already logged and the number of prompts has not been reached.
     * </p>
     *
     * @param activityClassSimpleName Simple Name of AppCompatActivity class you want Social Login flow to appear in. You can get the Simple Name by using this method: MyActivty.class.getSimpleName()
     * @param numberOfPrompts         Number of Login Prompts
     */
    public static void enableSocialLogin(@NonNull String activityClassSimpleName, final int numberOfPrompts) {
        if (!isPopdeemSDKInitialized()) {
            throw new PopdeemSDKNotInitializedException("Popdeem SDK is not initialized. Be sure to call PopdeemSDK.initializeSDK(Application application) in your Application class before using the SDK.");
        }

        PDPreferencesUtils.setNumberOfLoginAttempts(sApplication, numberOfPrompts);
        PDPreferencesUtils.setSocialLoginActivityName(sApplication, activityClassSimpleName);
    }


    /**
     * Push the Social Login Flow Fragment to the supplied Activity
     *
     * @param activity AppCompatActivity to show social login flow in
     */
    private static void pushSocialLoginFragmentToActivity(final AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(android.R.id.content, PDUISocialLoginFragment.newInstance())
                .addToBackStack(PDUISocialLoginFragment.class.getSimpleName())
                .commit();
    }


    /**
     * Check if Popdeem SDK has been initialized.
     *
     * @return true if Popdeem SDK is initialized, false otherwise
     */
    public static synchronized boolean isPopdeemSDKInitialized() {
        return sdkInitialized;
    }


    /**
     * Get the Popdeem API Key
     *
     * @return Popdeem API Key String
     */
    public static String getPopdeemAPIKey() {
        if (sPopdeemAPIKey == null) {
            sPopdeemAPIKey = PDUtils.getPopdeemAPIKey(sApplication);
        }
        return sPopdeemAPIKey;
    }


    /**
     * Get the Current Activity
     *
     * @return Current Activity
     */
    public static Activity currentActivity() {
        return sCurrentActivity;
    }


    /**
     * Used to keep track of the current Activity.
     */
    private static final Application.ActivityLifecycleCallbacks PD_ACTIVITY_LIFECYCLE_CALLBACKS = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activity instanceof AppCompatActivity && activity.getClass().getSimpleName().equalsIgnoreCase(PDPreferencesUtils.getSocialLoginActivityName(activity))
                    && PDSocialUtils.shouldShowSocialLogin(activity)) {
                Log.i(PopdeemSDK.class.getSimpleName(), "showing social login");
                PDPreferencesUtils.incrementLoginUsesCount(activity);
                showSocialLogin((AppCompatActivity) activity);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            sCurrentActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            sCurrentActivity = null;
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

}
