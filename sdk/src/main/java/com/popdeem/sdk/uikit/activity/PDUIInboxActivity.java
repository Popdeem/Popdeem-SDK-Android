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
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.model.PDMessage;
import com.popdeem.sdk.uikit.fragment.PDUIInboxFragment;
import com.popdeem.sdk.uikit.fragment.PDUIInboxMessageFragment;

/**
 * Created by mikenolan on 14/03/16.
 */
public class PDUIInboxActivity extends PDBaseActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_inbox);

        setTitle(R.string.pd_inbox_title);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.pd_inbox_fragment_container, PDUIInboxFragment.newInstance(mInboxItemClickListener))
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_VIEWED_INBOX)
                .create());
    }

    private final PDUIInboxFragment.InboxItemClickListener mInboxItemClickListener = new PDUIInboxFragment.InboxItemClickListener() {
        @Override
        public void itemClicked(PDMessage message) {
            mFragmentManager.beginTransaction()
                    .add(R.id.pd_inbox_fragment_container, PDUIInboxMessageFragment.newInstance(message))
                    .addToBackStack(PDUIInboxMessageFragment.class.getSimpleName())
                    .commit();
        }
    };

    private boolean popBackStackIfNeeded() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            String name = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
            mFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }
        return false;
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

}
