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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.uikit.activity.PDUIInboxActivity;
import com.popdeem.sdk.uikit.activity.PDUISettingsActivity;
import com.popdeem.sdk.uikit.adapter.PDUIHomeFlowPagerAdapter;
import com.popdeem.sdk.uikit.utils.PDUIColorUtils;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PDUIHomeFlowFragment extends Fragment {

    public static PDUIHomeFlowFragment newInstance() {
        return new PDUIHomeFlowFragment();
    }

    private boolean mMoveToWallet = false;
    private String mAutoVerifyRewardId = null;
    private TabLayout mTabLayout;
    private PDUIHomeFlowPagerAdapter mAdapter;

    public PDUIHomeFlowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_home_flow, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.pd_home_inbox_fab);
        fab.setImageDrawable(PDUIColorUtils.getInboxButtonIcon(getActivity()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PDUIInboxActivity.class));
            }
        });

        ImageButton settingsButton = (ImageButton) view.findViewById(R.id.pd_home_flow_settings_image_button);
        settingsButton.setImageDrawable(PDUIColorUtils.getSettingsIcon(getActivity()));
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PDUISettingsActivity.class));
            }
        });

        mAdapter = new PDUIHomeFlowPagerAdapter(getChildFragmentManager(), getActivity());
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pd_home_view_pager);
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

        mTabLayout = (TabLayout) view.findViewById(R.id.pd_home_tab_layout);
        mTabLayout.setupWithViewPager(viewPager);
        return view;
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
    public void onResume() {
        super.onResume();
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
