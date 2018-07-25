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

package com.popdeem.sdk.core.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.popdeem.sdk.BuildConfig;
import com.popdeem.sdk.core.model.PDLocation;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;

/**
 * Created by mikenolan on 16/03/16.
 */
public class PDLocationValidator {

    public static boolean validateLocationForReward(@NonNull PDReward reward, @NonNull Location userLocation) {
        // Check if user is a tester
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        boolean isTester = userDetails != null && (userDetails.getUserFacebook() != null && userDetails.getUserFacebook().getTester().equalsIgnoreCase(PDReward.PD_TRUE));
        realm.close();

        // If location verification is disabled for this reward, return true
        if (reward.getDisableLocationVerification().equalsIgnoreCase(PDReward.PD_TRUE) || isTester) {
            return true;
        }

        // Otherwise check all locations for reward to find closest and check

        final float closestDistance = distanceToClosestLocation(new ArrayList<>(reward.getLocations()), userLocation);
        final float accuracy = userLocation.getAccuracy();
        final float checkAccuracy = accuracy > 750 ? accuracy : 500;

        boolean isAtLocation = closestDistance < checkAccuracy;
        PDLog.d(PDLocationValidator.class, String.format(Locale.getDefault(), "User %1s at location: Distance %2s meters", isAtLocation ? "is" : "is NOT", closestDistance));
        return isAtLocation;
    }

    private static float distanceToClosestLocation(ArrayList<PDLocation> locations, Location userLocation) {
        if (locations == null || locations.size() == 0 || userLocation == null) {
            return -1;
        }

        float closestDistance = -1;
        Location brandLoc = new Location("");

        for (PDLocation pdLoc : locations) {
            double lat = PDNumberUtils.toDouble(pdLoc.getLatitude(), -1);
            double lng = PDNumberUtils.toDouble(pdLoc.getLongitude(), -1);

            if (lat == -1 || lng == -1) {
                continue;
            }

            brandLoc.setLatitude(lat);
            brandLoc.setLongitude(lng);

            float distanceToLoc = userLocation.distanceTo(brandLoc);
            if (closestDistance == -1 || closestDistance > distanceToLoc) {
                closestDistance = distanceToLoc;
            }
        }

        return closestDistance;
    }

}
