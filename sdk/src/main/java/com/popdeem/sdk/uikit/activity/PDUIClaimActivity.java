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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.core.utils.PDSocialUtils;
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
public class PDUIClaimActivity extends PDBaseActivity implements View.OnClickListener {

    private PDReward mReward;

    private EditText mMessageEditText;
    private Button mFacebookButton;
    private Button mTwitterButton;

    private boolean mFacebookOptionEnabled = false;
    private boolean mTwitterOptionEnabled = false;

    private boolean mImageAdded = false;
    private String mCurrentPhotoPath;
    private String mCurrentCroppedPhotoPath;
    private ArrayList<String> mTaggedNames = new ArrayList<>();
    private ArrayList<String> mTaggedIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_claim);
        setTitle(R.string.pd_claim_string);

        mReward = new Gson().fromJson(getIntent().getStringExtra("reward"), PDReward.class);

        addRewardDetailsToUI();

        mMessageEditText = (EditText) findViewById(R.id.pd_claim_share_edit_text);
        mFacebookButton = (Button) findViewById(R.id.pd_facebook_share_option_button);
        mTwitterButton = (Button) findViewById(R.id.pd_twitter_share_option_button);

        if (noShareMediaForced()) {
            mFacebookOptionEnabled = false;
            mTwitterOptionEnabled = false;
        } else {
            mFacebookOptionEnabled = facebookShareForced();
            mTwitterOptionEnabled = twitterShareForced();
        }

        addClickListenersToViews();
        updateFacebookButton();
        updateTwitterButton();
    }

    private void addClickListenersToViews() {
        mFacebookButton.setOnClickListener(this);
        mTwitterButton.setOnClickListener(this);
        findViewById(R.id.pd_claim_add_image_button).setOnClickListener(this);
        findViewById(R.id.pd_claim_share_button).setOnClickListener(this);
        findViewById(R.id.pd_claim_tag_friends_button).setOnClickListener(this);
    }

    private void addRewardDetailsToUI() {
        // Logo
        final PDUIBezelImageView logoImageView = (PDUIBezelImageView) findViewById(R.id.pd_reward_star_imgae_view);
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
        final TextView rewardDescTextView = (TextView) findViewById(R.id.pd_reward_offer_text_view);
        rewardDescTextView.setText(mReward.getDescription());

        // Time Remaining
        final TextView timeRemainingTextView = (TextView) findViewById(R.id.pd_reward_time_remaining_text_view);
        long timeInMillis = PDNumberUtils.toLong(mReward.getAvailableUntilInSeconds(), -1);
        timeRemainingTextView.setText(timeInMillis == -1 ? "" : PDUIUtils.timeUntil(timeInMillis, true, false));

        // Action
        final TextView actionTextView = (TextView) findViewById(R.id.pd_reward_request_text_view);
        String action = mReward.getAction();
        if (action.equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
            actionTextView.setText(String.format(Locale.getDefault(), "%1s Required", twitterShareForced() ? "Tweet with Photo" : "Photo"));
        } else if (action.equalsIgnoreCase(PDReward.PD_REWARD_ACTION_CHECKIN)) {
            actionTextView.setText(String.format(Locale.getDefault(), "%1s Required", twitterShareForced() ? "Tweet" : "Check-in"));
        } else {
            actionTextView.setText(R.string.pd_instant_coupon_label);
        }
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

        // Check if at least one network is selected
        if (!mFacebookOptionEnabled && !mTwitterOptionEnabled) {
            showBasicOKAlertDialog(R.string.pd_claim_no_network_selected_string, R.string.pd_claim_no_network_selected_message_string);
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

        if (mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_NONE)) {
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
                        Log.d(PDUIClaimActivity.class.getSimpleName(), "image_upload->encodedImage: " + encodedImage);
                    }
                }
            }
        }

        if (mReward.getTweetOptions() != null && mReward.getTweetOptions().isForceTag() && mReward.getTweetOptions().getForcedTag() != null) {
            message = String.format("%1s %2s", message, mReward.getTweetOptions().getForcedTag());
        }
        Log.d(PDUIClaimActivity.class.getSimpleName(), "message: " + message);

        performClaimReward(message, encodedImage);
    }

    private void performClaimReward(String message, String encodedImage) {
        String twitterToken = null;
        String twitterSecret = null;
        if (mTwitterOptionEnabled && PDSocialUtils.isTwitterLoggedIn() && Twitter.getSessionManager().getActiveSession().getAuthToken().token != null
                && Twitter.getSessionManager().getActiveSession().getAuthToken().secret != null) {
            twitterToken = Twitter.getSessionManager().getActiveSession().getAuthToken().token;
            twitterSecret = Twitter.getSessionManager().getActiveSession().getAuthToken().secret;
        }

        PDRealmUserLocation userLocation = Realm.getDefaultInstance().where(PDRealmUserLocation.class).findFirst();

        PDAPIClient.instance().claimReward(this, mFacebookOptionEnabled ? AccessToken.getCurrentAccessToken().getToken() : null,
                twitterToken, twitterSecret, mReward.getId(), message, mTaggedNames, mTaggedIds, encodedImage,
                String.valueOf(userLocation.getLongitude()), String.valueOf(userLocation.getLatitude()),
                new PDAPICallback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject) {
                        new AlertDialog.Builder(PDUIClaimActivity.this)
                                .setTitle(R.string.pd_claim_reward_claimed_string)
                                .setMessage(R.string.pd_claim_reward_in_wallet_string)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .create()
                                .show();
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        showBasicOKAlertDialog(R.string.pd_error_title_text, R.string.pd_claim_something_went_wrong_string);
                    }
                });
    }

    private void connectTwitterAccount(TwitterSession session, final boolean addImage) {
        PDAPIClient.instance().connectWithTwitterAccount(String.valueOf(session.getUserId()),
                session.getAuthToken().token, session.getAuthToken().secret, new PDAPICallback<PDUser>() {
                    @Override
                    public void success(PDUser user) {
                        PDRealmUserDetails userDetails = new PDRealmUserDetails(user);
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

    private File setUpCroppedPhotoFile() throws IOException {
        File f = PDUIImageUtils.createImageFile(true);
        mCurrentCroppedPhotoPath = f.getAbsolutePath();
        return f;
    }

    private void handleCroppedPhoto() {
        int orientation = PDUIImageUtils.getOrientation(mCurrentPhotoPath);
        Log.d(PDUIClaimActivity.class.getSimpleName(), "orientation: " + orientation);
        setPic(mCurrentCroppedPhotoPath, orientation);
    }

    private void setPic(String path, int orientation) {
        ImageView mImageView = (ImageView) findViewById(R.id.pd_claim_share_image_view);

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        mImageAdded = true;
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

    @Override
    protected void onDestroy() {
        PDUIImageUtils.deletePhotoFile(mCurrentPhotoPath);
        PDUIImageUtils.deletePhotoFile(mCurrentCroppedPhotoPath);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int ID = item.getItemId();
        if (ID == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(PDUITagFriendsFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

        if (requestCode == PDUIImageUtils.PD_TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                Log.d(PDUIImageUtils.class.getSimpleName(), "processImage");
                try {
                    Uri croppedImageDestination = Uri.fromFile(setUpCroppedPhotoFile());
                    Crop.of(Uri.fromFile(new File(mCurrentPhotoPath)), croppedImageDestination).asSquare().start(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Log.d(PDUIImageUtils.class.getSimpleName(), "handle cropped image");
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
            updateTwitterButton();
        } else if (ID == R.id.pd_claim_share_button) {
            if (!mImageAdded && mReward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_PHOTO)) {
                showBasicOKAlertDialog(R.string.pd_claim_photo_required_string, R.string.pd_claim_photo_required_message_string);
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
                startCameraIntentWithImagePath();
            }

        } else if (ID == R.id.pd_claim_tag_friends_button) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.pd_claim_tag_friends_container, PDUITagFriendsFragment.newInstance())
                    .addToBackStack(PDUITagFriendsFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Claim", "permissions");
                startCameraIntentWithImagePath();
            } else {
                Log.d("Claim", "no permissions");
                Toast.makeText(this, R.string.pd_storage_permissions_denied_string, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
