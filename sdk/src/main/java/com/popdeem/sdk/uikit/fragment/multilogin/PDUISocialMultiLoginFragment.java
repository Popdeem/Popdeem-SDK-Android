package com.popdeem.sdk.uikit.fragment.multilogin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.popdeem.sdk.core.model.PDInstagramResponse;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.fragment.PDUIInstagramLoginFragment;
import com.popdeem.sdk.uikit.fragment.PDUIRewardsFragment;
import com.popdeem.sdk.uikit.fragment.PDUISocialLoginFragment;
import com.popdeem.sdk.uikit.utils.PDUIColorUtils;
import com.popdeem.sdk.uikit.utils.PDUIDialogUtils;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;

/**
 * Created by dave on 21/04/2017.
 * Project: Popdeem-SDK-Android
 */


public class PDUISocialMultiLoginFragment extends Fragment implements View.OnClickListener {

    private static String TAG = PDUISocialMultiLoginFragment.class.getSimpleName();

    private View view;

    private final int LOCATION_PERMISSION_REQUEST = 90;

    private PDLocationManager mLocationManager;

    private ProgressBar mProgressFacebook;
    private LinearLayout progressView;

    private TextView mRewardsInfoTextView;

    private Button mContinueButton;

    private Button mFacebookLoginButton;
    private Button mTwitterLoginButton;
    private Button mInstaLoginButton;

    private CallbackManager mCallbackManager;

    private boolean mAskForPermission = true;

    private boolean isFacebook = false, isTwitter = false, isInstagram = false;

    private Location location;

    private PDFragmentCommunicator communicator; //used for certain instances where login does not occur at the beginning
    private ArrayList<PDReward> rewards;

    public PDUISocialMultiLoginFragment() {
    }

    public static PDUISocialMultiLoginFragment newInstance() {
        return new PDUISocialMultiLoginFragment();
    }

    public static PDUISocialMultiLoginFragment newInstance(ArrayList<PDReward> rewards) {
        PDUISocialMultiLoginFragment frag = new PDUISocialMultiLoginFragment();
        frag.rewards = rewards;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pd_social_multi_login, container, false);


        registerCallBacks();
        setupBackButton();
        setupSocialButtons();
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
                                .override(R.dimen.pd_reward_item_image_dimen, R.dimen.pd_reward_item_image_dimen)
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

    ////////////////////////////////////////////////////
    // Facebook Callbacks
    //////////////////////////////////////////////////

    private void registerCallBacks() {
        mLocationManager = new PDLocationManager(getActivity());
        mCallbackManager = CallbackManager.Factory.create();

        //Facebook specific callback - starts location
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mProgressFacebook.setVisibility(View.VISIBLE);
                progressView.setVisibility(View.VISIBLE);
                mFacebookLoginButton.setText(R.string.pd_log_out_facebook_text);
                PDLog.d(PDUISocialMultiLoginFragment.class, "Facebook Login onSuccess(): " + loginResult.getAccessToken().getToken());
                checkForLocationPermissionAndStartLocationManager();
            }

            @Override
            public void onCancel() {
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CANCELLED_FACEBOOK_LOGIN, null);
                PDLog.d(PDUISocialLoginFragment.class, "Facebook Login onCancel()");
                progressView.setVisibility(View.GONE);
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
                progressView.setVisibility(View.GONE);
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                        .setTitle(R.string.pd_common_sorry_text)
                        .setMessage(error.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });
    }

    private final PDAPICallback<PDUser> PD_API_CALLBACK = new PDAPICallback<PDUser>() {
        @Override
        public void success(PDUser user) {
            PDLog.d(PDUISocialMultiLoginFragment.class, "registered with Social A/C: " + user.toString());

            PDUtils.updateSavedUser(user);
            updateUser();

            PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_LOGIN, new PDAbraProperties.Builder()
                    .add("Source", "Login Takeover")
                    .create());
            PDAbraLogEvent.onboardUser();
        }

        @Override
        public void failure(int statusCode, Exception e) {
            PDLog.d(PDUISocialMultiLoginFragment.class, "failed register with social a/c: statusCode=" + statusCode + ", message=" + e.getMessage());

            if (isFacebook)
                LoginManager.getInstance().logOut();

            mProgressFacebook.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);

            mFacebookLoginButton.setVisibility(View.VISIBLE);
            mTwitterLoginButton.setVisibility(View.VISIBLE);
            mInstaLoginButton.setVisibility(View.VISIBLE);
            mFacebookLoginButton.setText(R.string.pd_log_in_with_facebook_text);
            mTwitterLoginButton.setText(R.string.pd_log_in_with_twitter_text);
            mInstaLoginButton.setText(R.string.pd_log_in_with_instagram_text);

            new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                    .setTitle(R.string.pd_common_sorry_text)
                    .setMessage("An error occurred while registering. Please try again")
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show();
        }
    };

    ////////////////////////////////////////////////////
    // Social Login Buttons
    ///////////////////////////////////////////////////

    private void setupSocialButtons() {
        mFacebookLoginButton = (Button) view.findViewById(R.id.pd_facebook_login_button);
        mFacebookLoginButton.setOnClickListener(this);
        mTwitterLoginButton = (Button) view.findViewById(R.id.pd_twitter_login_button);
        mTwitterLoginButton.setOnClickListener(this);
        mInstaLoginButton = (Button) view.findViewById(R.id.pd_instagram_login_button);
        mInstaLoginButton.setOnClickListener(this);
        mInstaLoginButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mInstaLoginButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mInstaLoginButton.setBackground(getRoundedInstagramButton());
                }else{
                    mInstaLoginButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mInstaLoginButton.setBackgroundDrawable(getRoundedInstagramButton());

                }
            }
        });

        if(PDSocialUtils.getTwitterConsumerKey(getContext()) == null){
            mTwitterLoginButton.setVisibility(View.GONE);
        }

        mProgressFacebook = (ProgressBar) view.findViewById(R.id.pd_progress_bar);
        progressView = (LinearLayout) view.findViewById(R.id.pd_progress_layout);
    }

    public Drawable getRoundedInstagramButton() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pd_insta_button_v2);
        Bitmap output = Bitmap.createBitmap(mInstaLoginButton.getWidth(), mInstaLoginButton.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());
        final RectF rectF = new RectF(0, 0, output.getWidth(), output.getHeight());
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, r.getDisplayMetrics());
        final float roundPx = px;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return new BitmapDrawable(getResources(), output);
    }


    ////////////////////////////////////////////////////
    // Social Login Buttons Click Listeners
    //////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        final int ID = v.getId();

        if (ID == R.id.pd_facebook_login_button) {
            Log.i(TAG, "onClick: Facebook Login Selected");
            isFacebook = true;
            isTwitter = false;
            isInstagram = false;
            loginFacebook();

        } else if (ID == R.id.pd_twitter_login_button) {
            Log.i(TAG, "onClick: Twitter Login Selected");
            isFacebook = false;
            isTwitter = true;
            isInstagram = false;
            checkForLocationPermissionAndStartLocationManager();
        } else if (ID == R.id.pd_instagram_login_button) {
            Log.i(TAG, "onClick: Instagram Login Selected");
            isFacebook = false;
            isTwitter = false;
            isInstagram = true;
            checkForLocationPermissionAndStartLocationManager();
        }
    }

    ////////////////////////////////////////////////////
    // Social Login Methods                          //
    //////////////////////////////////////////////////


    /**
     * Facebook
     */
    private void loginFacebook() {

        if (PDLocationManager.isGpsEnabled(getActivity())) {
            mProgressFacebook.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.VISIBLE);
            LoginManager.getInstance().logInWithReadPermissions(PDUISocialMultiLoginFragment.this, Arrays.asList(PDSocialUtils.FACEBOOK_READ_PERMISSIONS));
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

    /**
     * Twitter
     */

    private void loginTwitter() {
        Log.i(TAG, "loginTwitter: Activity = " + getActivity().getClass().getSimpleName());
        PDSocialUtils.loginWithTwitter(getActivity(), new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                if (result.data != null) {
                    Log.i(TAG, "success: Twitter Data present");
                    registerTwitterAccount(result.data);
                } else {
                    progressView.setVisibility(View.GONE);
                    showGenericAlert();
                }
            }

            @Override
            public void failure(TwitterException e) {
                if (getActivity() != null) {
                    progressView.setVisibility(View.GONE);
                    PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), R.string.pd_claim_twitter_button_text, e.getMessage());
                }
            }
        });
    }

    private void registerTwitterAccount(TwitterSession session) {
//        PDAPIClient.instance().connectWithTwitterAccount(String.valueOf(session.getUserId()),
//                session.getAuthToken().token, session.getAuthToken().secret, PD_API_CALLBACK);

        PDAPIClient.instance().registerUserwithTwitterParams(session.getAuthToken().token,
                session.getAuthToken().secret,
                String.valueOf(session.getUserId()), PD_API_CALLBACK);


    }

    /**
     * Instagram
     */

    private void loginInstagram() {
        if (PDSocialUtils.canUseInstagram()) {

            mProgressFacebook.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.VISIBLE);

            PDUIInstagramLoginFragment fragment = PDUIInstagramLoginFragment.newInstance(new PDUIInstagramLoginFragment.PDInstagramLoginCallback() {
                @Override
                public void loggedIn(PDInstagramResponse response) {
                    Log.i(TAG, "loggedIn: Instagram Logged In");
                    registerInstagramAccount(response);
                }

                @Override
                public void error(String message) {
                    mProgressFacebook.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    showGenericAlert();
                }

                @Override
                public void canceled() {
                    progressView.setVisibility(View.GONE);
                }
            });
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment, PDUIInstagramLoginFragment.getName())
                    .addToBackStack(PDUIInstagramLoginFragment.getName())
                    .commit();
        } else {
            PDLog.w(getClass(), "Could not initialise Instagram");
        }
    }

    private void registerInstagramAccount(PDInstagramResponse instagramResponse) {
        PDAPIClient.instance().registerWithInstagramId(instagramResponse.getUser().getId(),
                instagramResponse.getAccessToken(),
                instagramResponse.getUser().getFullName(),
                instagramResponse.getUser().getUsername(),
                instagramResponse.getUser().getProfilePicture(),
                PD_API_CALLBACK);
    }


    //////////////////////////////////////////////////////////////////////////////////
    // Back Button - just closes the login fragment to continue with a Non-Social User
    //////////////////////////////////////////////////////////////////////////////////

    private void setupBackButton() {
        ImageButton backButton = (ImageButton) view.findViewById(R.id.pd_social_login_back_button);
        backButton.setImageDrawable(PDUIColorUtils.getSocialLoginBackButtonIcon(getActivity()));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressView.getVisibility() == View.GONE) {
                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CLICKED_CLOSE_LOGIN_TAKEOVER, new PDAbraProperties.Builder()
                            .add("Source", "Dismiss Button")
                            .create());
                    removeThisFragment();
                }
            }
        });
    }

    ////////////////////////////////////////////////////
    // Pop this fragment off the stack               //
    //////////////////////////////////////////////////

    public void removeThisFragment() {
        getActivity().getSupportFragmentManager().popBackStack(PDUISocialMultiLoginFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

//
//        mFacebookLoginButton.setVisibility(View.INVISIBLE);
//        mTwitterLoginButton.setVisibility(View.INVISIBLE);
//        mInstaLoginButton.setVisibility(View.INVISIBLE);

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

        if (isFacebook) {
            PDAPIClient.instance().registerUserWithFacebook(AccessToken.getCurrentAccessToken().getToken(), AccessToken.getCurrentAccessToken().getUserId(), PD_API_CALLBACK);
        } else if (isTwitter) {
            loginTwitter();
        } else if (isInstagram) {
            loginInstagram();
        }
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
        if (isFacebook)
            socialType = PDSocialUtils.SOCIAL_TYPE_FACEBOOK;
        if (isTwitter)
            socialType = PDSocialUtils.SOCIAL_TYPE_TWITTER;
        if (isInstagram)
            socialType = PDSocialUtils.SOCIAL_TYPE_INSTAGRAM;

        PDAPIClient.instance().updateUserLocationAndDeviceToken(socialType, userDetails.getId(), deviceToken, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDLog.d(PDUISocialMultiLoginFragment.class, "update user: " + user);

                PDUtils.updateSavedUser(user);

                // Send broadcast to any registered receivers that user has logged in
                getActivity().sendBroadcast(new Intent(PDUIRewardsFragment.PD_LOGGED_IN_RECEIVER_FILTER));
                // Update view
                updateViewAfterLogin();
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUISocialMultiLoginFragment.class, "failed update user: status=" + statusCode + ", e=" + e.getMessage());
                // Send broadcast to any registered receivers that user has logged in
                updateViewAfterLogin();
            }
        });
        realm.close();
    }


    ////////////////////////////////////////////////////
    // Generic Methods                               //
    //////////////////////////////////////////////////

    private void showGenericAlert() {
        if (getActivity() != null) {
            PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), R.string.pd_common_sorry_text, R.string.pd_common_something_wrong_text);
        }
    }

    private void updateViewAfterLogin() {
//        mProgressFacebook.setVisibility(View.GONE);
//        mRewardsInfoTextView = (TextView) view.findViewById(R.id.pd_social_rewards_info_text_view);
//        mRewardsInfoTextView.setText(R.string.pd_social_login_success_description_text);
//
//        mFacebookLoginButton.setVisibility(View.GONE);
//        mTwitterLoginButton.setVisibility(View.GONE);
//        mInstaLoginButton.setVisibility(View.GONE);
//
//        mContinueButton = (Button) view.findViewById(R.id.pd_social_continue_button);
//        mContinueButton.setVisibility(View.VISIBLE);
//        mContinueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeThisFragment();
//            }
//        });

        removeThisFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mProgressFacebook.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.VISIBLE);
            Log.i(TAG, "onActivityResult: twitter Auth Config");
            TwitterLoginButton loginButton = new TwitterLoginButton(getActivity());
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static String getName() {
        return PDUISocialMultiLoginFragment.class.getSimpleName();
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

    ////////////////////////////////////////////////////
    // Generic Methods                               //
    //////////////////////////////////////////////////
}
