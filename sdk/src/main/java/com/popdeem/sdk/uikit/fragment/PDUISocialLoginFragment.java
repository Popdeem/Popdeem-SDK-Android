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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.LocationListener;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDSocialUtils;

import java.util.Arrays;

import io.realm.Realm;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUISocialLoginFragment extends Fragment {

    private final int LOCATION_PERMISSION_REQUEST = 90;

    private ProgressBar mProgress;
    private ImageView mImageView;
    private TextView mHeaderTextView;
    private TextView mRewardsInfoTextView;
    private Button mContinueButton;
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    private boolean mAskForPermission = true;

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
                if (mProgress.getVisibility() == View.GONE) {
                    removeThisFragment();
                }
            }
        });

        mCallbackManager = CallbackManager.Factory.create();

        mProgress = (ProgressBar) view.findViewById(R.id.pd_progress_bar);
        mImageView = (ImageView) view.findViewById(R.id.pd_social_login_image_view);
        mHeaderTextView = (TextView) view.findViewById(R.id.pd_social_login_header_text_view);
        mRewardsInfoTextView = (TextView) view.findViewById(R.id.pd_social_rewards_info_text_view);
        mContinueButton = (Button) view.findViewById(R.id.pd_social_continue_button);

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

    private void updateViewAfterLogin() {
        mProgress.setVisibility(View.GONE);
        mRewardsInfoTextView.setText(R.string.pd_social_login_rewards_unlocked_text);
        mHeaderTextView.setText(R.string.pd_social_connected_text);
        mHeaderTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.pd_continue_button_background_color));
        mImageView.setImageResource(R.drawable.pd_ui_rewards_success_icon);
        mLoginButton.setVisibility(View.GONE);
        mContinueButton.setVisibility(View.VISIBLE);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeThisFragment();
            }
        });
    }

    private void checkForLocationPermissionAndStartLocationManager() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.pd_location_permission_title_text)
                        .setMessage(R.string.pd_location_permission_rationale_text)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create()
                        .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        } else {
            startLocationManagerAfterLogin();
        }
    }

    private void startLocationManagerAfterLogin() {
        mProgress.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.INVISIBLE);

        final PDLocationManager locationManager = new PDLocationManager(getContext());
        locationManager.start(new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                PDRealmUserLocation userLocation = new PDRealmUserLocation();
                userLocation.setId(0);
                userLocation.setLatitude(location.getLatitude());
                userLocation.setLongitude(location.getLongitude());

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(userLocation);
                realm.commitTransaction();
                realm.close();

                locationManager.stop();
                PDAPIClient.instance().registerUserWithFacebook(AccessToken.getCurrentAccessToken().getToken(), AccessToken.getCurrentAccessToken().getUserId(), new PDAPICallback<PDUser>() {
                    @Override
                    public void success(PDUser user) {
                        Log.d(PDAPIClient.class.getSimpleName(), "registered with Facebook: " + user.toString());

                        PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(userDetails);
                        realm.commitTransaction();
                        realm.close();

                        updateUser(location);
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        Log.d(PDAPIClient.class.getSimpleName(), "failed register with Facebook: statusCode=" + statusCode + ", message=" + e.getMessage());

                        LoginManager.getInstance().logOut();

                        mProgress.setVisibility(View.GONE);
                        mLoginButton.setVisibility(View.VISIBLE);

                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.pd_error_title_text)
                                .setMessage("An error occurred while registering. Please try again")
                                .setPositiveButton(android.R.string.ok, null)
                                .create()
                                .show();
                    }
                });
            }
        });
    }

    private void updateUser(Location location) {
        Realm realm = Realm.getDefaultInstance();

        PDRealmGCM gcm = realm.where(PDRealmGCM.class).findFirst();
        String deviceToken = gcm == null ? "" : gcm.getRegistrationToken();

        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        if (userDetails == null) {
            return;
        }

        PDAPIClient.instance().updateUserLocationAndDeviceToken(userDetails.getId(), deviceToken, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                Log.d(PDAPIClient.class.getSimpleName(), "update user: " + user);

                PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(userDetails);
                realm.commitTransaction();
                realm.close();

                updateViewAfterLogin();
            }

            @Override
            public void failure(int statusCode, Exception e) {
                Log.d(PDAPIClient.class.getSimpleName(), "failed update user: status=" + statusCode + ", e=" + e.getMessage());
                updateViewAfterLogin();
            }
        });
    }

    public void removeThisFragment() {
        getActivity().getSupportFragmentManager().popBackStack(PDUISocialLoginFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationManagerAfterLogin();
                } else {
                    // Permission was not given
                    Log.d("Popdeem", "permission for location not granted");
                    if (mAskForPermission) {
                        mAskForPermission = false;
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.pd_location_permission_title_text)
                                .setMessage(R.string.pd_location_permission_are_you_sure_text)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                                LOCATION_PERMISSION_REQUEST);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LoginManager.getInstance().logOut();
                                        removeThisFragment();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                LoginManager.getInstance().logOut();
                                removeThisFragment();
                            }
                        });
                    }
                }
                break;
        }
    }
}
