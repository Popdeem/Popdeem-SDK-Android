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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.fragment.PDUITagFriendsFragment;
import com.popdeem.sdk.uikit.utils.PDUIImageUtils;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
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
public class PDUIClaimActivity extends PDBaseActivity implements View.OnClickListener, LocationListener {

    private FragmentManager mFragmentManager;

    private PDReward mReward;

    private EditText mMessageEditText;
    private Button mFacebookButton;
    private Button mTwitterButton;
    private View mNotHereView;

    private boolean mIsHere = false;
    private boolean mFacebookOptionEnabled = false;
    private boolean mTwitterOptionEnabled = false;

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
        setTitle(R.string.pd_claim_string);

        mLocationManager = new PDLocationManager(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(BACK_STACK_CHANGED_LISTENER);

        mCallbackManager = CallbackManager.Factory.create();

        mReward = new Gson().fromJson(getIntent().getStringExtra("reward"), PDReward.class);
        addRewardDetailsToUI();

        mMessageEditText = (EditText) findViewById(R.id.pd_claim_share_edit_text);
        mFacebookButton = (Button) findViewById(R.id.pd_facebook_share_option_button);
        mTwitterButton = (Button) findViewById(R.id.pd_twitter_share_option_button);
        mNotHereView = findViewById(R.id.pd_claim_not_here_container);

        if (noShareMediaForced()) {
            mFacebookOptionEnabled = true;
            mTwitterOptionEnabled = false;
        } else {
            mFacebookOptionEnabled = facebookShareForced();
            mTwitterOptionEnabled = twitterShareForced();
        }

        mMessageEditText.addTextChangedListener(MESSAGE_TEXT_WATCHER);

        performTwitterEnabledActions();
        addClickListenersToViews();
        updateFacebookButton();
        updateTwitterButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationManager.startLocationUpdates(this);
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

    private void checkIsHere(final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsHere = PDLocationValidator.validateLocationForReward(mReward, location);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mNotHereView.setVisibility(mIsHere ? View.GONE : View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private void performTwitterEnabledActions() {
        TextView twitterCharactersTextView = (TextView) findViewById(R.id.pd_claim_twitter_characters_text_view);
        TextView hashTagTextView = (TextView) findViewById(R.id.pd_claim_twitter_hashtag_text_view);

        if (!mTwitterOptionEnabled) {
            twitterCharactersTextView.setVisibility(View.INVISIBLE);
            hashTagTextView.setVisibility(View.INVISIBLE);
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
        mFacebookButton.setOnClickListener(this);
        mTwitterButton.setOnClickListener(this);
        findViewById(R.id.pd_claim_add_image_button).setOnClickListener(this);
        findViewById(R.id.pd_claim_share_button).setOnClickListener(this);
        findViewById(R.id.pd_claim_tag_friends_button).setOnClickListener(this);
    }

    private void updateTwitterCharsLeft() {
        int charsLeft = calculateTwitterCharsLeft();
        TextView textView = (TextView) findViewById(R.id.pd_claim_twitter_characters_text_view);
        textView.setTextColor(ContextCompat.getColor(PDUIClaimActivity.this, charsLeft < 1 ? R.color.pd_claim_over_character_limit_color : R.color.pd_twitter_blue));
        textView.setText(String.valueOf(charsLeft));
    }

    private final TextWatcher MESSAGE_TEXT_WATCHER = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mTwitterOptionEnabled) {
                updateTwitterCharsLeft();
            }
        }
    };

    private FragmentManager.OnBackStackChangedListener BACK_STACK_CHANGED_LISTENER = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            final int entryCount = mFragmentManager.getBackStackEntryCount();
            if (entryCount == 0) {
                setTitle(R.string.pd_claim_string);
                return;
            }

            FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(entryCount - 1);
            String name = entry.getName();
            if (name.equalsIgnoreCase(PDUITagFriendsFragment.class.getSimpleName())) {
                setTitle(R.string.pd_tag_friends_choose_friends_string);
            }
        }
    };

    private void addRewardDetailsToUI() {
        // Logo
        final PDUIBezelImageView logoImageView = (PDUIBezelImageView) findViewById(R.id.pd_reward_star_image_view);
        if (mReward.getCoverImage().contains("default")) {
            Picasso.with(this)
                    .load(R.drawable.pd_ui_star_icon)
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(logoImageView);
        } else {
            Picasso.with(this)
                    .load(mReward.getCoverImage())
                    .error(R.drawable.pd_ui_star_icon)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .into(logoImageView);
        }

        // Reward Description
        TextView textView = (TextView) findViewById(R.id.pd_reward_offer_text_view);
        textView.setText(mReward.getDescription());

        // Rules
        textView = (TextView) findViewById(R.id.pd_reward_item_rules_text_view);
        textView.setText(mReward.getRules());

        StringBuilder actionStringBuilder = new StringBuilder("");

        // Action
        final boolean TWITTER_ACTION_REQUIRED = twitterShareForced();
        if (mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
            actionStringBuilder.append(String.format(Locale.getDefault(), "%1s Required", TWITTER_ACTION_REQUIRED ? "Tweet with Photo" : "Photo"));
        } else if (mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_CHECKIN)) {
            actionStringBuilder.append(String.format(Locale.getDefault(), "%1s Required", TWITTER_ACTION_REQUIRED ? "Tweet" : "Check-in"));
        } else {
            actionStringBuilder.append(getString(R.string.pd_instant_coupon_label));
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

    private void updateFacebookButton() {
        mFacebookButton.setTextColor(ContextCompat.getColor(this, mFacebookOptionEnabled ? R.color.pd_facebook_blue : R.color.pd_divider_color));
        mFacebookButton.setCompoundDrawablesWithIntrinsicBounds(mFacebookOptionEnabled ? R.drawable.pd_fb_button_selected : R.drawable.pd_fb_button_deselected, 0, 0, 0);
    }

    private void updateTwitterButton() {
        mTwitterButton.setTextColor(ContextCompat.getColor(this, mTwitterOptionEnabled ? R.color.pd_twitter_blue : R.color.pd_divider_color));
        mTwitterButton.setCompoundDrawablesWithIntrinsicBounds(mTwitterOptionEnabled ? R.drawable.pd_twitter_button_selected : R.drawable.pd_twitter_button_deselected, 0, 0, 0);
    }

    private boolean facebookShareForced() {
        List<String> mediaTypes = Arrays.asList(mReward.getSocialMediaTypes());
        return mediaTypes.size() == 1 && mediaTypes.contains(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK);
    }

    private boolean twitterShareForced() {
        List<String> mediaTypes = Arrays.asList(mReward.getSocialMediaTypes());
        return mediaTypes.size() == 1 && mediaTypes.contains(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER);
    }

    private boolean noShareMediaForced() {
        List<String> mediaTypes = Arrays.asList(mReward.getSocialMediaTypes());
        return mediaTypes.contains(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK) && mediaTypes.contains(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER);
    }

    private void post(final boolean addImage) {
        PDUIUtils.hideKeyboard(this, mMessageEditText);

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

        if (calculateTwitterCharsLeft() < 0 && mTwitterOptionEnabled) {
            showBasicOKAlertDialog(R.string.pd_error_title_text, R.string.pd_claim_twitter_character_limit_reached_string);
            return;
        }

        // Check if at least one network is selected
        if (!mFacebookOptionEnabled && !mTwitterOptionEnabled) {
            showBasicOKAlertDialog(R.string.pd_claim_no_network_selected_string, R.string.pd_claim_no_network_selected_message_string);
            return;
        }

        // Check if use has given Facebook Publish permission
        if (mFacebookOptionEnabled && !PDSocialUtils.hasAllFacebookPublishPermissions()) {
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
                            .setTitle(R.string.pd_error_title_text)
                            .setMessage(error.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                }
            });
            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(PDSocialUtils.FACEBOOK_PUBLISH_PERMISSIONS));
            return;
        }

        // Check if Twitter share is enabled and Twitter is logged in
        if (mTwitterOptionEnabled && !PDSocialUtils.isTwitterLoggedIn()) {
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

        String message = "";
        String encodedImage = null;

        if (!mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_NONE)) {
            message = mMessageEditText.getText().toString();
            if (addImage) {
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
            }
        }

        if (mReward.getTweetOptions() != null && mReward.getTweetOptions().isForceTag() && mReward.getTweetOptions().getForcedTag() != null) {
            message = String.format("%1s %2s", message, mReward.getTweetOptions().getForcedTag());
        }
        PDLog.d(PDUIClaimActivity.class, "message: " + message);

        performClaimReward(message, encodedImage);
    }

    private void performClaimReward(String message, String encodedImage) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pd_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        final Button shareButton = (Button) findViewById(R.id.pd_claim_share_button);
        shareButton.setEnabled(false);
        shareButton.animate().alpha(0.5f);

        String twitterToken = null;
        String twitterSecret = null;
        if (mTwitterOptionEnabled && PDSocialUtils.isTwitterLoggedIn() && Twitter.getSessionManager().getActiveSession().getAuthToken().token != null
                && Twitter.getSessionManager().getActiveSession().getAuthToken().secret != null) {
            twitterToken = Twitter.getSessionManager().getActiveSession().getAuthToken().token;
            twitterSecret = Twitter.getSessionManager().getActiveSession().getAuthToken().secret;
        }

        Realm realm = Realm.getDefaultInstance();
        PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();

        PDAPIClient.instance().claimReward(this, mFacebookOptionEnabled ? AccessToken.getCurrentAccessToken().getToken() : null,
                twitterToken, twitterSecret, mReward.getId(), message, mTaggedNames, mTaggedIds, encodedImage,
                String.valueOf(userLocation.getLongitude()), String.valueOf(userLocation.getLatitude()),
                new PDAPICallback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject) {
                        progressBar.setVisibility(View.GONE);
                        shareButton.setEnabled(true);
                        shareButton.animate().alpha(1.0f);

                        new AlertDialog.Builder(PDUIClaimActivity.this)
                                .setTitle(R.string.pd_claim_reward_claimed_string)
                                .setMessage(R.string.pd_claim_reward_in_wallet_string)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent data = new Intent();
                                        data.putExtra("id", mReward.getId());
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
                        showBasicOKAlertDialog(R.string.pd_error_title_text, R.string.pd_claim_something_went_wrong_string);
                    }
                });

        realm.close();
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
                        showBasicOKAlertDialog(R.string.pd_error_title_text, R.string.pd_claim_something_went_wrong_string);
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
                .setTitle(R.string.pd_add_photo_title_string)
                .setItems(R.array.pd_photo_dialog_items_array, new DialogInterface.OnClickListener() {
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


    private void updateSavedUserLocation(Location location) {
        PDRealmUserLocation userLocation = new PDRealmUserLocation(location.getLatitude(), location.getLongitude());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userLocation);
        realm.commitTransaction();
        realm.close();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int ID = item.getItemId();
        if (ID == android.R.id.home) {
            if (mFragmentManager.getBackStackEntryCount() > 0) {
                mFragmentManager.popBackStack(PDUITagFriendsFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                finish();
            }
            return true;
        }
        return false;
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

    @Override
    public void onClick(View v) {
        final int ID = v.getId();
        if (ID == R.id.pd_facebook_share_option_button) {
            if (mFacebookOptionEnabled && facebookShareForced()) {
                showBasicOKAlertDialog(R.string.pd_claim_cannot_deselect_string, R.string.pd_claim_facebook_forced_message_string);
                return;
            }
            mFacebookOptionEnabled = !mFacebookOptionEnabled;
            updateFacebookButton();
        } else if (ID == R.id.pd_twitter_share_option_button) {
            if (mTwitterOptionEnabled && twitterShareForced()) {
                showBasicOKAlertDialog(R.string.pd_claim_cannot_deselect_string, R.string.pd_claim_twitter_forced_message_string);
                return;
            }
            mTwitterOptionEnabled = !mTwitterOptionEnabled;
            performTwitterEnabledActions();
            updateTwitterButton();
        } else if (ID == R.id.pd_claim_share_button) {
            PDUIUtils.hideKeyboard(this, mMessageEditText);
            if (!mIsHere) {
                showBasicOKAlertDialog(R.string.pd_not_here_title_text, R.string.pd_not_here_message_text);
                return;
            }
            if (!mImageAdded && mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
                showBasicOKAlertDialog(R.string.pd_claim_photo_required_string, R.string.pd_claim_photo_required_message_string);
            } else {
                post(mImageAdded);
            }
        } else if (ID == R.id.pd_claim_add_image_button) {
            PDUIUtils.hideKeyboard(this, mMessageEditText);
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
            PDUIUtils.hideKeyboard(this, mMessageEditText);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
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
                startCameraIntentWithImagePath();
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
            updateSavedUserLocation(location);
        }

        if (locationCounter >= 3) {
            mLocationManager.stop();
        }
    }
}
