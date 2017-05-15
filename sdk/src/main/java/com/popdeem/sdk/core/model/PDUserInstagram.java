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

import com.google.gson.annotations.SerializedName;

/**
 * Created by mikenolan on 05/08/16.
 */
public class PDUserInstagram {

    @SerializedName("social_account_id")
    private long socialAccountId;
    @SerializedName("instagram_id")
    private String instagramId;
    private boolean tester;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("access_secret")
    private String accessSecret;
    private String screenName;
    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    private String influenceReachScore;
    private String influenceEngagementScore;
    private String influenceFrequencyScore;
    private int advocacyScore;
    private String[] favouriteBrandIds;
    private String totalScore;

    public PDUserInstagram() {
    }

    public PDUserInstagram(long socialAccountId, String instagramId, boolean tester, String accessToken, String accessSecret, String screenName, String profilePictureUrl) {
        this.socialAccountId = socialAccountId;
        this.instagramId = instagramId;
        this.tester = tester;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
        this.screenName = screenName;
        this.profilePictureUrl = profilePictureUrl;
    }

    public long getSocialAccountId() {
        return socialAccountId;
    }

    public void setSocialAccountId(long socialAccountId) {
        this.socialAccountId = socialAccountId;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public void setInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }

    public boolean getTester() {
        return tester;
    }

    public void setTester(boolean tester) {
        this.tester = tester;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isTester() {
        return tester;
    }

    public String getInfluenceReachScore() {
        return influenceReachScore;
    }

    public void setInfluenceReachScore(String influenceReachScore) {
        this.influenceReachScore = influenceReachScore;
    }

    public String getInfluenceEngagementScore() {
        return influenceEngagementScore;
    }

    public void setInfluenceEngagementScore(String influenceEngagementScore) {
        this.influenceEngagementScore = influenceEngagementScore;
    }

    public String getInfluenceFrequencyScore() {
        return influenceFrequencyScore;
    }

    public void setInfluenceFrequencyScore(String influenceFrequencyScore) {
        this.influenceFrequencyScore = influenceFrequencyScore;
    }

    public int getAdvocacyScore() {
        return advocacyScore;
    }

    public void setAdvocacyScore(int advocacyScore) {
        this.advocacyScore = advocacyScore;
    }

    public String[] getFavouriteBrandIds() {
        return favouriteBrandIds;
    }

    public void setFavouriteBrandIds(String[] favouriteBrandIds) {
        this.favouriteBrandIds = favouriteBrandIds;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }
}
