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

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserInstagram;
import com.popdeem.sdk.core.realm.PDRealmUserTwitter;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.adapter.PDUISettingsRecyclerViewAdapter;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class PDUISettingsActivity extends PDBaseActivity implements PDUISettingsRecyclerViewAdapter.PDUISettingsSwitchCallback {

    private ArrayList<PDSettingsSocialNetwork> mItems = new ArrayList<>();
    private PDUISettingsRecyclerViewAdapter mAdapter;
    private PDRealmUserDetails mUser;
    private Realm mRealm;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_settings);

        mFragmentManager = getSupportFragmentManager();
        mRealm = Realm.getDefaultInstance();
        mUser = mRealm.where(PDRealmUserDetails.class).findFirst();
        mUser.addChangeListener(new RealmChangeListener<PDRealmUserDetails>() {
            @Override
            public void onChange(PDRealmUserDetails userDetails) {
                mUser = userDetails;
            }
        });

        if (mUser != null) {
            TextView textView = (TextView) findViewById(R.id.pd_settings_user_name_text_view);
            textView.setText(String.format(Locale.getDefault(), "%1s %2s", mUser.getFirstName(), mUser.getLastName()));

            final PDUIBezelImageView imageView = (PDUIBezelImageView) findViewById(R.id.pd_settings_user_image_view);
            if (mUser.getUserFacebook() != null && mUser.getUserFacebook().getProfilePictureUrl() != null && !mUser.getUserFacebook().getProfilePictureUrl().isEmpty()) {
                Picasso.with(this)
                        .load(mUser.getUserFacebook().getProfilePictureUrl())
                        .centerCrop()
                        .placeholder(R.drawable.pd_ui_default_user)
                        .error(R.drawable.pd_ui_default_user)
                        .resizeDimen(R.dimen.pd_settings_image_dimen, R.dimen.pd_settings_image_dimen)
                        .into(imageView);
            } else {
                Picasso.with(this)
                        .load(R.drawable.pd_ui_default_user)
                        .centerCrop()
                        .resizeDimen(R.dimen.pd_settings_image_dimen, R.dimen.pd_settings_image_dimen)
                        .into(imageView);
            }
        }

        mAdapter = new PDUISettingsRecyclerViewAdapter(this, mItems);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.pd_settings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        refreshList();
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

    //********************************************************************
    //      Disconnect Social Account Methods
    //********************************************************************

    private void disconnectFacebook() {
        LoginManager.getInstance().logOut();
    }

    private void disconnectTwitter(PDRealmUserTwitter twitterParams, final int position) {
        PDAPIClient.instance().disconnectTwitterAccount(twitterParams.getAccessToken(), twitterParams.getAccessSecret(), twitterParams.getTwitterId(), new PDAPICallback<PDUser>() {
            @Override
            public void success(PDUser user) {
                PDUtils.updateSavedUser(user);
                Toast.makeText(PDUISettingsActivity.this, "Twitter disconnected.", Toast.LENGTH_SHORT).show();
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
        mItems.add(new PDSettingsSocialNetwork(PDReward.PD_SOCIAL_MEDIA_TYPE_TWITTER, PDSocialUtils.userHasTwitterCredentials(), R.drawable.pd_twitter_icon_small));
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
            // TODO connect social account
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

        public PDSettingsSocialNetwork() {
        }

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
