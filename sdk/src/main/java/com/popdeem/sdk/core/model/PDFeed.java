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

import io.realm.RealmObject;

/**
 * Popdeem Feed Model Class
 */
public class PDFeed extends RealmObject {

    private int userId;
    private String brandLogoUrlString;
    private String brandName;
    @SerializedName("picture=")
    private String imageUrlString;
    private String rewardTypeString;
    private String userProfilePicUrlString;
    private String userFirstName;
    private String userLastName;
    @SerializedName("text")
    private String actionText;
    private String timeAgo;
    private String descriptionString;
    private String caption;

    public PDFeed() {
        this.userId = -1;
        this.brandLogoUrlString = "";
        this.brandName = "";
        this.imageUrlString = "";
        this.rewardTypeString = "";
        this.userProfilePicUrlString = "";
        this.userFirstName = "";
        this.userLastName = "";
        this.actionText = "";
        this.timeAgo = "";
        this.descriptionString = "";
        this.caption = "";
    }

    public PDFeed(int userId, String brandLogoUrlString, String brandName, String imageUrlString, String rewardTypeString, String userProfilePicUrlString, String userFirstName, String userLastName, String actionText, String timeAgo, String descriptionString, String caption) {
        this.userId = userId;
        this.brandLogoUrlString = brandLogoUrlString;
        this.brandName = brandName;
        this.imageUrlString = imageUrlString;
        this.rewardTypeString = rewardTypeString;
        this.userProfilePicUrlString = userProfilePicUrlString;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.actionText = actionText;
        this.timeAgo = timeAgo;
        this.descriptionString = descriptionString;
        this.caption = caption;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBrandLogoUrlString() {
        return brandLogoUrlString;
    }

    public void setBrandLogoUrlString(String brandLogoUrlString) {
        this.brandLogoUrlString = brandLogoUrlString;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getImageUrlString() {
        return imageUrlString;
    }

    public void setImageUrlString(String imageUrlString) {
        this.imageUrlString = imageUrlString;
    }

    public String getRewardTypeString() {
        return rewardTypeString;
    }

    public void setRewardTypeString(String rewardTypeString) {
        this.rewardTypeString = rewardTypeString;
    }

    public String getUserProfilePicUrlString() {
        return userProfilePicUrlString;
    }

    public void setUserProfilePicUrlString(String userProfilePicUrlString) {
        this.userProfilePicUrlString = userProfilePicUrlString;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getDescriptionString() {
        return descriptionString;
    }

    public void setDescriptionString(String descriptionString) {
        this.descriptionString = descriptionString;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
