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

package com.popdeem.sdk.core.utils;

import android.app.Activity;
import android.content.Context;

import com.facebook.AccessToken;
import com.popdeem.sdk.core.realm.PDRealmInstagramConfig;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Arrays;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDSocialUtils {

    public static final String[] FACEBOOK_READ_PERMISSIONS = {"public_profile", "email", "user_birthday", "user_posts", "user_friends", "user_education_history"};
    public static final String[] FACEBOOK_PUBLISH_PERMISSIONS = {"publish_actions"};

    public static final int TWITTER_CHARACTER_LIMIT = 140;
    public static final int TWITTER_DEFAULT_MEDIA_CHARACTERS_COUNT = 25;

    private static final String TWITTER_CONSUMER_KEY_META_KEY = "TwitterConsumerKey";
    private static final String TWITTER_CONSUMER_SECRET_META_KEY = "TwitterConsumerSecret";

    private static final String INSTAGRAM_CLIENT_ID_KEY = "InstagramClientId";
    private static final String INSTAGRAM_CLIENT_SECRET_KEY = "InstagramClientSecret";
    private static final String INSTAGRAM_CALLBACK_URL_KEY = "InstagramCallbackUrl";

    //------------------------------------------------------------------------
    //                          Instagram Methods
    //------------------------------------------------------------------------

    /**
     * Initialise Instagram config.
     * This will gather the Instagram client ID, secret and callback URL to be used when authenticating an Instagram user.
     *
     * @param context Application Context
     */
    public static void initInstagram(Context context) {
        final String clientId = getInstagramClientId(context);
        final String clientSecret = getInstagramClientSecret(context);
        final String callbackUrl = getInstagramCallbackUrl(context);

        if (clientId == null || clientSecret == null || callbackUrl == null) {
            PDLog.w(PDSocialUtils.class, "Could not initialise Instagram");
            return;
        }

        PDRealmInstagramConfig config = new PDRealmInstagramConfig();
        config.setUid(0);
        config.setInstagramClientId(clientId);
        config.setInstagramClientSecret(clientSecret);
        config.setInstagramCallbackUrl(callbackUrl);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(config);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * Get Instagram Client ID from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getInstagramClientId(Context context) {
        final String clientId = PDUtils.getStringFromMetaData(context, INSTAGRAM_CLIENT_ID_KEY);
        if (clientId == null) {
            PDLog.e(PDSocialUtils.class, "Instagram Error: Please ensure you have your Instagram Client ID in your AndroidManifest.xml\n" +
                    "<meta-data android:name=\"InstagramClientId\" android:value=\"YOUR_INSTAGRAM_CLIENT_ID\" />");
            return null;
        }
        return clientId;
    }

    /**
     * Get Instagram Client Secret from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getInstagramClientSecret(Context context) {
        final String clientSecret = PDUtils.getStringFromMetaData(context, INSTAGRAM_CLIENT_SECRET_KEY);
        if (clientSecret == null) {
            PDLog.e(PDSocialUtils.class, "Instagram Error: Please ensure you have your Instagram Client Secret in your AndroidManifest.xml\n" +
                    "<meta-data android:name=\"InstagramClientSecret\" android:value=\"YOUR_INSTAGRAM_CLIENT_SECRET\" />");
            return null;
        }
        return clientSecret;
    }

    /**
     * Get Instagram Client Secret from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getInstagramCallbackUrl(Context context) {
        final String callbackUrl = PDUtils.getStringFromMetaData(context, INSTAGRAM_CALLBACK_URL_KEY);
        if (callbackUrl == null) {
            PDLog.e(PDSocialUtils.class, "Instagram Error: Please ensure you have your Instagram Callback URL in your AndroidManifest.xml\n" +
                    "<meta-data android:name=\"InstagramCallbackUrl\" android:value=\"YOUR_INSTAGRAM_CALLBACK_URL\" />");
            return null;
        }
        return callbackUrl;
    }


    //------------------------------------------------------------------------
    //                          Facebook Methods
    //------------------------------------------------------------------------

    /**
     * Check if user has granted all Facebook Read Permissions
     *
     * @return true if permissions are granted, false if not
     */
    public static boolean hasAllFacebookReadPermissions() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        }
        Set<String> grantedPermissions = AccessToken.getCurrentAccessToken().getPermissions();
        return grantedPermissions.containsAll(Arrays.asList(FACEBOOK_READ_PERMISSIONS));
    }


    /**
     * Check if user has granted all Facebook Publish Permissions
     *
     * @return true if permissions are granted, false if not
     */
    public static boolean hasAllFacebookPublishPermissions() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        }
        Set<String> grantedPermissions = AccessToken.getCurrentAccessToken().getPermissions();
        return grantedPermissions.containsAll(Arrays.asList(FACEBOOK_PUBLISH_PERMISSIONS));
    }


    /**
     * Check if user is logged in to Facebook
     *
     * @return true if logged in, false if not
     */
    public static boolean isLoggedInToFacebook() {
        return AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired();
    }


    //------------------------------------------------------------------------
    //                          Twitter Methods
    //------------------------------------------------------------------------

    public static void initTwitter(Context context) {
        Twitter twitterKit = getTwitterKitForFabric(context);
        if (twitterKit != null) {
            Fabric.with(context, twitterKit);
        }
    }

    public static Twitter getTwitterKitForFabric(Context context) {
        TwitterAuthConfig twitterAuthConfig = getTwitterAuthConfig(context);
        if (twitterAuthConfig != null) {
            return new Twitter(twitterAuthConfig);
        }
        return null;
    }

    private static TwitterAuthConfig getTwitterAuthConfig(Context context) {
        final String consumerKey = getTwitterConsumerKey(context);
        final String consumerSecret = getTwitterConsumerSecret(context);

        if (consumerKey == null || consumerSecret == null) {
            if (consumerKey == null) {
                PDLog.e(PDSocialUtils.class, "Twitter Error: Please ensure you have your Twitter Consumer Key in your AndroidManifest.xml\n" +
                        "<meta-data android:name=\"TwitterConsumerKey\" android:value=\"YOUR_TWITTER_CONSUMER_KEY\" />");
            }
            if (consumerSecret == null) {
                PDLog.e(PDSocialUtils.class, "Twitter Error: Please ensure you have your Twitter Consumer Secret in your AndroidManifest.xml\n" +
                        "<meta-data android:name=\"TwitterConsumerSecret\" android:value=\"YOUR_TWITTER_CONSUMER_SECRET\" />");
            }
            return null;
        }

        return new TwitterAuthConfig(consumerKey, consumerSecret);
    }

    public static void loginWithTwitter(Activity activity, Callback<TwitterSession> callback) {
        if (isFabricInitialisedWithTwitter()) {
            Twitter.logIn(activity, callback);
        }
    }

    public static boolean isTwitterLoggedIn() {
        return isFabricInitialisedWithTwitter() && Twitter.getSessionManager().getActiveSession() != null && Twitter.getSessionManager().getActiveSession().getAuthToken() != null;
    }

    /**
     * Get Twitter Consumer Key from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getTwitterConsumerKey(Context context) {
        return PDUtils.getStringFromMetaData(context, TWITTER_CONSUMER_KEY_META_KEY);
    }


    /**
     * Get Twitter Consumer Secret from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getTwitterConsumerSecret(Context context) {
        return PDUtils.getStringFromMetaData(context, TWITTER_CONSUMER_SECRET_META_KEY);
    }

    private static boolean isFabricInitialisedWithTwitter() {
        if (!Fabric.isInitialized()) {
            PDLog.e(PDSocialUtils.class, "Fabric is not initialised");
            return false;
        }
        if (Fabric.getKit(Twitter.class) == null) {
            PDLog.e(PDSocialUtils.class, "Twitter is not initialised with Fabric");
            return false;
        }
        return true;
    }


    /**
     * Should the user be presented with the Social Login Flow.
     *
     * @param context Application Context
     * @return true if to be shown, false otherwise
     */
    public static boolean shouldShowSocialLogin(Context context) {
        return !(PDSocialUtils.isLoggedInToFacebook() && PDUtils.getUserToken() != null) && PDPreferencesUtils.getLoginUsesCount(context) < PDPreferencesUtils.getNumberOfLoginAttempts(context);
    }

}
