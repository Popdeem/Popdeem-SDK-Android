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

package com.popdeem.sdk;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit.Ok3Client;
import com.popdeem.sdk.core.api.PDAPIConfig;
import com.popdeem.sdk.core.api.PopdeemAPI;
import com.popdeem.sdk.core.deserializer.PDBrandsDeserializer;
import com.popdeem.sdk.core.deserializer.PDFeedsDeserializer;
import com.popdeem.sdk.core.deserializer.PDRewardsDeserializer;
import com.popdeem.sdk.core.deserializer.PDUserDeserializer;
import com.popdeem.sdk.core.model.PDBrand;
import com.popdeem.sdk.core.model.PDFeed;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PopdeemAPITest {

    private final String TAG = PopdeemAPITest.class.getSimpleName();

    private MockWebServer mMockWebServer;

    @Before
    public void setUp() throws IOException {
        mMockWebServer = new MockWebServer();
        mMockWebServer.setDispatcher(DISPATCHER);
        mMockWebServer.start();
    }

    @Test
    public void testRegisterAPI() {
        // Facebook
        buildRestAdapter(new GsonConverter(new GsonBuilder().registerTypeAdapter(PDUser.class, new PDUserDeserializer()).create())).create(PopdeemAPI.class).registerUserWithFacebook("", "", "", "", new Callback<PDUser>() {
            @Override
            public void success(PDUser pdUser, Response response) {
                assert pdUser != null;
                Log.i(TAG, pdUser.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, error.getMessage());
            }
        });
    }

    @Test
    public void testBrandsAPI() {
        buildRestAdapter(new GsonConverter(new GsonBuilder().registerTypeAdapter(PDBrandsDeserializer.BRANDS_TYPE, new PDBrandsDeserializer()).create())).create(PopdeemAPI.class).getBrands(new Callback<ArrayList<PDBrand>>() {
            @Override
            public void success(ArrayList<PDBrand> brands, Response response) {
                Log.i(TAG, "" + brands.size());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, error.getMessage());
            }
        });
    }

    @Test
    public void testRewardsAPI() {
        buildRestAdapter(new GsonConverter(new GsonBuilder().registerTypeAdapter(PDRewardsDeserializer.REWARDS_TYPE, new PDRewardsDeserializer()).create())).create(PopdeemAPI.class).getAllRewards(new Callback<ArrayList<PDReward>>() {
            @Override
            public void success(ArrayList<PDReward> rewards, Response response) {
                Log.i(TAG, "" + rewards.size());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, error.getMessage());
            }
        });
    }

    @Test
    public void testFeedsAPI() {
        buildRestAdapter(new GsonConverter(new GsonBuilder().registerTypeAdapter(PDFeedsDeserializer.FEEDS_TYPE, new PDFeedsDeserializer()).create())).create(PopdeemAPI.class).getFeeds(new Callback<ArrayList<PDFeed>>() {
            @Override
            public void success(ArrayList<PDFeed> feed, Response response) {
                Log.i(TAG, "" + feed.size());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, error.getMessage());
            }
        });
    }


    @After
    public void tearDown() throws IOException {
        mMockWebServer.shutdown();
    }

    private RestAdapter buildRestAdapter(GsonConverter converter) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(mMockWebServer.url("").toString())
                .setClient(new Ok3Client(new OkHttpClient()))
                .setExecutors(new Executor() {
                    @Override
                    public void execute(@NonNull Runnable command) {
                        command.run();
                    }
                }, new Executor() {
                    @Override
                    public void execute(@NonNull Runnable command) {
                        command.run();
                    }
                });

        if (converter != null) {
            builder.setConverter(converter);
        }

        return builder.build();
    }

    private final String REGISTER_FACEBOOK_RESPONSE = "{\"status\":\"User Data\",\"user\":{\"facebook\":{\"social_account_id\":57726,\"facebook_id\":\"1699511153597496\",\"tester\":\"false\",\"access_token\":\"CAAFQ6jdDe4oBAJFGOFnjoTyz7YZAVhgkLlq36nUOlUgl58jsTX5qvvn7SqdZBnd2uPZAXgkudIVWsRZB5axiAvc8a2xqjpJnjNsygwmjJ97YVOqYThZCKB6b7zZCMNrV3lDk2AZBuqh3T4VBwE45RpgbZBzE7aSowlqh44Msy59fOxBXX4NhoKVTBOGrb1lM8GAFJE8vUjDq1sZCTmTJRN4gvwzBbaIP7jZAXO5tSf44TrQAZDZD\",\"expiration_time\":1461333043,\"profile_picture_url\":\"http://s3-eu-west-1.amazonaws.com/popdeem-staging/social_accounts/profile_images/000/057/726/medium/open-uri20160223-29542-1gvqwf8?1456235037\",\"score\":{\"total_score\":{\"value\":\"17.9112\"},\"influence_score\":{\"reach_score_value\":\"0.597\",\"engagement_score_value\":\"29.1089\",\"frequency_score_value\":\"2.6678\"},\"advocacy_score\":{\"value\":0}},\"favourite_brand_ids\":[],\"default_privacy_setting\":\"\"},\"twitter\":{\"social_account_id\":\"\",\"twitter_id\":\"\",\"tester\":\"false\",\"access_token\":\"\",\"access_secret\":\"\",\"expiration_time\":\"\",\"profile_picture_url\":\"\",\"score\":{\"total_score\":{\"value\":0},\"influence_score\":{\"reach_score_value\":0,\"engagement_score_value\":0,\"frequency_score_value\":0},\"advocacy_score\":{\"value\":0}},\"favourite_brand_ids\":[]},\"first_name\":\"Randall\",\"last_name\":\"Timmothy Austin\",\"sex\":\"male\",\"college\":\" \",\"type\":\"general\",\"location\":{\"latitude\":51.51,\"longitude\":-0.1337},\"id\":\"58473\",\"user_token\":\"9a1dbfff91ba33d6a25255dad67c36e7\",\"suspend_until\":\"\"}}";
    private final String BRANDS_RESPONSE = "{\"brands\":[{\"id\":\"123\",\"name\":\"Conors Coffee\",\"logo\":\"http://s3-eu-west-1.amazonaws.com/popdeem-staging/brands/logo_images/000/000/123/original/Untitled_5.png?1440414100\",\"cover_image\":\"http://s3-eu-west-1.amazonaws.com/popdeem-staging/brands/cover_images/000/000/123/original/coffeeheader.png?1440414101\",\"contacts\":{\"phone\":\"018251508\",\"email\":\"info@conorscoffee.ie\",\"web\":\"www.conorscoffee.ie\",\"facebook\":\"facebook.com/ccoffee\",\"twitter\":\"@ccoffee\"},\"opening_hours\":{\"sunday\":{\"closed\":false,\"from\":\"09:00\",\"to\":\"17:00\"},\"monday\":{\"closed\":false,\"from\":\"09:00\",\"to\":\"17:00\"},\"tuesday\":{\"closed\":false,\"from\":\"09:00\",\"tuesday\":\"17:00\"},\"wednesday\":{\"closed\":false,\"from\":\"09:00\",\"to\":\"17:00\"},\"thursday\":{\"closed\":false,\"from\":\"09:00\",\"to\":\"17:00\"},\"friday\":{\"closed\":false,\"from\":\"09:00\",\"to\":\"17:00\"},\"saturday\":{\"closed\":false,\"from\":\"09:00\",\"to\":\"17:00\"}},\"number_of_locations\":\"1\"}]}";
    private final String REWARDS_RESPONSE = "{\"rewards\":[{\"id\":\"728\",\"reward_type\":\"coupon\",\"social_media_types\":[\"Facebook\"],\"picture\":\"http://popdeem.com/images/brand_default.png\",\"blurred_picture\":\"http://popdeem.com/images/brand_default.png\",\"cover_image\":\"http://s3-eu-west-1.amazonaws.com/popdeem/rewards/cover_images/000/000/728/original/10401200_47976816068_100_n.jpg?1440068738\",\"created_at\":\"1440068740\",\"rules\":\"Test Rules\",\"status\":\"live\",\"description\":\"Test Reward\",\"action\":\"photo\",\"remaining_count\":100,\"name\":null,\"available_until\":\"1440885540\",\"available_next\":\"1440021600\",\"locations\":[]}]}";
    private final String FEEDS_RESPONSE = "{\"feeds\":[{\"brand\":{\"name\":\"Popdeem Bakery\",\"logo\":\"http://s3-eu-west-1.amazonaws.com/popdeem-staging/brands/logo_images/000/000/010/original/popdeem_logo.png?1370879332\"},\"reward\":{\"type\":\"coupon\",\"description\":\"Free Coffee\"},\"time_ago\":\"10 months ago\",\"picture=\":\"http://s3-eu-west-1.amazonaws.com/popdeem-staging/requests/photos/000/000/455/original/455.png?1413206567\",\"text\":\"You took a photo at <b>Popdeem Bakery</b>. Your score increased by <b>2</b>\",\"social_account\":{\"user\":{\"id\":1231,\"first_name\":\"Niall\",\"last_name\":\"Quinn\"}}}]}";
    private final String MESSAGES_RESPONSE = "{\"messages\":[{\"id\":682,\"brand_id\":null,\"reward_id\":null,\"title\":\"Notification Title\",\"body\":\"Test Notification All Users\",\"image_url\":\"\",\"read\":false,\"created_at\":1456248046,\"sender_name\":\"PopdeemStaging\"},{\"id\":684,\"brand_id\":null,\"reward_id\":null,\"title\":\"Notification Title\",\"body\":\"Test Notification All Users\",\"image_url\":\"\",\"read\":false,\"created_at\":1456248293,\"sender_name\":\"PopdeemStaging\"},{\"id\":685,\"brand_id\":null,\"reward_id\":null,\"title\":\"Notification Title\",\"body\":\"Test Notification All Users\",\"image_url\":\"\",\"read\":false,\"created_at\":1456248426,\"sender_name\":\"PopdeemStaging\"}]}";

    private final Dispatcher DISPATCHER = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            final String PATH = request.getPath().split("\\?", 2)[0];
            switch (PATH) {
                case PDAPIConfig.PD_USERS_PATH:
                    return new MockResponse().setResponseCode(200).setBody(REGISTER_FACEBOOK_RESPONSE);
                case PDAPIConfig.PD_BRANDS_PATH:
                    return new MockResponse().setResponseCode(200).setBody(BRANDS_RESPONSE);
                case PDAPIConfig.PD_REWARDS_PATH:
                    return new MockResponse().setResponseCode(200).setBody(REWARDS_RESPONSE);
                case PDAPIConfig.PD_FEEDS_PATH:
                    return new MockResponse().setResponseCode(200).setBody(FEEDS_RESPONSE);
                default:
                    return new MockResponse().setResponseCode(404).setBody("no path found");
            }
        }
    };

}
