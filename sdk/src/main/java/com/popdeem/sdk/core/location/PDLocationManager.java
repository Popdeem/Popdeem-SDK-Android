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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

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
    private boolean startedForLastKnownLocation = false;

    public PDLocationManager(@NonNull Context context) {
        this.mContext = context;
        buildGoogleApiClient();
    }

    public void startLocationUpdates(@NonNull LocationListener locationListener) {
        startedForLastKnownLocation = false;
        start(locationListener);
    }

    public void startForLastKnownLocation(@NonNull LocationListener locationListener) {
        startedForLastKnownLocation = true;
        start(locationListener);
    }

    private void start(LocationListener locationListener) {
        this.mLocationListener = locationListener;
        this.mGoogleApiClient.connect();
    }

    public void stop() {
        setState(STATE_STOPPED);
        if (mGoogleApiClient != null && (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mGoogleApiClient.disconnect();
        }
    }

    private void buildGoogleApiClient() {
        if (this.mGoogleApiClient != null && (this.mGoogleApiClient.isConnected() || this.mGoogleApiClient.isConnecting())) {
            this.mGoogleApiClient.disconnect();
        }

        this.mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
        if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setState(STATE_RUNNING);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, locationRequest, this.mLocationListener);
            mLocationListener.onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        } else {
            PDLog.i(PopdeemSDK.class, "Your application does not have ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION permissions. Please ensure these permissions are present and you have asked the user permission to access their location.");
        }
    }

    private void requestLastKnownLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationListener.onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        } else {
            PDLog.i(PopdeemSDK.class, "Your application does not have ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION permissions. Please ensure these permissions are present and you have asked the user permission to access their location.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        setState(STATE_CONNECTED);
        if (startedForLastKnownLocation) {
            requestLastKnownLocationIfPermitted();
        } else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(0);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            requestLastKnownLocationIfPermitted();
            requestLocationUpdatesIfPermitted(locationRequest);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        setState(STATE_SUSPENDED);
        String causeString = cause == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST ? "CAUSE_NETWORK_LOST"
                : cause == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED ? "CAUSE_SERVICE_DISCONNECTED" : "UNKNOWN";
        PDLog.d(getClass(), "onConnectionSuspended(): " + causeString);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        setState(STATE_CONNECTION_FAILED);
        PDLog.d(getClass(), "onConnectionFailed(): errorCode=" + connectionResult.getErrorCode());
        PDLog.d(getClass(), "onConnectionFailed(): errorMessage=" + connectionResult.getErrorMessage());
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        this.mLocation = location;
//    }


    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void startLocationSettingsActivity(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

}
