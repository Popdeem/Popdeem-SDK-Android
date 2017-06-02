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

package com.popdeem.sdk.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mikenolan on 24/02/16.
 */
public class PDPreferencesUtils {

    private static final String PREFS_NAME = "com.popdeem.sdk.preferences";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
    }

    public static void setNumberOfLoginAttempts(Context context, int numberOfAttempts) {
        getSharedPreferences(context).edit().putInt("numberOfAttempts", numberOfAttempts).apply();
    }

    public static int getNumberOfLoginAttempts(Context context) {
        return getSharedPreferences(context).getInt("numberOfAttempts", 0);
    }

    public static void setSocialLoginActivityName(Context context, String name) {
        getSharedPreferences(context).edit().putString("socialLoginActivityName", name).apply();
    }

    public static String getSocialLoginActivityName(Context context) {
        return getSharedPreferences(context).getString("socialLoginActivityName", "");
    }

    public static void setLoginUsesCount(Context context, int uses) {
        getSharedPreferences(context).edit().putInt("loginUsesCount", uses).apply();
    }

    public static void incrementLoginUsesCount(Context context) {
        getSharedPreferences(context).edit().putInt("loginUsesCount", getLoginUsesCount(context) + 1).apply();
    }

    public static int getLoginUsesCount(Context context) {
        return getSharedPreferences(context).getInt("loginUsesCount", 0);
    }

    public static void setMultiLoginEnabled(Context context, boolean isEnabled){
        getSharedPreferences(context).edit().putBoolean("isMultiLoginEnabled", isEnabled).apply();
    }

    public static boolean getIsMultiLoginEnabled(Context context){
        return getSharedPreferences(context).getBoolean("isMultiLoginEnabled", false);
    }


    /**
     * Clear com.popdeem.sdk.preferences
     *
     * @param context Application Context
     */
    public static void clearPrefs(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }

}
