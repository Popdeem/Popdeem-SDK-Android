package com.popdeem.sdk.core.deserializer;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by dave on 15/05/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDStringDeserializer implements JsonDeserializer<Long> {
    private String TAG = PDStringDeserializer.class.getSimpleName();

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.i(TAG, "deserialize: " + json.getAsString());
        JsonObject jsonObject = (JsonObject) json;
        if (jsonObject.has("social_account_id"))
        {
            if (jsonObject.get("social_account_id").getAsString().equalsIgnoreCase("")){
                return (long) -1;
            }
        }
        return json.getAsLong();
    }
}
