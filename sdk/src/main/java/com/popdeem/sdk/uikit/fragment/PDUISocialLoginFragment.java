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

package com.popdeem.sdk.uikit.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.LocationListener;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.core.utils.PDSocialUtils;

import java.util.Arrays;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUISocialLoginFragment extends Fragment {

    private final int LOCATION_PERMISSION_REQUEST = 90;

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    public PDUISocialLoginFragment() {
    }

    public static PDUISocialLoginFragment newInstance() {
        return new PDUISocialLoginFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_social_login, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(PDUISocialLoginFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton = (LoginButton) view.findViewById(R.id.pd_fb_login_button);
        mLoginButton.setReadPermissions(Arrays.asList(PDSocialUtils.FACEBOOK_READ_PERMISSIONS));
        mLoginButton.setFragment(this);
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(PDUISocialLoginFragment.class.getSimpleName(), "Facebook Login onSuccess(): " + loginResult.getAccessToken().getToken());
                checkForLocationPermissionAndStartLocationManager();
            }

            @Override
            public void onCancel() {
                Log.d(PDUISocialLoginFragment.class.getSimpleName(), "Facebook Login onCancel()");
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.pd_error_title_text)
                        .setMessage(R.string.pd_facebook_login_cancelled_error_message)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(PDUISocialLoginFragment.class.getSimpleName(), "Facebook Login onError(): " + error.getMessage());
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.pd_error_title_text)
                        .setMessage(error.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });

        return view;
    }

    private void checkForLocationPermissionAndStartLocationManager() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        } else {
            startLocationManagerAfterLogin();
        }
    }

    private void startLocationManagerAfterLogin() {
        final PDLocationManager locationManager = new PDLocationManager(getContext());
        locationManager.start(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationManager.stop();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationManagerAfterLogin();
                } else {
                    // Permission was not given
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
