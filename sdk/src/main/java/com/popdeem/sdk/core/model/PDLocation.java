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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.popdeem.sdk.core.deserializer.PDIntDeserializer;
import com.popdeem.sdk.core.deserializer.PDLongDeserializer;
import com.popdeem.sdk.core.realm.PDRealmLocation;

import java.lang.reflect.Type;

import io.realm.RealmObject;

/**
 * Popdeem Location Model Class
 */
public class PDLocation {

    private String id;
    private String latitude;
    private String longitude;
    private String address;
    private String twitterPageId;
    private String fbPageId;
    private String fbPageUrl;
    private String numberOfRewards;
    private String brandIdentifier;
    private String brandName;

    public PDLocation() {
    }

    public PDLocation(String id, String latitude, String longitude, String address, String twitterPageId, String fbPageId, String fbPageUrl, String numberOfRewards, String brandIdentifier, String brandName) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.twitterPageId = twitterPageId;
        this.fbPageId = fbPageId;
        this.fbPageUrl = fbPageUrl;
        this.numberOfRewards = numberOfRewards;
        this.brandIdentifier = brandIdentifier;
        this.brandName = brandName;
    }

    public PDLocation(PDRealmLocation location) {
        this.id = location.getId();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.address = location.getAddress();
        this.twitterPageId = location.getTwitterPageId();
        this.fbPageId = location.getFbPageId();
        this.fbPageUrl = location.getFbPageUrl();
        this.numberOfRewards = location.getNumberOfRewards();
        this.brandIdentifier = location.getBrandIdentifier();
        this.brandName = location.getBrandName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTwitterPageId() {
        return twitterPageId;
    }

    public void setTwitterPageId(String twitterPageId) {
        this.twitterPageId = twitterPageId;
    }

    public String getFbPageId() {
        return fbPageId;
    }

    public void setFbPageId(String fbPageId) {
        this.fbPageId = fbPageId;
    }

    public String getFbPageUrl() {
        return fbPageUrl;
    }

    public void setFbPageUrl(String fbPageUrl) {
        this.fbPageUrl = fbPageUrl;
    }

    public String getNumberOfRewards() {
        return numberOfRewards;
    }

    public void setNumberOfRewards(String numberOfRewards) {
        this.numberOfRewards = numberOfRewards;
    }

    public String getBrandIdentifier() {
        return brandIdentifier;
    }

    public void setBrandIdentifier(String brandIdentifier) {
        this.brandIdentifier = brandIdentifier;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public static class PDLocationDeserializer implements JsonDeserializer<PDLocation> {

        @Override
        public PDLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(long.class, new PDLongDeserializer())
                    .registerTypeAdapter(int.class, new PDIntDeserializer())
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            JsonObject jsonObject = json.getAsJsonObject();

            PDLocation location = gson.fromJson(jsonObject, PDLocation.class);
            if (jsonObject.has("brand")) {
                JsonObject brandObject = jsonObject.getAsJsonObject("brand");
                location.setBrandIdentifier(brandObject.get("id").getAsString());
                location.setBrandName(brandObject.get("name").getAsString());
            }

            return location;
        }
    }

//    @Deprecated
//    public static class PDLocationJsonDeserializer implements JsonDeserializer<PDLocation> {
//
//        private PDBrand brand;
//
//        public PDLocationJsonDeserializer(PDBrand brand) {
//            this.brand = brand;
//        }
//
//        @Override
//        public PDLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            PDLocation location = new Gson().fromJson(json, PDLocation.class);
//            location.setBrandIdentifier(this.brand.getId());
//            location.setBrandName(this.brand.getName());
//            return location;
//        }
//    }
}
