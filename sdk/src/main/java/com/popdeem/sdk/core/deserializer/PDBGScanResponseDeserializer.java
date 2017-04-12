package com.popdeem.sdk.core.deserializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.popdeem.sdk.core.model.PDBGScanResponseModel;

import java.lang.reflect.Type;

/**
 * Created by dave on 12/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDBGScanResponseDeserializer implements JsonDeserializer<PDBGScanResponseModel> {

    @Override
    public PDBGScanResponseModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PDBGScanResponseModel.class, null)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.fromJson(jsonObject, PDBGScanResponseModel.class);
    }
}
