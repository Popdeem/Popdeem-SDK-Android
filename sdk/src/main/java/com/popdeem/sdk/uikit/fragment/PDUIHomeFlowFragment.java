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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.popdeem.sdk.R;
import com.popdeem.sdk.uikit.adapter.PDUIHomeFlowPagerAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class PDUIHomeFlowFragment extends Fragment {

    public PDUIHomeFlowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_pd_home_flow, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        activity.setSupportActionBar(toolbar);

//        ActionBar actionBar = activity.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pd_home_view_pager);
        viewPager.setAdapter(new PDUIHomeFlowPagerAdapter(activity.getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.pd_home_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_home_flow, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        final int ID = item.getItemId();
//        if (ID == android.R.id.home) {
//            finish();
//            return true;
//        } else if (ID == R.id.action_inbox) {
//            // TODO Push Inbox
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
