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

package com.popdeem.sdk.core.api.abra;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.google.gson.JsonObject;
import com.jakewharton.retrofit.Ok3Client;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIConfig;
import com.popdeem.sdk.core.exception.PopdeemSDKNotInitializedException;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDUniqueIdentifierUtils;
import com.popdeem.sdk.core.utils.PDUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * {@link PDAbraAPIClient} is used to interact with the Popdeem Abra Insights Layer.
 * Create an instance of {@link PDAbraAPIClient} using the {@link #instance()} method.
 * <p>
 * Created by mikenolan on 21/09/2016.
 */
class PDAbraAPIClient {

    /**
     * {@link Interceptor} for adding Popdeem API Key to request headers.
     */
    private static final Interceptor PD_API_KEY_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request().newBuilder()
                    .addHeader(PDAPIConfig.REQUEST_HEADER_API_KEY, PopdeemSDK.getPopdeemAPIKey())
                    .build());
        }
    };


    /**
     * Create an instance of {@link PDAbraAPIClient} to access Abra API.
     * <p>
     * Popdeem SDK must be initialized using {@link PopdeemSDK#initializeSDK(Application)} and your Popdeem API Key must be present in your Application's AndroidManifest.xml before creating an instance of {@link PDAbraAPIClient}.
     * </p>
     *
     * @return instance of {@link PDAbraAPIClient}
     */
    public static PDAbraAPIClient instance() {
        if (!PopdeemSDK.isPopdeemSDKInitialized()) {
            throw new PopdeemSDKNotInitializedException("Popdeem SDK is not initialized. Be sure to call PopdeemSDK.initializeSDK(Application application) in your Application class before using the SDK.");
        }
        PDUtils.validateAPIKeyIsPresent();
        return new PDAbraAPIClient();
    }


    /**
     * Private empty constructor to stop instances being created using "new"
     */
    private PDAbraAPIClient() {
    }


    /**
     * Build RestAdapter for request
     *
     * @param converter - {@link Converter} for Request (optional)
     * @return - {@link RestAdapter} used to create {@link PDAbraAPI}
     */
    private RestAdapter buildRestAdapter(Converter converter) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(PD_API_KEY_INTERCEPTOR);

        Interceptor userInterceptor = getUserIdInterceptor();
        if (userInterceptor != null) {
            builder.addInterceptor(userInterceptor);
        }

        OkHttpClient okHttpClient = builder.build();
        RestAdapter.Builder adapterBuilder = new RestAdapter.Builder()
                .setClient(new Ok3Client(okHttpClient))
                .setEndpoint(PDAbraConfig.ABRA_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        if (converter != null) {
            adapterBuilder.setConverter(converter);
        }

        return adapterBuilder.build();
    }

    private Interceptor getUserIdInterceptor() {
        final String userToken = PDUtils.getUserToken();
        final String uniqueId = PDUniqueIdentifierUtils.getUIDString();
        if (userToken == null && uniqueId == null) {
            return null;
        }

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                if (userToken != null) {
                    builder.addHeader("User-Id", userToken);
                }
                if (uniqueId != null) {
                    builder.addHeader("Device-Id", uniqueId);
                }
                return chain.proceed(builder.build());
            }
        };
    }

    /**
     * Get Abra API interface
     *
     * @param converter - {@link Converter} users for request response (optional)
     * @return - {@link PDAbraAPI} used for interacting with Abra API
     */
    private PDAbraAPI apiInterface(Converter converter) {
        RestAdapter restAdapter = buildRestAdapter(converter);
        return restAdapter.create(PDAbraAPI.class);
    }


    /**
     * Onboard user through Abra Insights Layer
     */
    void onboardUser() {
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails user = realm.where(PDRealmUserDetails.class).findFirst();
        if (user == null) {
            realm.close();
            return;
        }

        JsonObject traitsJson = new JsonObject();
        traitsJson.addProperty(PDAbraConfig.ABRA_USER_TRAITS_ID, user.getId());
        traitsJson.addProperty(PDAbraConfig.ABRA_USER_TRAITS_FIRST_NAME, user.getFirstName());
        traitsJson.addProperty(PDAbraConfig.ABRA_USER_TRAITS_LAST_NAME, user.getLastName());
        traitsJson.addProperty(PDAbraConfig.ABRA_USER_TRAITS_GENDER, user.getSex());
        traitsJson.addProperty(PDAbraConfig.ABRA_USER_TRAITS_TIME_ZONE, TimeZone.getDefault().getDisplayName());
        traitsJson.addProperty(PDAbraConfig.ABRA_USER_TRAITS_COUNTRY_CODE, Locale.getDefault().getISO3Country());
        traitsJson.addProperty(PDAbraConfig.ABRA_USER_TRAITS_PUSH_NOTIFICATIONS_ENABLED, PopdeemSDK.areNotificationsEnabledForApp());

        JsonObject eventJson = new JsonObject();
        eventJson.addProperty(PDAbraConfig.ABRA_KEY_TAG, PDAbraConfig.ABRA_EVENT_ONBOARD);

        final JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty(PDAbraConfig.ABRA_KEY_PROJECT_TOKEN, PDAbraConfig.ABRA_PROJECT_TOKEN);
        jsonBody.addProperty(PDAbraConfig.ABRA_KEY_USER_ID, user.getId());
        jsonBody.add(PDAbraConfig.ABRA_KEY_TRAITS, traitsJson);
        jsonBody.add(PDAbraConfig.ABRA_KEY_EVENT, eventJson);

        realm.close();

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, jsonBody.toString().getBytes());
        apiInterface(null).onboardUser(body, new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                PDLog.d(PDAbraAPIClient.class, "onboard success: " + jsonObject.toString());
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDAbraAPIClient.class, "onboard failure: " + e.getLocalizedMessage());
            }
        });
    }


    /**
     * Log and Event through the Abra Insights Layer.
     *
     * @param eventName  Name of event {@link String}
     * @param properties List of {@link Pair} objects for each property to log.
     */
    void logEvent(@NonNull String eventName, PDAbraProperties properties) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails user = realm.where(PDRealmUserDetails.class).findFirst();
        final String id = user == null ? null : user.getId();
        realm.close();

        JsonObject propertiesJson = new JsonObject();
        if (properties != null) {
            for (Pair<String, String> property : properties.getProperties()) {
                propertiesJson.addProperty(property.first, property.second);
            }
        }

        JsonObject eventJson = new JsonObject();
        eventJson.addProperty(PDAbraConfig.ABRA_KEY_TAG, eventName);
        eventJson.add(PDAbraConfig.ABRA_KEY_PROPERTIES, propertiesJson);

        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty(PDAbraConfig.ABRA_KEY_PROJECT_TOKEN, PDAbraConfig.ABRA_PROJECT_TOKEN);
        if (id != null && !id.isEmpty()) {
            bodyJson.addProperty(PDAbraConfig.ABRA_KEY_USER_ID, id);
        }
        bodyJson.add(PDAbraConfig.ABRA_KEY_EVENT, eventJson);

        TypedInput body = new TypedByteArray(PDAPIConfig.PD_JSON_MIME_TYPE, bodyJson.toString().getBytes());
        apiInterface(null).logEvent(body, new PDAPICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                PDLog.d(PDAbraAPIClient.class, "logEvent success");
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDAbraAPIClient.class, "logEvent failure: " + e.getLocalizedMessage());
            }
        });
    }

}
