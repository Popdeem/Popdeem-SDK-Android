package com.popdeem.sdk.core.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PDPostScan {

    @SerializedName("validated")
    @Expose
    private Boolean validated;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("media_url")
    @Expose
    private String mediaUrl;
    @SerializedName("network")
    @Expose
    private String network;
    @SerializedName("post_key")
    @Expose
    private String postKey;
    @SerializedName("profile_picture_url")
    @Expose
    private String profilePictureUrl;
    @SerializedName("social_name")
    @Expose
    private String socialName;

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getSocialName() {
        return socialName;
    }

    public void setSocialName(String socialName) {
        this.socialName = socialName;
    }

}

