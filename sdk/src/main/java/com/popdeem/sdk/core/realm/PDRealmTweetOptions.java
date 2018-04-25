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

package com.popdeem.sdk.core.realm;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.popdeem.sdk.core.model.PDTweetOptions;

import java.lang.reflect.Type;

import io.realm.RealmObject;

/**
 * Created by mikenolan on 12/11/2015.
 */
public class PDRealmTweetOptions extends RealmObject {

    /*
    "tweet_options": {
        "prefill": "true",
        "force_tag": "true",
        "free_form": "false",
        "prefilled_message": "This is prefilled message, allow the user to delete",
        "forced_tag": "#forcedHashTag",
        "include_download_link": ""
      }
     */

    private boolean prefill;
    private boolean forceTag;
    private boolean freeForm;
    private String prefilledMessage;
    private String forcedTag;
    private String includeDownloadLink;

    public PDRealmTweetOptions() {
    }

    public PDRealmTweetOptions(boolean prefill, boolean forceTag, boolean freeForm, String prefilledMessage, String forcedTag, String includeDownloadLink) {
        this.prefill = prefill;
        this.forceTag = forceTag;
        this.freeForm = freeForm;
        this.prefilledMessage = prefilledMessage;
        this.forcedTag = forcedTag;
        this.includeDownloadLink = includeDownloadLink;
    }

    public PDRealmTweetOptions(PDTweetOptions pdTweetOptions) {
        this.prefill = pdTweetOptions.isPrefill();
        this.forceTag = pdTweetOptions.isForceTag();
        this.freeForm = pdTweetOptions.isFreeForm();
        this.prefilledMessage = pdTweetOptions.getPrefilledMessage();
        this.forcedTag = pdTweetOptions.getForcedTag();
        this.includeDownloadLink = pdTweetOptions.getIncludeDownloadLink();
    }

    public boolean isPrefill() {
        return prefill;
    }

    public void setPrefill(boolean prefill) {
        this.prefill = prefill;
    }

    public boolean isForceTag() {
        return forceTag;
    }

    public void setForceTag(boolean forceTag) {
        this.forceTag = forceTag;
    }

    public boolean isFreeForm() {
        return freeForm;
    }

    public void setFreeForm(boolean freeForm) {
        this.freeForm = freeForm;
    }

    public String getPrefilledMessage() {
        return prefilledMessage;
    }

    public void setPrefilledMessage(String prefilledMessage) {
        this.prefilledMessage = prefilledMessage;
    }

    public String getForcedTag() {
        return forcedTag;
    }

    public void setForcedTag(String forcedTag) {
        this.forcedTag = forcedTag;
    }

    public String getIncludeDownloadLink() {
        return includeDownloadLink;
    }

    public void setIncludeDownloadLink(String includeDownloadLink) {
        this.includeDownloadLink = includeDownloadLink;
    }


    public static class PDTweetOptionsJsonDeserializer implements JsonDeserializer<PDRealmTweetOptions> {

        @Override
        public PDRealmTweetOptions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(int.class, new PDUser.PDIntDeserializer())
//                    .registerTypeAdapter(int.class, new PDUser.PDIntDeserializer())
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            PDRealmTweetOptions tweetOptions = gson.fromJson(json, PDRealmTweetOptions.class);
            return tweetOptions;
        }
    }

}
