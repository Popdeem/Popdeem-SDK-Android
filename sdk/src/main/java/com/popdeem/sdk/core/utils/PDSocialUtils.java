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
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.realm.PDRealmCustomer;
import com.popdeem.sdk.core.realm.PDRealmInstagramConfig;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import io.realm.Realm;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDSocialUtils {

//    public static final String[] FACEBOOK_READ_PERMISSIONS = {"public_profile", "email", "user_birthday", "user_posts", "user_friends"};
    public static final String[] FACEBOOK_READ_PERMISSIONS = {"public_profile", "email", "user_birthday", "user_posts"};
//    public static final String[] FACEBOOK_PUBLISH_PERMISSIONS = {"publish_actions"};

    private static final String FACEBOOK_APP_ID = "com.facebook.sdk.ApplicationId";

    public static final int TWITTER_CHARACTER_LIMIT = 180;
    public static final int TWITTER_DEFAULT_MEDIA_CHARACTERS_COUNT = 25;

    private static final String TWITTER_CONSUMER_KEY_META_KEY = "TwitterConsumerKey";
    private static final String TWITTER_CONSUMER_SECRET_META_KEY = "TwitterConsumerSecret";

    private static final String INSTAGRAM_CLIENT_ID_KEY = "InstagramClientId";
    private static final String INSTAGRAM_CLIENT_SECRET_KEY = "InstagramClientSecret";
    private static final String INSTAGRAM_CALLBACK_URL_KEY = "InstagramCallbackUrl";

    public static final String SOCIAL_TYPE_FACEBOOK = "facebook";
    public static final String SOCIAL_TYPE_TWITTER = "twitter";
    public static final String SOCIAL_TYPE_INSTAGRAM = "instagram";

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
     * Is Instagram config available for use
     *
     * @return true if available, false if not
     */
    public static boolean canUseInstagram() {
        Realm realm = Realm.getDefaultInstance();
        PDRealmInstagramConfig config = realm.where(PDRealmInstagramConfig.class).findFirst();
        boolean foundInstagramConfig = config != null;
        realm.close();
        return foundInstagramConfig;
    }

    public static boolean usesInstagram(Context context){
        Realm realm = Realm.getDefaultInstance();
        PDRealmCustomer customer = realm.where(PDRealmCustomer.class).findFirst();
        if(customer != null && customer.getInstagram_client_id() != null && customer.getInstagram_client_id().length() > 0  && customer.getInstagram_client_secret() != null && customer.getInstagram_client_secret().length() > 0) {
            realm.close();
            return true;
        }
        realm.close();
        return false;

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

    /**
     * Check if user is logged in to Instagram instantly
     */
    public static boolean isInstagramLoggedIn() {
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        String accessToken = null;
        if (userDetails != null && userDetails.getUserInstagram() != null) {
            accessToken = userDetails.getUserInstagram().getAccessToken();
        }
        realm.close();
        return(accessToken!=null&&!accessToken.equalsIgnoreCase(""));
    }

    /**
     * Check if user is logged in to Instagram
     */
    public static void isInstagramLoggedIn(@NonNull final PDAPICallback<Boolean> callback) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        String accessToken = null;
        if (userDetails != null && userDetails.getUserInstagram() != null) {
            accessToken = userDetails.getUserInstagram().getAccessToken();
        }
        realm.close();

        if (accessToken == null || accessToken.isEmpty()) {
            callback.success(false);
        } else {
            PDAPIClient.instance().checkInstagramAccessToken(accessToken, new PDAPICallback<Boolean>() {
                @Override
                public void success(Boolean success) {
                    callback.success(success);
                }

                @Override
                public void failure(int statusCode, Exception e) {
                    callback.success(false);
                }
            });
        }
    }


    /**
     * Check if user has Instagram installed
     *
     * @param pm PackageManger
     * @return true if Instagram is installed, false if it is not
     */
    public static boolean hasInstagramAppInstalled(PackageManager pm) {
        try {
            pm.getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Create a chooser Intent for sharing an Image file to Instagram
     *
     * @param path Path to Image file
     * @return Chooser Intent
     */
    public static Intent createInstagramIntent(Context context, String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.instagram.android");
        Log.i("PROVIDER", "createInstagramIntent: ");
        Uri sharedFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName()+".fileprovider", file);

        intent.putExtra(Intent.EXTRA_STREAM, sharedFileUri);
        return intent;
//        return Intent.createChooser(intent, "Share to");
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
//    public static boolean hasAllFacebookPublishPermissions() {
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (accessToken == null) {
//            return false;
//        }
//        Set<String> grantedPermissions = AccessToken.getCurrentAccessToken().getPermissions();
//        return grantedPermissions.containsAll(Arrays.asList(FACEBOOK_PUBLISH_PERMISSIONS));
//    }


    /**
     * Check if user is logged in to Facebook
     *
     * @return true if logged in, false if not
     */
    public static boolean isLoggedInToFacebook() {
        return AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired();
    }


    /**
     * Refresh Facebook Access Token.
     * According to Facebook's docs this may or may not extend the expiration date.
     * Must be called from the UI Thread.
     */
    public static void refreshFacebookAccessToken() {
        AccessToken.refreshCurrentAccessTokenAsync();
    }


    /**
     * Check if Facebook Access Token is valid.
     *
     * @param callback {@link PDAPICallback} with boolean. Boolean is true if Access Token is valid, false otherwise.
     */
    public static void validateFacebookAccessToken(@NonNull final PDAPICallback<Boolean> callback) {
        if (AccessToken.getCurrentAccessToken() == null) {
            callback.success(false);
            return;
        }
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                callback.success(!object.has("error"));
            }
        });
        request.executeAsync();
    }

    public static boolean usesFacebook(){
        Realm realm = Realm.getDefaultInstance();
        PDRealmCustomer customer = realm.where(PDRealmCustomer.class).findFirst();
        if(customer != null && customer.getFb_app_id() != null && customer.getFb_app_id().length() > 0 && customer.getFacebook_namespace() != null && customer.getFacebook_namespace().length() > 0) {
            realm.close();
            return true;
        }
        realm.close();
        return false;
    }

    /**
     * Get Facebook AppID from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getFacebookAppId(Context context) {


        final String clientSecret = PDUtils.getStringFromMetaData(context, FACEBOOK_APP_ID);

        if (clientSecret == null) {
            PDLog.e(PDSocialUtils.class, "Facebook Error: Please ensure you have your Instagram Client Secret in your AndroidManifest.xml\n" +
                    "<meta-data android:name=\"com.facebook.sdk.ApplicationId\" android:value=\"YOUR_FACEBOOK_ID\" />");
            return null;
        }
        return clientSecret;
    }

    /**
     * Get Facebook AppID from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getFacebookAppName(Context context) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmCustomer customer = realm.where(PDRealmCustomer.class).findFirst();

        if(customer == null || customer.getFacebook_namespace() == null || customer.getFb_app_id().length() == 0) {
            realm.close();
            return null;
        }
        String ret = customer.getFacebook_namespace();
        realm.close();
        return ret;

    }

    /**
     * Get Facebook AppID from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getFacebookAppToken(Context context) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmCustomer customer = realm.where(PDRealmCustomer.class).findFirst();

        if(customer == null || customer.getFb_app_id() == null || customer.getFb_app_id().length() == 0) {
            realm.close();
            return null;
        }
        String ret = customer.getFb_app_access_token();
        realm.close();
        return ret;
    }

    public static void initFacebook(Application application){
//        FacebookSdk.sdkInitialize(application);
        FacebookSdk.setApplicationId(getFacebookAppId(application));
        if(PDSocialUtils.getFacebookAppName(application)!=null) {
            FacebookSdk.setApplicationName(PDSocialUtils.getFacebookAppName(application));
        }

        if(PDSocialUtils.getFacebookAppToken(application)!=null) {
            FacebookSdk.setClientToken(PDSocialUtils.getFacebookAppToken(application));
        }
    }


    //------------------------------------------------------------------------
    //                          Twitter Methods
    //------------------------------------------------------------------------

    public static void initTwitter(Context context) {
//        Twitter twitterKit = getTwitterKitForFabric(context);
//        if (twitterKit != null) {
//            Fabric.with(context, twitterKit);
//        }
        TwitterConfig twitterConfig = getTwitterKitForFabric(context);
        if(twitterConfig!=null){
            Twitter.initialize(twitterConfig);
        }
    }

    public static TwitterConfig getTwitterKitForFabric(Context context) {



        TwitterAuthConfig twitterAuthConfig = getTwitterAuthConfig(context);
        if (twitterAuthConfig != null) {

            TwitterConfig twitterConfig = new TwitterConfig.Builder(context)
                    .logger(new DefaultLogger(Log.DEBUG))
                    .twitterAuthConfig(twitterAuthConfig)
                    .debug(true)
                    .build();

            return twitterConfig;
        }
        return null;
    }

    public static TwitterAuthConfig getTwitterAuthConfig(Context context) {
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
    public static TwitterAuthClient client;

    public static void loginWithTwitter(Activity activity, Callback<TwitterSession> callback) {
        if (isTwitterInitialised()) {
            Log.i("PDSocialUtils", "loginWithTwitter: Fabric is initialized with Twitter");
//            Twitter.getInstance().logIn(activity, callback);
            if(client != null) {
                client.cancelAuthorize();
                client = null;
            }
            client = new TwitterAuthClient();
            client.authorize(activity, callback);
        }
    }

    public static TwitterAuthClient getTwitterAuthClient(){
        return client;
    }

    public static boolean isTwitterLoggedIn() {

        boolean twitterInitialised = isTwitterInitialised();
        if(twitterInitialised) {
            boolean getActiveSession = (TwitterCore.getInstance().getSessionManager().getActiveSession() != null);
            if(getActiveSession) {
                boolean gotAuthToken = (TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken() != null);
                if(gotAuthToken){
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean userHasTwitterCredentials() {
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();

        boolean hasCreds = false;
        if (userDetails != null && userDetails.getUserTwitter() != null) {
            String accessToken = userDetails.getUserTwitter().getAccessToken();
            String accessSecret = userDetails.getUserTwitter().getAccessSecret();
            if (accessToken != null && !accessToken.isEmpty() && accessSecret != null && !accessSecret.isEmpty()) {
                hasCreds = true;
            }
        }
        realm.close();

        return hasCreds;
    }


    public static boolean usesTwitter(Context context) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmCustomer customer = realm.where(PDRealmCustomer.class).findFirst();
        if (customer != null && customer.getTwitter_consumer_key() != null && customer.getTwitter_consumer_key().length() > 0 && customer.getTwitter_consumer_secret() != null && customer.getTwitter_consumer_secret().length()> 0) {
            return true;
        } else {
            return false;
        }
//        return (customer != null && customer.getTwitter_consumer_key() != null && customer.getTwitter_consumer_key().length() > 0 && customer.getTwitter_consumer_secret() != null && customer.getTwitter_consumer_secret().length() > 0);
    }

    /**
     * Get Twitter Consumer Key from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getTwitterConsumerKey(Context context) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmCustomer customer = realm.where(PDRealmCustomer.class).findFirst();
        if(customer != null){
            Log.i("TWITTER_KEY", "customer.getTwitter_consumer_key(): "+ customer.getTwitter_consumer_key());
        }
        Log.i("TWITTER_KEY", "getTwitterConsumerKey MANIFEST: " + PDUtils.getStringFromMetaData(context, TWITTER_CONSUMER_KEY_META_KEY));
        String ret = PDUtils.getStringFromMetaData(context, TWITTER_CONSUMER_KEY_META_KEY);
        realm.close();
        return ret;
    }




    /**
     * Get Twitter Consumer Secret from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @return null if value does not exist, String value otherwise
     */
    public static String getTwitterConsumerSecret(Context context) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmCustomer customer = realm.where(PDRealmCustomer.class).findFirst();
        customer = null;
        if(customer == null || customer.getTwitter_consumer_secret() == null || customer.getTwitter_consumer_secret().length() == 0) {
            String ret = PDUtils.getStringFromMetaData(context, TWITTER_CONSUMER_SECRET_META_KEY);
            realm.close();
            return ret;

        }else{
            String ret = customer.getTwitter_consumer_secret();
            realm.close();
            return ret;
        }
    }

    private static boolean isTwitterInitialised() {
//        if (!Twitter.isInitialized()) {
//            PDLog.e(PDSocialUtils.class, "Fabric is not initialised");
//            return false;
//        }
//        if (Fabric.getKit(Twitter.class) == null) {
//            PDLog.e(PDSocialUtils.class, "Twitter is not initialised with Fabric");
//            return false;
//        }

        try{
            if(TwitterCore.getInstance()!=null) {
                return true;
            }
        }catch (IllegalStateException e){
            PDLog.e(PDSocialUtils.class, "Twitter is not initialised");
        }
//        if(TwitterCore.getInstance()==null){
//            return false;
//        }
        return false;
    }


    /**
     * Should the user be presented with the Social Login Flow.
     *
     * @param context Application Context
     * @return true if to be shown, false otherwise
     */
    public static boolean shouldShowSocialLogin(Context context) {

        boolean isFacebokLoggedIn = PDSocialUtils.isLoggedInToFacebook();
        boolean isTwitterLoggedIn = PDSocialUtils.isTwitterLoggedIn();
        boolean isInstagramLoggedIn = PDSocialUtils.isInstagramLoggedIn();
        String token = PDUtils.getUserToken();
        return !((isFacebokLoggedIn || isInstagramLoggedIn || isTwitterLoggedIn) && token != null) && PDPreferencesUtils.getLoginUsesCount(context) < PDPreferencesUtils.getNumberOfLoginAttempts(context);
    }

}
