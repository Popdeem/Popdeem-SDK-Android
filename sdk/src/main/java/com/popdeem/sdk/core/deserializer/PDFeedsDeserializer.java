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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.popdeem.sdk.core.model.PDFeed;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by mikenolan on 18/02/16.
 */
public class PDFeedsDeserializer implements JsonDeserializer<ArrayList<PDFeed>> {

    public static Type FEEDS_TYPE = new TypeToken<ArrayList<PDFeed>>() {
    }.getType();

    @Override
    public ArrayList<PDFeed> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("feeds")) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PDFeed.class, new PDFeedDeserializer())
                    .registerTypeAdapter(long.class, new PDLongDeserializer())
                    .registerTypeAdapter(int.class, new PDIntDeserializer())
                    .create();

            JsonArray feedsArray = jsonObject.getAsJsonArray("feeds");
            return gson.fromJson(feedsArray, typeOfT);
        }

        return new ArrayList<>();
    }

}
