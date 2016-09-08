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
 * Popdeem User Twitter Model Class
 */
public class PDUserTwitter {

    private long socialAccountId;
    private String accessToken;
    private String accessSecret;
    private String twitterId;
    private boolean tester;
    private String twitterScreenName;
    private String profilePictureUrl;
    private long expirationTime;

    public PDUserTwitter() {
    }

    public PDUserTwitter(long socialAccountId, String accessToken, String accessSecret, String twitterId, boolean tester, String twitterScreenName, String profilePictureUrl, long expirationTime) {
        this.socialAccountId = socialAccountId;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
        this.twitterId = twitterId;
        this.tester = tester;
        this.twitterScreenName = twitterScreenName;
        this.profilePictureUrl = profilePictureUrl;
        this.expirationTime = expirationTime;
    }

    public long getSocialAccountId() {
        return socialAccountId;
    }

    public void setSocialAccountId(long socialAccountId) {
        this.socialAccountId = socialAccountId;
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

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public boolean isTester() {
        return tester;
    }

    public void setTester(boolean tester) {
        this.tester = tester;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }

    public void setTwitterScreenName(String twitterScreenName) {
        this.twitterScreenName = twitterScreenName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "[accessToken: \"" + value(this.accessToken) + "\", accessSecret: \"" + value(this.accessSecret)
                + "\", twitterId: \"" + value(this.twitterId) + "\", twitterScreenName: \"" + value(this.twitterScreenName)
                + "\" profilePictureUrl: \"" + value(this.profilePictureUrl) + "\", profilePictureUrl: " + value(this.profilePictureUrl) + "]\n";
    }

    private String value(String s) {
        return s == null ? "null" : s;
    }
}
