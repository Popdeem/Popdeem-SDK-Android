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

package com.popdeem.sdk.uikit.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.popdeem.sdk.R;

/**
 * Created by mikenolan on 14/03/16.
 */
public class PDUIColorUtils {

    public static Drawable getBackButtonIcon(Context context) {
        return getTintedDrawable(context, R.drawable.pd_ic_arrow_back, R.color.pd_back_button_color, true);
    }

    public static Drawable getSocialLoginBackButtonIcon(Context context) {
        return getTintedDrawable(context, R.drawable.baseline_close_white_36, R.color.pd_social_login_back_button_color, true);
    }

    public static Drawable getInboxButtonIcon(Context context) {
        return getTintedDrawable(context, R.drawable.pd_ic_inbox, R.color.pd_inbox_button_icon_color, false);
    }

    public static Drawable getLocationVerificationTickIcon(Context context) {
        return getTintedDrawable(context, R.drawable.ic_tick, R.color.pd_claim_location_verification_tick_color, false);
    }

    public static Drawable getListDivider(Context context, @ColorRes int color) {
        return getTintedDrawable(context, R.drawable.pd_divider, color, true);
    }

    public static Drawable getSettingsIcon(Context context) {
        return getTintedDrawable(context, R.drawable.ic_pd_settings, R.color.pd_home_flow_settings_icon_color, false);
    }

    public static Drawable getTintedDrawable(Context context, @DrawableRes int drawableRes, @ColorRes int colorRes, boolean mutate) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable == null) {
            return null;
        }

        if (mutate) {
            drawable = drawable.mutate();
        }
        drawable.setColorFilter(ContextCompat.getColor(context, colorRes), PorterDuff.Mode.SRC_IN);
        return drawable;
    }

}
