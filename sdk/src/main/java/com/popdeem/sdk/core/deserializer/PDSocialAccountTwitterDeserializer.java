package com.popdeem.sdk.core.deserializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.popdeem.sdk.core.model.PDUserTwitter;

import java.lang.reflect.Type;

/**
 * Created by dave on 03/05/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDSocialAccountTwitterDeserializer implements JsonDeserializer<PDUserTwitter> {

    @Override
    public PDUserTwitter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        PDUserTwitter socialAccountTwitter = gson.fromJson(json, typeOfT);

        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("score")) {
            JsonObject scoreObject = jsonObject.getAsJsonObject("score");

            // Total Score
            if (scoreObject.has("total_score")) {
                JsonObject totalScoreObject = scoreObject.getAsJsonObject("total_score");
                if (totalScoreObject.has("value")) {
                    socialAccountTwitter.setTotalScore(totalScoreObject.get("value").getAsString());
                }
            }

            // influence score
            if (scoreObject.has("influence_score")) {
                JsonObject influenceScoreObject = scoreObject.getAsJsonObject("influence_score");
                if (influenceScoreObject.has("reach_score_value")) {
                    socialAccountTwitter.setInfluenceReachScore(influenceScoreObject.get("reach_score_value").getAsString());
                }
                if (influenceScoreObject.has("engagement_score_value")) {
                    socialAccountTwitter.setInfluenceEngagementScore(influenceScoreObject.get("engagement_score_value").getAsString());
                }
                if (influenceScoreObject.has("frequency_score_value")) {
                    socialAccountTwitter.setInfluenceFrequencyScore(influenceScoreObject.get("frequency_score_value").getAsString());
                }
            }

            // advocacy score
            if (scoreObject.has("advocacy_score")) {
                JsonObject advocacyScoreObject = scoreObject.getAsJsonObject("advocacy_score");
                if (advocacyScoreObject.has("value")) {
                    socialAccountTwitter.setAdvocacyScore(advocacyScoreObject.get("value").getAsInt());
                }
            }
        }
        return socialAccountTwitter;
    }
}
