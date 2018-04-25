package com.popdeem.sdk.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dave on 12/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDBGScanResponseModel {

    @SerializedName("media_url")
    private String mediaUrl;

    @SerializedName("network")
    private String network;

    @SerializedName("post_key")
    private String objectID;

    @SerializedName("text")
    private String text;

    @SerializedName("social_name")
    private String socialName;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    @SerializedName("validated")
    private boolean validated;

    public PDBGScanResponseModel(String mediaUrl, String network, String objectID, String text, String socialName, String profilePictureUrl, boolean validated) {
        this.mediaUrl = mediaUrl;
        this.network = network;
        this.objectID = objectID;
        this.text = text;
        this.socialName = socialName;
        this.profilePictureUrl = profilePictureUrl;
        this.validated = validated;
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

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSocialName() {
        return socialName;
    }

    public void setSocialName(String socialName) {
        this.socialName = socialName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
