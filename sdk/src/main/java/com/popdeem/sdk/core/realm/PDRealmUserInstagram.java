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

import com.popdeem.sdk.core.model.PDUserInstagram;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikenolan on 05/08/16.
 */
public class PDRealmUserInstagram extends RealmObject {

    @PrimaryKey
    private long socialAccountId;
    private String instagramId;
    private boolean tester;
    private String accessToken;
    private String accessSecret;
    private String screenName;
    private String profilePictureUrl;

    public PDRealmUserInstagram() {
    }

    public PDRealmUserInstagram(PDUserInstagram instagram) {
        this.socialAccountId = instagram.getSocialAccountId();
        this.instagramId = instagram.getInstagramId();
        this.tester = instagram.getTester();
        this.accessToken = instagram.getAccessToken();
        this.accessSecret = instagram.getAccessSecret();
        this.screenName = instagram.getScreenName();
        this.profilePictureUrl = instagram.getProfilePictureUrl();
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
}
