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

package com.popdeem.sdk.core.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;

import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;

import io.realm.Realm;

public class PDUtils {

    private static final String TAG = PDUtils.class.getSimpleName();

    public static final String POPDEEM_API_KEY_PROPERTY = "com.popdeem.sdk.ApiKey";


    /**
     * Get Popdeem API Key String from Application's Meta Data
     *
     * @param context Application Context
     * @return API Key String
     */
    public static String getPopdeemAPIKey(Context context) {
        String apiKey = getStringFromMetaData(context, POPDEEM_API_KEY_PROPERTY);
        if (apiKey == null) {
            throw new RuntimeException("Popdeem API key not found.\n" +
                    "Check that: <meta-data android:name=\"" + POPDEEM_API_KEY_PROPERTY + "\" android:value=\"YOUR_API_KEY\" /> is in the <application> element of your app's AndroidManifest.xml");
        }
        return apiKey;
    }


    /**
     * Get String value from AndroidManifest Meta Data
     *
     * @param context Application Context
     * @param key     Key of String to retrieve
     * @return null if no String is present, String value otherwise
     */
    public static String getStringFromMetaData(Context context, String key) {
        ApplicationInfo ai;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("PackageManager.NameNotFoundException: " + e.getMessage());
        }
        if (ai.metaData == null) {
            throw new RuntimeException("Cannot access Application Meta Data.");
        }
        return ai.metaData.getString(key);
    }


    /**
     *
     */
    public static void validateAPIKeyIsPresent() {
        PopdeemSDK.getPopdeemAPIKey();
    }


//    /**
//     * Get User from Realm DB
//     *
//     * @return User object, null if none is saved
//     */
//    public static synchronized PDUser getUser() {
//        Realm realm = Realm.getDefaultInstance();
//        final PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
//        String token = userDetails == null ? null : userDetails.getUserToken();
//
//        PDUser user = PDRealmUserDetails.
//        realm.close();
//        return token;
//    }

    /**
     * Get User Token from Realm DB
     *
     * @return User Token String, null if none is saved
     */
    public static synchronized String getUserToken() {
        Realm realm = Realm.getDefaultInstance();
        final PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        String token = userDetails == null ? null : userDetails.getUserToken();
        realm.close();
        return token;
    }


    /**
     * Check if User is Suspended
     *
     * @return true if suspended, false if not
     */
    public static synchronized boolean isUserSuspended() {
        Realm realm = Realm.getDefaultInstance();
        final PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        boolean suspended = userDetails != null && userDetails.getSuspendUntil() != null && !userDetails.getSuspendUntil().isEmpty();
        realm.close();
        return suspended;
    }

    /**
     * Check if User is Suspended
     *
     * @return true if suspended, false if not
     */
    public static synchronized String getUserSuspendedUntil() {
        Realm realm = Realm.getDefaultInstance();
        final PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        String suspendedUntil = userDetails == null ? null : userDetails.getSuspendUntil();
        realm.close();
        return suspendedUntil;
    }


    /**
     * Update the saved location for the current user
     *
     * @param location Users current location
     */
    public static void updateSavedUserLocation(Location location) {
        if (location == null) {
            return;
        }

        PDRealmUserLocation userLocation = new PDRealmUserLocation(location.getLatitude(), location.getLongitude());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userLocation);
        realm.commitTransaction();
        realm.close();
    }


    /**
     * Update the saved user details
     *
     * @param user New User data
     */
    public static void updateSavedUser(PDUser user) {
        if (user == null) {
            return;
        }

        PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
        userDetails.setUid(0);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userDetails);
        realm.commitTransaction();
        realm.close();
    }

}
