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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.response.PDBasicResponse;
import com.popdeem.sdk.core.exception.PopdeemSDKNotInitializedException;
import com.popdeem.sdk.core.gcm.GCMIntentService;
import com.popdeem.sdk.core.gcm.PDGCMUtils;
import com.popdeem.sdk.core.model.PDNonSocialUID;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.realm.PDRealmNonSocialUID;
import com.popdeem.sdk.core.realm.PDRealmReferral;
import com.popdeem.sdk.core.realm.PDRealmThirdPartyToken;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserFacebook;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.realm.PDRealmUtils;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.core.utils.PDPreferencesUtils;
import com.popdeem.sdk.core.utils.PDReferralUtils;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUniqueIdentifierUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.activity.PDUIHomeFlowActivity;
import com.popdeem.sdk.uikit.fragment.PDUIRewardsFragment;
import com.popdeem.sdk.uikit.fragment.PDUISocialLoginFragment;
import com.popdeem.sdk.uikit.fragment.dialog.PDUINotificationDialogFragment;

import java.net.HttpURLConnection;

import bolts.AppLinks;
import io.realm.Realm;
import io.realm.RealmResults;

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
    public static void initializeSDK(@NonNull Application application) {
        sApplication = application;

        // Register Activity Lifecycle Callbacks
        application.registerActivityLifecycleCallbacks(PD_ACTIVITY_LIFECYCLE_CALLBACKS);

        // Init Realm
        PDRealmUtils.initRealmDB(application);

        // Get Popdeem API Key
        getPopdeemAPIKey();

        // Init Facebook
        FacebookSdk.sdkInitialize(application);

        // Init Twitter
        PDSocialUtils.initTwitter(application);

        // Init Instagram
        PDSocialUtils.initInstagram(application);

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
                    PDLog.d(PDUniqueIdentifierUtils.class, "failed to create uid: " + message);
                }
            });
        }

        // Init GCM
        PDGCMUtils.initGCM(application, new PDGCMUtils.PDGCMRegistrationCallback() {
            @Override
            public void success(String registrationToken) {
                PDLog.d(PDGCMUtils.class, "Init GCM success. Registration token: " + registrationToken);
                registerNonSocialUser();
            }

            @Override
            public void failure(String message) {
                PDLog.d(PDGCMUtils.class, "Init GCM failure: " + message);
            }
        });

        sdkInitialized = true;
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
     * Process a referral if one is present.
     *
     * @param context Application context
     * @param intent  Incoming intent
     */
    public static void processReferral(Context context, Intent intent) {
        if (intent != null && intent.getData() != null) {
            // Compare schemes
            String scheme = context.getString(R.string.facebook_url_scheme);
            String schemeFromIntent = intent.getData().getScheme();
            if (!scheme.equalsIgnoreCase(schemeFromIntent)) {
                // Schemes are not the same, ignore the intent data.
                return;
            }

            Bundle appLinkData = AppLinks.getAppLinkData(intent);
            if (appLinkData != null) {
//                PDLog.d(PopdeemSDK.class, "appLinkData: " + appLinkData.toString());
                PDRealmReferral referral = new PDRealmReferral();
                referral.setId(0); // Always 0. Only used for storage as we only want to save one referral at a time.
                referral.setType("open");
                referral.setSenderAppName("");
                referral.setSenderId(-1);
                referral.setRequestId(-1);

                Bundle referrerBundle = appLinkData.getBundle("referer_app_link");
                if (referrerBundle != null && referrerBundle.containsKey("app_name")) {
                    referral.setSenderAppName(referrerBundle.getString("app_name", ""));
                }

                Uri data = intent.getData();
                if (data != null) {
                    referral.setSenderId(PDNumberUtils.toLong(data.getQueryParameter("user_id"), -1));
                }

                Uri targetUri = AppLinks.getTargetUrlFromInboundIntent(context, intent);
                int requestId = PDReferralUtils.getRequestIdFromUrl(targetUri);
                referral.setRequestId(requestId);

                // Save PDReferral
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(referral);
                realm.commitTransaction();

                // Send Referral in Update call if logged in
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
                        PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
                        userDetails.setUid(0);

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(userDetails);
                        realm.commitTransaction();
                        realm.close();
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
                    PDLog.d(PopdeemSDK.class, "registerNonSocialUser: " + response.toString());
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
                    PDLog.d(PDAPIClient.class, "Register non social user failed. code:" + statusCode + ", message: " + e.getMessage());
                }
            });
        }
    }


    /**
     * Show Popdeem Home Flow
     */
    public static void showHomeFlow(Context context) {
        if (!isPopdeemSDKInitialized()) {
            throw new PopdeemSDKNotInitializedException("Popdeem SDK is not initialized. Be sure to call PopdeemSDK.initializeSDK(Application application) in your Application class before using the SDK.");
        }

        Intent intent = new Intent(context, PDUIHomeFlowActivity.class);
        context.startActivity(intent);
    }

    /**
     * Show social login flow.
     *
     * @param activity FragmentActivity / AppCompatActivity initiating the social login flow
     */
    public static void showSocialLogin(final FragmentActivity activity) {
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
     * @param activityClass   AppCompatActivity / FragmentActivity class you want Social Login flow to appear in. e.g. MainActivity.class
     * @param numberOfPrompts Number of Login Prompts
     */
    public static void enableSocialLogin(@NonNull Class activityClass, final int numberOfPrompts) {
        if (!isPopdeemSDKInitialized()) {
            throw new PopdeemSDKNotInitializedException("Popdeem SDK is not initialized. Be sure to call PopdeemSDK.initializeSDK(Application application) in your Application class before using the SDK.");
        }

        PDPreferencesUtils.setNumberOfLoginAttempts(sApplication, numberOfPrompts);
        PDPreferencesUtils.setSocialLoginActivityName(sApplication, activityClass.getSimpleName());
    }


    /**
     * @param moment   Moment to log
     * @param callback
     */
    public static void logMoment(@NonNull String moment, @NonNull PDAPICallback<PDBasicResponse> callback) {
        if (PDSocialUtils.isLoggedInToFacebook() && PDUtils.getUserToken() != null) {
            PDAPIClient.instance().logMoment(moment, callback);
        } else {
            callback.failure(HttpURLConnection.HTTP_UNAUTHORIZED, new IllegalStateException("Not logged in."));
        }
    }


    /**
     * Set a Third Parry Token that will be used for custom backend integration
     *
     * @param thirdPartyToken String Token for Third Party service
     */
    public static void setThirdPartyToken(@NonNull String thirdPartyToken) {
        PDRealmThirdPartyToken tokenRealm = new PDRealmThirdPartyToken();
        tokenRealm.setId(0);
        tokenRealm.setToken(thirdPartyToken);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tokenRealm);
        realm.commitTransaction();

        PDRealmUserDetails userDetailsRealm = realm.where(PDRealmUserDetails.class).findFirst();

        if (userDetailsRealm != null && userDetailsRealm.getUserToken() != null) {
            PDRealmGCM gcmRealm = realm.where(PDRealmGCM.class).findFirst();
            String gcmToken = gcmRealm == null ? "" : gcmRealm.getRegistrationToken();

            PDRealmUserLocation userLocationRealm = realm.where(PDRealmUserLocation.class).findFirst();
            double lat = 0, lng = 0;
            if (userLocationRealm != null) {
                lat = userLocationRealm.getLatitude();
                lng = userLocationRealm.getLongitude();
            }

            PDAPIClient.instance().updateUserLocationAndDeviceToken(userDetailsRealm.getId(), gcmToken, String.valueOf(lat), String.valueOf(lng), new PDAPICallback<PDUser>() {
                @Override
                public void success(PDUser user) {
                    PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
                    userDetails.setUid(0);

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(userDetails);
                    realm.commitTransaction();
                    realm.close();
                }

                @Override
                public void failure(int statusCode, Exception e) {

                }
            });
        }

        realm.close();
    }


    /**
     * Push the Social Login Flow Fragment to the supplied Activity
     *
     * @param activity FragmentActivity / AppCompatActivity to show social login flow in
     */
    private static void pushSocialLoginFragmentToActivity(final FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(android.R.id.content, PDUISocialLoginFragment.newInstance())
                .addToBackStack(PDUISocialLoginFragment.class.getSimpleName())
                .commit();
    }


    public static void logout(@NonNull Context context) {
        // Facebook Logout
        LoginManager.getInstance().logOut();

        // Clear Shared Preferences
//        PDPreferencesUtils.clearPrefs(context);

        // Clear some DB data
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmResults<PDRealmUserDetails> userResults = realm.where(PDRealmUserDetails.class).findAll();
        userResults.deleteAllFromRealm();

        RealmResults<PDRealmUserFacebook> fbResults = realm.where(PDRealmUserFacebook.class).findAll();
        fbResults.deleteAllFromRealm();

        realm.commitTransaction();
        realm.close();

        // Broadcast to update rewards / wallet / etc
        context.sendBroadcast(new Intent(PDUIRewardsFragment.PD_LOGGED_IN_RECEIVER_FILTER));
    }


    /**
     * Used to keep track of the current Activity.
     */
    private static final Application.ActivityLifecycleCallbacks PD_ACTIVITY_LIFECYCLE_CALLBACKS = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // Show social login if needed
            if ((activity instanceof AppCompatActivity || activity instanceof FragmentActivity) && activity.getClass().getSimpleName().equalsIgnoreCase(PDPreferencesUtils.getSocialLoginActivityName(activity))
                    && PDSocialUtils.shouldShowSocialLogin(activity)) {
                PDLog.i(PopdeemSDK.class, "showing social login");
                PDPreferencesUtils.incrementLoginUsesCount(activity);
                showSocialLogin((FragmentActivity) activity);
            }

            // Check if intent was started from a Popdeem Notification click and show dialog if it was
            if (activity.getIntent() != null && activity.getIntent().getExtras() != null && (activity instanceof AppCompatActivity || activity instanceof FragmentActivity)) {
                String messageId = activity.getIntent().getExtras().getString(GCMIntentService.PD_NOTIFICATION_INTENT_MESSAGE_ID_KEY, null);
                String imageUrl = activity.getIntent().getExtras().getString(GCMIntentService.PD_NOTIFICATION_INTENT_IMAGE_URL_KEY, null);
                String targetUrl = activity.getIntent().getExtras().getString(GCMIntentService.PD_NOTIFICATION_INTENT_URL_KEY, null);
                String title = activity.getIntent().getExtras().getString(GCMIntentService.PD_NOTIFICATION_INTENT_TITLE_KEY, null);
                String message = activity.getIntent().getExtras().getString(GCMIntentService.PD_NOTIFICATION_INTENT_MESSAGE_KEY, null);

                if (messageId != null && title != null && message != null) {
                    FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
                    PDUINotificationDialogFragment.showNotificationDialog(fm, title, message, imageUrl, targetUrl, null, messageId);
                }
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
