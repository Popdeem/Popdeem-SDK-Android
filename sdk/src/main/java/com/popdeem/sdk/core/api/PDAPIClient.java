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

package com.popdeem.sdk.core.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.retrofit.Ok3Client;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.api.response.PDBasicResponse;
import com.popdeem.sdk.core.deserializer.PDBGScanResponseDeserializer;
import com.popdeem.sdk.core.deserializer.PDBrandsDeserializer;
import com.popdeem.sdk.core.deserializer.PDFeedsDeserializer;
import com.popdeem.sdk.core.deserializer.PDInstagramUserDeserializer;
import com.popdeem.sdk.core.deserializer.PDMessagesDeserializer;
import com.popdeem.sdk.core.deserializer.PDRewardsDeserializer;
import com.popdeem.sdk.core.deserializer.PDTwitterUserDeserializer;
import com.popdeem.sdk.core.deserializer.PDUserDeserializer;
import com.popdeem.sdk.core.exception.PopdeemSDKNotInitializedException;
import com.popdeem.sdk.core.model.PDBGScanResponseModel;
import com.popdeem.sdk.core.model.PDBrand;
import com.popdeem.sdk.core.model.PDFeed;
import com.popdeem.sdk.core.model.PDMessage;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmNonSocialUID;
import com.popdeem.sdk.core.realm.PDRealmReferral;
import com.popdeem.sdk.core.realm.PDRealmThirdPartyToken;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

/**
 * PDAPIClient is used to access the Popdeem API.
 * You access this class using the {@link #instance()}.
 * <p>
 * Created by mikenolan on 18/02/16.
 * </p>
 */
public class PDAPIClient {

    /**
     * {@link Interceptor} for adding Popdeem API Key to request headers.
     */
    private static final Interceptor PD_API_KEY_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Log.i("Popdeem API Key", "intercept: " + PopdeemSDK.getPopdeemAPIKey());
            return chain.proceed(chain.request().newBuilder()
                    .addHeader(PDAPIConfig.REQUEST_HEADER_API_KEY, PopdeemSDK.getPopdeemAPIKey())
                    .build());
        }
    };


//    private static Interceptor PD_USER_TOKEN_INTERCEPTOR = null;
//    private static String userToken = null;


    /**
     * Private empty constructor to stop instances being created using "new"
     */
    private PDAPIClient() {
    }


    /**
     * Create an instance of {@link PDAPIClient} to access Popdeem API.
     * <p>
     * Popdeem SDK must be initialized using {@link PopdeemSDK#initializeSDK(Application)} and your Popdeem API Key must be present in your Application's AndroidManifest.xml before creating an instance of {@link PDAPIClient}.
     * </p>
     *
     * @return instance of {@link PDAPIClient}
     */
    public static PDAPIClient instance() {
        if (!PopdeemSDK.isPopdeemSDKInitialized()) {
            throw new PopdeemSDKNotInitializedException("Popdeem SDK is not initialized. Be sure to call PopdeemSDK.initializeSDK(Application application) in your Application class before using the SDK.");
        }
        PDUtils.validateAPIKeyIsPresent();
//        if (userToken == null) {
//            userToken = PDUtils.getUserToken();
//        }
        return new PDAPIClient();
    }


    /**
     * Create a Non Social User. This user will be updated upon a social login.
     *
     * @param uid         Unique Identifier for user
     * @param deviceToken Device Token for GCM Push
     * @param callback    {@link PDAPICallback} for API result
     */
    public void createNonSocialUser(@NonNull String uid, String deviceToken, @NonNull final PDAPICallback<PDBasicResponse> callback) {
        PopdeemAPI api = getApiInterface(null, null);
        api.createNonSocialUser("", uid, deviceToken, PDAPIConfig.PLATFORM_VALUE, callback);
    }


    /**
     * Register a user using the Facebook Access Token and Facebook App ID from Login
     * <p>
     * If user is a new user, then a new user will be created
     * If not, user will be loaded
     * In both cases response is a User object
     * </p>
     *
     * @param facebookAccessToken Access Token {@link String} received on successful Facebook login
     * @param facebookUserID      Facebook Application ID received on successful Facebook login
     * @param callback            {@link PDAPICallback} for API result
     */
    public void registerUserWithFacebook(@NonNull final String facebookAccessToken, @NonNull final String facebookUserID, @NonNull final PDAPICallback<PDUser> callback) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDUserDeserializer())
                .create();

        Realm realm = Realm.getDefaultInstance();
        final PDRealmNonSocialUID uid = realm.where(PDRealmNonSocialUID.class).findFirst();
        final String uidString = uid == null ? null : uid.getUid();
        realm.close();

        PopdeemAPI api = getApiInterface(null, new GsonConverter(gson));
        api.registerUserWithFacebook("", facebookAccessToken, facebookUserID, uidString, callback);
    }


    /**
     * Register a user using the Facebook Access Token and Facebook App ID from Login
     * <p>
     * If user is a new user, then a new user will be created
     * If not, user will be loaded
     * In both cases response is a User object
     * </p>
     *
     * @param context             Application {@link Context}
     * @param facebookAccessToken Access Token {@link String} received on successful Facebook login
     * @param facebookUserID      Facebook Application ID received on successful Facebook login
     * @param callback            {@link PDAPICallback} for API result
     * @deprecated use {@link #registerUserWithFacebook(String, String, PDAPICallback)} instead
     */
    @Deprecated
    public void registerUserWithFacebookAccessToken(@NonNull final Context context, @NonNull final String facebookAccessToken,
                                                    @NonNull final String facebookUserID, @NonNull final PDAPICallback<PDUser> callback) {
//        PDDataManager.setFacebookAccessTokenProperty(context, facebookAccessToken);

        Realm realm = Realm.getDefaultInstance();
        final PDRealmNonSocialUID uid = realm.where(PDRealmNonSocialUID.class).findFirst();
        final String uidString = uid == null ? null : uid.getUid();
        realm.close();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PDAPIConfig.PD_API_ENDPOINT + PDAPIConfig.PD_USERS_PATH);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty(PDAPIConfig.REQUEST_HEADER_API_KEY, PopdeemSDK.getPopdeemAPIKey());

                    ArrayList<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry<>("user[facebook][access_token]", facebookAccessToken));
                    params.add(new AbstractMap.SimpleEntry<>("user[facebook][id]", facebookUserID));
                    if (uidString != null && !uidString.isEmpty()) {
                        params.add(new AbstractMap.SimpleEntry<>("user[unique_identifier]", uid.getUid()));
                    }

                    StringBuilder result = new StringBuilder();
                    boolean first = true;

                    for (AbstractMap.SimpleEntry<String, String> map : params) {
                        if (first) {
                            first = false;
                        } else {
                            result.append("&");
                        }

                        result.append(URLEncoder.encode(map.getKey(), "UTF-8"));
                        result.append("=");
                        result.append(URLEncoder.encode(map.getValue(), "UTF-8"));
                    }

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    connection.connect();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    final StringBuilder response = new StringBuilder();
                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new GsonBuilder().registerTypeAdapter(PDUser.class, new PDUserDeserializer()).create();
                            PDUser user = gson.fromJson(response.toString(), PDUser.class);
                            callback.success(user);
                        }
                    });
                    connection.disconnect();
                } catch (final IOException e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.failure(400, e);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Connects a user's Facebook account - if already registered with another social medium
     *
     * @param userID
     * @param accessToken
     */
    public void connectFacebookAccount(@NonNull long userID, @NonNull String accessToken, @NonNull final PDAPICallback<PDUser> callback){
        JsonObject facebookObject = new JsonObject();
        facebookObject.addProperty("id", userID);
        facebookObject.addProperty("access_token", accessToken);

        JsonObject userObject = new JsonObject();
        userObject.add("facebook", facebookObject);

        JsonObject json = new JsonObject();
        json.add("user", userObject);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, json.toString().getBytes());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDUserDeserializer())
                .create();

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.connectFacebookAccount(body, callback);
    }


    /**
     * Connect a users Twitter account
     *
     * @param userID     Twitter User ID
     * @param userToken  Twitter User Token
     * @param userSecret Twitter User Secret
     * @param callback   {@link PDAPICallback} for API result
     */
    public void connectWithTwitterAccount(@NonNull String userID, @NonNull String userToken, @NonNull String userSecret, @NonNull final PDAPICallback<PDUser> callback) {
        JsonObject twitterObject = new JsonObject();
        twitterObject.addProperty("social_id", userID);
        twitterObject.addProperty("access_token", userToken);
        twitterObject.addProperty("access_secret", userSecret);

        JsonObject userJson = new JsonObject();
        userJson.add("twitter", twitterObject);

        JsonObject json = new JsonObject();
        json.add("user", userJson);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, json.toString().getBytes());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDTwitterUserDeserializer())
                .create();

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.connectWithTwitterAccount(body, callback);
    }


    /**
     * Connect a users Instagram Account
     *
     * @param userId      Instagram User ID
     * @param accessToken Instagram Access Token
     * @param screenName  Users Screen Name
     * @param callback    {@link PDAPICallback} for API result
     */
    public void connectWithInstagramAccount(@NonNull String userId, @NonNull String accessToken, @NonNull String screenName, @NonNull final PDAPICallback<PDUser> callback) {
        JsonObject instagramObject = new JsonObject();
        instagramObject.addProperty("id", userId);
        instagramObject.addProperty("access_token", accessToken);
        instagramObject.addProperty("screen_name", screenName);

        JsonObject userJson = new JsonObject();
        userJson.add("instagram", instagramObject);

        JsonObject json = new JsonObject();
        json.add("user", userJson);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, json.toString().getBytes());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDInstagramUserDeserializer())
                .create();

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.connectWithInstagramAccount(body, callback);
    }

    /**
     *
     * @param instagramId       Instagram User ID
     * @param accessToken       Instagram Access Token
     * @param fullname          User's Full Name
     * @param userName          User's Screen Name
     * @param profilePicture    User's Profile Picture
     * @param callback          {@link PDAPICallback} for API Result
     */
    public void registerWithInstagramId(@NonNull String instagramId,
                                        @NonNull String accessToken,
                                        @NonNull String fullname,
                                        @NonNull String userName,
                                        @NonNull String profilePicture,
                                        @NonNull final PDAPICallback<PDUser> callback){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDInstagramUserDeserializer())
                .create();

        Realm realm = Realm.getDefaultInstance();
        final PDRealmNonSocialUID uid = realm.where(PDRealmNonSocialUID.class).findFirst();
        final String uidString = uid == null ? null : uid.getUid();
        realm.close();

        JsonObject jsonBody = new JsonObject();

        JsonObject userBody = new JsonObject();

        JsonObject instaBody = new JsonObject();
        instaBody.addProperty("id", instagramId);
        instaBody.addProperty("access_token", accessToken);
        instaBody.addProperty("full_name", fullname);
        instaBody.addProperty("profile_picture", profilePicture);

        userBody.add("instagram", instaBody);

        userBody.addProperty("unique_identifier", uidString);

        jsonBody.add("user", userBody);


        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, jsonBody.toString().getBytes());
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));

        api.registerUserWithInstagram(body, callback);

    }

    /**
     * Check if users Instagram access token is still valid
     *
     * @param accessToken Instagram Access token to check
     * @param callback    {@link PDAPICallback} for API result
     */
    public void checkInstagramAccessToken(@NonNull String accessToken, @NonNull final PDAPICallback<Boolean> callback) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(PDAPIConfig.PD_INSTAGRAM_PATH + "/users/self?access_token=" + accessToken)
                .get()
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failure(400, e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                ResponseBody body = response.body();
                callback.success(response.code() == 200);
                body.close();
            }
        });
    }


    /**
     * Disconnect users Twitter account
     *
     * @param accessToken  Twitter access token
     * @param accessSecret Twitter access secret
     * @param twitterId    Users Twitter account ID
     * @param callback     {@link PDAPICallback} for API result
     */
    public void disconnectTwitterAccount(String accessToken, String accessSecret, String twitterId, @NonNull final PDAPICallback<PDUser> callback) {
        JsonObject twitterJson = new JsonObject();
        twitterJson.addProperty("access_token", accessToken);
        twitterJson.addProperty("access_secret", accessSecret);
        twitterJson.addProperty("id", twitterId);

        JsonObject userJson = new JsonObject();
        userJson.add("twitter", twitterJson);

        JsonObject jsonBody = new JsonObject();
        jsonBody.add("user", userJson);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, jsonBody.toString().getBytes());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDTwitterUserDeserializer())
                .create();

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.disconnectSocialAccount(body, callback);
    }


    /**
     * Disconnect users Instagram Account
     *
     * @param accessToken Instagram access token
     * @param instagramId Users Instagram account ID
     * @param screenName  Users Instagram screen name
     * @param callback    {@link PDAPICallback} for API result
     */
    public void disconnectInstagramAccount(String accessToken, String instagramId, String screenName, @NonNull final PDAPICallback<PDUser> callback) {
        JsonObject twitterJson = new JsonObject();
        twitterJson.addProperty("access_token", accessToken);
        twitterJson.addProperty("screen_name", screenName);
        twitterJson.addProperty("id", instagramId);

        JsonObject userJson = new JsonObject();
        userJson.add("instagram", twitterJson);

        JsonObject jsonBody = new JsonObject();
        jsonBody.add("user", userJson);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, jsonBody.toString().getBytes());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDInstagramUserDeserializer())
                .create();

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.disconnectSocialAccount(body, callback);
    }

    public void disconnectFacebookAccount(@NonNull final String facebookAccessToken,
                                          @NonNull final String facebookUserID,
                                          @NonNull final PDAPICallback<PDUser> callback) {
        JsonObject facebookObject = new JsonObject();
        facebookObject.addProperty("id", facebookUserID);
        facebookObject.addProperty("access_token", facebookAccessToken);

        JsonObject userObject = new JsonObject();
        userObject.add("facebook", facebookObject);

        JsonObject json = new JsonObject();
        json.add("user", userObject);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, json.toString().getBytes());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDUserDeserializer())
                .create();

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.disconnectSocialAccount(body, callback);
    }


    /**
     * Update the Users Location and Device token
     *
     * @param id          - User ID
     * @param deviceToken - Device token
     * @param latitude    - Current latitude for user
     * @param longitude   - Current longitude for user
     * @param callback    {@link PDAPICallback} for API result
     */
    public void updateUserLocationAndDeviceToken(@NonNull String socialType, @NonNull String id, @NonNull String deviceToken,
                                                 @NonNull String latitude, @NonNull String longitude,
                                                 @NonNull final PDAPICallback<PDUser> callback) {
        Gson gson;
        if (socialType.equalsIgnoreCase(PDSocialUtils.SOCIAL_TYPE_FACEBOOK)){
            gson = new GsonBuilder()
                    .registerTypeAdapter(PDUser.class, new PDUserDeserializer())
                    .create();
        } else if (socialType.equalsIgnoreCase(PDSocialUtils.SOCIAL_TYPE_TWITTER)){
            gson = new GsonBuilder()
                    .registerTypeAdapter(PDUser.class, new PDTwitterUserDeserializer())
                    .create();
        } else if (socialType.equalsIgnoreCase(PDSocialUtils.SOCIAL_TYPE_INSTAGRAM)){
            gson = new GsonBuilder()
                    .registerTypeAdapter(PDUser.class, new PDInstagramUserDeserializer())
                    .create();
        } else {
            gson = new GsonBuilder()
                    .registerTypeAdapter(PDUser.class, new PDUserDeserializer())
                    .create();
        }

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));

        // Realm Instance
        Realm realm = Realm.getDefaultInstance();

        // Non Social UID
        PDRealmNonSocialUID uidRealm = realm.where(PDRealmNonSocialUID.class).findFirst();
        String uid = uidRealm == null ? null : uidRealm.getUid();

        // Third Party Token
        PDRealmThirdPartyToken thirdPartyTokenRealm = realm.where(PDRealmThirdPartyToken.class).findFirst();
        String thirdPartToken = thirdPartyTokenRealm == null ? null : thirdPartyTokenRealm.getToken();

        // Check if we have a PDReferral
        PDRealmReferral referral = realm.where(PDRealmReferral.class).findFirst();
        if (referral != null) {
            // Referral present, send with referral
            api.updateUserLocationAndDeviceTokenWithReferral("", id, PDAPIConfig.PLATFORM_VALUE, deviceToken, latitude, longitude, uid,
                    String.valueOf(referral.getSenderId()), referral.getType(), referral.getSenderAppName(), String.valueOf(referral.getRequestId()), thirdPartToken, new PDAPICallback<PDUser>() {
                        @Override
                        public void success(PDUser user) {
                            Realm realm = Realm.getDefaultInstance();
                            PDRealmReferral referral = realm.where(PDRealmReferral.class).findFirst();
                            realm.beginTransaction();
                            referral.deleteFromRealm();
                            realm.commitTransaction();
                            realm.close();
                            callback.success(user);
                        }

                        @Override
                        public void failure(int statusCode, Exception e) {
                            callback.failure(statusCode, e);
                        }
                    });
        } else {
            // No referrals, send as normal
            api.updateUserLocationAndDeviceToken("", id, PDAPIConfig.PLATFORM_VALUE, deviceToken, latitude, longitude, uid, thirdPartToken, callback);
        }

        // Close Realm
        realm.close();
    }


    /**
     * Update the Users Location and Device token.
     *
     * @param context     Application {@link Context}
     * @param id          User ID
     * @param deviceToken Device token
     * @param latitude    Current latitude for user
     * @param longitude   Current longitude for user
     * @param callback    {@link PDAPICallback} for API result
     * @deprecated use {@link #updateUserLocationAndDeviceToken(String, String, String, String, String, PDAPICallback)} instead.
     */
    @Deprecated
    public void updateUserLocationAndDeviceToken(@NonNull final Context context, @NonNull final String id, @NonNull final String deviceToken,
                                                 @NonNull final String latitude, @NonNull final String longitude,
                                                 @NonNull final PDAPICallback<String> callback) {
        Realm realm = Realm.getDefaultInstance();
        final PDRealmNonSocialUID uid = realm.where(PDRealmNonSocialUID.class).findFirst();
        final String uidString = uid == null ? null : uid.getUid();
        realm.close();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PDAPIConfig.PD_API_ENDPOINT + PDAPIConfig.PD_USERS_PATH + "/" + id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("PUT");
                    connection.setConnectTimeout(15000);
                    connection.setRequestProperty(PDAPIConfig.REQUEST_HEADER_API_KEY, PopdeemSDK.getPopdeemAPIKey());
                    connection.setRequestProperty(PDAPIConfig.REQUEST_HEADER_USER_TOKEN, PDUtils.getUserToken());

                    ArrayList<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry<>("user[platform]", PDAPIConfig.PLATFORM_VALUE));
                    params.add(new AbstractMap.SimpleEntry<>("user[device_token]", deviceToken));
                    params.add(new AbstractMap.SimpleEntry<>("user[latitude]", latitude));
                    params.add(new AbstractMap.SimpleEntry<>("user[longitude]", longitude));
                    if (uidString != null && !uidString.isEmpty()) {
                        params.add(new AbstractMap.SimpleEntry<>("user[unique_identifier]", uid.getUid()));
                    }

                    StringBuilder result = new StringBuilder();
                    boolean first = true;

                    for (AbstractMap.SimpleEntry<String, String> map : params) {
                        if (first) {
                            first = false;
                        } else {
                            result.append("&");
                        }

                        result.append(URLEncoder.encode(map.getKey(), "UTF-8"));
                        result.append("=");
                        result.append(URLEncoder.encode(map.getValue(), "UTF-8"));
                    }

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    connection.connect();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    final StringBuilder response = new StringBuilder();

                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }

                    callback.success(response.toString());
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(response.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.failure(400, e);
                        }
                    });
                }
            }
        }).start();
    }


    /**
     * Get details for User ID
     * (Method will be public once it is tested)
     *  @param id       User ID
     * @param callback {@link PDAPICallback} for API result
     */
    public void getUserDetailsForId(@NonNull String id, @NonNull PDAPICallback<JsonObject> callback) {
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.getUserDetailsForId(id, callback);
    }


    /**
     * Register user with Twitter
     * (Method will be public once it is tested)
     *
     * @param twitterAccessToken  - Twitter access token
     * @param twitterAccessSecret - Twitter access  secret
     * @param twitterID           - Twitter ID
     * @param callback            {@link PDAPICallback} for API result
     */
    public void registerUserwithTwitterParams(@NonNull String twitterAccessToken, @NonNull String twitterAccessSecret,
                                               @NonNull String twitterID,
                                               @NonNull PDAPICallback<PDUser> callback) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDUser.class, new PDTwitterUserDeserializer())
                .create();

        Realm realm = Realm.getDefaultInstance();
        final PDRealmNonSocialUID uid = realm.where(PDRealmNonSocialUID.class).findFirst();
        final String uidString = uid == null ? null : uid.getUid();
        realm.close();

        JsonObject jsonBody = new JsonObject();

        JsonObject userBody = new JsonObject();

        JsonObject twitterBody = new JsonObject();
        twitterBody.addProperty("id", twitterID);
        twitterBody.addProperty("access_token", twitterAccessToken);
        twitterBody.addProperty("access_secret", twitterAccessSecret);

        userBody.add("twitter", twitterBody);

        userBody.addProperty("unique_identifier", uidString);

        jsonBody.add("user", userBody);


        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, jsonBody.toString().getBytes());
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));

        api.registerUserWithTwitterParams(body, callback);
    }

    /**
     * Get Popdeem Friends
     * (Method will be public once it is tested)
     *
     * @param id
     * @param callback {@link PDAPICallback} for API result
     */
    private void getPopdeemFriends(@NonNull String id, @NonNull PDAPICallback<JsonObject> callback) {
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.getPopdeemFriends(id, callback);
    }

    /**
     * Get Customer details
     * (Method will be public once it is tested)
     *
     * @param callback {@link PDAPICallback} for API result
     */
    public void getCustomer(@NonNull PDAPICallback<JsonObject> callback) {
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.getCustomer(callback);
    }


    /**
     * Get Brands
     *
     * @param callback {@link PDAPICallback} for API result
     */
    public void getBrands(@NonNull PDAPICallback<ArrayList<PDBrand>> callback) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDBrandsDeserializer.BRANDS_TYPE, new PDBrandsDeserializer())
                .create();
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.getBrands(callback);
    }


    /**
     * @param brandID
     * @param callback {@link PDAPICallback} for API result
     */
    public void getRewardsForBrandID(@NonNull String brandID, @NonNull PDAPICallback<ArrayList<PDReward>> callback) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDRewardsDeserializer.REWARDS_TYPE, new PDRewardsDeserializer())
                .create();
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.getRewardsForBrandID(brandID, callback);
    }


    /**
     * Get all rewards
     * <p>
     * Request all rewards which are relevant to user.
     * </p>
     *
     * @param callback {@link PDAPICallback} for API result
     */
    public void getAllRewards(@NonNull final PDAPICallback<ArrayList<PDReward>> callback) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDRewardsDeserializer.REWARDS_TYPE, new PDRewardsDeserializer())
                .create();
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.getAllRewards(callback);
    }


    /**
     * Claim a reward and post social action
     *
     * @param context             - @NonNull Application {@link Context}
     * @param facebookAccessToken - @NonNull Facebook Access Token {@link String}
     * @param rewardId            - Reward ID {@link String}
     * @param message             - Message {@link String}
     * @param taggedFriendsNames  - {@link String} {@link ArrayList} of tagged Friends.
     * @param taggedFriendsIds    - {@link String} {@link ArrayList} of tagged Friend IDs.
     * @param image               - {@link android.util.Base64} encoded {@link String}. The image data must be Base64 encoded. If left null, message will only be posted.
     * @param longitude           - Current longitude for user
     * @param latitude            - Current latitude for user
     * @param callback            {@link PDAPICallback} for API result
     */
    public void claimReward(@NonNull final Context context, final String facebookAccessToken, final String twitterAuthToken, final String twitterSecret, final String instagramAccessToken,
                            @NonNull final String rewardId, @NonNull final String message, ArrayList<String> taggedFriendsNames, ArrayList<String> taggedFriendsIds,
                            final String image, @NonNull final String longitude, @NonNull final String latitude, @NonNull final PDAPICallback<JsonObject> callback) {
        final Handler mainHandler = new Handler(context.getMainLooper());
        final String url = PDAPIConfig.PD_API_ENDPOINT + PDAPIConfig.PD_REWARDS_PATH + "/" + rewardId + "/claim";

        final OkHttpClient client = new OkHttpClient();
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("message", message)
                .add("location[latitude]", latitude)
                .add("location[longitude]", longitude);

        if (facebookAccessToken != null) {
            bodyBuilder.add("facebook[access_token]", facebookAccessToken);
            if (taggedFriendsIds != null && taggedFriendsNames != null && taggedFriendsIds.size() == taggedFriendsNames.size() && taggedFriendsIds.size() > 0 && taggedFriendsNames.size() > 0) {
                for (int i = 0; i < taggedFriendsIds.size(); i++) {
                    String name = taggedFriendsNames.get(i);
                    String id = taggedFriendsIds.get(i);

                    bodyBuilder.add("facebook[associated_account_ids][][name]", name);
                    bodyBuilder.add("facebook[associated_account_ids][][id]", id);
                }
            }
        }
        if (twitterAuthToken != null) {
            bodyBuilder.add("twitter[access_token]", twitterAuthToken);
            bodyBuilder.add("twitter[access_secret]", twitterSecret);
        }
        if (instagramAccessToken != null) {
            bodyBuilder.add("instagram[access_token]", instagramAccessToken);
        }

        if (image != null) {
            bodyBuilder.add("file", image);
        }

        RequestBody body = bodyBuilder.build();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader(PDAPIConfig.REQUEST_HEADER_API_KEY, PopdeemSDK.getPopdeemAPIKey())
                .addHeader(PDAPIConfig.REQUEST_HEADER_USER_TOKEN, PDUtils.getUserToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failure(RetrofitError.networkError(url, new IOException("Unexpected code " + e)));
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                // Convert OkHTTP response to Retrofit Response
                final String responseBody = response.body().string();
                final JsonElement object = new JsonParser().parse(responseBody);
                final TypedInput bodyTypedInput = new TypedString(responseBody);
                final ArrayList<Header> headers = new ArrayList<>();

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    headers.add(new Header(responseHeaders.name(i), responseHeaders.value(i)));
                }

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.success(object.isJsonNull() ? null : object.getAsJsonObject(), new Response(response.request().url().toString(), response.networkResponse().code(), response.networkResponse().message(), headers, bodyTypedInput));
                    }
                });
            }
        });
    }


    /**
     * Redeem Reward
     * <p>
     * Redeem a reward in the wallet
     * </p>
     *
     * @param rewardId - Reward ID
     * @param callback {@link PDAPICallback} for API result
     */
    public void redeemReward(String rewardId, @NonNull PDAPICallback<JsonObject> callback) {
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.redeemReward("", rewardId, callback);
    }


    /**
     * Get rewards currently in wallet
     * <p>
     * Request all rewards which are relevant to user.
     * </p>
     *
     * @param callback {@link PDAPICallback} for API result
     */
    public void getRewardsInWallet(@NonNull final PDAPICallback<JsonObject> callback) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDRewardsDeserializer.REWARDS_TYPE, new PDRewardsDeserializer())
                .create();
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.getRewardsInWallet(callback);
    }
// public void getRewardsInWallet(@NonNull final PDAPICallback<ArrayList<PDReward>> callback) {
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(PDRewardsDeserializer.REWARDS_TYPE, new PDRewardsDeserializer())
//                .create();
//        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
//        api.getRewardsInWallet(callback);
//    }




    /**
     * Verify that a user posted to Instagram to claim a reward.
     *
     * @param rewardId Reward ID to verify against
     * @param callback {@link PDAPICallback} for API result
     */
    public void verifyInstagramPostForReward(@NonNull String rewardId, @NonNull final PDAPICallback<JsonObject> callback) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        final String accessToken = userDetails.getUserInstagram().getAccessToken();
        realm.close();

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.verifyInstagramPostForReward("", accessToken, rewardId, callback);
    }


    /**
     * Get Popdeem Messages
     *
     * @param callback {@link PDAPICallback} for API result
     */
    public void getPopdeemMessages(@NonNull PDAPICallback<ArrayList<PDMessage>> callback) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDMessagesDeserializer.MESSAGES_TYPE, new PDMessagesDeserializer())
                .create();
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.getPopdeemMessages(callback);
    }


    /**
     * Mark a message as read.
     *
     * @param messageId ID of message to mark as read
     * @param callback  {@link PDAPICallback} for API result
     */
    public void markMessageAsRead(String messageId, @NonNull PDAPICallback<PDBasicResponse> callback) {
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.markMessageAsRead("", messageId, callback);
    }


    /**
     * Get Feeds
     *
     * @param callback {@link PDAPICallback} for API result
     */
    public void getFeeds(@NonNull PDAPICallback<ArrayList<PDFeed>> callback) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDFeedsDeserializer.FEEDS_TYPE, new PDFeedsDeserializer())
                .create();
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), new GsonConverter(gson));
        api.getFeeds(callback);
    }


    /**
     * Deliver a custom action trigger for an event
     *
     * @param moment   Action configured on backend
     * @param callback {@link PDAPICallback} for API result
     */
    public void logMoment(@NonNull String moment, @NonNull PDAPICallback<PDBasicResponse> callback) {
        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.logMoment("", moment, callback);
    }


    /**
     * Get User Token Interceptor
     *
     * @return Interceptor with User Token Header or null if no token is saved.
     */
    private Interceptor getUserTokenInterceptor() {
        final String userToken = PDUtils.getUserToken();
        Log.i("User Token", "getUserTokenInterceptor: " + userToken);
        if (userToken == null) {
            return null;
        }

        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain.request().newBuilder()
                        .addHeader(PDAPIConfig.REQUEST_HEADER_USER_TOKEN, userToken)
                        .build());
            }
        };
    }


    /**
     * Get Popdeem API interface
     *
     * @param interceptor - {@link Interceptor} used for request
     * @param converter   - {@link Converter} users for request response (optional)
     * @return - {@link PopdeemAPI} used for interacting with API
     */
    private PopdeemAPI getApiInterface(Interceptor interceptor, Converter converter) {
        RestAdapter restAdapter = buildRestAdapter(interceptor, converter);
        return restAdapter.create(PopdeemAPI.class);
    }


    /**
     * Build RestAdapter for request
     *
     * @param interceptor - {@link Interceptor} for request (optional)
     * @param converter   - {@link Converter} for Request (optional)
     * @return - {@link RestAdapter} used to create {@link PopdeemAPI}
     */
    private RestAdapter buildRestAdapter(Interceptor interceptor, Converter converter) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(PD_API_KEY_INTERCEPTOR);

        if (interceptor != null) {
            builder.addInterceptor(interceptor);
        }

        OkHttpClient okHttpClient = builder.build();

        RestAdapter.Builder adapterBuilder = new RestAdapter.Builder()
                .setClient(new Ok3Client(okHttpClient))
                .setEndpoint(PDAPIConfig.PD_API_ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        if (converter != null) {
            adapterBuilder.setConverter(converter);
        }

        return adapterBuilder.build();
    }

    /**
     * Background Scan
     *
     * @param rewardID - id of reward user is scanning for
     * @param network  - String of the network the user is scanning (facebook, instagram, twitter)
     * @param callback {@link PDAPICallback} for API result
     */
    public void scanSocialNetwork(String rewardID, String network, @NonNull final PDAPICallback<JsonObject> callback) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("network", network);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, jsonBody.toString().getBytes());

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.scanSocialNetwork(body, rewardID, callback);
    }

    /**
     * Claim Discovery
     */
    public void claimDiscovery(PDBGScanResponseModel post,
                               String facebookAccessToken,
                               String twitterAccessToken, String twitterAccessSecret,
                               String instagramAccessToken,
                               String latitude, String longitude, String locationID,
                               String rewardID,
                               Context context,
                               @NonNull final PDAPICallback<JsonObject> callback) {


        JsonObject jsonBody = new JsonObject();

        //message
        jsonBody.addProperty("message", post.getText());

        //file
        jsonBody.addProperty("file", post.getMediaUrl());

        //post_key
        jsonBody.addProperty("post_key", post.getObjectID());

        //social network keys
        if (post.getNetwork().equalsIgnoreCase("facebook")){
            JsonObject facebookObject = new JsonObject();
            facebookObject.addProperty("access_token", facebookAccessToken);
            jsonBody.add("facebook", facebookObject);
        } else if (post.getNetwork().equalsIgnoreCase("twitter")){
            JsonObject twitterObject = new JsonObject();
            twitterObject.addProperty("access_token", twitterAccessToken);
            twitterObject.addProperty("access_secret", twitterAccessSecret);
            jsonBody.add("twitter", twitterObject);
        } else if (post.getNetwork().equalsIgnoreCase("instagram")){
            JsonObject instagramObject = new JsonObject();
            instagramObject.addProperty("access_token", instagramAccessToken);
            jsonBody.add("instagram", instagramObject);
        }

        //location
        JsonObject locationObject = new JsonObject();
        locationObject.addProperty("latitude", latitude);
        locationObject.addProperty("longitude", longitude);
        locationObject.addProperty("id", locationID);

        jsonBody.add("location", locationObject);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, jsonBody.toString().getBytes());

        PopdeemAPI api = getApiInterface(getUserTokenInterceptor(), null);
        api.claimDiscovery(body, rewardID, callback);
    }
}
