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

package com.popdeem.sdk.core.gcm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.popdeem.sdk.core.realm.PDRealmGCM;

import java.io.IOException;

import io.realm.Realm;

/**
 * Created by mikenolan on 21/02/16.
 */
public class PDGCMUtils {

    /**
     * Callback for GCM registration result
     */
    public interface PDGCMRegistrationCallback {
        void success(String registrationToken);

        void failure(String message);
    }


    /**
     * Initialise GCM.
     * If Google Play Services is available and a registration token is needed, one will be requested.
     *
     * @param context  Application context
     * @param callback Callback for GCM registration
     */
    public static void initGCM(Context context, PDGCMRegistrationCallback callback) {
        if (isGooglePlayServicesAvailable(context)) {
            String token = getRegistrationToken(context);
            if (token == null || token.isEmpty()) {
                registerInBackground(context, callback);
            }
        } else {
            Log.d(PDGCMUtils.class.getSimpleName(), "Google Play Services is not available");
        }
    }


    /**
     * Check if Google Play Services is available
     *
     * @param context Application Context
     * @return true if Play Services are available, false if not
     */
    public static boolean isGooglePlayServicesAvailable(Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Save GCM Registration Token
     *
     * @param registrationToken Registration Token {@link String} to save
     * @param appVersion        Current app version
     */
    private static void saveRegistrationID(String registrationToken, int appVersion) {
        PDRealmGCM gcmRealm = new PDRealmGCM();
        gcmRealm.setId(0);
        gcmRealm.setRegistrationToken(registrationToken);
        gcmRealm.setAppVersion(appVersion);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(gcmRealm);
        realm.commitTransaction();
        realm.close();
    }


    /**
     * Get GCM Registration Token
     *
     * @param context Application context
     * @return Registration Token if there is one present for the current version of the app, empty String otherwise.
     */
    public static String getRegistrationToken(Context context) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmGCM gcmRealm = realm.where(PDRealmGCM.class).findFirst();
        if (gcmRealm == null) {
            Log.i(PDGCMUtils.class.getSimpleName(), "no registration token saved");
            return "";
        }

        int registeredVersion = gcmRealm.getAppVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(PDGCMUtils.class.getSimpleName(), "App version changed.");
            return "";
        }

        return gcmRealm.getRegistrationToken();
    }


    /**
     * Register GCM in background thread
     *
     * @param context  Application Context
     * @param callback Callback for registration result
     */
    private static void registerInBackground(Context context, PDGCMRegistrationCallback callback) {
        new PDGCMRegisterAsync(context, callback).execute();
    }


    /**
     * {@link AsyncTask} used to register GCM
     */
    private static class PDGCMRegisterAsync extends AsyncTask<Void, Void, String> {

        private Context mContext;
        private PDGCMRegistrationCallback mCallback;

        public PDGCMRegisterAsync(Context mContext, PDGCMRegistrationCallback mCallback) {
            this.mContext = mContext;
            this.mCallback = mCallback;
        }

        @Override
        protected String doInBackground(Void... params) {
            InstanceID instanceID = InstanceID.getInstance(mContext);
            try {
                String token = instanceID.getToken("TODO Sender ID", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(PDGCMUtils.class.getSimpleName(), "token: " + token);
                return token;
            } catch (IOException e) {
                Log.e(PDGCMUtils.class.getSimpleName(), e.getMessage());
                if (mCallback != null) {
                    mCallback.failure(e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            if (token != null && !token.isEmpty()) {
                saveRegistrationID(token, getAppVersion(mContext));
                if (mCallback != null) {
                    mCallback.success(token);
                }
            }
        }
    }

}
