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

package com.popdeem.sdk.uikit.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.popdeem.sdk.R;

/**
 * Created by mikenolan on 14/03/16.
 */
public class PDUISwipeRefreshLayout extends SwipeRefreshLayout {

    private LinearLayoutManager mLinearLayoutManager;

    public PDUISwipeRefreshLayout(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public PDUISwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        int[] colors = context.getResources().getIntArray(R.array.pd_swipe_refresh_colors_array);
        if (colors.length > 0) {
            setColorSchemeColors(colors);
        } else {
            setColorSchemeColors(ContextCompat.getColor(context, R.color.pd_toolbar_color));
        }
    }

    public void addLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mLinearLayoutManager != null) {
            return mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() > 0;
        }
        return super.canChildScrollUp();
    }
}
