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

package com.popdeem.sdk.uikit.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.popdeem.sdk.R;
import com.popdeem.sdk.uikit.fragment.PDUIFeedFragment;
import com.popdeem.sdk.uikit.fragment.PDUIRewardsFragment;
import com.popdeem.sdk.uikit.fragment.PDUIWalletFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIHomeFlowPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TITLES;
    private final int TAB_COUNT = 3;

    private ArrayList<Fragment> mFragments = new ArrayList<>(Arrays.asList(new Fragment[TAB_COUNT]));

    public PDUIHomeFlowPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        TITLES = context.getResources().getStringArray(R.array.home_flow_tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (mFragments.get(0) == null) {
                    mFragments.add(0, PDUIRewardsFragment.newInstance());
                }
                return mFragments.get(0);
            case 1:
                if (mFragments.get(1) == null) {
                    mFragments.add(1, PDUIFeedFragment.newInstance());
                }
                return mFragments.get(1);
            case 2:
                if (mFragments.get(2) == null) {
                    mFragments.add(2, PDUIWalletFragment.newInstance());
                }
                return mFragments.get(2);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    public Fragment getFragmentAtPosition(int position) {
        if (position > mFragments.size() - 1) {
            return null;
        }
        return mFragments.get(position);
    }

}
