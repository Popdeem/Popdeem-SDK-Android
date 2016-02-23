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

    // Score
//    private float totalScore;
//    private float reachScore;
//    private float engagementScore;
//    private float frequencyScore;
//    private float advocacyScore;

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

    public PDUser(String userToken, String id, String firstName, String lastName, String sex, String college, String suspendUntil, String type, PDUserFacebook pdUserFacebook, PDUserTwitter pdUserTwitter, double latitude, double longitude, ArrayList<PDSocialMediaFriend> taggableFriends, int likesCount) {
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
//
//    public float getAdvocacyScore() {
//        return advocacyScore;
//    }
//
//    public void setAdvocacyScore(float advocacyScore) {
//        this.advocacyScore = advocacyScore;
//    }

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


//    /**
//     * JsonDeserializer for PDUser object
//     */
//    public static class PDUserDeserializer implements JsonDeserializer<PDUser> {
//
//        private Context appContext;
//
//        public PDUserDeserializer(Context appContext) {
//            this.appContext = appContext;
//        }
//
//        @Override
//        public PDUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//
//            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(long.class, new PDLongDeserializer())
//                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                    .create();
//
//            JsonElement userElement = json.getAsJsonObject().get("user");
//            PDUser userDetail = gson.fromJson(userElement, PDUser.class);
//
//            JsonElement facebookElement = userElement.getAsJsonObject().get("facebook");
//            PDUserFacebook facebookDetail = gson.fromJson(facebookElement, PDUserFacebook.class);
//
//            if (facebookElement.getAsJsonObject().has("score")) {
//                JsonElement scoreElement = facebookElement.getAsJsonObject().get("score");
//
//                // Total Score
//                JsonElement totalScoreElement = scoreElement.getAsJsonObject().get("total_score");
//
//                String totalScore = totalScoreElement.getAsJsonObject().get("value").getAsString();
////                userDetail.setTotalScore(PDNumberUtils.toFloat(totalScore, 0));
////                if (userDetail.getTotalScore() == 0) {
////                    userDetail.setTotalScore(75);
////                }
//
//                // Influence
//                if (scoreElement.getAsJsonObject().has("influence_score")) {
//                    JsonElement influenceElement = scoreElement.getAsJsonObject().get("influence_score");
//
//                    String reachScore = influenceElement.getAsJsonObject().get("reach_score_value").getAsString();
////                    userDetail.setReachScore(PDNumberUtils.toFloat(reachScore, 0));
//
//                    // TODO remove misspelling when it is removed on server side
//                    String engagementScore = null;
//                    if (influenceElement.getAsJsonObject().has("engangement_score_value")) {
//                        engagementScore = influenceElement.getAsJsonObject().get("engangement_score_value").getAsString();
//                    } else if (influenceElement.getAsJsonObject().has("engagement_score_value")) {
//                        engagementScore = influenceElement.getAsJsonObject().get("engagement_score_value").getAsString();
//                    }
//                    if (engagementScore != null) {
////                        userDetail.setEngagementScore(PDNumberUtils.toFloat(engagementScore, 0));
////                        if (userDetail.getEngagementScore() == 0) {
////                            userDetail.setEngagementScore(60);
////                        }
//                    }
//
//                    if (influenceElement.getAsJsonObject().has("frequency_score_value")) {
//                        String frequencyScore = influenceElement.getAsJsonObject().get("frequency_score_value").getAsString();
////                        userDetail.setFrequencyScore(PDNumberUtils.toFloat(frequencyScore, 0));
//                    }
//                } else {
////                    userDetail.setReachScore(0);
////                    userDetail.setEngagementScore(60);
////                    userDetail.setFrequencyScore(0);
//                }
//
//
//                //advocacy_score
//                if (scoreElement.getAsJsonObject().has("advocacy_score")) {
//                    JsonElement advocacyElement = scoreElement.getAsJsonObject().get("advocacy_score");
//
//                    String advocacyScore = advocacyElement.getAsJsonObject().get("value").getAsString();
////                    userDetail.setAdvocacyScore(PDNumberUtils.toFloat(advocacyScore, 0));
//                } else {
////                    userDetail.setAdvocacyScore(0);
//                }
//            }
//
//            JsonElement twitterElement = userElement.getAsJsonObject().get("twitter");
//            PDUserTwitter twitterDetail = gson.fromJson(twitterElement, PDUserTwitter.class);
//
//            userDetail.setPdUserFacebook(facebookDetail);
//            userDetail.setPdUserTwitter(twitterDetail);
//
//            if (appContext != null) {
////                PDDataManager.setUserToken(appContext, userDetail.getUserToken());
////                PDDataManager.savePDUser(appContext, userDetail);
//            }
//
//            return userDetail;
//        }
//    }

//    /**
//     *
//     */
//    public static class PDUserLongDeserializer implements JsonDeserializer<Long> {
//
//        @Override
//        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            if (json.getAsString().equalsIgnoreCase("")) {
//                return (long) -1;
//            } else {
//                return json.getAsLong();
//            }
//        }
//    }

//    /**
//     *
//     */
//    public static class PDIntDeserializer implements JsonDeserializer<Integer> {
//
//        @Override
//        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            String s = json.getAsString();
//            if (s.equalsIgnoreCase("") || s.equalsIgnoreCase("no limit")) {
//                return -1;
//            } else {
//                return json.getAsInt();
//            }
//        }
//    }

}
