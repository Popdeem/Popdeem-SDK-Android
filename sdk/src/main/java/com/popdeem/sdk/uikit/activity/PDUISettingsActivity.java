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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserInstagram;
import com.popdeem.sdk.core.realm.PDRealmUserTwitter;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.adapter.PDUISettingsRecyclerViewAdapter;
import com.popdeem.sdk.uikit.fragment.PDUIConnectSocialAccountFragment;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class PDUISettingsActivity extends PDBaseActivity implements PDUISettingsRecyclerViewAdapter.PDUISettingsSwitchCallback {

    private static  String TAG = PDUISettingsActivity.class.getSimpleName();
    private ArrayList<PDSettingsSocialNetwork> mItems = new ArrayList<>();
    private PDUISettingsRecyclerViewAdapter mAdapter;
    private PDRealmUserDetails mUser;
    private Realm mRealm;
    private Button mBtnLogout;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_settings);

        mFragmentManager = getSupportFragmentManager();
        mRealm = Realm.getDefaultInstance();

        updateUserFromRealm();
        displayUserDetails();

        mAdapter = new PDUISettingsRecyclerViewAdapter(this, mItems);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.pd_settings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        refreshList();

        /*Log Out*/
        //Dont show the logout button if user is not logged in
        mBtnLogout = (Button) findViewById(R.id.pd_button_log_out);
        if (mUser == null || mUser.getUserToken() == null) {
            mBtnLogout.setVisibility(View.INVISIBLE);
        } else {
            mBtnLogout.setVisibility(View.VISIBLE);
        }
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: LogOut");
                new AlertDialog.Builder(PDUISettingsActivity.this)
                        .setTitle(R.string.pd_common_logout_text)
                        .setMessage(R.string.pd_common_logout_message_text)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PopdeemSDK.logout(PDUISettingsActivity.this);
                                PDUISettingsActivity.this.finish();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //AbraLogEvent(ABRA_EVENT_PAGE_VIEWED, @{ABRA_PROPERTYNAME_SOURCE_PAGE : ABRA_PROPERTYVALUE_PAGE_SETTINGS});
        PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_VIEWED_SETTINGS)
                .create());
    }

    private void updateUserFromRealm() {
        mUser = mRealm.where(PDRealmUserDetails.class).findFirst();
        //Reshow the logout button if necessary
        if (mUser != null && mUser.getUserToken() != null) {
            if (mBtnLogout != null) {
                mBtnLogout.setVisibility(View.VISIBLE);
            }
        }
    }



    private void displayUserDetails() {
        if (mUser == null) {
            displayDefaultUserImage();
            return;
        }

        TextView textView = (TextView) findViewById(R.id.pd_settings_user_name_text_view);
        if (mUser.getFirstName() != null && mUser.getLastName() != null) {
            textView.setText(String.format(Locale.getDefault(), "%1s %2s", mUser.getFirstName(), mUser.getLastName()));
        }else if (mUser.getFirstName() !=null){
            textView.setText(mUser.getFirstName());
        }else if (mUser.getLastName() !=null){
            textView.setText(mUser.getFirstName());
        }
        String profileUrl = "";
        if (mUser.getUserFacebook() != null && mUser.getUserFacebook().getProfilePictureUrl() != null && !mUser.getUserFacebook().getProfilePictureUrl().isEmpty()) {
            profileUrl = mUser.getUserFacebook().getProfilePictureUrl();
        } else if (mUser.getUserTwitter() != null && mUser.getUserTwitter().getProfilePictureUrl() != null && !mUser.getUserTwitter().getProfilePictureUrl().isEmpty()) {
            profileUrl = mUser.getUserTwitter().getProfilePictureUrl();
        } else if (mUser.getUserInstagram() != null && mUser.getUserInstagram().getProfilePictureUrl() != null && !mUser.getUserInstagram().getProfilePictureUrl().isEmpty()){
            profileUrl = mUser.getUserInstagram().getProfilePictureUrl();
        }
        if (profileUrl.length() > 0) {
            final PDUIBezelImageView imageView = (PDUIBezelImageView) findViewById(R.id.pd_settings_user_image_view);
            Picasso.with(this)
                    .load(profileUrl)
                    .centerCrop()
                    .resizeDimen(R.dimen.pd_settings_image_dimen, R.dimen.pd_settings_image_dimen)
                    .into(imageView);
        } else {
            displayDefaultUserImage();
        }
    }

    private void displayDefaultUserImage() {
        final PDUIBezelImageView imageView = (PDUIBezelImageView) findViewById(R.id.pd_settings_user_image_view);
//        Picasso.with(this)
//                .load(R.drawable.pd_ui_default_user)
//                .centerCrop()
//                .resizeDimen(R.dimen.pd_settings_image_dimen, R.dimen.pd_settings_image_dimen)
//                .into(imageView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRealm.removeAllChangeListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!popBackStackIfNeeded()) {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abraLog(String eventName, String network) {
        PDAbraLogEvent.log(eventName, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOCIAL_NETWORK, network)
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_SOURCE_PAGE_SETTINGS)
                .create());
    }

    //********************************************************************
    //      Disconnect Social Account Methods
    //********************************************************************

    private void disconnectFacebook() {
        PDAPIClient.instance().disconnectFacebookAccount(AccessToken.getCurrentAccessToken().getToken(), AccessToken.getCurrentAccessToken().getUserId(), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser pdUser) {
                PDUtils.updateSavedUser(pdUser);
                LoginManager.getInstance().logOut();
                Toast.makeText(PDUISettingsActivity.this, "Facebook disconnected.", Toast.LENGTH_SHORT).show();
                abraLog(PDAbraConfig.ABRA_EVENT_LOGOUT, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_FACEBOOK);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUISettingsActivity.class, "disconnectFacebook:error: " + statusCode + " msg: " + e.getLocalizedMessage());
            }
        });
    }

    private void disconnectTwitter(PDRealmUserTwitter twitterParams, final int position) {
        PDAPIClient.instance().disconnectTwitterAccount(twitterParams.getAccessToken(), twitterParams.getAccessSecret(), twitterParams.getTwitterId(), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDUtils.updateSavedUser(user);
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
//                Twitter.logOut();
                Toast.makeText(PDUISettingsActivity.this, "Twitter disconnected.", Toast.LENGTH_SHORT).show();
                abraLog(PDAbraConfig.ABRA_EVENT_DISCONNECT_SOCIAL_ACCOUNT, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_TWITTER);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUISettingsActivity.class, "disconnectTwitter:error: " + statusCode + " msg: " + e.getLocalizedMessage());
                mItems.get(position).setValidated(true);
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    private void disconnectInstagram(PDRealmUserInstagram instagramParams, final int position) {
        PDAPIClient.instance().disconnectInstagramAccount(instagramParams.getAccessToken(), instagramParams.getInstagramId(), instagramParams.getScreenName(), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDUtils.updateSavedUser(user);
                Toast.makeText(PDUISettingsActivity.this, "Instagram disconnected.", Toast.LENGTH_SHORT).show();
                abraLog(PDAbraConfig.ABRA_EVENT_DISCONNECT_SOCIAL_ACCOUNT, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_INSTAGRAM);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUISettingsActivity.class, "disconnectInstagram:error: " + statusCode + " msg: " + e.getLocalizedMessage());
                mItems.get(position).setValidated(true);
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    private void refreshList() {
        mItems.clear();
        mItems.add(new PDSettingsSocialNetwork(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK, PDSocialUtils.isLoggedInToFacebook(), R.drawable.pd_facebook_icon_small));
        boolean addTwitter = PDSocialUtils.usesTwitter(this);
        if(addTwitter) {
            mItems.add(new PDSettingsSocialNetwork(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER, PDSocialUtils.userHasTwitterCredentials(), R.drawable.pd_twitter_icon_small));
        }
        mItems.add(new PDSettingsSocialNetwork(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM, false, R.drawable.pd_instagram_icon_small));
        mAdapter.notifyDataSetChanged();

        PDSocialUtils.isInstagramLoggedIn(new PDAPICallback<Boolean>() {
            @Override
            public void success(final Boolean loggedIn) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mItems.get(mItems.size() - 1).setValidated(loggedIn);
                        mAdapter.notifyItemChanged(mItems.size() - 1);
                    }
                });
            }

            @Override
            public void failure(int statusCode, Exception e) {
            }
        });
    }

    private boolean popBackStackIfNeeded() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            String name = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
            mFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }
        return false;
    }

    private void showConnectAccountDialog(@PDUIConnectSocialAccountFragment.PDConnectSocialAccountType int type, final int position) {
        PDUIConnectSocialAccountFragment fragment = PDUIConnectSocialAccountFragment.newInstance(type, true, new PDUIConnectSocialAccountFragment.PDUIConnectSocialAccountCallback() {
            @Override
            public void onAccountConnected(@PDUIConnectSocialAccountFragment.PDConnectSocialAccountType int type) {
                updateUserFromRealm();
                displayUserDetails();
                mItems.get(position).setValidated(true);
                mAdapter.notifyItemChanged(position);

                switch (type) {
                    case PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_FACEBOOK:
                        abraLog(PDAbraConfig.ABRA_EVENT_CONNECTED_ACCOUNT, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_FACEBOOK);
                        break;
                    case PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_TWITTER:
                        abraLog(PDAbraConfig.ABRA_EVENT_CONNECTED_ACCOUNT, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_TWITTER);
                        break;
                    case PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_INSTAGRAM:
                        abraLog(PDAbraConfig.ABRA_EVENT_CONNECTED_ACCOUNT, PDAbraConfig.ABRA_PROPERTYVALUE_SOCIAL_NETWORK_INSTAGRAM);
                        break;
                }
            }
        });
        mFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment, PDUIConnectSocialAccountFragment.getName())
                .addToBackStack(PDUIConnectSocialAccountFragment.getName())
                .commit();
    }

    @Override
    public void onSwitchCheckedChange(int position, boolean isChecked) {
        PDLog.d(PDUISettingsActivity.class, "onSwitchCheckedChange:(" + position + ") " + isChecked);
        PDSettingsSocialNetwork network = mItems.get(position);
        if (!isChecked) { // Disconnect
            if (network.getName().equalsIgnoreCase(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK)) {
                disconnectFacebook();
            } else if (network.getName().equalsIgnoreCase(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER)) {
                disconnectTwitter(mUser.getUserTwitter(), position);
            } else if (network.getName().equalsIgnoreCase(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM)) {
                disconnectInstagram(mUser.getUserInstagram(), position);
            }
        } else { // Connect
            network.setValidated(false);
            mAdapter.notifyItemChanged(position);
            if (network.getName().equalsIgnoreCase(PDReward.PD_SOCIAL_MEDIA_TYPE_FACEBOOK)) {
                showConnectAccountDialog(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_FACEBOOK, position);
            } else if (network.getName().equalsIgnoreCase(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER)) {
                showConnectAccountDialog(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_TWITTER, position);
            } else if (network.getName().equalsIgnoreCase(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM)) {
                showConnectAccountDialog(PDUIConnectSocialAccountFragment.PD_CONNECT_TYPE_INSTAGRAM, position);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            Fragment fragment = mFragmentManager.findFragmentByTag(PDUIConnectSocialAccountFragment.getName());
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * Class used to fill recycler view.
     */
    public class PDSettingsSocialNetwork {
        private String name;
        private boolean validated;
        @DrawableRes
        private int drawableRes;

        public PDSettingsSocialNetwork(String name, boolean validated, int drawableRes) {
            this.name = name;
            this.validated = validated;
            this.drawableRes = drawableRes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isValidated() {
            return validated;
        }

        public void setValidated(boolean validated) {
            this.validated = validated;
        }

        public int getDrawableRes() {
            return drawableRes;
        }

        public void setDrawableRes(int drawableRes) {
            this.drawableRes = drawableRes;
        }
    }
}
