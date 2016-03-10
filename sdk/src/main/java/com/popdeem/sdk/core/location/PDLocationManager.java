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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.utils.PDLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mikenolan on 21/02/16.
 */
public class PDLocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener/*, LocationListener*/ {


    public static final int STATE_RUNNING = 0;
    public static final int STATE_STOPPED = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_SUSPENDED = 3;
    public static final int STATE_CONNECTION_FAILED = 4;

    @IntDef({STATE_RUNNING, STATE_STOPPED, STATE_CONNECTED, STATE_SUSPENDED, STATE_CONNECTION_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PDLocationManagerState {
    }

    @PDLocationManagerState
    private int mState = STATE_STOPPED;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener mLocationListener = null;
//    private Location mLocation;

    public PDLocationManager(@NonNull Context context) {
        this.mContext = context;
    }

    public void start(@NonNull LocationListener locationListener) {
        this.mLocationListener = locationListener;
        this.mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        this.mGoogleApiClient.connect();
    }

    public void stop() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
            mGoogleApiClient.disconnect();
        }
    }

    private void setState(@PDLocationManagerState int state) {
        this.mState = state;
    }

    @PDLocationManagerState
    public int getState() {
        return mState;
    }

//    public Location getLastLocation() {
//        return this.mLocation;
//    }

    private void requestLocationUpdatesIfPermitted(LocationRequest locationRequest) {
        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, locationRequest, this.mLocationListener);
        } else {
            PDLog.i(PopdeemSDK.class, "Your application does not have ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION permissions. Please ensure these permissions are present and you have asked the user permission to access their location.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        requestLocationUpdatesIfPermitted(locationRequest);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        String causeString = cause == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST ? "CAUSE_NETWORK_LOST"
                : cause == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED ? "CAUSE_SERVICE_DISCONNECTED" : "UNKNOWN";
        PDLog.d(getClass(), "onConnectionSuspended(): " + causeString);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        PDLog.d(getClass(), "onConnectionFailed(): errorCode=" + connectionResult.getErrorCode());
        PDLog.d(getClass(), "onConnectionFailed(): errorMessage=" + connectionResult.getErrorMessage());
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        this.mLocation = location;
//    }

}
