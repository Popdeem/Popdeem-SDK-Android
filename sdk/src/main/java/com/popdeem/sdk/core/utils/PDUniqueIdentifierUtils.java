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
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.popdeem.sdk.core.model.PDNonSocialUID;
import com.popdeem.sdk.core.realm.PDRealmNonSocialUID;

import java.io.IOException;

import io.realm.Realm;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUniqueIdentifierUtils {

    public interface PDUIDCallback {
        void success(String uid);

        void failure(String message);
    }

    public static void createUID(@NonNull Context context, @NonNull PDUIDCallback callback) {
        new GenerateUIDAsync(context, callback).execute();
    }

    public static PDNonSocialUID getUID() {
        PDNonSocialUID uid = null;
        Realm realm = Realm.getDefaultInstance();
        PDRealmNonSocialUID uidRealm = realm.where(PDRealmNonSocialUID.class).findFirst();
        if (uidRealm != null) {
            uid = new PDNonSocialUID(uidRealm);
        }
        realm.close();
        return uid;
    }

    public static String getUIDString() {
        PDNonSocialUID nonSocialUID = getUID();
        if (nonSocialUID == null) {
            return null;
        }
        return nonSocialUID.getUid();
    }

    public static boolean isRegistered() {
        Realm realm = Realm.getDefaultInstance();
        PDRealmNonSocialUID uid = realm.where(PDRealmNonSocialUID.class).findFirst();
        boolean registered = uid != null && uid.isRegistered();
        realm.close();
        return registered;
    }

    private static class GenerateUIDAsync extends AsyncTask<Void, Void, String> {

        private Context mContext;
        private PDUIDCallback mCallback;
        private boolean success;

        public GenerateUIDAsync(Context mContext, PDUIDCallback mCallback) {
            this.mContext = mContext;
            this.mCallback = mCallback;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
                success = true;
                return info.getId();
            } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                success = false;
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mCallback != null) {
                if (success) {
                    PDLog.d(PDUniqueIdentifierUtils.class, "uid: " + result);
                    mCallback.success(result);
                } else {
                    mCallback.failure(result);
                }
            }
        }
    }

}
