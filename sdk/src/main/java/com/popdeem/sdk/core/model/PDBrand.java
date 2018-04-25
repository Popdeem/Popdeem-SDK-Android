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

import java.util.ArrayList;

/**
 * Popdeem Brand Model Class
 */
public class PDBrand {

    private String id;
    private String name;
    private String logo;
    private String coverImage;
    private String fbPageId;
    private String fbPageUrl;
    private String numberOfLocations;
    private String numberOfRewardsAvailable;
    private boolean locationVerification;
    private PDBrandContact contacts;
    private ArrayList<PDBrandOpeningTime> openingTimes;
    private ArrayList<PDLocation> locations;
    private ArrayList<PDReward> rewards;

    public PDBrand() {
    }

    public PDBrand(String id, String name, String logo, String coverImage, String fbPageId, String fbPageUrl, String numberOfLocations, String numberOfRewardsAvailable, boolean locationVerification, PDBrandContact contacts, ArrayList<PDBrandOpeningTime> openingTimes, ArrayList<PDLocation> locations) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.coverImage = coverImage;
        this.fbPageId = fbPageId;
        this.fbPageUrl = fbPageUrl;
        this.numberOfLocations = numberOfLocations;
        this.numberOfRewardsAvailable = numberOfRewardsAvailable;
        this.locationVerification = locationVerification;
        this.contacts = contacts;
        this.openingTimes = openingTimes;
        this.locations = locations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public String getNumberOfLocations() {
        return numberOfLocations;
    }

    public void setNumberOfLocations(String numberOfLocations) {
        this.numberOfLocations = numberOfLocations;
    }

    public String getNumberOfRewardsAvailable() {
        return numberOfRewardsAvailable;
    }

    public void setNumberOfRewardsAvailable(String numberOfRewardsAvailable) {
        this.numberOfRewardsAvailable = numberOfRewardsAvailable;
    }

    public boolean isLocationVerification() {
        return locationVerification;
    }

    public void setLocationVerification(boolean locationVerification) {
        this.locationVerification = locationVerification;
    }

    public PDBrandContact getContacts() {
        return contacts;
    }

    public void setContacts(PDBrandContact contacts) {
        this.contacts = contacts;
    }

    public ArrayList<PDBrandOpeningTime> getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(ArrayList<PDBrandOpeningTime> openingTimes) {
        this.openingTimes = openingTimes;
    }

    public ArrayList<PDLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<PDLocation> locations) {
        this.locations = locations;
    }

    public ArrayList<PDReward> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<PDReward> rewards) {
        this.rewards = rewards;
    }

//    public static class PDBrandArrayDeserialize implements JsonDeserializer<ArrayList<PDBrand>> {
//
//        @Override
//        public ArrayList<PDBrand> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
//            JsonObject jsonObject = json.getAsJsonObject();
//            if (jsonObject.has("brands")) {
//                Gson gson = new GsonBuilder()
//                        .registerTypeAdapter(PDBrand.class, new PDBrandDeserializer())
//                        .registerTypeAdapter(long.class, new PDLongDeserializer())
//                        .registerTypeAdapter(int.class, new PDIntDeserializer())
//                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                        .create();
//
//                JsonArray brandsArray = jsonObject.getAsJsonArray("brands");
//                return gson.fromJson(brandsArray, type);
//            }
//
//            return new ArrayList<>();
//        }
//    }

//    public static class PDBrandDeserializer implements JsonDeserializer<PDBrand> {
//
//        @Override
//        public PDBrand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            JsonObject jsonObject = json.getAsJsonObject();
//
//            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(PDLocation.class, new PDLocation.PDLocationDeserializer())
//                    .registerTypeAdapter(long.class, new PDLongDeserializer())
//                    .registerTypeAdapter(int.class, new PDIntDeserializer())
//                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                    .create();
//
//            PDBrand brand = gson.fromJson(jsonObject, PDBrand.class);
//
//            JsonElement contactsElement = jsonObject.getAsJsonObject("contacts");
//            PDBrandContact contact = gson.fromJson(contactsElement, PDBrandContact.class);
//            brand.setContacts(contact);
//
//            ArrayList<PDBrandOpeningTime> openingTimes = new ArrayList<>();
//            // TODO opening times
////            JsonElement openingTimesElement = json.getAsJsonObject().getAsJsonObject("opening_time");
////            JsonObject openingTimesObject = openingTimesElement.getAsJsonObject();
////            if (openingTimesObject.has("monday")) {
////                JsonElement mondayElement = openingTimesElement.getAsJsonObject().getAsJsonObject("monday");
////                PDBrandOpeningTime openingTime = gson.fromJson(mondayElement, PDBrandOpeningTime.class);
////                openingTime.setDay(Calendar.MONDAY);
////            }
//            brand.setOpeningTimes(openingTimes);
//
//            JsonArray locationsArray = jsonObject.getAsJsonArray("locations");
//            Type locationsType = new TypeToken<ArrayList<PDLocation>>() {
//            }.getType();
//            ArrayList<PDLocation> locations = gson.fromJson(locationsArray, locationsType);
//
//            brand.setLocations(locations);
//
//            return brand;
//        }
//    }

}
