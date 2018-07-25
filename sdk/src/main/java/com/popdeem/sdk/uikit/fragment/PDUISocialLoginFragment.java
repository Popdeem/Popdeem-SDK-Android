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
import android.content.Context;
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
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.location.LocationListener;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.interfaces.PDFragmentCommunicator;
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.utils.PDUIColorUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUISocialLoginFragment extends Fragment {

    private final int LOCATION_PERMISSION_REQUEST = 90;
    private static final String TAG = PDUISocialLoginFragment.class.getSimpleName();

    private PDLocationManager mLocationManager;

    private ProgressBar mProgress;
    //    private TextView mHeaderTextView;
    private TextView mRewardsInfoTextView;
    private Button mContinueButton;
    //    private LoginButton mLoginButton;
    private Button mFacebookLoginButton;
    private CallbackManager mCallbackManager;

    private boolean mAskForPermission = true;

    private PDFragmentCommunicator communicator; //used for certain instances where login does not occur at the beginning
    private ArrayList<PDReward> rewards;

    public PDUISocialLoginFragment() {
    }

    public static PDUISocialLoginFragment newInstance() {
        return new PDUISocialLoginFragment();
    }

    public static PDUISocialLoginFragment newInstance(ArrayList<PDReward> rewards) {
        PDUISocialLoginFragment frag = new PDUISocialLoginFragment();
        frag.rewards = rewards;

        return new PDUISocialLoginFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_social_login, container, false);

        mLocationManager = new PDLocationManager(getActivity());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mFacebookLoginButton.setText(R.string.pd_log_out_facebook_text);
                PDLog.d(PDUISocialLoginFragment.class, "Facebook Login onSuccess(): " + loginResult.getAccessToken().getToken());
                checkForLocationPermissionAndStartLocationManager();
            }

            @Override
            public void onCancel() {
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CANCELLED_FACEBOOK_LOGIN, null);
                PDLog.d(PDUISocialLoginFragment.class, "Facebook Login onCancel()");
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                        .setTitle(R.string.pd_common_facebook_login_cancelled_title_text)
                        .setMessage(R.string.pd_common_facebook_login_cancelled_message_text)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }

            @Override
            public void onError(FacebookException error) {
                PDLog.d(PDUISocialLoginFragment.class, "Facebook Login onError(): " + error.getMessage());
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                        .setTitle(R.string.pd_common_sorry_text)
                        .setMessage(error.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });

        ImageButton backButton = (ImageButton) view.findViewById(R.id.pd_social_login_back_button);
        backButton.setImageDrawable(PDUIColorUtils.getSocialLoginBackButtonIcon(getActivity()));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress.getVisibility() == View.GONE) {
                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CLICKED_CLOSE_LOGIN_TAKEOVER, new PDAbraProperties.Builder()
                            .add("Source", "Dismiss Button")
                            .create());
                    removeThisFragment();
                }
            }
        });

        mProgress = (ProgressBar) view.findViewById(R.id.pd_progress_bar);
//        mHeaderTextView = (TextView) view.findViewById(R.id.pd_social_login_header_text_view);
        mRewardsInfoTextView = (TextView) view.findViewById(R.id.pd_social_rewards_info_text_view);
        mContinueButton = (Button) view.findViewById(R.id.pd_social_continue_button);

        mFacebookLoginButton = (Button) view.findViewById(R.id.pd_facebook_login_button);
        mFacebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PDLocationManager.isGpsEnabled(getActivity())) {
                    LoginManager.getInstance().logInWithReadPermissions(PDUISocialLoginFragment.this, Arrays.asList(PDSocialUtils.FACEBOOK_READ_PERMISSIONS));
                } else {
                    new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                            .setTitle(R.string.pd_location_disabled_title_text)
                            .setMessage(R.string.pd_location_disabled_message_text)
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PDLocationManager.startLocationSettingsActivity(getActivity());
                                }
                            })
                            .create().show();
                }
            }
        });

        view.findViewById(R.id.pd_login_reward_layout).setVisibility(View.GONE);
        view.findViewById(R.id.pd_login_text_description).setVisibility(View.VISIBLE);
        if (rewards != null){
            final ImageView logoImageView = (ImageView) view.findViewById(R.id.pd_reward_star_image_view);

            for (int i = 0; i < rewards.size(); i++) {
                if(rewards.get(i).getAction().equalsIgnoreCase("social_login")) {

                    final String imageUrl = rewards.get(i).getCoverImage();
                    if (imageUrl == null || imageUrl.isEmpty() || imageUrl.contains("default")) {
                        Glide.with(getActivity())
                                .load(R.drawable.pd_ui_star_icon)
                                .error(R.drawable.pd_ui_star_icon)
                                .dontAnimate()
                                .placeholder(R.drawable.pd_ui_star_icon)
                                .into(logoImageView);
                    } else {
                        Glide.with(getActivity())
                                .load(imageUrl)
                                .error(R.drawable.pd_ui_star_icon)
                                .dontAnimate()
                                .placeholder(R.drawable.pd_ui_star_icon)
                                .into(logoImageView);
                    }

                    // Reward Description
                    TextView textView = (TextView) view.findViewById(R.id.pd_reward_offer_text_view);
                    textView.setText(rewards.get(i).getDescription());

                    // Rules
                    textView = (TextView) view.findViewById(R.id.pd_reward_item_rules_text_view);
                    textView.setText(rewards.get(i).getRules());
                    if (rewards.get(i).getRules() == null || rewards.get(i).getRules().isEmpty()) {
                        textView.setVisibility(View.GONE);
                    }
                    view.findViewById(R.id.pd_login_reward_layout).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pd_login_text_description).setVisibility(View.GONE);

                    break;
                }
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_LOGINTAKEOVER)
                .create());
    }

    private void updateViewAfterLogin() {
//        mProgress.setVisibility(View.GONE);
//        mRewardsInfoTextView.setText(R.string.pd_social_login_success_description_text);
////        mHeaderTextView.setText(R.string.pd_social_login_success_text);
////        mHeaderTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.pd_continue_button_background_color));
////        mLoginButton.setVisibility(View.GONE);
//        mFacebookLoginButton.setVisibility(View.GONE);
//        mContinueButton.setVisibility(View.VISIBLE);
//        mContinueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeThisFragment();
//            }
//        });

        removeThisFragment();
    }

    private void checkForLocationPermissionAndStartLocationManager() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
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
//        mLoginButton.setVisibility(View.INVISIBLE);
        mFacebookLoginButton.setVisibility(View.INVISIBLE);

        mLocationManager.startLocationUpdates(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    handleLocationUpdate(location);
                }
            }
        });
    }

    private void handleLocationUpdate(final Location location) {
        mLocationManager.stop();

        PDUtils.updateSavedUserLocation(location);
        PDAPIClient.instance().registerUserWithFacebook(AccessToken.getCurrentAccessToken().getToken(), AccessToken.getCurrentAccessToken().getUserId(), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDLog.d(PDUISocialLoginFragment.class, "registered with Facebook: " + user.toString());

                PDUtils.updateSavedUser(user);
                updateUser(location);


                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_LOGIN, new PDAbraProperties.Builder()
                        .add("Source", "Login Takeover")
                        .create());
                PDAbraLogEvent.onboardUser();
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUISocialLoginFragment.class, "failed register with Facebook: statusCode=" + statusCode + ", message=" + e.getMessage());

                LoginManager.getInstance().logOut();

                mProgress.setVisibility(View.GONE);
//                        mLoginButton.setVisibility(View.VISIBLE);
                mFacebookLoginButton.setVisibility(View.VISIBLE);
                mFacebookLoginButton.setText(R.string.pd_log_in_with_facebook_text);

                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                        .setTitle(R.string.pd_common_sorry_text)
                        .setMessage("An error occurred while registering. Please try again")
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });
    }

    private void updateUser(Location location) {
        Realm realm = Realm.getDefaultInstance();

        PDRealmGCM gcm = realm.where(PDRealmGCM.class).findFirst();
        String deviceToken = gcm == null ? "" : gcm.getRegistrationToken();

        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();

        if (userDetails == null) {
            realm.close();
            return;
        }

        PDAPIClient.instance().updateUserLocationAndDeviceToken(PDSocialUtils.SOCIAL_TYPE_FACEBOOK, userDetails.getId(), deviceToken, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDLog.d(PDUISocialLoginFragment.class, "update user: " + user);

                PDUtils.updateSavedUser(user);

                // Send broadcast to any registered receivers that user has logged in
//                getActivity().sendBroadcast(new Intent(PDUIRewardsFragment.PD_LOGGED_IN_RECEIVER_FILTER));
                // Update view
                updateViewAfterLogin();
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUISocialLoginFragment.class, "failed update user: status=" + statusCode + ", e=" + e.getMessage());
                updateViewAfterLogin();
            }
        });
        realm.close();
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
                    PDLog.d(getClass(), "permission for location not granted");
                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_DENIED_LOCATION, null);
                    if (mAskForPermission) {
                        mAskForPermission = false;
                        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
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

    /**
     * Used to allow the client a hook into the SDK, in order to determine when the LoginFragments are detached
     * allows for custom func client side
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PDFragmentCommunicator){
            communicator = (PDFragmentCommunicator) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (communicator != null){
            communicator.fragmentDetached();
        }
    }
}
