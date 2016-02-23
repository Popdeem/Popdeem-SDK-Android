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
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;

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

    @GET(PDAPIConfig.PD_USERS_PATH)
    void registerUserWithTwitterParams(
            @Query("user[twitter][access_token]") String twitterAccessToken,
            @Query("user[twitter][access_secret]") String twitterAccessSecret,
            @Query("user[twitter][id]") String twitterID,
            @Query("user[twitter][**]") String screenName,
            Callback<JsonObject> callback);

    @POST(PDAPIConfig.PD_USERS_PATH)
    void registerUserWithFacebookAccessToken(
            @Body String emptyBody,
            @Query("user[facebook][access_token]") String facebookAccessToken,
            @Query("user[facebook][id]") String facebookUserID,
            @Query("user[unique_identifier]") String uid,
            Callback<PDUser> callback);

    @POST(PDAPIConfig.PD_CONNECT_SOCIAL_ACCOUNT)
    void connectWithTwitterAccount(
            @Body TypedInput body,
            Callback<JsonObject> callback);

    @PUT(PDAPIConfig.PD_USERS_PATH + "/{id}")
    void updateUserLocationAndDeviceToken(
            @Path("id") String id,
            @Query("user[platform]") String platform,
            @Query("user[device_token]") String deviceToken,
            @Query("user[latitude]") String latitude,
            @Query("user[longitude]") String longitude,
            @Query("user[unique_identifier]") String uid,
            Callback<JsonObject> callback);

    @GET(PDAPIConfig.PD_USERS_PATH + "/{id}")
    void getUserDetailsForId(@Path("id") String id, Callback<JsonObject> callback);

    @GET(PDAPIConfig.PD_USERS_PATH + "/{id}/friends")
    void getPopdeemFriends(@Path("id") String id, Callback<JsonObject> callback);


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
            @Path("rewardId") String rewardId,
            Callback<JsonObject> callback);


    //****************************************
    // Wallet API Call
    //****************************************

    @GET(PDAPIConfig.PD_WALLET_PATH)
    void getRewardsInWallet(Callback<ArrayList<PDReward>> callback);


    //****************************************
    // Messages API Call
    //****************************************

    @GET(PDAPIConfig.PD_MESSAGES_PATH)
    void getPopdeemMessages(Callback<JsonObject> callback);


    //****************************************
    // Feeds API Call
    //****************************************

    @GET(PDAPIConfig.PD_FEEDS_PATH)
    void getFeeds(@Query("limit") String limit, Callback<ArrayList<PDFeed>> callback);

}
