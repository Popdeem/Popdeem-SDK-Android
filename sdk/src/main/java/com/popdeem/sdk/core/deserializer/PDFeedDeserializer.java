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

package com.popdeem.sdk.core.deserializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.popdeem.sdk.core.model.PDFeed;

import java.lang.reflect.Type;

/**
 * Created by mikenolan on 18/02/16.
 */
public class PDFeedDeserializer implements JsonDeserializer<PDFeed> {

    @Override
    public PDFeed deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        PDFeed feed = gson.fromJson(json, typeOfT);

        JsonObject feedObject = json.getAsJsonObject();
        // Brand
        if (feedObject.has("brand")) {
            JsonObject brandObject = feedObject.getAsJsonObject("brand");
            feed.setBrandName(brandObject.get("name").getAsString());
            feed.setBrandLogoUrlString(brandObject.get("logo").getAsString());
        }

        // Reward
        if (feedObject.has("reward")) {
            JsonObject brandObject = feedObject.getAsJsonObject("reward");
//            feed.setRewardTypeString(brandObject.get("type").getAsString());
            feed.setDescriptionString(brandObject.get("description").getAsString());
        }

        // Social Account
        if (feedObject.has("social_account")) {
            JsonObject socialObject = feedObject.getAsJsonObject("social_account");
            if (socialObject.has("profile_picture")) {
                feed.setUserProfilePicUrlString(socialObject.get("profile_picture").getAsString());
            }

            if (socialObject.has("user")) {
                JsonObject userObject = socialObject.getAsJsonObject("user");
                feed.setUserId(userObject.get("id").getAsInt());
                if (userObject.has("first_name") && !userObject.get("first_name").isJsonNull()) {
                    feed.setUserFirstName(userObject.get("first_name").getAsString());
                }
                if (userObject.has("last_name") && !userObject.get("last_name").isJsonNull()) {
                    feed.setUserLastName(userObject.get("last_name").getAsString());
                }
            }
        }

//        feed.setTimeAgo(feedObject.get("time_ago").getAsString());
//        feed.setImageUrlString(feedObject.get("picture=").getAsString());
//        feed.setActionText(feedObject.get("text").getAsString());

        return feed;
    }

}
