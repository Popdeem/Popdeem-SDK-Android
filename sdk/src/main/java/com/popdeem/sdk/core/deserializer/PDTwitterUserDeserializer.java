package com.popdeem.sdk.core.deserializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.model.PDUserTwitter;

import java.lang.reflect.Type;

/**
 * Created by dave on 03/05/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDTwitterUserDeserializer implements JsonDeserializer<PDUser> {

    @Override
    public PDUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(long.class, new PDLongDeserializer())
                .registerTypeAdapter(int.class, new PDIntDeserializer())
                .registerTypeAdapter(PDUserTwitter.class, new PDSocialAccountTwitterDeserializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        JsonElement userElement = json.getAsJsonObject().get("user");
        return gson.fromJson(userElement, PDUser.class);
    }
}
