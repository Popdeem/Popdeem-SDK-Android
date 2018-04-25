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

import com.popdeem.sdk.core.model.PDUser;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikenolan on 18/02/16.
 */
public class PDRealmUserDetails extends RealmObject {

    @PrimaryKey
    private int uid;
    private String id;
    private String firstName;
    private String lastName;
    private String sex;
    private String college;
    private String type;
    private String userToken;
    private String suspendUntil;
    private PDRealmUserFacebook userFacebook;
    private PDRealmUserInstagram userInstagram;
    private PDRealmUserTwitter userTwitter;
    private float advocacy_score;

    public PDRealmUserDetails() {
    }

    public PDRealmUserDetails(PDUser user) {
        this.uid = 0;
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.sex = user.getSex();
        this.college = user.getCollege();
        this.type = user.getType();
        this.userToken = user.getUserToken();
        this.suspendUntil = user.getSuspendUntil();
        this.userFacebook = new PDRealmUserFacebook(user.getPdUserFacebook());
        this.userInstagram = new PDRealmUserInstagram(user.getPdUserInstagram());
        this.userTwitter = new PDRealmUserTwitter(user.getPdUserTwitter());
        this.advocacy_score = Float.valueOf(user.getAdvocacyScore());
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getSuspendUntil() {
        return suspendUntil;
    }

    public void setSuspendUntil(String suspendUntil) {
        this.suspendUntil = suspendUntil;
    }

    public PDRealmUserFacebook getUserFacebook() {
        return userFacebook;
    }

    public void setUserFacebook(PDRealmUserFacebook userFacebook) {
        this.userFacebook = userFacebook;
    }

    public PDRealmUserInstagram getUserInstagram() {
        return userInstagram;
    }

    public void setUserInstagram(PDRealmUserInstagram userInstagram) {
        this.userInstagram = userInstagram;
    }

    public PDRealmUserTwitter getUserTwitter() {
        return userTwitter;
    }

    public void setUserTwitter(PDRealmUserTwitter userTwitter) {
        this.userTwitter = userTwitter;
    }

    public float getAdvocacyScore() {
        return advocacy_score;
    }

    public void setAdvocacyScore(float advocacy_score) {
        this.advocacy_score = advocacy_score;
    }
}
