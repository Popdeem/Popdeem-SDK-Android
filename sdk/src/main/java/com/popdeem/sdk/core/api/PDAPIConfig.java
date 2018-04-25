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

/**
 * Created by mikenolan on 15/02/16.
 * <p>
 * This class contains all API Config fields such as Endpoints, Paths, Header names etc.
 * </p>
 */
public class PDAPIConfig {

    public static final String PD_JSON_MIME_TYPE = "application/json";

    public static final String PD_PROD_API_ENDPOINT = "https://api.popdeem.com";         // Production API Endpoint
    public static final String PD_STAGING_API_ENDPOINT = "http://api.staging.popdeem.com";  // Staging API Endpoint
    public static final String PD_LOCAL_API_ENDPOINT = "http://87f3a997.ngrok.io";

    public static String PD_API_ENDPOINT = PD_PROD_API_ENDPOINT;

//    public static final String PD_API_ENDPOINT = PD_STAGING_API_ENDPOINT;

    private static final String API_PATH = "/api/";
    public static final String API_VERSION = "v2";
    public static final String PD_REWARDS_ENDPOINT = "/rewards";
    public static final String PD_USERS_ENDPOINT = "/users";
    public static final String PD_CUSTOMER_ENDPOINT = "/customer";

    public static final String PD_USERS_PATH = API_PATH + API_VERSION + PD_USERS_ENDPOINT;
    public static final String PD_REWARDS_PATH = API_PATH + API_VERSION + PD_REWARDS_ENDPOINT;
    public static final String PD_WALLET_PATH = API_PATH + API_VERSION + PD_REWARDS_ENDPOINT + "/wallet";
    public static final String PD_MESSAGES_PATH = API_PATH + API_VERSION + "/messages";
    public static final String PD_FEEDS_PATH = API_PATH + API_VERSION + "/feeds";
    public static final String PD_CONNECT_SOCIAL_ACCOUNT = API_PATH + API_VERSION + PD_USERS_ENDPOINT + "/connect_social_account";
    public static final String PD_INIT_NON_SOCIAL_USER = API_PATH + API_VERSION + PD_USERS_ENDPOINT + "/init_non_social_user";
    public static final String PD_MOMENTS_PATH = API_PATH + API_VERSION + "/moments";
    public static final String PD_CUSTOMER_PATH = API_PATH + API_VERSION + PD_CUSTOMER_ENDPOINT;


    public static final String PD_BRANDS_PATH = API_PATH + API_VERSION + "/brands";

    public static final String PD_INSTAGRAM_PATH = "https://api.instagram.com/v1";

    public static final String PLATFORM_VALUE = "Android";

    public static final String REQUEST_HEADER_API_KEY = "Api-Key";
    public static final String REQUEST_HEADER_USER_TOKEN = "User-Token";

    /** The Scan Feature
     * /api/v2/rewards/{rewardID}/autodiscovery
     Where {rewardID} is the id of reward being scanned **/
    public static final String PD_AUTODISCOVERY_PATH = API_PATH + API_VERSION + PD_REWARDS_ENDPOINT;

    /**
     * The Claim Discovery Path
     * /api/v2/rewards/{rewardID}/claim_discovered
     * Used in the Post Scan Success Screen (PDScanSuccessView)
     */
    public static final String PD_CLAIM_DISCOVERY_PATH = API_PATH + API_VERSION + PD_REWARDS_ENDPOINT;

}
