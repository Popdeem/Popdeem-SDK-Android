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

package com.popdeem.sdk.core.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Popdeem Reward Model Class
 */
public class PDReward {

    public static final String PD_TRUE = "true";
    public static final String PD_FALSE = "false";

    public static final String PD_SOCIAL_MEDIA_TYPE_FACEBOOK = "Facebook";
    public static final String PD_SOCIAL_MEDIA_TYPE_TWITTER = "Twitter";
    public static final String PD_SOCIAL_MEDIA_TYPE_INSTAGRAM = "Instagram";

    @StringDef({PD_SOCIAL_MEDIA_TYPE_FACEBOOK, PD_SOCIAL_MEDIA_TYPE_TWITTER, PD_SOCIAL_MEDIA_TYPE_INSTAGRAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PDSocialMediaType {
    }

    public static final String PD_REWARD_TYPE_COUPON = "coupon";
    public static final String PD_REWARD_TYPE_SWEEPSTAKE = "sweepstake";
    public static final String PD_REWARD_TYPE_INSTANT = "instant";
    public static final String PD_REWARD_TYPE_CREDIT = "credit";

    public static final String PD_REWARD_ACTION_CHECKIN = "checkin";
    public static final String PD_REWARD_ACTION_PHOTO = "photo";
    public static final String PD_REWARD_ACTION_NONE = "none";
    public static final String PD_REWARD_ACTION_SOCIAL_LOGIN = "social_login";

    public static final String PD_REWARD_STATUS_LIVE = "live";
    public static final String PD_REWARD_STATUS_EXPIRED = "expired";


    private String id;
    private String rewardType;
    private String description;
    private String picture;
    private String blurredPicture;
    private String coverImage;
    private String rules;
    private int remainingCount;
    private String status;
    private String action;

    @SerializedName("available_until")
    private String availableUntilInSeconds;

    @SerializedName("available_next")
    private String availableNextInSeconds;
    private String revoked;

    private String twitterMediaCharacters;
    private String[] socialMediaTypes;

    private String disableLocationVerification;
    private String credit;

    private PDTweetOptions tweetOptions;
    @SerializedName("instagram_option")
    private PDInstagramOptions instagramOptions;
    private ArrayList<PDLocation> locations;
    private long countdownTimer;

    private float distanceFromUser;

    private boolean instagramVerified;

    public PDReward() {
    }

    public PDReward(String id, String rewardType, String description, String picture, String blurredPicture, String coverImage, String rules, int remainingCount, String status, String action, String availableUntilInSeconds, String availableNextInSeconds, String revoked, String twitterMediaCharacters, String[] socialMediaTypes, String disableLocationVerification, String credit, PDTweetOptions tweetOptions, ArrayList<PDLocation> locations, long countdownTimer, boolean instagramVerified) {
        this.id = id;
        this.rewardType = rewardType;
        this.description = description;
        this.picture = picture;
        this.blurredPicture = blurredPicture;
        this.coverImage = coverImage;
        this.rules = rules;
        this.remainingCount = remainingCount;
        this.status = status;
        this.action = action;
        this.availableUntilInSeconds = availableUntilInSeconds;
        this.availableNextInSeconds = availableNextInSeconds;
        this.revoked = revoked;
        this.twitterMediaCharacters = twitterMediaCharacters;
        this.socialMediaTypes = socialMediaTypes;
        this.disableLocationVerification = disableLocationVerification;
        this.credit = credit;
        this.tweetOptions = tweetOptions;
        this.locations = locations;
        this.countdownTimer = countdownTimer;
        this.instagramVerified = instagramVerified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getBlurredPicture() {
        return blurredPicture;
    }

    public void setBlurredPicture(String blurredPicture) {
        this.blurredPicture = blurredPicture;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public int getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(int remainingCount) {
        this.remainingCount = remainingCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAvailableUntilInSeconds() {
        return availableUntilInSeconds;
    }

    public void setAvailableUntilInSeconds(String availableUntilInSeconds) {
        this.availableUntilInSeconds = availableUntilInSeconds;
    }

    public String getAvailableNextInSeconds() {
        return availableNextInSeconds;
    }

    public void setAvailableNextInSeconds(String availableNextInSeconds) {
        this.availableNextInSeconds = availableNextInSeconds;
    }

    public String getRevoked() {
        return revoked;
    }

    public void setRevoked(String revoked) {
        this.revoked = revoked;
    }

    public String getTwitterMediaCharacters() {
        return twitterMediaCharacters;
    }

    public void setTwitterMediaCharacters(String twitterMediaCharacters) {
        this.twitterMediaCharacters = twitterMediaCharacters;
    }

    public String[] getSocialMediaTypes() {
        return socialMediaTypes;
    }

    public void setSocialMediaTypes(String[] socialMediaTypes) {
        this.socialMediaTypes = socialMediaTypes;
    }

    public PDTweetOptions getTweetOptions() {
        return tweetOptions;
    }

    public void setTweetOptions(PDTweetOptions tweetOptions) {
        this.tweetOptions = tweetOptions;
    }

    public PDInstagramOptions getInstagramOptions() {
        return instagramOptions;
    }

    public void setInstagramOptions(PDInstagramOptions instagramOptions) {
        this.instagramOptions = instagramOptions;
    }

    public ArrayList<PDLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<PDLocation> locations) {
        this.locations = locations;
    }

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(float distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public String getDisableLocationVerification() {
        return disableLocationVerification;
    }

    public void setDisableLocationVerification(String disableLocationVerification) {
        this.disableLocationVerification = disableLocationVerification;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public long getCountdownTimer() {
        return countdownTimer;
    }

    public void setCountdownTimer(long countdownTimer) {
        this.countdownTimer = countdownTimer;
    }

    public boolean isInstagramVerified() {
        return instagramVerified;
    }

    public void setInstagramVerified(boolean instagramVerified) {
        this.instagramVerified = instagramVerified;
    }
}
