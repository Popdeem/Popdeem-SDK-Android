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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mikenolan on 04/03/16.
 */
public class PDReferral {

    public static final String PD_REFERRAL_TYPE_OPEN = "open";
    public static final String PD_REFERRAL_TYPE_INSTALL = "install";

    @StringDef({PD_REFERRAL_TYPE_OPEN, PD_REFERRAL_TYPE_INSTALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PDReferralType {
    }


    @PDReferralType
    private String type;
    private String senderAppName;
    private long senderId;
    private long requestId;

    public PDReferral() {
    }

    public PDReferral(String type, String senderAppName, long senderId, long requestId) {
        this.type = type;
        this.senderAppName = senderAppName;
        this.senderId = senderId;
        this.requestId = requestId;
    }

    public String getType() {
        return type;
    }

    public void setType(@PDReferralType String type) {
        this.type = type;
    }

    public String getSenderAppName() {
        return senderAppName;
    }

    public void setSenderAppName(String senderAppName) {
        this.senderAppName = senderAppName;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

}
