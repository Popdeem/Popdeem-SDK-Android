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

/**
 * Popdeem Users Facebook Model Class
 */
public class PDUserFacebook {

    private long socialAccountId;
    private String facebookId;
    private String tester;
    private String accessToken;
    private long expirationTime;
    private String profilePictureUrl;
    private String totalScore;
    private String influenceReachScore;
    private String influenceEngagementScore;
    private String influenceFrequencyScore;
    private int advocacyScore;
    private String[] favouriteBrandIds;
    private String defaultPrivacySetting;

    public PDUserFacebook() {
    }

    public PDUserFacebook(long socialAccountId, String facebookId, String tester, String accessToken, long expirationTime, String profilePictureUrl, String totalScore, String influenceReachScore, String influenceEngagementScore, String influenceFrequencyScore, int advocacyScore, String[] favouriteBrandIds, String defaultPrivacySetting) {
        this.socialAccountId = socialAccountId;
        this.facebookId = facebookId;
        this.tester = tester;
        this.accessToken = accessToken;
        this.expirationTime = expirationTime;
        this.profilePictureUrl = profilePictureUrl;
        this.totalScore = totalScore;
        this.influenceReachScore = influenceReachScore;
        this.influenceEngagementScore = influenceEngagementScore;
        this.influenceFrequencyScore = influenceFrequencyScore;
        this.advocacyScore = advocacyScore;
        this.favouriteBrandIds = favouriteBrandIds;
        this.defaultPrivacySetting = defaultPrivacySetting;
    }

    public long getSocialAccountId() {
        return socialAccountId;
    }

    public void setSocialAccountId(long socialAccountId) {
        this.socialAccountId = socialAccountId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
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

    public String getDefaultPrivacySetting() {
        return defaultPrivacySetting;
    }

    public void setDefaultPrivacySetting(String defaultPrivacySetting) {
        this.defaultPrivacySetting = defaultPrivacySetting;
    }

    @Override
    public String toString() {
        return "[accessToken: \"" + value(this.accessToken) + "\", profilePictureUrl: \"" + value(this.profilePictureUrl)
                + "\", facebookId: \"" + value(this.facebookId) + "\", expirationTime: \"" + this.expirationTime + "]\n";
    }

    private String value(String s) {
        return s == null ? "null" : s;
    }
}
