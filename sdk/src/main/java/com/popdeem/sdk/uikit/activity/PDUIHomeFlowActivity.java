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

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.popdeem.sdk.R;
import com.popdeem.sdk.uikit.fragment.PDUIHomeFlowFragment;
import com.popdeem.sdk.uikit.fragment.PDUIInboxFragment;

/**
 * Created by mikenolan on 22/02/16.
 */
public class PDUIHomeFlowActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @MenuRes
    private int menuRes = R.menu.menu_pd_home;
    private final int NO_MENU = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.pd_toolbar_color_dark));
        }
        setContentView(R.layout.activity_pd_home_flow);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mFragmentManager.getBackStackEntryCount() == 0) {
                    menuRes = R.menu.menu_pd_home;
                    invalidateOptionsMenu();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.pd_toolbar_text_color));
//        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.pd_toolbar_up_button_color));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        showHomeFragment();
    }

    private void showHomeFragment() {
        Fragment fragment = new PDUIHomeFlowFragment();
        mFragmentManager.beginTransaction()
                .add(R.id.pd_home_fragment_container, fragment)
                .commit();
    }

    private void showInboxFragment() {
        menuRes = NO_MENU;
        invalidateOptionsMenu();
        Fragment fragment = new PDUIInboxFragment();
        mFragmentManager.beginTransaction()
                .add(R.id.pd_home_fragment_container, fragment)
                .addToBackStack(PDUIInboxFragment.class.getSimpleName())
                .commit();
    }

    private boolean popBackStackIfNeeded() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (menuRes != NO_MENU) {
            getMenuInflater().inflate(menuRes, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int ID = item.getItemId();
        if (ID == android.R.id.home) {
            if (!popBackStackIfNeeded()) {
                finish();
            }
            return true;
        } else if (ID == R.id.action_inbox) {
            showInboxFragment();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!popBackStackIfNeeded()) {
            super.onBackPressed();
        }
    }

}
