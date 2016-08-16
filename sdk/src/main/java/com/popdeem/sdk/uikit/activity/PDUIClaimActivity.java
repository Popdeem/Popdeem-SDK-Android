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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.core.location.PDLocationValidator;
import com.popdeem.sdk.core.model.PDInstagramResponse;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDClipboardUtils;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.fragment.PDUIInstagramLoginFragment;
import com.popdeem.sdk.uikit.fragment.PDUIInstagramShareFragment;
import com.popdeem.sdk.uikit.fragment.PDUITagFriendsFragment;
import com.popdeem.sdk.uikit.fragment.dialog.PDUIConnectSocialAccountDialogFragment;
import com.popdeem.sdk.uikit.utils.PDUIColorUtils;
import com.popdeem.sdk.uikit.utils.PDUIImageUtils;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDUIClaimActivity extends PDBaseActivity implements View.OnClickListener, LocationListener, CompoundButton.OnCheckedChangeListener {

    private FragmentManager mFragmentManager;

    private PDReward mReward;

    private EditText mMessageEditText;
    private SwitchCompat mFacebookSwitch;
    private SwitchCompat mTwitterSwitch;
    private SwitchCompat mInstagramSwitch;
    private View mNotHereView;

    private boolean mIsHere = false;
    private boolean mHashTagValidated = false;
    private boolean mUserHasLeftForInstagram = false;

    private boolean mImageAdded = false;
    private String mCurrentPhotoPath;
    private String mCurrentResizedPhotoPath;
    private String mCurrentCroppedPhotoPath;

    private ArrayList<String> mTaggedNames = new ArrayList<>();
    private ArrayList<String> mTaggedIds = new ArrayList<>();

    private CallbackManager mCallbackManager;
    private PDLocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_claim);
        setTitle(R.string.pd_claim_title);

        mLocationManager = new PDLocationManager(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(BACK_STACK_CHANGED_LISTENER);

        mCallbackManager = CallbackManager.Factory.create();

        mReward = new Gson().fromJson(getIntent().getStringExtra("reward"), PDReward.class);
        addRewardDetailsToUI();

        mMessageEditText = (EditText) findViewById(R.id.pd_claim_share_edit_text);
        mFacebookSwitch = (SwitchCompat) findViewById(R.id.pd_claim_facebook_switch);
        mTwitterSwitch = (SwitchCompat) findViewById(R.id.pd_claim_twitter_switch);
        mInstagramSwitch = (SwitchCompat) findViewById(R.id.pd_claim_instagram_switch);
        mNotHereView = findViewById(R.id.pd_claim_not_here_container);

        ImageView notHereTickImageView = (ImageView) findViewById(R.id.pd_claim_not_here_tick_image_view);
        notHereTickImageView.setImageDrawable(PDUIColorUtils.getLocationVerificationTickIcon(this));

        mMessageEditText.addTextChangedListener(MESSAGE_TEXT_WATCHER);

        addClickListenersToViews();
        updateEnabledStateOfViews();
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
            mFragmentManager.popBackStack(PDUIInstagramShareFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            performClaimReward(getMessage(), getEncodedImage());
        }
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

    private void toggleInstagramViews() {
        TextView twitterCharactersTextView = (TextView) findViewById(R.id.pd_claim_twitter_characters_text_view);
        TextView hashTagTextView = (TextView) findViewById(R.id.pd_claim_twitter_hashtag_text_view);
        twitterCharactersTextView.setVisibility(View.INVISIBLE);
        hashTagTextView.setVisibility(View.INVISIBLE);

        if (!mInstagramSwitch.isChecked()) {
            removeHashTagSpans(mMessageEditText.getText());
            return;
        }

        if (mReward.getInstagramOptions() != null) {
            if (mReward.getInstagramOptions().getForcedTag() != null && !mReward.getInstagramOptions().getForcedTag().isEmpty()) {
                hashTagTextView.setText(mReward.getInstagramOptions().getForcedTag());
                hashTagTextView.setVisibility(View.VISIBLE);
            }
        }
        validateHashTag();
    }

    private void toggleTwitterViews() {
        TextView twitterCharactersTextView = (TextView) findViewById(R.id.pd_claim_twitter_characters_text_view);
        TextView hashTagTextView = (TextView) findViewById(R.id.pd_claim_twitter_hashtag_text_view);
        twitterCharactersTextView.setVisibility(View.INVISIBLE);
        hashTagTextView.setVisibility(View.INVISIBLE);

        if (!mTwitterSwitch.isChecked()) {
            removeHashTagSpans(mMessageEditText.getText());
            return;
        }

        twitterCharactersTextView.setVisibility(View.VISIBLE);
        twitterCharactersTextView.setText(String.valueOf(calculateTwitterCharsLeft()));

        if (mReward.getTweetOptions() != null) {
            if (mReward.getTweetOptions().isPrefill() && mReward.getTweetOptions().getPrefilledMessage() != null) {
                mMessageEditText.setText(mReward.getTweetOptions().getPrefilledMessage());
                PDLog.d(PDUIClaimActivity.class, mReward.getTweetOptions().getPrefilledMessage());
            }
            if (mReward.getTweetOptions().isForceTag()) {
                hashTagTextView.setText(mReward.getTweetOptions().getForcedTag());
                hashTagTextView.setVisibility(View.VISIBLE);
            }
            mMessageEditText.setSelection(mMessageEditText.getText().length());
        }
        validateHashTag();
    }

    private void validateHashTag() {
        if (!mTwitterSwitch.isChecked() && !mInstagramSwitch.isChecked()) {
            removeHashTagSpans(mMessageEditText.getText());
            mHashTagValidated = true;
            return;
        }

        String hashTagLowerCase = null;
        if (mTwitterSwitch.isChecked()) {
            hashTagLowerCase = mReward.getTweetOptions().getForcedTag().toLowerCase(Locale.getDefault());
        } else if (mInstagramSwitch.isChecked()) {
            hashTagLowerCase = mReward.getInstagramOptions().getForcedTag().toLowerCase(Locale.getDefault());
        }

        if (hashTagLowerCase == null || hashTagLowerCase.isEmpty()) {
            mHashTagValidated = true;
            return;
        }

        final String currentMessageLowerCase = mMessageEditText.getText().toString().toLowerCase(Locale.getDefault());
        if (currentMessageLowerCase.isEmpty()) {
            mHashTagValidated = false;
            return;
        }

        mHashTagValidated = currentMessageLowerCase.contains(hashTagLowerCase);
        Spannable messageSpannable = mMessageEditText.getText();
        if (mHashTagValidated) {
            final int startIndex = currentMessageLowerCase.indexOf(hashTagLowerCase);
            final int hashTagLength = hashTagLowerCase.length();
            messageSpannable.setSpan(new BackgroundColorSpan(ContextCompat.getColor(this, R.color.pd_toolbar_color)), startIndex, startIndex + hashTagLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            messageSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.pd_toolbar_text_color)), startIndex, startIndex + hashTagLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!mHashTagValidated) {
            removeHashTagSpans(messageSpannable);
        }
    }

    private void removeHashTagSpans(Spannable messageSpannable) {
        ForegroundColorSpan[] fgSpans = messageSpannable.getSpans(0, messageSpannable.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan s : fgSpans) {
            messageSpannable.removeSpan(s);
        }
        BackgroundColorSpan[] bgSpans = messageSpannable.getSpans(0, messageSpannable.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan s : bgSpans) {
            messageSpannable.removeSpan(s);
        }
    }

    private int calculateTwitterCharsLeft() {
        int totalChars = mMessageEditText.getText().length();
        if (mImageAdded) {
            totalChars += getTwitterMediaCharacters();
        }
        if (mReward.getTweetOptions() != null) {
            totalChars += mReward.getTweetOptions().getIncludeDownloadLink().length();
            if (mReward.getTweetOptions().isForceTag()) {
                totalChars += mReward.getTweetOptions().getForcedTag().length();
            }
        }
        return PDSocialUtils.TWITTER_CHARACTER_LIMIT - totalChars;
    }

    private int getTwitterMediaCharacters() {
        return PDNumberUtils.toInt(mReward.getTwitterMediaCharacters(), PDSocialUtils.TWITTER_DEFAULT_MEDIA_CHARACTERS_COUNT);
    }

    private void addClickListenersToViews() {
        mFacebookSwitch.setOnCheckedChangeListener(this);
        mTwitterSwitch.setOnCheckedChangeListener(this);
        mInstagramSwitch.setOnCheckedChangeListener(this);
        findViewById(R.id.pd_claim_add_image_button).setOnClickListener(this);
        findViewById(R.id.pd_claim_share_button).setOnClickListener(this);
        findViewById(R.id.pd_claim_tag_friends_button).setOnClickListener(this);
    }

    private void updateEnabledStateOfViews() {
        mFacebookSwitch.setEnabled(mIsHere && isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK));
        mTwitterSwitch.setEnabled(mIsHere && isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER));
        mInstagramSwitch.setEnabled(mIsHere && isNetworkAvailableForShare(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM));
        findViewById(R.id.pd_claim_add_image_button).setEnabled(mIsHere);
        findViewById(R.id.pd_claim_share_button).setEnabled(mIsHere);
        findViewById(R.id.pd_claim_tag_friends_button).setEnabled(mIsHere);
        mMessageEditText.setEnabled(mIsHere);
    }

    private void updateTwitterCharsLeft() {
        int charsLeft = calculateTwitterCharsLeft();
        TextView textView = (TextView) findViewById(R.id.pd_claim_twitter_characters_text_view);
        textView.setTextColor(ContextCompat.getColor(PDUIClaimActivity.this, charsLeft < 1 ? R.color.pd_claim_over_character_limit_text_color : R.color.pd_twitter_blue));
        textView.setText(String.valueOf(charsLeft));
    }

    private void addRewardDetailsToUI() {
        // Logo
        final ImageView logoImageView = (ImageView) findViewById(R.id.pd_reward_star_image_view);
        final String imageUrl = mReward.getCoverImage();
        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.contains("default")) {
            Picasso.with(this)
                    .load(R.drawable.pd_ui_star_icon)
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(logoImageView);
        } else {
            Picasso.with(this)
                    .load(imageUrl)
                    .error(R.drawable.pd_ui_star_icon)
                    .resizeDimen(R.dimen.pd_reward_item_image_dimen, R.dimen.pd_reward_item_image_dimen)
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
            actionStringBuilder.append(getString(TWITTER_ACTION_REQUIRED ? R.string.pd_claim_action_tweet_photo : R.string.pd_claim_action_photo));
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
    }

    private boolean twitterShareForced() {
        List<String> mediaTypes = Arrays.asList(mReward.getSocialMediaTypes());
        return mediaTypes.size() == 1 && mediaTypes.contains(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER);
    }

    private boolean isNetworkAvailableForShare(@PDReward.PDSocialMediaType String network) {
        List<String> mediaTypes = Arrays.asList(mReward.getSocialMediaTypes());
        return mediaTypes.contains(network);
    }

    private String getEncodedImage() {
        String encodedImage = null;
        File imageFile = new File(mCurrentCroppedPhotoPath);
        if (imageFile.exists()) {
            Bitmap b = PDUIImageUtils.getResizedBitmap(imageFile.getAbsolutePath(), 500, 500, PDUIImageUtils.getOrientation(mCurrentPhotoPath));
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

    private String getMessage() {
        return mMessageEditText.getText().toString();
//        if (mTwitterSwitch.isChecked() && mReward.getTweetOptions() != null && mReward.getTweetOptions().isForceTag() && mReward.getTweetOptions().getForcedTag() != null) {
//            message = String.format("%1s %2s", message, mReward.getTweetOptions().getForcedTag());
//        } else if (mInstagramSwitch.isChecked() && mReward.getInstagramOptions() != null && mReward.getInstagramOptions().getForcedTag() != null && !mReward.getInstagramOptions().getForcedTag().isEmpty()) {
//            message = String.format("%1s %2s", message, mReward.getInstagramOptions().getForcedTag());
//        }
//        return message;
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
                showBasicOKAlertDialog(R.string.pd_common_sorry_text, "Instagram App is not installed.");
                return;
            }
            // Check if hashtag is present in message
            if (!mHashTagValidated) {
                String errorMessage = getString(R.string.pd_claim_required_hashtag_not_present_message_text, mReward.getInstagramOptions().getForcedTag(), getString(R.string.pd_connect_instagram_title));
                showBasicOKAlertDialog(R.string.pd_common_oops_text, errorMessage);
                return;
            }
        }

        // If posting to Twitter
        if (mTwitterSwitch.isChecked()) {
            // Check if over character limit
            if (calculateTwitterCharsLeft() < 0) {
                showBasicOKAlertDialog(R.string.pd_common_sorry_text, R.string.pd_claim_tweet_too_long_text);
                return;
            }

            // Check if hashtag is present in message
            if (!mHashTagValidated && mReward.getTweetOptions().isForceTag()) {
                String errorMessage = getString(R.string.pd_claim_required_hashtag_not_present_message_text, mReward.getTweetOptions().getForcedTag(), getString(R.string.pd_connect_twitter_title));
                showBasicOKAlertDialog(R.string.pd_common_oops_text, errorMessage);
                return;
            }

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
            // Check if user has given Facebook Publish permission
            if (!PDSocialUtils.hasAllFacebookPublishPermissions()) {
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        post(addImage);
                    }

                    @Override
                    public void onCancel() {
                        PDLog.d(PDUIClaimActivity.class, "Facebook Login onCancel:");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        PDLog.d(PDUIClaimActivity.class, "Facebook Login onError(): " + error.getMessage());
                        new AlertDialog.Builder(PDUIClaimActivity.this)
                                .setTitle(R.string.pd_common_sorry_text)
                                .setMessage(error.getMessage())
                                .setPositiveButton(android.R.string.ok, null)
                                .create()
                                .show();
                    }
                });
                LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(PDSocialUtils.FACEBOOK_PUBLISH_PERMISSIONS));
                return;
            }
        }

        String message = getMessage();
        PDLog.d(PDUIClaimActivity.class, "message: " + message);

        String encodedImage = null;
        if (addImage) {
            encodedImage = getEncodedImage();
        }

        if (mInstagramSwitch.isChecked()) {
            postToInstagram(message, mCurrentCroppedPhotoPath);
        } else {
            performClaimReward(message, encodedImage);
        }
    }

    private void performClaimReward(String message, String encodedImage) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pd_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        final Button shareButton = (Button) findViewById(R.id.pd_claim_share_button);
        shareButton.setEnabled(false);
        shareButton.animate().alpha(0.5f);

        String twitterToken = null;
        String twitterSecret = null;
        if (mTwitterSwitch.isChecked() && PDSocialUtils.isTwitterLoggedIn() && Twitter.getSessionManager().getActiveSession().getAuthToken().token != null
                && Twitter.getSessionManager().getActiveSession().getAuthToken().secret != null) {
            twitterToken = Twitter.getSessionManager().getActiveSession().getAuthToken().token;
            twitterSecret = Twitter.getSessionManager().getActiveSession().getAuthToken().secret;
        }

        Realm realm = Realm.getDefaultInstance();
        PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();

        PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        String instagramAccessToken = null;
        if (mInstagramSwitch.isChecked() && userDetails.getUserInstagram() != null && userDetails.getUserInstagram().getAccessToken() != null && !userDetails.getUserInstagram().getAccessToken().isEmpty()) {
            instagramAccessToken = userDetails.getUserInstagram().getAccessToken();
        }

        PDAPIClient.instance().claimReward(this, mFacebookSwitch.isChecked() ? AccessToken.getCurrentAccessToken().getToken() : null,
                twitterToken, twitterSecret, instagramAccessToken, mReward.getId(), message, mTaggedNames, mTaggedIds, encodedImage,
                String.valueOf(userLocation.getLongitude()), String.valueOf(userLocation.getLatitude()),
                new PDAPICallback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject) {
                        PDLog.d(PDUIClaimActivity.class, "claim: " + jsonObject.toString());
                        progressBar.setVisibility(View.GONE);
                        shareButton.setEnabled(true);
                        shareButton.animate().alpha(1.0f);

                        new AlertDialog.Builder(PDUIClaimActivity.this)
                                .setTitle(R.string.pd_claim_reward_claimed_text)
                                .setMessage(R.string.pd_claim_reward_claimed_success_text)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent data = new Intent();
                                        data.putExtra("id", mReward.getId());
                                        data.putExtra("verificationNeeded", mInstagramSwitch.isChecked());
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                })
                                .create()
                                .show();
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        progressBar.setVisibility(View.GONE);
                        shareButton.setEnabled(true);
                        shareButton.animate().alpha(1.0f);
                        showBasicOKAlertDialog(R.string.pd_common_sorry_text, R.string.pd_common_something_wrong_text);
                    }
                });

        realm.close();
    }

    private void postToInstagram(final String message, final String imagePath) {
        PDClipboardUtils.copyTextToClipboard(this, message, message);
        PDUIInstagramShareFragment fragment = PDUIInstagramShareFragment.newInstance(new PDUIInstagramShareFragment.PDInstagramShareCallback() {
            @Override
            public void onShareClick() {
                mUserHasLeftForInstagram = true;
                startActivity(PDSocialUtils.createInstagramIntent(imagePath));
            }

            @Override
            public void onCancel() {
                mFragmentManager.popBackStack(PDUIInstagramShareFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mFragmentManager.beginTransaction()
                .replace(R.id.pd_claim_tag_friends_container, fragment, PDUIInstagramShareFragment.getName())
                .addToBackStack(PDUIInstagramShareFragment.getName())
                .commit();
    }

    private void connectTwitterAccount(TwitterSession session, final boolean addImage) {
        PDAPIClient.instance().connectWithTwitterAccount(String.valueOf(session.getUserId()),
                session.getAuthToken().token, session.getAuthToken().secret, new PDAPICallback<PDUser>() {
                    @Override
                    public void success(PDUser user) {
                        PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
                        userDetails.setUid(0);

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(userDetails);
                        realm.commitTransaction();
                        realm.close();

                        post(addImage);
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        showBasicOKAlertDialog(R.string.pd_common_sorry_text, R.string.pd_common_something_wrong_text);
                    }
                });
    }

    private void showConnectToInstagramFragment() {
        PDUIConnectSocialAccountDialogFragment.showDialog(mFragmentManager, PDUIConnectSocialAccountDialogFragment.PD_CONNECT_INSTAGRAM_DIALOG, new PDUIConnectSocialAccountDialogFragment.ConnectSocialAccountCallback() {
            @Override
            public void connectClick() {
                PDUIInstagramLoginFragment fragment = PDUIInstagramLoginFragment.newInstance(new PDUIInstagramLoginFragment.PDInstagramLoginCallback() {
                    @Override
                    public void loggedIn(PDInstagramResponse response) {
                        mFragmentManager.popBackStack(PDUIInstagramLoginFragment.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        connectInstagramAccount(response);
                    }

                    @Override
                    public void error(String message) {
                        uncheckSwitchIfChecked(mInstagramSwitch);
                        showBasicOKAlertDialog(R.string.pd_common_sorry_text, message);
                    }
                });

                mFragmentManager.beginTransaction()
                        .replace(R.id.pd_claim_tag_friends_container, fragment, PDUIInstagramLoginFragment.getName())
                        .addToBackStack(PDUIInstagramLoginFragment.getName())
                        .commit();
            }

            @Override
            public void dialogDismissed() {
                uncheckSwitchIfChecked(mInstagramSwitch);
            }
        });
    }

    private void connectInstagramAccount(PDInstagramResponse instagramResponse) {
        PDAPIClient.instance().connectWithInstagramAccount(instagramResponse.getUser().getId(), instagramResponse.getAccessToken(), instagramResponse.getUser().getUsername(), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
                userDetails.setUid(0);

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(userDetails);
                realm.commitTransaction();
                realm.close();
            }

            @Override
            public void failure(int statusCode, Exception e) {
                showBasicOKAlertDialog(R.string.pd_common_sorry_text, R.string.pd_common_something_wrong_text);
            }
        });
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
        int orientation = PDUIImageUtils.getOrientation(mCurrentPhotoPath);
        PDLog.d(PDUIClaimActivity.class, "orientation: " + orientation);
        setPic(mCurrentCroppedPhotoPath, orientation);
    }

    private void setPic(String path, int orientation) {
        ImageView mImageView = (ImageView) findViewById(R.id.pd_claim_share_image_view);

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        mImageAdded = true;
        updateTwitterCharsLeft();
        mImageView.setImageBitmap(PDUIImageUtils.getResizedBitmap(path, targetH, targetW, orientation));
        mImageView.setVisibility(View.VISIBLE);
    }

    private void startCameraIntentWithImagePath() {
        try {
            File f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(takePictureIntent, PDUIImageUtils.PD_TAKE_PHOTO_REQUEST_CODE);
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
        Crop.of(source, croppedImageDestination).start(this);
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
     * TextWatcher for message EditText
     */
    private final TextWatcher MESSAGE_TEXT_WATCHER = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mTwitterSwitch.isChecked()) {
                updateTwitterCharsLeft();
                validateHashTag();
            } else if (mInstagramSwitch.isChecked()) {
                validateHashTag();
            }
        }
    };

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
            if (isChecked) {
                uncheckSwitchIfChecked(mTwitterSwitch);
                uncheckSwitchIfChecked(mInstagramSwitch);
            }
        } else if (id == R.id.pd_claim_twitter_switch) {
            if (isChecked) {
                uncheckSwitchIfChecked(mFacebookSwitch);
                uncheckSwitchIfChecked(mInstagramSwitch);
            }
            toggleTwitterViews();
        } else if (id == R.id.pd_claim_instagram_switch) {
            if (isChecked) {
                uncheckSwitchIfChecked(mFacebookSwitch);
                uncheckSwitchIfChecked(mTwitterSwitch);

                // Check user is logged in to Instagram and if not show connect Fragment
                PDSocialUtils.isInstagramLoggedIn(new PDAPICallback<Boolean>() {
                    @Override
                    public void success(Boolean success) {
                        PDLog.d(PDUIClaimActivity.class, "Instagram access token " + (success ? "valid" : "expired"));
                        if (!success) {
                            showConnectToInstagramFragment();
                        }
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        showConnectToInstagramFragment();
                    }
                });
            }
            toggleInstagramViews();
        }
    }


    /*
     * On Click listener for buttons
     */
    @Override
    public void onClick(View v) {
        PDUIUtils.hideKeyboard(this, mMessageEditText);

        final int ID = v.getId();
        if (ID == R.id.pd_claim_share_button) {
            if (!mIsHere) {
                showBasicOKAlertDialog(R.string.pd_claim_verify_location_failed_title_text, R.string.pd_claim_verify_location_failed_text);
                return;
            }
            if (!mImageAdded && mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
                showBasicOKAlertDialog(R.string.pd_claim_photo_required_text, R.string.pd_claim_photo_required_message_text);
            } else {
                post(mImageAdded);
            }
        } else if (ID == R.id.pd_claim_add_image_button) {
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
        } else if (ID == R.id.pd_claim_tag_friends_button) {
            mFragmentManager.beginTransaction()
                    .add(R.id.pd_claim_tag_friends_container, PDUITagFriendsFragment.newInstance(new PDUITagFriendsFragment.TagFriendsConfirmedCallback() {
                        @Override
                        public void taggedFriendsUpdated(@NonNull ArrayList<String> taggedNames, @NonNull ArrayList<String> taggedIds) {
                            mTaggedNames = taggedNames;
                            mTaggedIds = taggedIds;

                            boolean anyCheckBoxChecked = mTaggedIds.size() > 0;
                            TextView textView = (TextView) findViewById(R.id.pd_claim_tagged_friends_count_text_view);
                            textView.setVisibility(anyCheckBoxChecked ? View.VISIBLE : View.INVISIBLE);
                            if (anyCheckBoxChecked) {
                                if (mTaggedNames.size() == 1) {
                                    textView.setText(String.format("With %1s", mTaggedNames.get(0)));
                                } else {
                                    textView.setText(String.format("With %1s and %2s", mTaggedNames.get(0), ((mTaggedNames.size() > 2) ? ((mTaggedNames.size() - 1) + " others") : "one other")));
                                }
                            } else {
                                textView.setText("");
                            }
                        }
                    }, mTaggedNames, mTaggedIds))
                    .addToBackStack(PDUITagFriendsFragment.class.getSimpleName())
                    .commit();
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
                PDLog.d(PDUIImageUtils.class, "processImage");
                try {
                    // reduce image size and use resized file path
                    setUpResizedPhotoFile();
                    PDUIImageUtils.reduceImageSizeAndSaveToPath(mCurrentPhotoPath, mCurrentResizedPhotoPath, 500, 500);

                    showCropView(Uri.fromFile(new File(mCurrentResizedPhotoPath)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PDUIImageUtils.PD_GALLERY_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    showCropView(data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Error picking image
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            PDLog.d(PDUIImageUtils.class, "handle cropped image");
            handleCroppedPhoto();
        } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            TwitterLoginButton loginButton = new TwitterLoginButton(this);
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }
}
