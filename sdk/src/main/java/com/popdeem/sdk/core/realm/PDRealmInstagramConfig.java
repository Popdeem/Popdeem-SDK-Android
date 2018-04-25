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

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikenolan on 05/08/16.
 */
public class PDRealmInstagramConfig extends RealmObject {

    @PrimaryKey
    private int uid;
    private String instagramClientId;
    private String instagramClientSecret;
    private String instagramCallbackUrl;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getInstagramClientId() {
        return instagramClientId;
    }

    public void setInstagramClientId(String instagramClientId) {
        this.instagramClientId = instagramClientId;
    }

    public String getInstagramClientSecret() {
        return instagramClientSecret;
    }

    public void setInstagramClientSecret(String instagramClientSecret) {
        this.instagramClientSecret = instagramClientSecret;
    }

    public String getInstagramCallbackUrl() {
        return instagramCallbackUrl;
    }

    public void setInstagramCallbackUrl(String instagramCallbackUrl) {
        this.instagramCallbackUrl = instagramCallbackUrl;
    }
}
