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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.realm.PDRealmCustomer;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.uikit.adapter.PDUIHomeFlowPagerAdapter;
import com.popdeem.sdk.uikit.widget.BadgedTabLayout;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */
public class PDUIHomeFlowFragment extends Fragment {

    public static final String PD_LOGGED_IN_RECEIVER_FILTER = "com.popdeem.sdk.LOGGED_IN";
    private ViewPager viewPager;

    public static PDUIHomeFlowFragment newInstance() {
        return new PDUIHomeFlowFragment();
    }

    private boolean mMoveToWallet = false;
    private String mAutoVerifyRewardId = null;
    private BadgedTabLayout mTabLayout;
    private PDUIHomeFlowPagerAdapter mAdapter;

    public PDUIHomeFlowFragment() {
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Picasso.Builder builder = new Picasso.Builder(getActivity());
//        builder.downloader(new OkHttp3Downloader(getActivity(),Integer.MAX_VALUE));
//        Picasso built = builder.build();
//        built.setIndicatorsEnabled(true);
//        built.setLoggingEnabled(true);
//        try {
//            Picasso.setSingletonInstance(built);
//        }catch (IllegalStateException e){
//
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_home_flow, container, false);

//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.pd_home_inbox_fab);
//        fab.setImageDrawable(PDUIColorUtils.getInboxButtonIcon(getActivity()));
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), PDUIInboxActivity.class));
//            }
//        });
//        fab.setVisibility(View.GONE);
//
//        ImageButton settingsButton = (ImageButton) view.findViewById(R.id.pd_home_flow_settings_image_button);
//        settingsButton.setImageDrawable(PDUIColorUtils.getSettingsIcon(getActivity()));
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), PDUISettingsActivity.class));
//            }
//        });
//
//        settingsButton.setVisibility(View.GONE);

        mAdapter = new PDUIHomeFlowPagerAdapter(getChildFragmentManager(), getActivity());
        viewPager = (ViewPager) view.findViewById(R.id.pd_home_view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                logTabPageView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTabLayout = (BadgedTabLayout) view.findViewById(R.id.pd_home_tab_layout);
        mTabLayout.setupWithViewPager(viewPager);
        PDAPIClient.instance().getCustomer(new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                Log.i("JsonObject", "success: ");
                if(jsonObject.has("customer")){
                    JsonObject customer = jsonObject.getAsJsonObject("customer");
                    PDRealmCustomer realmCustomer = PDRealmCustomer.fromJson(customer);

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    RealmResults<PDRealmCustomer> results = realm.where(PDRealmCustomer.class).findAll();
                    results.deleteAllFromRealm();
                    realm.copyToRealm(realmCustomer);
                    realm.commitTransaction();
                    realm.close();
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                e.printStackTrace();
            }
        });

        setProfileBadge(0);
        return view;
    }

    public void setProfileBadge(int messages){
        if(mTabLayout!=null) {
            if(messages>0) {
                mTabLayout.setBadgeText(2, "" + messages);
                mTabLayout.setBadgeTextColors(BadgedTabLayout.createColorStateList(R.color.badge_text_color, R.color.badge_text_color));
            }else{
                mTabLayout.setBadgeText(2, null);
            }
        }
    }




    private void logTabPageView(int position) {
        switch (position) {
            case 0: // Rewards
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_REWARDS_HOME)
                        .create());
                break;
            case 1: // Activity
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_ACTIVITY_FEED)
                        .create());
                break;
            case 2: // Wallet
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_WALLET)
                        .create());
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mLoggedInBroadcastReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mLoggedInBroadcastReceiver, new IntentFilter(PD_LOGGED_IN_RECEIVER_FILTER));

        PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_REWARDS_HOME)
                .create());

        if (mMoveToWallet) {
            mMoveToWallet = false;
            if (switchToWallet()) {
                if (mAutoVerifyRewardId != null) {
                    PDUIWalletFragment walletFragment = (PDUIWalletFragment) mAdapter.getFragmentAtPosition(mTabLayout.getTabCount() - 1);
                    if (walletFragment != null) {
                        walletFragment.autoVerifyReward(mAutoVerifyRewardId);
                        mAutoVerifyRewardId = null;
                    }
                }
            }
        }

    }

    private final BroadcastReceiver mLoggedInBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PDLog.i(PDUIRewardsFragment.class, "LoggedIn broadcast onReceive");
            if(viewPager!=null){
                viewPager.setCurrentItem(2,false);
            }
        }
    };

    public void switchToWalletForVerify(boolean verificationNeeded, String rewardId) {
        mMoveToWallet = true;
        mAutoVerifyRewardId = verificationNeeded ? rewardId : null;

    }

    public boolean switchToWallet() {
        TabLayout.Tab walletTab = mTabLayout.getTabAt(mTabLayout.getTabCount() - 1);
        if (walletTab != null) {
            walletTab.select();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}
