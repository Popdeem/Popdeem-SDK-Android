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
import com.popdeem.sdk.core.model.PDUserFacebook;

import java.lang.reflect.Type;

/**
 * Created by mikenolan on 16/03/16.
 */
public class PDSocialAccountFacebookDeserializer implements JsonDeserializer<PDUserFacebook> {

    @Override
    public PDUserFacebook deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        PDUserFacebook socialAccountFacebook = gson.fromJson(json, typeOfT);

        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("score")) {
            JsonObject scoreObject = jsonObject.getAsJsonObject("score");

            // Total Score
            if (scoreObject.has("total_score")) {
                JsonObject totalScoreObject = scoreObject.getAsJsonObject("total_score");
                if (totalScoreObject.has("value")) {
                    socialAccountFacebook.setTotalScore(totalScoreObject.get("value").getAsString());
                }
            }

            // influence score
            if (scoreObject.has("influence_score")) {
                JsonObject influenceScoreObject = scoreObject.getAsJsonObject("influence_score");
                if (influenceScoreObject.has("reach_score_value")) {
                    socialAccountFacebook.setInfluenceReachScore(influenceScoreObject.get("reach_score_value").getAsString());
                }
                if (influenceScoreObject.has("engagement_score_value")) {
                    socialAccountFacebook.setInfluenceEngagementScore(influenceScoreObject.get("engagement_score_value").getAsString());
                }
                if (influenceScoreObject.has("frequency_score_value")) {
                    socialAccountFacebook.setInfluenceFrequencyScore(influenceScoreObject.get("frequency_score_value").getAsString());
                }
            }

            // advocacy score
            if (scoreObject.has("advocacy_score")) {
                JsonObject advocacyScoreObject = scoreObject.getAsJsonObject("advocacy_score");
                if (advocacyScoreObject.has("value")) {
                    socialAccountFacebook.setAdvocacyScore(advocacyScoreObject.get("value").getAsInt());
                }
            }
        }

        return socialAccountFacebook;
    }

}
