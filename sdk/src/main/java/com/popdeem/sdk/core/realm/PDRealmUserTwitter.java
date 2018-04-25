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

package com.popdeem.sdk.core.realm;

import com.popdeem.sdk.core.model.PDUserTwitter;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikenolan on 16/08/16.
 */
public class PDRealmUserTwitter extends RealmObject {

    @PrimaryKey
    private long socialAccountId;
    private String twitterId;
    private boolean tester;
    private String accessToken;
    private String accessSecret;
    private long expirationTime;
    private String profilePictureUrl;

    public PDRealmUserTwitter() {
    }

    public PDRealmUserTwitter(PDUserTwitter userTwitter) {
        this.socialAccountId = userTwitter.getSocialAccountId();
        this.twitterId = userTwitter.getTwitterId();
        this.tester = userTwitter.isTester();
        this.accessToken = userTwitter.getAccessToken();
        this.accessSecret = userTwitter.getAccessSecret();
        this.expirationTime = userTwitter.getExpirationTime();
        this.profilePictureUrl = userTwitter.getProfilePictureUrl();
    }

    public long getSocialAccountId() {
        return socialAccountId;
    }

    public void setSocialAccountId(long socialAccountId) {
        this.socialAccountId = socialAccountId;
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
}
