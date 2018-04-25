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

import com.popdeem.sdk.core.realm.PDRealmRewardClaimingSocialNetwork;

import io.realm.RealmObject;

/**
 * Created by mikenolan on 10/08/16.
 */
public class PDRewardClaimingSocialNetwork {

    /*
        "name": "Instagram",
        "social_account_id": 57917,
        "created_at": "2016-08-09T20:53:39.000Z",
        "updated_at": "2016-08-09T20:53:39.000Z"
     */

    private String name;
    private long socialAccountId;
    private String createdAt;
    private String updatedAt;

    public PDRewardClaimingSocialNetwork() {
    }

    public PDRewardClaimingSocialNetwork(String name, long socialAccountId, String createdAt, String updatedAt) {
        this.name = name;
        this.socialAccountId = socialAccountId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public PDRewardClaimingSocialNetwork(PDRealmRewardClaimingSocialNetwork reward) {
        this.name = reward.getName();
        this.socialAccountId = reward.getSocialAccountId();
        this.createdAt = reward.getCreatedAt();
        this.updatedAt = reward.getUpdatedAt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSocialAccountId() {
        return socialAccountId;
    }

    public void setSocialAccountId(long socialAccountId) {
        this.socialAccountId = socialAccountId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
