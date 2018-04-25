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

package com.popdeem.sdk.core.api.abra;

/**
 * Created by mikenolan on 21/09/2016.
 */

public class PDAbraConfig {

    public static final String ABRA_PROJECT_TOKEN = "b414ae329d7993e2bce41198b899f871";

    public static final String ABRA_URL = "http://insights.popdeem.com/v1";
    public static final String ABRA_EVENT_PATH = "/event";
    public static final String ABRA_TOKEN_PATH = "/fetch_token";

    public static final String ABRA_KEY_USER_ID = "user_id";
    public static final String ABRA_KEY_EVENT = "event";
    public static final String ABRA_KEY_TRAITS = "traits";
    public static final String ABRA_KEY_PROJECT_TOKEN = "project_token";
    public static final String ABRA_KEY_TAG = "tag";
    public static final String ABRA_KEY_PROPERTIES = "properties";

    public static final String ABRA_EVENT_PAGE_VIEWED = "Viewed";
    public static final String ABRA_PROPERTYNAME_PAGE = "Page";
    public static final String ABRA_PROPERTYVALUE_PAGE_LOGINTAKEOVER = "Login Takeover";
    public static final String ABRA_PROPERTYVALUE_PAGE_REWARDS_HOME = "Rewards Home";
    public static final String ABRA_PROPERTYVALUE_PAGE_ACTIVITY_FEED = "Activity Feed";
    public static final String ABRA_PROPERTYVALUE_PAGE_WALLET = "Profile";
    public static final String ABRA_PROPERTYVALUE_PAGE_TUTORIAL_MODULE_ONE = "Instagram Tutorial Module One";
    public static final String ABRA_PROPERTYVALUE_PAGE_TUTORIAL_MODULE_TWO = "Instagram Tutorial Module Two";
    public static final String ABRA_PROPERTYVALUE_PAGE_CONNECT_INSTAGRAM = "Connect Instagram Module";
    public static final String ABRA_PROPERTYVALUE_PAGE_VIEWED_SETTINGS = "Settings";
    public static final String ABRA_PROPERTYVALUE_PAGE_VIEWED_INBOX = "Inbox";
    public static final String ABRA_PROPERTYVALUE_PAGE_VIEWED_CLAIM = "Claim Screen";

    public static final String ABRA_PROPERTYNAME_REWARD_TYPE = "Reward Type";
    public static final String ABRA_PROPERTYVALUE_REWARD_TYPE_COUPON = "Coupon";
    public static final String ABRA_PROPERTYVALUE_REWARD_TYPE_SWEEPSTAKE = "Sweepstake";

    public static final String ABRA_PROPERTYNAME_REWARD_ACTION = "Reward Action";
    public static final String ABRA_PROPERTYVALUE_REWARD_ACTION_CHECKIN = "Check In";
    public static final String ABRA_PROPERTYVALUE_REWARD_ACTION_PHOTO = "Photo";

    public static final String ABRA_PROPERTYNAME_NETWORKS_AVAILABLE = "Networks Available";
    public static final String ABRA_PROPERTYVALUE_NETWORKS_AVAILABLE_FACEBOOK = "Facebook";
    public static final String ABRA_PROPERTYVALUE_NETWORKS_AVAILABLE_TWITTER = "Twitter";
    public static final String ABRA_PROPERTYVALUE_NETWORKS_AVAILABLE_INSTAGRAM = "Instagram";

    public static final String ABRA_EVENT_CLICKED_CLOSE_LOGIN_TAKEOVER = "Clicked Close Login Takeover";
    public static final String ABRA_EVENT_CLICKED_CLOSE_LOGIN_SIGNUP = "Clicked Close Login Signup";
    public static final String ABRA_EVENT_CLICKED_CLOSE_INSTAGRAM_CONNECT = "Clicked Close Instagram Connect Module";
    public static final String ABRA_EVENT_CLICKED_NEXT_INSTAGRAM_TUTORIAL = "Clicked 'Next' on Instagram Connect";
    public static final String ABRA_EVENT_CLICKED_SIGN_IN_INSTAGRAM = "Clicked Sign in To Instagram";

    public static final String ABRA_EVENT_CLAIMED = "Claimed Reward";
    public static final String ABRA_PROPERTYNAME_SOCIAL_NETWORKS = "Social Networks";
    public static final String ABRA_PROPERTYVALUE_SOCIAL_NETWORK_FACEBOOK = "Facebook";
    public static final String ABRA_PROPERTYVALUE_SOCIAL_NETWORK_TWITTER = "Twitter";
    public static final String ABRA_PROPERTYVALUE_SOCIAL_NETWORK_INSTAGRAM = "Instagram";
    public static final String ABRA_PROPERTYNAME_PHOTO_ATTACHED = "Photo"; // Boolean Values

    public static final String ABRA_EVENT_CONNECTED_ACCOUNT = "Connected Social Account";
    public static final String ABRA_PROPERTYNAME_SOCIAL_NETWORK = "Social Network";
// Use value ABRA_PROPERTYVALUE_SOCIAL_NETWORK_FACEBOOK etc
    public static final String ABRA_PROPERTYNAME_SOURCE_PAGE = "Source Page";
    public static final String ABRA_PROPERTYVALUE_SOURCE_PAGE_TAKEOVER = "Login Takeover";
    public static final String ABRA_PROPERTYVALUE_SOURCE_PAGE_REWARD_LIST = "Reward List";
    public static final String ABRA_PROPERTYVALUE_SOURCE_PAGE_SETTINGS = "Settings";

    public static final String ABRA_PROPERTYNAME_PERMISSIONS = "Permissions Granted";
    public static final String ABRA_PROPERTYVALUE_PERMISSIONS_EMAIL = "Email";
    public static final String ABRA_PROPERTYVALUE_PERMISSIONS_EDUCATION = "Education History";
    public static final String ABRA_PROPERTYVALUE_PERMISSIONS_PUBLIC_PROFILE = "Public Profile";
    public static final String ABRA_PROPERTYVALUE_PERMISSIONS_USER_BIRTHDAY = "User Birthday";
    public static final String ABRA_PROPERTYVALUE_PERMISSIONS_USER_FRIENDS = "User Friends";
    public static final String ABRA_PROPERTYVALUE_PERMISSIONS_USER_POSTS = "User Posts";
    public static final String ABRA_PROPERTYVALUE_PERMISSIONS_TAGGABLE_FRIENDS = "User Posts";

    public static final String ABRA_EVENT_FACEBOOK_DENIED_PUBLISH_PERMISSIONS = "Denied Publish Permissions";

    public static final String ABRA_EVENT_ADDED_CLAIM_CONTENT = "Added Claim Content";
    public static final String ABRA_PROPERTYNAME_TEXT = "Claim Text"; //Value is user text
    public static final String ABRA_PROPERTYNAME_PHOTO = "Photo"; //Value null or Boolean?
    public static final String ABRA_PROPERTYNAME_TAGGED_FRIENDS = "Tagged Friends"; //Value null or Boolean?

    public static final String ABRA_EVENT_RECEIVED_ERROR_ON_CLAIM = "Error on Claim";
    public static final String ABRA_PROPERTYNAME_NO_HASHTAG = "No Hashtag";
    public static final String ABRA_PROPERTYNAME_NO_NETWORK_SELECTED = "No Network Selected";
    public static final String ABRA_PROPERTYNAME_NO_PHOTO = "No Photo Added";

    public static final String ABRA_EVENT_TOGGLED_SOCIAL_BUTTON = "Toggled Social Button";
    public static final String ABRA_PROPERTYNAME_SOCIAL_BUTTON_TYPE = "Social Network";
// Use value ABRA_PROPERTYVALUE_SOCIAL_NETWORK_FACEBOOK etc
    public static final String ABRA_PROPERTYNAME_SOCIAL_BUTTON_STATE = "Social Button State";
    public static final String ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_ON = "On";
    public static final String ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_OFF = "Off";

    public static final String ABRA_EVENT_REDEEMED_REWARD = "Redeemed Reward";
    public static final String ABRA_PROPERTYNAME_REWARD_NAME = "Reward Name";
    public static final String ABRA_PROPERTYNAME_REWARD_ID = "Reward ID";

    public static final String ABRA_EVENT_LOGIN = "Login";
    public static final String ABRA_EVENT_LOGOUT = "Logout";
    public static final String ABRA_EVENT_SIGNUP = "Sign Up";

    public static final String ABRA_EVENT_DISCONNECT_SOCIAL_ACCOUNT = "Disconnect Social Account";

    public static final String ABRA_USER_TRAITS_ID = "id";
    public static final String ABRA_EVENT_ONBOARD = "onboard";
    public static final String ABRA_USER_TRAITS_FIRST_NAME = "first_name";
    public static final String ABRA_USER_TRAITS_LAST_NAME = "last_name";
    public static final String ABRA_USER_TRAITS_EMAIL = "email";
    public static final String ABRA_USER_TRAITS_DOB = "dob";
    public static final String ABRA_USER_TRAITS_GENDER = "gender";
    public static final String ABRA_USER_TRAITS_CITY = "city";
    public static final String ABRA_USER_TRAITS_COUNTRY_CODE = "country_code";
    public static final String ABRA_USER_TRAITS_REGION = "region";
    public static final String ABRA_USER_TRAITS_TIME_ZONE = "time_zone";
    public static final String ABRA_USER_TRAITS_IP = "ip";
    public static final String ABRA_USER_TRAITS_PUSH_NOTIFICATIONS_ENABLED = "push_notifications_enabled";

    public static final String ABRA_PROPERTYNAME_ERROR = "Error";
    public static final String ABRA_PROPERTYVALUE_ERROR_HASHTAG = "Hashtag failed validation";
    public static final String ABRA_PROPERTYVALUE_ERROR_TOOMANYCHARS = "Too many characters for Twitter";
    public static final String ABRA_PROPERTYVALUE_ERROR_NOPHOTO = "No photo was added";

    public static final String ABRA_EVENT_DENIED_LOCATION = "User denied location access";
    public static final String ABRA_EVENT_DENIED_PUSH_NOTIFICATIONS = "User denied push notifications";

    public static final String ABRA_EVENT_CANCELLED_FACEBOOK_LOGIN = "User cancelled facebook login";
    
}
