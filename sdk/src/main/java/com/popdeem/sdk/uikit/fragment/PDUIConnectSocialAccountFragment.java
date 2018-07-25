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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
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
import com.google.android.gms.location.LocationListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.core.model.PDInstagramResponse;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.utils.PDUIDialogUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import io.realm.Realm;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

/**
 * Created by mikenolan on 16/08/16.
 */
public class PDUIConnectSocialAccountFragment extends Fragment implements View.OnClickListener {

    private static String TAG = PDUIConnectSocialAccountCallback.class.getSimpleName();
    private Realm realm;


    /**
     * Location is now needed in this Fragment, as the user can now Register as well as Connect
     */
    private final int LOCATION_PERMISSION_REQUEST = 90;
    private PDLocationManager mLocationManager;
    private Location location;
    private boolean mAskForPermission = true;
    private boolean fromSettings;

    /**
     * Callback for result
     */
    public interface PDUIConnectSocialAccountCallback {
        void onAccountConnected(@PDConnectSocialAccountType int type);
    }

    @IntDef({PD_CONNECT_TYPE_FACEBOOK, PD_CONNECT_TYPE_TWITTER, PD_CONNECT_TYPE_INSTAGRAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PDConnectSocialAccountType {
    }

    public static final int PD_CONNECT_TYPE_FACEBOOK = 0;
    public static final int PD_CONNECT_TYPE_TWITTER = 1;
    public static final int PD_CONNECT_TYPE_INSTAGRAM = 2;


    /**
     * Create a new instance of PDUIConnectSocialAccountFragment.
     *
     * @param type                     PDConnectSocialAccountType
     * @param accountConnectedCallback callback to Activity
     * @return Instance of PDUIConnectSocialAccountFragment
     */
    public static PDUIConnectSocialAccountFragment newInstance(@PDConnectSocialAccountType int type, @NonNull PDUIConnectSocialAccountCallback accountConnectedCallback) {
        return newInstance(type, false, accountConnectedCallback);
    }
    /**
     * Create a new instance of PDUIConnectSocialAccountFragment.
     *
     * @param type                     PDConnectSocialAccountType
     * @param fromSettings             Boolean for determining display text
     * @param accountConnectedCallback callback to Activity
     * @return Instance of PDUIConnectSocialAccountFragment
     */
    public static PDUIConnectSocialAccountFragment newInstance(@PDConnectSocialAccountType int type, Boolean fromSettings, @NonNull PDUIConnectSocialAccountCallback accountConnectedCallback) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        if(fromSettings)
            args.putBoolean("fromSettings", true);

        PDUIConnectSocialAccountFragment fragment = new PDUIConnectSocialAccountFragment();
        fragment.setAccountConnectedCallback(accountConnectedCallback);
        fragment.setArguments(args);
        return fragment;
    }

    private int mType = PD_CONNECT_TYPE_FACEBOOK;
    private PDUIConnectSocialAccountCallback mAccountConnectedCallback;
    private CallbackManager mCallbackManager;

    private Button mButton;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    // Empty constructor
    public PDUIConnectSocialAccountFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mType = args.getInt("type", PD_CONNECT_TYPE_FACEBOOK);
            if(args.containsKey("fromSettings")){
                fromSettings = args.getBoolean("fromSettings");
            }
        }
        mLocationManager = new PDLocationManager(getActivity());
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_connect_social_account, container, false);

        TextView textView = (TextView) view.findViewById(R.id.pd_connect_social_title_text_view);
        textView.setText(getMessageText());

        mImageView = (ImageView) view.findViewById(R.id.pd_connect_social_network_image_view);
        mImageView.setImageResource(getImageResource());

        mButton = (Button) view.findViewById(R.id.pd_connect_social_button);
        mButton.setText(getButtonText());
        mButton.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mType == PD_CONNECT_TYPE_FACEBOOK) {
                mButton.setBackgroundColor(getActivity().getColor(R.color.pd_facebook_blue));
            } else if (mType == PD_CONNECT_TYPE_TWITTER) {
                mButton.setBackgroundColor(getActivity().getColor(R.color.pd_twitter_color));
            } else if (mType == PD_CONNECT_TYPE_INSTAGRAM) {
                float height = getActivity().getResources().getDimension(R.dimen.pd_button_height);
                mButton.setBackgroundResource(R.drawable.pd_insta_button_v2);
                mButton.getLayoutParams().height = (int)height;
            }
        }else{
            if (mType == PD_CONNECT_TYPE_FACEBOOK) {
                mButton.setBackgroundColor(getActivity().getResources().getColor(R.color.pd_facebook_blue));
            } else if (mType == PD_CONNECT_TYPE_TWITTER) {
                mButton.setBackgroundColor(getActivity().getResources().getColor(R.color.pd_twitter_color));
            } else if (mType == PD_CONNECT_TYPE_INSTAGRAM) {
                float height = getActivity().getResources().getDimension(R.dimen.pd_button_height);
                mButton.setBackgroundResource(R.drawable.pd_insta_button_v2);
                mButton.getLayoutParams().height = (int)height;
            }
        }


        mProgressBar = (ProgressBar) view.findViewById(R.id.pd_connect_social_progress);

        return view;
    }

    public void setAccountConnectedCallback(@NonNull PDUIConnectSocialAccountCallback accountConnectedCallback) {
        this.mAccountConnectedCallback = accountConnectedCallback;
    }

    private String getMessageText() {
        String network = "";
        if (mType == PD_CONNECT_TYPE_FACEBOOK) {
            network = getString(R.string.pd_connect_facebook_title);
        } else if (mType == PD_CONNECT_TYPE_TWITTER) {
            network = getString(R.string.pd_connect_twitter_title);
        } else if (mType == PD_CONNECT_TYPE_INSTAGRAM) {
            network = getString(R.string.pd_connect_instagram_title);
        }
        if(fromSettings){
            return getString(R.string.pd_connect_dialog_message_text_settings, network, network);
        }
        return getString(R.string.pd_connect_dialog_message_text, network);
    }

    @DrawableRes
    private int getImageResource() {
        if (mType == PD_CONNECT_TYPE_FACEBOOK) {
            return R.drawable.pd_facebook_icon;
        } else if (mType == PD_CONNECT_TYPE_TWITTER) {
            return R.drawable.pd_twitter_icon;
        } else {
            return R.drawable.pd_instagram_icon;
        }
    }

    private String getButtonText() {
        if(fromSettings){
            return getString(R.string.pd_connect_dialog_button_text_settings);
        }
        String network = "";
        if (mType == PD_CONNECT_TYPE_FACEBOOK) {
            network = getString(R.string.pd_connect_facebook_title);
        } else if (mType == PD_CONNECT_TYPE_TWITTER) {
            network = getString(R.string.pd_connect_twitter_title);
        } else if (mType == PD_CONNECT_TYPE_INSTAGRAM) {
            network = getString(R.string.pd_connect_instagram_title);
        }
        return getString(R.string.pd_connect_dialog_button_text, network);
    }

    private void registerFacebookLoginManagerCallback() {
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                connectFacebookAccount();
            }

            @Override
            public void onCancel() {
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CANCELLED_FACEBOOK_LOGIN, null);
//                PDLog.d(PDUISocialLoginFragment.class, "Facebook Login onCancel()");
//                if (getActivity() != null) {
//                    PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), R.string.pd_common_facebook_login_cancelled_title_text, R.string.pd_common_facebook_login_cancelled_message_text);
//                }
            }

            @Override
            public void onError(FacebookException error) {
                PDLog.d(PDUISocialLoginFragment.class, "Facebook Login onError(): " + error.getMessage());
                if (getActivity() != null) {
                    PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), R.string.pd_common_sorry_text, error.getMessage());
                }
            }
        });
    }


    /*
     * All PDAPIClient connect calls require the same callback.
     * Facebook has extra steps which are handled by comparing the current PDConnectSocialAccountType
     */
    private final PDAPICallback<PDUser> PD_API_CALLBACK = new PDAPICallback<PDUser>() {
        @Override
        public void success(PDUser user) {
            PDUtils.updateSavedUser(user);
//            if ((mType == PD_CONNECT_TYPE_FACEBOOK || mType == PD_CONNECT_TYPE_INSTAGRAM || mType == PD_CONNECT_TYPE_TWITTER) && getActivity() != null) {
//                getActivity().sendBroadcast(new Intent(PDUIRewardsFragment.PD_LOGGED_IN_RECEIVER_FILTER));
//            }
            checkForLocationPermissionAndStartLocationManager();
//            updateUser();
        }

        @Override
        public void failure(int statusCode, Exception e) {
            toggleProgress(false);
            if (mType == PD_CONNECT_TYPE_FACEBOOK) {
                LoginManager.getInstance().logOut();
            }
            //Attecmpt to see if the error is an already connected account
            //New constraint - social account can be connected to one user only
            RetrofitError err = (RetrofitError)e;
            if (err.getResponse() != null && err.getResponse().getBody() != null) {
                String json =  new String(((TypedByteArray)err.getResponse().getBody()).getBytes());
                try {
                    JSONObject jsobj = new JSONObject(json);
                    if (jsobj.get("error") != null) {
                        if (jsobj.getString("error") != null) {
                            if (jsobj.getString("error").contains("social account is already connected")) {
                                if (getActivity() != null) {
                                    PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), "Sorry - Wrong Account", "This social account has been linked to another user.");
                                    return;
                                }
                            }
                        }
                    }
                } catch (JSONException jse) {
                    showGenericAlert();
                    return;
                }
            }
            showGenericAlert();
        }
    };

    /**
     * Connect Facebook Account
     */
    private void connectFacebookAccount() {
        toggleProgress(true);

        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        if (userDetails == null) {
            //register
            PDAPIClient.instance().registerUserWithFacebook(AccessToken.getCurrentAccessToken().getToken(),
                    AccessToken.getCurrentAccessToken().getUserId(), PD_API_CALLBACK);
        } else {
            //connect
            PDAPIClient.instance().connectFacebookAccount(Long.parseLong(AccessToken.getCurrentAccessToken().getUserId()),
                    AccessToken.getCurrentAccessToken().getToken(), PD_API_CALLBACK);
        }
    }


    /**
     * Connect / Register Instagram Account
     *
     * @param instagramResponse Response from Instagram WebView login
     */
    private void connectInstagramAccount(PDInstagramResponse instagramResponse) {
        toggleProgress(true);

        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        if (userDetails == null) {
            //register
            Log.i(TAG, "connectInstagramAccount: Registering user via Instagram");
            PDAPIClient.instance().registerWithInstagramId(instagramResponse.getUser().getId(),
                    instagramResponse.getAccessToken(),
                    instagramResponse.getUser().getFullName(),
                    instagramResponse.getUser().getUsername(),
                    instagramResponse.getUser().getProfilePicture(),
                    PD_API_CALLBACK);
        } else {
            //connect
            Log.i(TAG, "connectInstagramAccount: Connecting user via Instagram");
            PDAPIClient.instance().connectWithInstagramAccount(instagramResponse.getUser().getId(),
                    instagramResponse.getAccessToken(), instagramResponse.getUser().getUsername(), PD_API_CALLBACK);
        }
    }


    /**
     * Connect Twitter Account
     *
     * @param session TwitterSession from successful login
     */
    private void connectTwitterAccount(TwitterSession session) {
        toggleProgress(true);

        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        if (userDetails == null) {
            //register
            Log.i(TAG, "connectInstagramAccount: Registering user via Twitter");
            PDAPIClient.instance().registerUserwithTwitterParams(session.getAuthToken().token,
                    session.getAuthToken().secret,
                    String.valueOf(session.getUserId()),
                    PD_API_CALLBACK);
        } else {
            //connect
            Log.i(TAG, "connectInstagramAccount: Connecting user via Instagram");
            PDAPIClient.instance().connectWithTwitterAccount(String.valueOf(session.getUserId()),
                    session.getAuthToken().token, session.getAuthToken().secret, PD_API_CALLBACK);
        }
    }


    /**
     * After an account is successfully connected, trigger the callback and remove this fragment
     */
    private void triggerCallbackAfterSuccessfulConnect() {
        if (mAccountConnectedCallback != null) {
            mAccountConnectedCallback.onAccountConnected(mType);
        }
        removeThisFragment();
    }

    private void showGenericAlert() {
        if (getActivity() != null) {
            PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), R.string.pd_common_sorry_text, R.string.pd_common_something_wrong_text);
        }
    }

    public void removeThisFragment() {
        if (!isAdded()) {
            return;
        }
        getActivity().getSupportFragmentManager().popBackStack(getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void toggleProgress(boolean show) {
        mImageView.animate().alpha(show ? 0.5f : 1.0f);
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mButton.setEnabled(!show);
        mButton.setText(show ? getString(R.string.pd_social_connect_connecting_button_text) : getButtonText());
    }

    @Override
    public void onClick(View v) {
        switch (mType) {
            case PD_CONNECT_TYPE_FACEBOOK:
                registerFacebookLoginManagerCallback();
                LoginManager.getInstance().logInWithReadPermissions(PDUIConnectSocialAccountFragment.this, Arrays.asList(PDSocialUtils.FACEBOOK_READ_PERMISSIONS));
                break;

            case PD_CONNECT_TYPE_TWITTER:
                PDSocialUtils.loginWithTwitter(getActivity(), new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        if (result.data != null) {
                            connectTwitterAccount(result.data);
                        } else {
                            showGenericAlert();
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        if (getActivity() != null) {
                            PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), R.string.pd_claim_twitter_button_text, e.getMessage());
                        }
                    }
                });
                break;

            case PD_CONNECT_TYPE_INSTAGRAM:
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CLICKED_SIGN_IN_INSTAGRAM, null);
                if (PDSocialUtils.canUseInstagram()) {
                    PDUIInstagramLoginFragment fragment = PDUIInstagramLoginFragment.newInstance(new PDUIInstagramLoginFragment.PDInstagramLoginCallback() {
                        @Override
                        public void loggedIn(PDInstagramResponse response) {
                            connectInstagramAccount(response);
                        }

                        @Override
                        public void error(String message) {
                            showGenericAlert();
                        }

                        @Override
                        public void canceled() {

                        }
                    });
                    final int containerId = ((ViewGroup) getView().getParent()).getId();
                    getFragmentManager().beginTransaction()
                            .add(containerId, fragment, PDUIInstagramLoginFragment.getName())
                            .addToBackStack(PDUIInstagramLoginFragment.getName())
                            .commit();
                } else {
                    PDLog.w(PDUIConnectSocialAccountFragment.class, "Could not initialise Instagram");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            TwitterLoginButton loginButton = new TwitterLoginButton(getActivity());
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static String getName() {
        return PDUIConnectSocialAccountFragment.class.getSimpleName();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    ////////////////////////////////////////////////////
    // Location Methods                              //
    //////////////////////////////////////////////////

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
        mLocationManager.startLocationUpdates(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    handleLocationUpdate(location);
                }
            }
        });
    }

    private void handleLocationUpdate(final Location l) {
        mLocationManager.stop();
        location = l;
        PDUtils.updateSavedUserLocation(location);
        updateUser();
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

    ////////////////////////////////////////////////////
    // User Methods                                  //
    //////////////////////////////////////////////////

    private void updateUser() {
        Realm realm = Realm.getDefaultInstance();

        PDRealmGCM gcm = realm.where(PDRealmGCM.class).findFirst();
        String deviceToken = gcm == null ? "" : gcm.getRegistrationToken();

        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();

        if (userDetails == null) {
            realm.close();
            return;
        }

        String socialType = "";
        if (mType == PD_CONNECT_TYPE_FACEBOOK)
            socialType = PDSocialUtils.SOCIAL_TYPE_FACEBOOK;
        if (mType == PD_CONNECT_TYPE_TWITTER)
            socialType = PDSocialUtils.SOCIAL_TYPE_TWITTER;
        if (mType == PD_CONNECT_TYPE_INSTAGRAM)
            socialType = PDSocialUtils.SOCIAL_TYPE_INSTAGRAM;

        PDAPIClient.instance().updateUserLocationAndDeviceToken(socialType, userDetails.getId(), deviceToken, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDLog.d(PDUIConnectSocialAccountFragment.class, "update user: " + user);

                PDUtils.updateSavedUser(user);
                if ((mType == PD_CONNECT_TYPE_FACEBOOK || mType == PD_CONNECT_TYPE_INSTAGRAM || mType == PD_CONNECT_TYPE_TWITTER) && getActivity() != null) {
//                    getActivity().sendBroadcast(new Intent(PDUIRewardsFragment.PD_LOGGED_IN_RECEIVER_FILTER));
                }
                triggerCallbackAfterSuccessfulConnect();
                toggleProgress(false);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUIConnectSocialAccountFragment.class, "failed update user: status=" + statusCode + ", e=" + e.getMessage());
                // Send broadcast to any registered receivers that user has logged in
                toggleProgress(false);
                showGenericAlert();
            }
        });
        realm.close();
    }
}
