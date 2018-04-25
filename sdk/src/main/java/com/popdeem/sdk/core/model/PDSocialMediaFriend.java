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

/**
 * Popdeem Social Media Friend Model Class
 */
public class PDSocialMediaFriend {

    @SerializedName("id")
    private String tagIdentifier;

    private String name;
    private String imageUrl;

    boolean selected;

    public PDSocialMediaFriend() {
    }

    public PDSocialMediaFriend(String tagIdentifier, String name, String imageUrl, boolean selected) {
        this.tagIdentifier = tagIdentifier;
        this.name = name;
        this.imageUrl = imageUrl;
        this.selected = selected;
    }

    public String getTagIdentifier() {
        return tagIdentifier;
    }

    public void setTagIdentifier(String tagIdentifier) {
        this.tagIdentifier = tagIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


//    public static class PDSocialMediaFriendDeserializer implements JsonDeserializer<ArrayList<PDSocialMediaFriend>> {
//
//        @Override
//        public ArrayList<PDSocialMediaFriend> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            Gson gson = new GsonBuilder()
////                    .registerTypeAdapter(long.class, new PDUser.PDUserLongDeserializer())
////                    .registerTypeAdapter(int.class, new PDUser.PDIntDeserializer())
//                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                    .create();
//
//            ArrayList<PDSocialMediaFriend> friends = new ArrayList<>();
//
//            JsonArray dataArray = json.getAsJsonObject().getAsJsonArray("data");
//            for (int i = 0; i < dataArray.size(); i++) {
//                JsonElement element = dataArray.get(i);
//                PDSocialMediaFriend friend = gson.fromJson(element, PDSocialMediaFriend.class);
//
//                JsonObject dataObject = element.getAsJsonObject();
//                if (dataObject.has("picture")) {
//                    JsonObject pictureObject = dataObject.getAsJsonObject("picture");
//                    if (pictureObject.has("data")) {
//                        JsonObject pictureDataObject = pictureObject.getAsJsonObject("data");
//                        friend.setImageUrl(pictureDataObject.get("url").getAsString());
//                    }
//                }
//
//                friends.add(friend);
//            }
//
//            return friends;
//        }
//    }
}
