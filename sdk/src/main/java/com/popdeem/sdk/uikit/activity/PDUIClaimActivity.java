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

package com.popdeem.sdk.uikit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.location.LocationListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.popdeem.sdk.BuildConfig;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.api.abra.PDAbraUtils;
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.core.location.PDLocationValidator;
import com.popdeem.sdk.core.model.PDBGScanResponseModel;
import com.popdeem.sdk.core.model.PDPostScan;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDClipboardUtils;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.core.utils.PDPermissionHelper;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDTwitterBroadcastReceiver;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.fragment.PDUIConnectSocialAccountFragment;
import com.popdeem.sdk.uikit.fragment.PDUIInstagramLoginFragment;
import com.popdeem.sdk.uikit.fragment.PDUIShareMessageFragment;
import com.popdeem.sdk.uikit.fragment.PDUITagFriendsFragment;
import com.popdeem.sdk.uikit.fragment.PDUIWalletFragment;
import com.popdeem.sdk.uikit.utils.PDUIColorUtils;
import com.popdeem.sdk.uikit.utils.PDUIImageUtils;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.soundcloud.android.crop.Crop;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;
import com.yalantis.ucrop.UCrop;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

import static com.twitter.sdk.android.tweetcomposer.TweetUploadService.TWEET_COMPOSE_CANCEL;
import static com.twitter.sdk.android.tweetcomposer.TweetUploadService.UPLOAD_FAILURE;
import static com.twitter.sdk.android.tweetcomposer.TweetUploadService.UPLOAD_SUCCESS;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDUIClaimActivity extends PDBaseActivity implements View.OnClickListener, LocationListener, CompoundButton.OnCheckedChangeListener {
    String TAG = "PDUIClaimActivity";

    private FragmentManager mFragmentManager;

    private PDReward mReward;

//    private EditText mMessageEditText;
    private SwitchCompat mFacebookSwitch;
    private SwitchCompat mTwitterSwitch;
    private SwitchCompat mInstagramSwitch;
    private View mNotHereView;

    private boolean mIsHere = false;

    private boolean mUserHasLeftForInstagram = false;
    private boolean mUserHasLeftForTwitter = false;
    private boolean mUserHasLeftForFaceBook = false;


    private boolean mImageAdded = false;
    private boolean mImageFromCamera = false;
    private String mCurrentPhotoPath;
    private String mCurrentResizedPhotoPath;
    private String mCurrentCroppedPhotoPath;

    private ArrayList<String> mTaggedNames = new ArrayList<>();
    private ArrayList<String> mTaggedIds = new ArrayList<>();

    private CallbackManager mCallbackManager;
    private PDLocationManager mLocationManager;

    private RelativeLayout verifyView;
    private DilatingDotsProgressBar dotProgress;
    private TextView pdVerifyHeadingText;
    private CardView pdVerifyImageCard;
    private PDUIBezelImageView pdClaimProfileImageView;
    private TextView pdClaimUserNameTextView;
    private TextView pdClaimUserHashtagTextView;
    private ImageView pdVerifyImage;
    private Button pdProceedButton;
    private Button pdBackButton;
    private boolean claiming = false;
    private LinearLayout addPhotoView;
    private LinearLayout addedPhotoView;

    public PDTwitterBroadcastReceiver event_detail_receiver;

    private Uri twitterURI;
    private Bitmap image;
    private PDBGScanResponseModel pdbgScanResponseModel;

    public boolean isFacebookInstalled() {
        try {
            getApplicationContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_claim_v2);
        setTitle(R.string.pd_claim_title);


        mLocationManager = new PDLocationManager(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(BACK_STACK_CHANGED_LISTENER);

        mCallbackManager = CallbackManager.Factory.create();

        mReward = new Gson().fromJson(getIntent().getStringExtra("reward"), PDReward.class);
        addRewardDetailsToUI();

//        mMessageEditText = (EditText) findViewById(R.id.pd_claim_share_edit_text);
        mFacebookSwitch = (SwitchCompat) findViewById(R.id.pd_claim_facebook_switch);
        mTwitterSwitch = (SwitchCompat) findViewById(R.id.pd_claim_twitter_switch);
        mInstagramSwitch = (SwitchCompat) findViewById(R.id.pd_claim_instagram_switch);
        mNotHereView = findViewById(R.id.pd_claim_not_here_container);

        ImageView notHereTickImageView = (ImageView) findViewById(R.id.pd_claim_not_here_tick_image_view);
        notHereTickImageView.setImageDrawable(PDUIColorUtils.getLocationVerificationTickIcon(this));

//        mMessageEditText.addTextChangedListener(MESSAGE_TEXT_WATCHER);

        addClickListenersToViews();
        updateEnabledStateOfViews();


        verifyView = (RelativeLayout) findViewById(R.id.pd_claim_progress_view);
        dotProgress = (DilatingDotsProgressBar) findViewById(R.id.dots_progress);

        pdVerifyHeadingText = (TextView)findViewById( R.id.pd_verify_heading_text );
        pdVerifyImageCard = (CardView)findViewById( R.id.pd_verify_image_card );
        pdClaimProfileImageView = (PDUIBezelImageView)findViewById( R.id.pd_claim_profile_image_view );
        pdClaimUserNameTextView = (TextView)findViewById( R.id.pd_claim_user_name_text_view );
        pdClaimUserHashtagTextView = (TextView)findViewById( R.id.pd_claim_user_hashtag_text_view );
        pdVerifyImage = (ImageView)findViewById( R.id.pd_verify_image );
        pdProceedButton = (Button)findViewById( R.id.pd_verify_proceed_button );
        pdBackButton = (Button)findViewById( R.id.pd_verify_back_button );

        verifyView.setVisibility(View.GONE);
        pdVerifyHeadingText.setVisibility(View.GONE);
        pdVerifyImageCard.setVisibility(View.GONE);
        pdProceedButton.setVisibility(View.GONE);
        pdBackButton.setVisibility(View.GONE);

//        if(!isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK)) {
//            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(PDSocialUtils.FACEBOOK_PUBLISH_PERMISSIONS));
//        }

    }

    //*******************************************************
    //          Activity Lifecycle Methods
    //*******************************************************

    @Override
    protected void onStart() {
        super.onStart();
        mLocationManager.startLocationUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserHasLeftForInstagram) {
            mUserHasLeftForInstagram = false;
            PDLog.d(getClass(), "perform claim now, user returned from IG");
            mFragmentManager.popBackStack(PDUIShareMessageFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            performClaimReward("", getEncodedImage(), true, false, false);
//        }else if (mUserHasLeftForFaceBook) {
//            mUserHasLeftForInstagram = false;
//            PDLog.d(getClass(), "perform claim now, user returned from FB");
//            mFragmentManager.popBackStack(PDUIShareMessageFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            performClaimReward("", getEncodedImage(), false, false, true);
        }else

            PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_VIEWED_CLAIM)
                .add(PDAbraConfig.ABRA_PROPERTYNAME_REWARD_TYPE, PDAbraUtils.keyForRewardType(mReward.getRewardType()))
                .add(PDAbraConfig.ABRA_PROPERTYNAME_REWARD_ACTION, PDAbraUtils.keyForRewardAction(mReward.getAction()))
                .add(PDAbraConfig.ABRA_PROPERTYNAME_NETWORKS_AVAILABLE, readableMediaTypedAvailable())
                .add(PDAbraConfig.ABRA_PROPERTYNAME_REWARD_NAME, mReward.getDescription())
                .create());

        if (event_detail_receiver == null) {
            event_detail_receiver = new PDTwitterBroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
                        // success
                        Log.i(TAG, "onReceive: UPLOAD_SUCCESS");
//                        finishActivityAfterClaim("twitter");
                        if (mUserHasLeftForTwitter) {
                            mUserHasLeftForTwitter = false;
                            PDLog.d(getClass(), "perform claim now, user returned from IG");
                            mFragmentManager.popBackStack(PDUIShareMessageFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            performClaimReward("", getEncodedImage(), false, true, false);
                        }
                    } else if (TweetUploadService.UPLOAD_FAILURE.equals(intent.getAction())) {
                        // failure
                        Log.i(TAG, "onReceive: UPLOAD_FAILURE");
                    } else if (TweetUploadService.TWEET_COMPOSE_CANCEL.equals(intent.getAction())) {
                        showBasicOKAlertDialog(R.string.pd_twitter_error, R.string.pd_tweet_failed_to_send);
                        // cancel
                        Log.i(TAG, "onReceive: TWEET_COMPOSE_CANCEL");
                    }
                }
            };
        }
        IntentFilter filterTwitter = new IntentFilter(UPLOAD_SUCCESS);
        filterTwitter.addAction(UPLOAD_FAILURE);
        filterTwitter.addAction(TWEET_COMPOSE_CANCEL);

        registerReceiver(event_detail_receiver, filterTwitter);
    }

    @Override
    protected void onPause() {
        if (event_detail_receiver != null) {
            unregisterReceiver(event_detail_receiver);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationManager != null) {
            mLocationManager.stop();
        }
    }

    @Override
    protected void onDestroy() {
        PDUIImageUtils.deletePhotoFile(mCurrentPhotoPath);
        PDUIImageUtils.deletePhotoFile(mCurrentCroppedPhotoPath);
        PDUIImageUtils.deletePhotoFile(mCurrentResizedPhotoPath);
        super.onDestroy();
    }


    /**
     * Check if the user is at the Brands location
     *
     * @param location Users current location
     */
    private void checkIsHere(final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsHere = PDLocationValidator.validateLocationForReward(mReward, location);
//                if(BuildConfig.DEBUG) {
//                    mIsHere = false; // TODO: Remove for release
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mNotHereView.setVisibility(mIsHere ? View.GONE : View.VISIBLE);
                        updateEnabledStateOfViews();
                    }
                });
            }
        }).start();
    }

//

    private void addClickListenersToViews() {
        mFacebookSwitch.setOnCheckedChangeListener(this);
        mTwitterSwitch.setOnCheckedChangeListener(this);
        mInstagramSwitch.setOnCheckedChangeListener(this);
        findViewById(R.id.pd_claim_share_button).setOnClickListener(this);

        findViewById(R.id.pd_share_view_holder).setOnClickListener(this);

    }

    private void updateEnabledStateOfViews() {

        mFacebookSwitch.setEnabled(mIsHere && isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK));
        mTwitterSwitch.setEnabled(mIsHere && isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER));
        mInstagramSwitch.setEnabled(mIsHere && isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM));

        int counter = 0;
        if(!isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK)) {
            findViewById(R.id.pd_claim_facebook_holder).setVisibility(View.GONE);
            counter++;
        }

        if(!isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER)) {
            findViewById(R.id.pd_claim_twitter_holder).setVisibility(View.GONE);
            counter++;
        }

        if(!isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM)) {
            findViewById(R.id.pd_claim_instagram_holder).setVisibility(View.GONE);
            counter++;
        }

        if(counter>0){
            findViewById(R.id.spacer1).setVisibility(View.VISIBLE);
        }
        if(counter>1){
            findViewById(R.id.spacer2).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.pd_claim_share_button).setEnabled(mIsHere);
        findViewById(R.id.pd_share_view_holder).setEnabled(mIsHere);
        findViewById(R.id.pd_share_view_holder).setClickable(mIsHere);
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        realm.close();

    }
    private void addRewardDetailsToUI() {
        // Logo
        final ImageView logoImageView = (ImageView) findViewById(R.id.pd_reward_star_image_view);
        final String imageUrl = mReward.getCoverImage();
        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.contains("default")) {
            Glide.with(this)
                    .load(R.drawable.pd_ui_star_icon)
                    .error(R.drawable.pd_ui_star_icon)
                    .dontAnimate()
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(logoImageView);
        } else {
            Glide.with(this)
                    .load(imageUrl)
                    .error(R.drawable.pd_ui_star_icon)
                    .dontAnimate()
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(logoImageView);
        }

        // Reward Description
        TextView textView = (TextView) findViewById(R.id.pd_reward_offer_text_view);
        textView.setText(mReward.getDescription());

        // Rules
        textView = (TextView) findViewById(R.id.pd_reward_item_rules_text_view);
        textView.setText(mReward.getRules());
        if (mReward.getRules() == null || mReward.getRules().isEmpty()) {
            textView.setVisibility(View.GONE);
        }

        StringBuilder actionStringBuilder = new StringBuilder("");

        // Action
        final boolean TWITTER_ACTION_REQUIRED = twitterShareForced();
        if (mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
            actionStringBuilder.append(getString(TWITTER_ACTION_REQUIRED ? R.string.pd_claim_action_tweet_photo : R.string.pd_claim_action_photo_camera));
        } else if (mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_CHECKIN)) {
            actionStringBuilder.append(getString(TWITTER_ACTION_REQUIRED ? R.string.pd_claim_action_tweet_checkin : R.string.pd_claim_action_checkin));
        } else {
            actionStringBuilder.append(getString(R.string.pd_claim_action_none));
        }

        // End date
        long timeInSecs = PDNumberUtils.toLong(mReward.getAvailableUntilInSeconds(), -1);
        String convertedTimeString = PDUIUtils.convertTimeToDayAndMonth(timeInSecs);
        if (!convertedTimeString.isEmpty()) {
            actionStringBuilder.append(String.format(Locale.getDefault(), " | Exp %1s", convertedTimeString));
        }

        textView = (TextView) findViewById(R.id.pd_reward_request_text_view);
        textView.setText(actionStringBuilder.toString());
        textView.setVisibility(View.GONE);

        //add photo
        addPhotoView = findViewById(R.id.pd_share_view_icon_holder);
        addedPhotoView = findViewById(R.id.pd_share_view_taken_holder);
        TextView infoView = findViewById(R.id.pd_claim_info_text);


        //already shared button
        LinearLayout alreadySharedButton = findViewById(R.id.pd_claim_already_shared_view);
        TextView alreadySharedText = findViewById(R.id.pd_claim_already_shared_button);

        if(mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
            alreadySharedText.setText(getResources().getString(R.string.pd_claim_scan_for_already_shared_text));
            infoView.setText(String.format(getResources().getString(R.string.pd_claim_info_photo), mReward.getGlobalHashtag()));
        }else{
            alreadySharedText.setText(getResources().getString(R.string.pd_claim_scan_for_already_shared_activity_text));
            infoView.setText(String.format(getResources().getString(R.string.pd_claim_info_check_in), mReward.getGlobalHashtag()));
        }

        alreadySharedButton.setOnClickListener(this);
        if (mReward.getGlobalHashtag() != null && !mReward.getGlobalHashtag().equalsIgnoreCase("")) {
//            alreadySharedButton.setText(String.format(getString(R.string.pd_claim_get_already_shared_text), mReward.getGlobalHashtag()));
        } else {
//            alreadySharedButton.setText(R.string.pd_claim_already_shared_default);
        }
    }

    private boolean twitterShareForced() {
        List<String> mediaTypes = mReward.getSocialMediaTypes();
        return mediaTypes.size() == 1 && mediaTypes.contains(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER);
    }

    private boolean isNetworkAvailableForShare(@PDReward.PDSocialMediaType String network) {
        List<String> mediaTypes = mReward.getSocialMediaTypes();
        return mediaTypes.contains(network);
    }

    private String readableMediaTypedAvailable() {
        List<String> mediaTypes = mReward.getSocialMediaTypes();
        StringBuilder builder = new StringBuilder("");
        for (int i = 0, size = mediaTypes.size(); i < size; i++) {
            if (i > 0) {
                builder.append(i == (size - 1) ? " & " : ", ");
            }
            String type = mediaTypes.get(i);
            builder.append(type);
        }
        return builder.toString();
    }

    private String readableNetworksChosen() {
        StringBuilder builder = new StringBuilder("");
        if (mFacebookSwitch.isChecked()) {
            builder.append(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK);
        }
        if (mTwitterSwitch.isChecked()) {
            builder.append(" ");
            builder.append(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER);
        }
        if (mInstagramSwitch.isChecked()) {
            builder.append(" ");
            builder.append(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM);
        }
        return builder.toString();
    }

    private String getEncodedImage() {
        String encodedImage = null;
        File imageFile = new File(mCurrentCroppedPhotoPath);
        if (imageFile.exists()) {
            Bitmap b = PDUIImageUtils.getResizedBitmap(imageFile.getAbsolutePath(), 500, 500, mImageFromCamera ? -1 : PDUIImageUtils.getOrientation(mCurrentPhotoPath));
            if (b != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                byte[] byteArray = outputStream.toByteArray();

                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                b.recycle();
                PDLog.d(PDUIClaimActivity.class, "image_upload->encodedImage: " + encodedImage);
            }
        }
        return encodedImage;
    }

    private void post(final boolean addImage) {
        // Check if user is suspended
        if (PDUtils.isUserSuspended()) {
            String suspendedUntil = PDUtils.getUserSuspendedUntil();
            long suspendedUntilTime = PDNumberUtils.toLong(suspendedUntil, -1);
            if (suspendedUntilTime == -1) {
                showBasicOKAlertDialog(R.string.pd_suspended_title_string, R.string.pd_suspended_message_string);
            } else {
                String dateString = PDUIUtils.convertUnixTimeToDate(suspendedUntilTime, "EEE dd MMM 'at' kk:mm");
                showBasicOKAlertDialog(R.string.pd_suspended_title_string, String.format(Locale.getDefault(), getString(R.string.pd_suspended_message_with_date_string), dateString));
            }
            return;
        }

        // Check if at least one network is selected
        if (!mFacebookSwitch.isChecked() && !mTwitterSwitch.isChecked() && !mInstagramSwitch.isChecked()) {
            showBasicOKAlertDialog(R.string.pd_claim_no_network_selected_title_text, R.string.pd_claim_no_network_selected_message_text);
            return;
        }


        // If posting to instagram
        if (mInstagramSwitch.isChecked()) {
            // Check if Instagram app is installed
            if (!PDSocialUtils.hasInstagramAppInstalled(getPackageManager())) {
                showBasicOKAlertDialog(R.string.pd_common_sorry_text, R.string.pd_claim_insta_not_installed);
                return;
            }
        }

        // If posting to Twitter
        if (mTwitterSwitch.isChecked()) {


            // Check if Twitter is logged in
            if (!PDSocialUtils.isTwitterLoggedIn()) {
                PDSocialUtils.loginWithTwitter(this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        if (result.data != null) {
                            connectTwitterAccount(result.data, addImage);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        showBasicOKAlertDialog(R.string.pd_claim_twitter_button_text, e.getMessage());
                    }
                });
                return;
            }
        }

        // If posting to Facebook
        if (mFacebookSwitch.isChecked()) {

//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(PDSocialUtils.FACEBOOK_READ_PERMISSIONS));
//             Check if user has given Facebook Publish permission
//            if (!PDSocialUtils.hasAllFacebookPublishPermissions()) {
//                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        post(addImage);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        PDLog.d(PDUIClaimActivity.class, "Facebook Login onCancel:");
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//                        PDLog.d(PDUIClaimActivity.class, "Facebook Login onError(): " + error.getMessage());
//                        new AlertDialog.Builder(PDUIClaimActivity.this)
//                                .setTitle(R.string.pd_common_sorry_text)
//                                .setMessage(error.getMessage())
//                                .setPositiveButton(android.R.string.ok, null)
//                                .create()
//                                .show();
//                    }
//                });
//                LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(PDSocialUtils.FACEBOOK_PUBLISH_PERMISSIONS));
//                return;
//            }
        }

        String encodedImage = null;
        if (addImage) {
            encodedImage = getEncodedImage();
        }

        String message = "";
        message = mReward.getGlobalHashtag();

        if (mInstagramSwitch.isChecked()) {
            postToInstagram(message, mCurrentCroppedPhotoPath);
        } else if (mTwitterSwitch.isChecked()) {
            postToTwitter("", mCurrentCroppedPhotoPath);
        } else if (mFacebookSwitch.isChecked()) {
            postToFacebook(message, mCurrentCroppedPhotoPath);
        } else {
            performClaimReward("", encodedImage, false, false, false);
        }
    }

    private void parseResponse(JsonObject response) {
        pdbgScanResponseModel = new Gson().fromJson(response, PDBGScanResponseModel.class);

        if (pdbgScanResponseModel.isValidated()) {
            Log.i(TAG, "parseResponse: User has shared, show success view");
        } else {
            Log.i(TAG, "parseResponse: User has not shared, show failure view");
        }
    }

    private void claimReward(){

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pd_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        final Button shareButton = (Button) findViewById(R.id.pd_claim_share_button);
        shareButton.setEnabled(false);
        shareButton.animate().alpha(0.5f);

        claiming = true;
        int postTime = 1;
        String twitterToken = null;
        String twitterSecret = null;
        String facebookToken = null;

        if (mTwitterSwitch.isChecked() && PDSocialUtils.isTwitterLoggedIn() && TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().token != null
                && TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().secret != null) {
            twitterToken = TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().token;
            twitterSecret = TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().secret;
        }else if(mFacebookSwitch.isChecked()){
            facebookToken = AccessToken.getCurrentAccessToken().getToken();
        }

        Realm realm = Realm.getDefaultInstance();
        PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();

        String lat = "", lng = "", id = "";

        if (userLocation != null) {
            lat = String.valueOf(userLocation.getLatitude());
            lng = String.valueOf(userLocation.getLongitude());
            id = String.valueOf(userLocation.getId());
        }


        PDAPIClient.instance().claimDiscovery(pdbgScanResponseModel, facebookToken, twitterToken, twitterSecret, null, lat, lng, id, mReward.getId(), this, new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                Log.i(TAG, "success: " + jsonObject.toString());
                if (jsonObject.has("error")) {
                    new AlertDialog.Builder(PDUIClaimActivity.this)
                            .setTitle(R.string.pd_common_sorry_text)
                            .setMessage(jsonObject.get("error").getAsString())
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                } else {
                    PDLog.d(PDUIClaimActivity.class, "claim: " + jsonObject.toString());
                    progressBar.setVisibility(View.GONE);
                    shareButton.setEnabled(true);
                    shareButton.animate().alpha(1.0f);

                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CLAIMED, new PDAbraProperties.Builder()
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORKS, readableNetworksChosen())
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_PHOTO, mImageAdded ? "YES" : "NO")
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_REWARD_TYPE, PDAbraUtils.keyForRewardType(mReward.getRewardType()))
                            .create());

//                    finishActivityAfterClaim("twitter");

                    Intent data = new Intent();
                    data.putExtra("id", mReward.getId());
                    data.putExtra("verificationNeeded", false);
                    setResult(RESULT_OK, data);
                    finish();
                    claiming = false;
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                Log.e(TAG, e.toString());
                new AlertDialog.Builder(PDUIClaimActivity.this)
                        .setTitle(R.string.pd_common_sorry_text)
                        .setMessage(R.string.pd_common_something_wrong_text)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });

        realm.close();
    }

    private void performClaimReward(final String message, final String encodedImage, final boolean fromInstagram, final boolean fromTwitter, final Boolean fromFacebook) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pd_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        final Button shareButton = (Button) findViewById(R.id.pd_claim_share_button);
        shareButton.setEnabled(false);
        shareButton.animate().alpha(0.5f);

        claiming = true;
        int postTime = 1;
        if(fromFacebook){
            postTime = 3000;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String twitterToken = null;
                String twitterSecret = null;
                if (mTwitterSwitch.isChecked() && PDSocialUtils.isTwitterLoggedIn() && TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().token != null
                        && TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().secret != null) {
                    twitterToken = TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().token;
                    twitterSecret = TwitterCore.getInstance().getSessionManager().getActiveSession().getAuthToken().secret;
                }

                Realm realm = Realm.getDefaultInstance();
                PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();

                PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
                String instagramAccessToken = null;
                if (mInstagramSwitch.isChecked() && userDetails.getUserInstagram() != null && userDetails.getUserInstagram().getAccessToken() != null && !userDetails.getUserInstagram().getAccessToken().isEmpty()) {
                    instagramAccessToken = userDetails.getUserInstagram().getAccessToken();
                }

                if (mTaggedNames.size() == 0) {
                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_ADDED_CLAIM_CONTENT, new PDAbraProperties.Builder()
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_TAGGED_FRIENDS, "Yes")
                            .add("Friends Count", String.valueOf(mTaggedNames.size()))
                            .create());
                }

                if (fromTwitter) {
                    finishActivityAfterClaim("twitter");
                    realm.close();
                    return;
                }else if(fromFacebook) {
                    finishActivityAfterClaim("facebook");
                    realm.close();
                    return;
                }


                PDAPIClient.instance().claimReward(PDUIClaimActivity.this, mFacebookSwitch.isChecked() ? AccessToken.getCurrentAccessToken().getToken() : null,
                        twitterToken, twitterSecret, instagramAccessToken, mReward.getId(), message, mTaggedNames, mTaggedIds, encodedImage,
                        String.valueOf(userLocation.getLongitude()), String.valueOf(userLocation.getLatitude()),
                        new PDAPICallback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject) {
                                PDLog.d(PDUIClaimActivity.class, "claim: " + jsonObject.toString());
                                progressBar.setVisibility(View.GONE);
                                shareButton.setEnabled(true);
                                shareButton.animate().alpha(1.0f);

                                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CLAIMED, new PDAbraProperties.Builder()
                                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORKS, readableNetworksChosen())
                                        .add(PDAbraConfig.ABRA_PROPERTYNAME_PHOTO, mImageAdded ? "YES" : "NO")
                                        .add(PDAbraConfig.ABRA_PROPERTYNAME_REWARD_TYPE, PDAbraUtils.keyForRewardType(mReward.getRewardType()))
                                        .create());

                                if (fromInstagram) {
//                                    verifyReward();
                                    finishActivityAfterClaim("instagram");
                                } else if (fromTwitter) {
                                    finishActivityAfterClaim("twitter");
                                } else if (fromFacebook) {
                                    finishActivityAfterClaim("facebook");
                                } else {
//                            new AlertDialog.Builder(PDUIClaimActivity.this)
//                                    .setTitle(R.string.pd_claim_reward_claimed_text)
//                                    .setMessage(mReward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE) ? R.string.pd_claim_sweepstakes_claimed_success_text : R.string.pd_claim_reward_claimed_success_text)
//                                    .setPositiveButton(R.string.pd_go_to_wallet_text, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            Intent data = new Intent();
//                                            data.putExtra("id", mReward.getId());
//                                            data.putExtra("verificationNeeded", mInstagramSwitch.isChecked());
//                                            setResult(RESULT_OK, data);
//                                            finish();
//                                        }
//                                    })
//                                    .create()
//                                    .show();
                                    Intent data = new Intent();
                                    data.putExtra("id", mReward.getId());
//                                    data.putExtra("verificationNeeded", mInstagramSwitch.isChecked());
                                    data.putExtra("verificationNeeded", false);
                                    setResult(RESULT_OK, data);
                                    finish();
                                    claiming = false;

                                }
                            }

                            @Override
                            public void failure(int statusCode, Exception e) {

                                if (fromInstagram) {
                                    finishActivityAfterClaim("instagram");
                                } else if (fromTwitter) {
                                    finishActivityAfterClaim("twitter");
                                }else if (fromFacebook) {
                                    finishActivityAfterClaim("facebook");
                                }
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        shareButton.setEnabled(true);
                                        shareButton.animate().alpha(1.0f);
                                        if(!fromInstagram){
                                            showBasicOKAlertDialog(R.string.pd_common_sorry_text, R.string.pd_common_something_wrong_text);
                                        }
                                    }
                                };
                                mainHandler.post(myRunnable);


                            }
                        });
                realm.close();

            }
        }, postTime);


    }


    private void postToTwitter(final String message, final String imagePath){

        mUserHasLeftForTwitter = true;
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        if(mReward.getGlobalHashtag()!=null && mReward.getGlobalHashtag().length()>0) {
            final Intent intent = new ComposerActivity.Builder(PDUIClaimActivity.this)
                    .session(session)
                    .image(twitterURI)
                    .text("")
                    .hashtags(mReward.getGlobalHashtag())
                    .createIntent();
            startActivity(intent);
        }else{
            final Intent intent = new ComposerActivity.Builder(PDUIClaimActivity.this)
                    .session(session)
                    .image(twitterURI)
                    .text("")
                    .createIntent();
            startActivity(intent);
        }

    }

    private void postToFacebook(final String message, final String imagePath){


        PDUIShareMessageFragment fragment = PDUIShareMessageFragment.newInstance(mReward, "facebook", new PDUIShareMessageFragment.PDInstagramShareCallback() {
            @Override
            public void onShareClick() {
                mUserHasLeftForFaceBook = true;
                ShareDialog dialog = new ShareDialog(PDUIClaimActivity.this);
                dialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        if (mUserHasLeftForFaceBook) {
                            mUserHasLeftForFaceBook = false;
                            PDLog.d(getClass(), "perform claim now, user returned from FB");
                            mFragmentManager.popBackStack(PDUIShareMessageFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            performClaimReward("", getEncodedImage(), false, false, true);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "onCancel: ");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        error.printStackTrace();
                    }
                });
                String hashTag = message;
//                String hashTag = "";
                if(mReward!=null && mReward.getGlobalHashtag()!=null && mReward.getGlobalHashtag().length() > 0 && mReward.getTweetOptions()!=null && mReward.getTweetOptions().getForcedTag()!=null&& mReward.getTweetOptions().getForcedTag().length()>0) {
                    hashTag = mReward.getGlobalHashtag();
                }

                if(image !=null) {
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(image)
                            .build();
                    SharePhotoContent content;
                    if(!hashTag.equalsIgnoreCase("")) {
                        content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .setShareHashtag(new ShareHashtag.Builder()
                                        .setHashtag(hashTag)
                                        .build())
                                .build();
                    }else{
                        content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .setShareHashtag(new ShareHashtag.Builder()
                                        .build())
                                .build();
                    }

                    if(dialog.canShow(content, ShareDialog.Mode.AUTOMATIC)) {
                        dialog.show(content, ShareDialog.Mode.AUTOMATIC);
                    }
                }else{
                    SharePhotoContent content;
                    if(!hashTag.equalsIgnoreCase("")) {
                        content = new SharePhotoContent.Builder()
                                .setShareHashtag(new ShareHashtag.Builder()
                                        .setHashtag(hashTag)
                                        .build())
                                .build();
                    }else{
                        content = new SharePhotoContent.Builder()
                                .build();
                    }


                    if(dialog.canShow(content, ShareDialog.Mode.NATIVE)) {
                        dialog.setShouldFailOnDataError(false);
                        dialog.show(content, ShareDialog.Mode.NATIVE);
                    }
                }
            }

            @Override
            public void onCancel() {
                mFragmentManager.popBackStack(PDUIShareMessageFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mFragmentManager.beginTransaction()
                .replace(R.id.pd_claim_tag_friends_container, fragment, PDUIShareMessageFragment.getName())
                .addToBackStack(PDUIShareMessageFragment.getName())
                .commit();







    }


    private void postToInstagram(final String message, final String imagePath) {

        PDClipboardUtils.copyTextToClipboard(this, message, message);
        PDUIShareMessageFragment fragment = PDUIShareMessageFragment.newInstance(mReward, "instagram", new PDUIShareMessageFragment.PDInstagramShareCallback() {
            @Override
            public void onShareClick() {
                mUserHasLeftForInstagram = true;
                startActivity(PDSocialUtils.createInstagramIntent(PDUIClaimActivity.this, imagePath));
            }

            @Override
            public void onCancel() {
                mFragmentManager.popBackStack(PDUIShareMessageFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mFragmentManager.beginTransaction()
                .replace(R.id.pd_claim_tag_friends_container, fragment, PDUIShareMessageFragment.getName())
                .addToBackStack(PDUIShareMessageFragment.getName())
                .commit();
    }



    private void finishActivityAfterClaim(String type) {
        verifyReward(type);
    }

    private void verifyReward() {

        PDAPIClient.instance().verifyInstagramPostForReward(mReward.getId(), new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                PDLog.d(PDUIClaimActivity.class, "verify success: " + jsonObject.toString());
                boolean success = false;
                if (jsonObject.has("data")) {
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");
                    if (dataObject.has("status")) {
                        success = dataObject.get("status").getAsString().equalsIgnoreCase("success");
                    }
                }
                if (success) {
                    mReward.setInstagramVerified(true);
                }
                mReward.setVerifying(false);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUIWalletFragment.class, "verify failed: code=" + statusCode + ", message=" + e.getMessage());
                mReward.setVerifying(false);
            }
        });
    }

    private void verifyReward(final String type) {
        verifyView.setVisibility(View.VISIBLE);
        AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
        animation1.setDuration(250);
        verifyView.setAlpha(1f);
        verifyView.startAnimation(animation1);

        dotProgress.show();
        final long time = Calendar.getInstance().getTimeInMillis();
        Log.d("VERIFYING", "verifyReward: ");

        PDAPIClient.instance().scanSocialNetwork(mReward.getId(), type, new PDAPICallback<JsonObject>() {
            @Override
            public void success(final JsonObject jsonObject) {

                if(type.equalsIgnoreCase("instagram")) {
                    PDAPIClient.instance().verifyInstagramPostForReward(mReward.getId(), new PDAPICallback<JsonObject>() {
                        @Override
                        public void success(final JsonObject jsonObjectVerify) {
                            long timediff = Calendar.getInstance().getTimeInMillis()-time;
                            if(timediff>3000){
                                processValidationReturn(jsonObject);
                            }else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        processValidationReturn(jsonObject);
                                    }
                                }, timediff);
                            }
                        }

                        @Override
                        public void failure(int statusCode, Exception e) {
                            Log.d("VERIFYING", "verifyReward: failed");
                            claiming = false;
                            PDLog.d(PDUIWalletFragment.class, "verify failed: code=" + statusCode + ", message=" + e.getMessage());
                            mReward.setVerifying(false);

                            Realm realm = Realm.getDefaultInstance();
                            PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
                            realm.close();
                            showInstagramFailure(userDetails, mReward.getGlobalHashtag());
                            dotProgress.hide(150);
                        }
                    });
                    return;
                }
                long timediff = Calendar.getInstance().getTimeInMillis()-time;
                if(timediff>3000){
                    processValidationReturn(jsonObject);
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processValidationReturn(jsonObject);
                        }
                    }, timediff);
                }

            }

            @Override
            public void failure(int statusCode, Exception e) {
                Log.d("VERIFYING", "verifyReward: failed");
                claiming = false;
                PDLog.d(PDUIWalletFragment.class, "verify failed: code=" + statusCode + ", message=" + e.getMessage());
                mReward.setVerifying(false);

                Realm realm = Realm.getDefaultInstance();
                PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
                realm.close();
                showInstagramFailure(userDetails, mReward.getGlobalHashtag());
                dotProgress.hide(150);


            }
        });
    }

    private void processValidationReturn(JsonObject jsonObject){
        parseResponse(jsonObject);
        PDLog.d(PDUIClaimActivity.class, "verify success: " + jsonObject.toString());
        claiming = false;
        boolean success = false;
        if (jsonObject!=null && jsonObject.has("validated")) {
            success = true;
        }
        Realm realm = Realm.getDefaultInstance();
        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();

        if (success) {
            Gson gson = new Gson();
            PDPostScan postScan = gson.fromJson(jsonObject, PDPostScan.class);

            if(postScan!=null && postScan.getValidated()) {


                mReward.setInstagramVerified(true);
                Log.d("VERIFYING", "verifyReward: success");

                String headingText = pdVerifyHeadingText.getText().toString();
                if (userDetails.getFirstName() != null && userDetails.getFirstName().length()>0){
                    headingText = headingText.replace("Richard", userDetails.getFirstName());
                }else{
                    headingText = headingText.replace("Hey Richard, w", "W");
                }

                if(postScan!=null && postScan.getText()!=null && postScan.getText().length()>0) {
                    headingText = headingText.replace("#hashtag", postScan.getText());
                }else{
                    headingText = headingText.replace("with #hashtag", "");
                }
                pdVerifyHeadingText.setText(headingText);

                if(postScan!=null && postScan.getText()!=null && postScan.getText().length()>0) {
                    pdClaimUserHashtagTextView.setText(headingText);
                }else{
                    pdClaimUserHashtagTextView.setText("");
                }

                if(postScan!=null && postScan.getSocialName()!=null && postScan.getSocialName().length()>0) {
                    pdClaimUserNameTextView.setText(postScan.getSocialName());
                }else{
                    pdClaimUserNameTextView.setVisibility(View.GONE);
                }
                if(postScan!=null && postScan.getSocialName()!=null && postScan.getSocialName().length()>0) {
                    pdClaimUserHashtagTextView.setText(postScan.getText());
                }else{
                    pdClaimUserHashtagTextView.setVisibility(View.GONE);
                }
                Glide.with(PDUIClaimActivity.this)
                        .load(postScan.getMediaUrl())
                        .dontAnimate()
                        .into(pdVerifyImage);

                if(postScan!=null && postScan.getProfilePictureUrl()!=null) {

                    String url = postScan.getProfilePictureUrl();
                    if(!url.startsWith("http")){
                        url = "http:"  + url;
                    }
                    Glide.with(PDUIClaimActivity.this)
                            .load(url)
                            .placeholder(R.drawable.pd_ui_default_user)
                            .error(R.drawable.pd_ui_default_user)
                            .dontAnimate()
                            .into(pdClaimProfileImageView);
                }

                pdVerifyHeadingText.setVisibility(View.VISIBLE);
                pdVerifyImageCard.setVisibility(View.VISIBLE);
                pdProceedButton.setVisibility(View.VISIBLE);

                int animationTime = 1000;

                AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
                animation1.setDuration(animationTime);
                pdVerifyHeadingText.setAlpha(1f);
                pdVerifyHeadingText.startAnimation(animation1);

                AlphaAnimation animation2 = new AlphaAnimation(0f, 1.0f);
                animation2.setDuration(animationTime);
                animation2.setStartOffset(animationTime);
                pdVerifyImageCard.setAlpha(1f);
                pdVerifyImageCard.startAnimation(animation2);

                AlphaAnimation animation3 = new AlphaAnimation(0f, 1.0f);
                animation3.setDuration(animationTime);
                animation3.setStartOffset(animationTime*2);
                pdProceedButton.setAlpha(1f);
                pdProceedButton.startAnimation(animation3);
                pdProceedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mTwitterSwitch.isChecked()||mFacebookSwitch.isChecked()){
                            claimReward();
                            return;
                        }

                        Intent data = new Intent();
                        data.putExtra("id", mReward.getId());
                        data.putExtra("verificationNeeded", false);
//                        data.putExtra("verificationNeeded", mInstagramSwitch.isChecked());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });


            }else{
                showInstagramFailure(userDetails, mReward.getGlobalHashtag());
            }

        } else {
            showInstagramFailure(userDetails, mReward.getGlobalHashtag());
        }
        dotProgress.hide(150);
        mReward.setVerifying(false);
        realm.close();
    }

    private void showInstagramFailure(PDRealmUserDetails userDetails, String hashtag) {
        pdVerifyHeadingText.setVisibility(View.VISIBLE);
        pdBackButton.setVisibility(View.VISIBLE);

        AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
        animation1.setDuration(250);
        pdBackButton.setAlpha(1f);
        pdVerifyHeadingText.setAlpha(1f);
        pdBackButton.startAnimation(animation1);
        pdVerifyHeadingText.startAnimation(animation1);

        String headingText = "Whoops! Sorry XXXX, we could not find a post from the last 48 hours with the #hashtag\n\nPlease ensure you've shared from the correct social media account and try again.";
        if (userDetails.getFirstName() != null && userDetails.getFirstName().length() > 0) {
            headingText = headingText.replace("XXXX", userDetails.getFirstName());
        } else {
            headingText = headingText.replace(" XXXX", "");
        }

        if (hashtag != null && hashtag.length() > 0) {
            headingText = headingText.replace("the #hashtag", ""+hashtag);
        }

        pdVerifyHeadingText.setText(headingText);

        pdBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void connectTwitterAccount(TwitterSession session, final boolean addImage) {
        PDAPIClient.instance().connectWithTwitterAccount(String.valueOf(session.getUserId()),
                session.getAuthToken().token, session.getAuthToken().secret, new PDAPICallback<PDUser>() {
                    @Override
                    public void success(PDUser user) {
                        PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CONNECTED_ACCOUNT, new PDAbraProperties.Builder()
                                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORK, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_TWITTER)
                                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_VIEWED_CLAIM)
                                .create());
                        PDUtils.updateSavedUser(user);
                        post(addImage);
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        showBasicOKAlertDialog(R.string.pd_common_sorry_text, R.string.pd_common_something_wrong_text);
                    }
                });
    }

    private void showConnectToFacebookFragment() {
        uncheckSwitchIfChecked(mFacebookSwitch);
        PDUIConnectSocialAccountFragment fragment = PDUIConnectSocialAccountFragment.newInstance(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_FACEBOOK, new PDUIConnectSocialAccountFragment.PDUIConnectSocialAccountCallback() {
            @Override
            public void onAccountConnected(@PDUIConnectSocialAccountFragment.PDConnectSocialAccountType int type) {
                mFacebookSwitch.setChecked(true);
            }
        });
        mFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment, PDUIConnectSocialAccountFragment.getName())
                .addToBackStack(PDUIConnectSocialAccountFragment.getName())
                .commit();
    }

    private void showConnectToInstagramFragment() {
        uncheckSwitchIfChecked(mInstagramSwitch);
        PDUIConnectSocialAccountFragment fragment = PDUIConnectSocialAccountFragment.newInstance(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_INSTAGRAM, new PDUIConnectSocialAccountFragment.PDUIConnectSocialAccountCallback() {
            @Override
            public void onAccountConnected(@PDUIConnectSocialAccountFragment.PDConnectSocialAccountType int type) {
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CONNECTED_ACCOUNT, new PDAbraProperties.Builder()
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORK, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_INSTAGRAM)
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_VIEWED_CLAIM)
                        .create());
                mInstagramSwitch.setChecked(true);
            }
        });
        mFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment, PDUIConnectSocialAccountFragment.getName())
                .addToBackStack(PDUIConnectSocialAccountFragment.getName())
                .commit();
    }

    private void showConnectToTwitterFragment() {
        uncheckSwitchIfChecked(mTwitterSwitch);
        PDUIConnectSocialAccountFragment fragment = PDUIConnectSocialAccountFragment.newInstance(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_TWITTER, new PDUIConnectSocialAccountFragment.PDUIConnectSocialAccountCallback() {
            @Override
            public void onAccountConnected(@PDUIConnectSocialAccountFragment.PDConnectSocialAccountType int type) {
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CONNECTED_ACCOUNT, new PDAbraProperties.Builder()
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORK, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_TWITTER)
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_VIEWED_CLAIM)
                        .create());
                mTwitterSwitch.setChecked(true);
            }
        });
        mFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment, PDUIConnectSocialAccountFragment.getName())
                .addToBackStack(PDUIConnectSocialAccountFragment.getName())
                .commit();
    }

    private File setUpPhotoFile() throws IOException {
        File f = PDUIImageUtils.createImageFile(false);
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File setUpResizedPhotoFile() throws IOException {
        File f = PDUIImageUtils.createImageFile(false);
        mCurrentResizedPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File setUpCroppedPhotoFile() throws IOException {
        File f = PDUIImageUtils.createImageFile(true);
        mCurrentCroppedPhotoPath = f.getAbsolutePath();
        return f;
    }

    private void handleCroppedPhoto() {
        int orientation = mImageFromCamera ? -1 : PDUIImageUtils.getOrientation(mCurrentPhotoPath);
        orientation = -1;
        PDLog.d(PDUIClaimActivity.class, "orientation: " + orientation);
        setPic(mCurrentCroppedPhotoPath, orientation);

        addedPhotoView.setVisibility(View.VISIBLE);
        addPhotoView.setVisibility(View.GONE);
    }

    private void setPic(String path, int orientation) {
        ImageView mImageView = (ImageView) findViewById(R.id.pd_claim_share_image_view);

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        mImageAdded = true;
        image = PDUIImageUtils.getResizedBitmap(path, 500, 500, orientation);
        mImageView.setImageBitmap(image);
        twitterURI = saveBitmapToInternalCache(this, image);
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PDUIClaimActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(PDUIClaimActivity.this)
                            .setTitle(getString(R.string.pd_storage_permissions_title_string))
                            .setMessage(getString(R.string.pd_storage_permission_rationale_string))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(PDUIClaimActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create()
                            .show();
                } else {
                    showAddPictureChoiceDialog();
                }
            }
        });
    }
    public static Uri saveBitmapToInternalCache(Context context, Bitmap bitmap) {
        Uri targetUri = null;
        FileOutputStream fos = null;
        try {
            File imgFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".png", context.getCacheDir());
            //imgFile.createNewFile();
            fos = new FileOutputStream(imgFile);
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            if (compressed) targetUri = Uri.fromFile(imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return targetUri;
    }

    private void startCameraIntentWithImagePath() {

        /*first we need to check of client app has the Camera Permission*/
        if (PDPermissionHelper.hasPermissionInManifest(this, Manifest.permission.CAMERA)) {
            //ask for camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.pd_camera_permissions_title_string))
                        .setMessage(getString(R.string.pd_camera_permissions_rationale_string))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PDUIClaimActivity.this, new String[]{Manifest.permission.CAMERA}, 321);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create()
                        .show();
            } else {
                startCamera();
            }
        } else {
            //we can continue as normal
            startCamera();
        }
    }

    private void startCamera() {
        try {
            File f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            startActivityForResult(takePictureIntent, PDUIImageUtils.PD_TAKE_PHOTO_REQUEST_CODE); /*this line causes a crash if client app has camera permission - need to ask for camera permission of it exists*/

        } catch (IOException e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }
    }

    private void showAddPictureChoiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.pd_claim_add_photo_title_text)
                .setItems(R.array.pd_claim_add_photo_dialog_items_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {       // Gallery
                            Crop.pickImage(PDUIClaimActivity.this, PDUIImageUtils.PD_GALLERY_PHOTO_REQUEST_CODE);
                        } else if (which == 1) {  // Camera
                            startCameraIntentWithImagePath();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    private void showCropView(Uri source) throws IOException {
        Uri croppedImageDestination = Uri.fromFile(setUpCroppedPhotoFile());
        //Crop.of(source, croppedImageDestination).start(this);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.pd_crop_toolbar_colour));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.pd_crop_status_bar_colour));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.pd_crop_active_widget_colour));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.pd_crop_toolbar_widget_colour));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.pd_crop_content_background_colour));

        UCrop.of(source, croppedImageDestination).withOptions(options).start(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int ID = item.getItemId();
        if (ID == android.R.id.home) {
            final int backStackCount = mFragmentManager.getBackStackEntryCount();
            if (backStackCount > 0) {
                String name = mFragmentManager.getBackStackEntryAt(backStackCount - 1).getName();
                mFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    private void uncheckSwitchIfChecked(SwitchCompat switchCompat) {
        if (switchCompat.isChecked()) {
            switchCompat.setChecked(false);
        }
    }

    /**
     * OnBackStackChangedListener to watch the FragmentManager back stack
     */
    private FragmentManager.OnBackStackChangedListener BACK_STACK_CHANGED_LISTENER = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            final int entryCount = mFragmentManager.getBackStackEntryCount();
            if (entryCount == 0) {
                setTitle(R.string.pd_claim_title);
                return;
            }

            FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(entryCount - 1);
            String name = entry.getName();
            if (name.equalsIgnoreCase(PDUITagFriendsFragment.class.getSimpleName())) {
                setTitle(R.string.pd_claim_choose_friends_title);
            } else if (name.equalsIgnoreCase(PDUIInstagramLoginFragment.class.getSimpleName())) {
                setTitle(R.string.pd_claim_connect_instagram_title);
            }
        }
    };


    /*
     * Checked Changed listener for SwitchCompat
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final int id = buttonView.getId();
        if (id == R.id.pd_claim_facebook_switch) {
            PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_TOGGLED_SOCIAL_BUTTON,
                    new PDAbraProperties.Builder()
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORK, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_FACEBOOK)
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_BUTTON_STATE, isChecked ? PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_ON : PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_OFF)
                            .create());

            if (isChecked) {
                uncheckSwitchIfChecked(mTwitterSwitch);
                uncheckSwitchIfChecked(mInstagramSwitch);

                // Check if Facebook Access token is valid and if not show connect fragment
                PDSocialUtils.validateFacebookAccessToken(new PDAPICallback<Boolean>() {
                    @Override
                    public void success(Boolean valid) {
                        PDLog.d(PDUIClaimActivity.class, "validateFacebookAccessToken:success:" + valid);
                        if (!valid) {
                            showConnectToFacebookFragment();
                        }
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        // unused
                    }
                });
            }
        } else if (id == R.id.pd_claim_twitter_switch) {
            PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_TOGGLED_SOCIAL_BUTTON,
                    new PDAbraProperties.Builder()
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORK, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_TWITTER)
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_BUTTON_STATE, isChecked ? PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_ON : PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_OFF)
                            .create());

            if (isChecked) {
                uncheckSwitchIfChecked(mFacebookSwitch);
                uncheckSwitchIfChecked(mInstagramSwitch);
                if(!PDSocialUtils.isTwitterLoggedIn()){
                    showConnectToTwitterFragment();
                }
            }
        } else if (id == R.id.pd_claim_instagram_switch) {
            PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_TOGGLED_SOCIAL_BUTTON,
                    new PDAbraProperties.Builder()
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORK, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_INSTAGRAM)
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_BUTTON_STATE, isChecked ? PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_ON : PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_BUTTON_STATE_OFF)
                            .create());

            if (isChecked) {
                uncheckSwitchIfChecked(mFacebookSwitch);
                uncheckSwitchIfChecked(mTwitterSwitch);

                // Check user is logged in to Instagram and if not show connect Fragment
                PDSocialUtils.isInstagramLoggedIn(new PDAPICallback<Boolean>() {
                    @Override
                    public void success(Boolean valid) {
                        PDLog.d(PDUIClaimActivity.class, "Instagram access token " + (valid ? "valid" : "expired"));
                        if (!valid) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showConnectToInstagramFragment();
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showConnectToInstagramFragment();
                            }
                        });
                    }
                });
            }
        }
    }


    /*
     * On Click listener for buttons
     */
    @Override
    public void onClick(View v) {

        final int ID = v.getId();
        if (ID == R.id.pd_claim_share_button) {
            if (!mIsHere) {
                showBasicOKAlertDialog(R.string.pd_claim_verify_location_failed_title_text, R.string.pd_claim_verify_location_failed_text);
                return;
            }
            if (!mImageAdded && mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
                showBasicOKAlertDialog(R.string.pd_claim_photo_required_text, R.string.pd_claim_photo_required_message_text);
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_RECEIVED_ERROR_ON_CLAIM, new PDAbraProperties.Builder()
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_ERROR, PDAbraConfig.ABRA_PROPERTYNAME_NO_PHOTO)
                        .create());
            } else {
                post(mImageAdded);
            }
        } else if (ID == R.id.pd_share_view_holder) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.pd_storage_permissions_title_string))
                        .setMessage(getString(R.string.pd_storage_permission_rationale_string))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PDUIClaimActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create()
                        .show();
            } else {
                showAddPictureChoiceDialog();
            }
        } else if (ID == R.id.pd_claim_already_shared_view) {

//            if(true){
//                //TODO: remove for release
//                finishActivityAfterClaim();
//                return;
//            }

            Log.i("Claim Activity", mReward.getGlobalHashtag());
            if (!mIsHere) {
                showBasicOKAlertDialog(R.string.pd_claim_verify_location_failed_title_text, R.string.pd_claim_verify_location_failed_text);
                return;
            }

            Intent intent = new Intent(this, PDUISelectNetworkActivity.class);
            intent.putExtra("reward", new Gson().toJson(mReward, PDReward.class));
            startActivity(intent);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PDLog.d(getClass(), "permissions");
                showAddPictureChoiceDialog();
            } else {
                PDLog.d(getClass(), "no permissions");
                Toast.makeText(this, R.string.pd_storage_permissions_denied_string, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 321) /*camera permission*/ {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PDLog.d(getClass(), "permissions");
                startCamera();
            } else {
                PDLog.d(getClass(), "no camera permissions");
                Toast.makeText(this, getString(R.string.pd_camera_permissions_denied_string), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int locationCounter = 0;

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (mIsHere) {
                locationCounter++;
            }
            checkIsHere(location);
            PDUtils.updateSavedUserLocation(location);
        }
        if (locationCounter >= 3) {
            mLocationManager.stop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);



        if (requestCode == PDUIImageUtils.PD_TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
//                PDLog.d(PDUIImageUtils.class, "processImage");
                try {
                    // reduce image size and use resized file path
                    mImageFromCamera = true;
                    setUpResizedPhotoFile();
                    PDUIImageUtils.reduceImageSizeAndSaveToPath(mCurrentPhotoPath, mCurrentResizedPhotoPath, 500, 500);
                    showCropView(Uri.fromFile(new File(mCurrentResizedPhotoPath)));

                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_ADDED_CLAIM_CONTENT, new PDAbraProperties.Builder()
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_PHOTO, "Yes")
                            .add("Source", "Camera")
                            .create());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PDUIImageUtils.PD_GALLERY_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    mImageFromCamera = false;
                    showCropView(data.getData());
                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_ADDED_CLAIM_CONTENT, new PDAbraProperties.Builder()
                            .add(PDAbraConfig.ABRA_PROPERTYNAME_PHOTO, "Yes")
                            .add("Source", "Photo Library")
                            .create());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Error picking image
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
//            PDLog.d(PDUIImageUtils.class, "handle cropped image");
            handleCroppedPhoto();
        } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            TwitterLoginButton loginButton = new TwitterLoginButton(this);
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if(!claiming) {
            if (verifyView.getVisibility() == View.VISIBLE) {
                AlphaAnimation animation = new AlphaAnimation(1f, 0.0f);
                animation.setDuration(250);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        verifyView.setVisibility(View.GONE);
                        pdVerifyHeadingText.setVisibility(View.GONE);
                        pdVerifyImageCard.setVisibility(View.GONE);
                        pdProceedButton.setVisibility(View.GONE);
                        pdBackButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                verifyView.startAnimation(animation);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
