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

        setTitle(R.string.pd_inbox_text);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.pd_inbox_fragment_container, PDUIInboxFragment.newInstance(mInboxItemClickListener))
                .commit();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
