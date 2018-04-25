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

import java.util.ArrayList;

/**
 * Popdeem User Model Class
 */
public final class PDUser {

    @SerializedName("user_token")
    private String userToken;

    // User detail
    private String id;
    private String firstName;
    private String lastName;
    private String sex;

    private String college;
    private String suspendUntil;
    private String type;

    @SerializedName("facebook")
    private PDUserFacebook pdUserFacebook;
    @SerializedName("twitter")
    private PDUserTwitter pdUserTwitter;
    @SerializedName("instagram")
    private PDUserInstagram pdUserInstagram;

    // Score
//    private float totalScore;
//    private float reachScore;
//    private float engagementScore;
//    private float frequencyScore;
    private float advocacy_score;

    //Location lat/long for user
    private double latitude;
    private double longitude;

//    private double lastLocationLat;
//    private double lastLocationLong;


    // Taggable Friends
    private ArrayList<PDSocialMediaFriend> taggableFriends;
    private int likesCount;

    public PDUser() {
        this.firstName = "";
        this.lastName = "";
    }

    public PDUser(String userToken, String id, String firstName, String lastName, String sex, String college, String suspendUntil, String type, PDUserFacebook pdUserFacebook, PDUserTwitter pdUserTwitter, PDUserInstagram pdUserInstagram, double latitude, double longitude, ArrayList<PDSocialMediaFriend> taggableFriends, int likesCount) {
        this.userToken = userToken;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.college = college;
        this.suspendUntil = suspendUntil;
        this.type = type;
        this.pdUserFacebook = pdUserFacebook;
        this.pdUserTwitter = pdUserTwitter;
        this.pdUserInstagram = pdUserInstagram;
        this.latitude = latitude;
        this.longitude = longitude;
        this.taggableFriends = taggableFriends;
        this.likesCount = likesCount;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
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

    public String getSuspendUntil() {
        return suspendUntil;
    }

    public void setSuspendUntil(String suspendUntil) {
        this.suspendUntil = suspendUntil;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PDUserFacebook getPdUserFacebook() {
        return pdUserFacebook;
    }

    public void setPdUserFacebook(PDUserFacebook pdUserFacebook) {
        this.pdUserFacebook = pdUserFacebook;
    }

    public PDUserTwitter getPdUserTwitter() {
        return pdUserTwitter;
    }

    public void setPdUserTwitter(PDUserTwitter pdUserTwitter) {
        this.pdUserTwitter = pdUserTwitter;
    }

    public PDUserInstagram getPdUserInstagram() {
        return pdUserInstagram;
    }

    public void setPdUserInstagram(PDUserInstagram pdUserInstagram) {
        this.pdUserInstagram = pdUserInstagram;
    }

    //    public float getTotalScore() {
//        return totalScore;
//    }
//
//    public void setTotalScore(float totalScore) {
//        this.totalScore = totalScore;
//    }
//
//    public float getReachScore() {
//        return reachScore;
//    }
//
//    public void setReachScore(float reachScore) {
//        this.reachScore = reachScore;
//    }
//
//    public float getEngagementScore() {
//        return engagementScore;
//    }
//
//    public void setEngagementScore(float engagementScore) {
//        this.engagementScore = engagementScore;
//    }
//
//    public float getFrequencyScore() {
//        return frequencyScore;
//    }
//
//    public void setFrequencyScore(float frequencyScore) {
//        this.frequencyScore = frequencyScore;
//    }

    public float getAdvocacyScore() {
        return advocacy_score;
    }

    public void setAdvocacyScore(float advocacy_score) {
        this.advocacy_score = advocacy_score;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

//    public double getLastLocationLat() {
//        return lastLocationLat;
//    }
//
//    public void setLastLocationLat(double lastLocationLat) {
//        this.lastLocationLat = lastLocationLat;
//    }
//
//    public double getLastLocationLong() {
//        return lastLocationLong;
//    }
//
//    public void setLastLocationLong(double lastLocationLong) {
//        this.lastLocationLong = lastLocationLong;
//    }

    public ArrayList<PDSocialMediaFriend> getTaggableFriends() {
        return taggableFriends;
    }

    public void setTaggableFriends(ArrayList<PDSocialMediaFriend> taggableFriends) {
        this.taggableFriends = taggableFriends;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    @Override
    public String toString() {
        return "[firstName: \"" + value(this.firstName) + "\", lastName: \"" + value(this.lastName) + "\", sex: \"" + value(this.sex)
                + "\", userToken: \"" + value(this.userToken) + "\", id: \"" + value(this.id) + "\"]\n"
                + "[facebook: " + (this.pdUserFacebook == null ? "null" : this.pdUserFacebook.toString()) + "]\n[twitter: " + (this.pdUserTwitter == null ? "null" : this.pdUserTwitter.toString()) + "]";
    }

    private String value(String s) {
        return s == null ? "null" : s;
    }

    /**
     * @return a boolean as to whether the userToken is NULL
     */
    public boolean isRegistered(){
        return userToken != null;
    }
}
