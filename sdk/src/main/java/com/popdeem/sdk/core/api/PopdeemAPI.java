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

import com.google.gson.JsonObject;
import com.popdeem.sdk.core.api.response.PDBasicResponse;
import com.popdeem.sdk.core.model.PDBrand;
import com.popdeem.sdk.core.model.PDFeed;
import com.popdeem.sdk.core.model.PDMessage;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

/**
 * Created by mikenolan on 22/02/16.
 */
public interface PopdeemAPI {

    //****************************************
    // Users API Calls
    //****************************************

    @POST(PDAPIConfig.PD_INIT_NON_SOCIAL_USER)
    void createNonSocialUser(
            @Body String emptyBody,
            @Query("unique_identifier") String uid,
            @Query("device_token") String deviceToken,
            @Query("platform") String platform,
            Callback<PDBasicResponse> callback);

//    @GET(PDAPIConfig.PD_USERS_PATH)
//    void registerUserWithTwitterParams(
//            @Query("user[twitter][access_token]") String twitterAccessToken,
//            @Query("user[twitter][access_secret]") String twitterAccessSecret,
//            @Query("user[twitter][id]") String twitterID,
//            @Query("user[twitter][**]") String screenName,
//            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_USERS_PATH)
    void registerUserWithTwitterParams(
            @Body TypedInput body,
            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_USERS_PATH)
    void registerUserWithFacebook(
            @Body String emptyBody,
            @Query("user[facebook][access_token]") String facebookAccessToken,
            @Query("user[facebook][id]") String facebookUserID,
            @Query("user[unique_identifier]") String uid,
            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_CONNECT_SOCIAL_ACCOUNT)
    void connectFacebookAccount(
            @Body TypedInput body,
            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_CONNECT_SOCIAL_ACCOUNT)
    void connectWithTwitterAccount(
            @Body TypedInput body,
            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_CONNECT_SOCIAL_ACCOUNT)
    void connectWithInstagramAccount(
            @Body TypedInput body,
            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_USERS_PATH)
    void registerUserWithInstagram(
            @Body TypedInput body,
            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_USERS_PATH + "/disconnect_social_account")
    void disconnectSocialAccount(
            @Body TypedInput body,
            Callback<PDUser> callback);

    @PUT(PDAPIConfig.PD_USERS_PATH + "/{id}")
    void updateUserLocationAndDeviceToken(
            @Body String emptyBody,
            @Path("id") String id,
            @Query("user[platform]") String platform,
            @Query("user[device_token]") String deviceToken,
            @Query("user[latitude]") String latitude,
            @Query("user[longitude]") String longitude,
            @Query("user[unique_identifier]") String uid,
            @Query("user[third_party_user_token]") String thirdPartyToken,
            Callback<PDUser> callback);

    @PUT(PDAPIConfig.PD_USERS_PATH + "/{id}")
    void updateUserLocationAndDeviceTokenWithReferral(
            @Body String emptyBody,
            @Path("id") String id,
            @Query("user[platform]") String platform,
            @Query("user[device_token]") String deviceToken,
            @Query("user[latitude]") String latitude,
            @Query("user[longitude]") String longitude,
            @Query("user[unique_identifier]") String uid,
            @Query("user[referral][referrer_id]") String referrerId,
            @Query("user[referral][type]") String type,
            @Query("user[referral][referrer_app_name]") String referrerAppName,
            @Query("user[referral][request_id]") String requestId,
            @Query("user[third_party_user_token]") String thirdPartyToken,
            Callback<PDUser> callback);

    @GET(PDAPIConfig.PD_USERS_PATH + "/{id}")
    void getUserDetailsForId(@Path("id") String id, PDAPICallback<JsonObject> callback);

    @GET(PDAPIConfig.PD_USERS_PATH + "/{id}/friends")
    void getPopdeemFriends(@Path("id") String id, Callback<JsonObject> callback);


    //****************************************
    // Customer API Calls
    //****************************************

    @GET(PDAPIConfig.PD_CUSTOMER_PATH)
    void getCustomer(Callback<JsonObject> callback);


    //****************************************
    // Brand API Calls
    //****************************************

    @GET(PDAPIConfig.PD_BRANDS_PATH)
    void getBrands(Callback<ArrayList<PDBrand>> callback);


    //****************************************
    // Rewards API Calls
    //****************************************

    // http://staging.popdeem.com/api/v2/brands/91/rewards
    @GET(PDAPIConfig.PD_BRANDS_PATH + "/{brandID}" + PDAPIConfig.PD_REWARDS_ENDPOINT)
    void getRewardsForBrandID(@Path("brandID") String brandID,
                              Callback<ArrayList<PDReward>> callback);

    @GET(PDAPIConfig.PD_REWARDS_PATH)
    void getAllRewards(Callback<ArrayList<PDReward>> callback);

    @POST(PDAPIConfig.PD_REWARDS_PATH + "/{rewardId}/claim")
    void claimReward(
            @Query("facebook[access_token]") String facebookAccessToken,
            @Path("rewardId") String rewardId,
            @Query("message") String message,
//            @Query("") String[] taggedFriends,
            @Query("file") String image,
            @Query("location[latitude]") String latitude,
            @Query("location[longitude]") String longitude,
            Callback<JsonObject> callback);

    @POST(PDAPIConfig.PD_REWARDS_PATH + "/{rewardId}/claim")
    void claimReward(
            @Query("facebook[access_token]") String facebookAccessToken,
            @Path("rewardId") String rewardId,
            @Query("message") String message,
//            @Query("") String[] taggedFriends,
            @Query("location[latitude]") String latitude,
            @Query("location[longitude]") String longitude,
            Callback<JsonObject> callback);

    @POST(PDAPIConfig.PD_REWARDS_PATH + "/{rewardId}/redeem")
    void redeemReward(
            @Body String emptyBody,
            @Path("rewardId") String rewardId,
            Callback<JsonObject> callback);

    @POST(PDAPIConfig.PD_REWARDS_PATH + "/verify")
    void verifyInstagramPostForReward(
            @Body String emptyBody,
            @Query("instagram[access_token]") String accessToken,
            @Query("instagram[reward_id]") String rewardId,
            Callback<JsonObject> callback);


    //****************************************
    // Wallet API Call
    //****************************************

    @GET(PDAPIConfig.PD_WALLET_PATH)
    void getRewardsInWallet(Callback<JsonObject> callback);
//    void getRewardsInWallet(Callback<ArrayList<PDReward>> callback);


    //****************************************
    // Messages API Calls
    //****************************************

    @GET(PDAPIConfig.PD_MESSAGES_PATH)
    void getPopdeemMessages(Callback<ArrayList<PDMessage>> callback);

    @PUT(PDAPIConfig.PD_MESSAGES_PATH + "/{messageId}/mark_as_read")
    void markMessageAsRead(
            @Body String emptyBody,
            @Path("messageId") String messageId,
            Callback<PDBasicResponse> callback);


    //****************************************
    // Feeds API Call
    //****************************************

    @GET(PDAPIConfig.PD_FEEDS_PATH)
    void getFeeds(Callback<ArrayList<PDFeed>> callback);


    @POST(PDAPIConfig.PD_MOMENTS_PATH)
    void logMoment(@Body String emptyBody,
                   @Query("trigger_action") String moment,
                   Callback<PDBasicResponse> callback);


    //****************************************
    // Background Scan API Calls
    //****************************************

    @POST(PDAPIConfig.PD_AUTODISCOVERY_PATH + "/{rewardId}/autodiscovery")
    void scanSocialNetwork(
            @Body TypedInput body,
            @Path("rewardId") String rewardID,
            Callback<JsonObject>callback
    );

    //****************************************
    // Claim Discovery Calls
    //****************************************

    @POST(PDAPIConfig.PD_CLAIM_DISCOVERY_PATH + "/{rewardId}/claim_discovered")
    void claimDiscovery(
            @Body TypedInput body,
            @Path("rewardId") String rewardID,
            Callback<JsonObject>callback
    );
}
