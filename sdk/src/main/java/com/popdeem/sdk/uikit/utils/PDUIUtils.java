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
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Date;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIUtils {

    /**
     * Method to calculate the time until (Needs refactoring)
     *
     * @param expiryTimeInSeconds {@link Long} value for expiry time in seconds
     * @param nonClaimedReward    {@link Boolean}
     * @param isSweepstakes       {@link Boolean}
     * @return {@link String} value for time until
     */
    public static String timeUntil(long expiryTimeInSeconds, boolean nonClaimedReward, boolean isSweepstakes) {
        Date expiryDate = new Date(expiryTimeInSeconds * 1000);
        Date now = new Date();

        if (expiryDate.before(now)) {
            return "Reward has expired";
        }

        Period period = new Period(now.getTime(), expiryDate.getTime(), PeriodType.yearMonthDayTime());

        int days = period.getDays();
        if (days == 1) {
            if (isSweepstakes) {
                return "1 day";
            } else {
                return nonClaimedReward ? "1 day remaining" : "Expires in 1 day";
            }
        }
        if (days > 1) {
            if (isSweepstakes) {
                return days + " days";
            } else {
                return nonClaimedReward ? days + " days remaining" : "Expires in " + days + " days";
            }
        }

        int hours = period.getHours();
        if (hours == 1) {
            if (isSweepstakes) {
                return "1 hour";
            } else {
                return nonClaimedReward ? "1 hour remaining" : "Expires in 1 hour";
            }
        }
        if (hours > 1) {
            if (isSweepstakes) {
                return hours + " hours";
            } else {
                return nonClaimedReward ? hours + " hours remaining" : "Expires in " + hours + " hours";
            }
        }

        int minutes = period.getMinutes();
        if (minutes == 1) {
            if (isSweepstakes) {
                return "1 minute";
            } else {
                return nonClaimedReward ? "1 minute remaining" : "Expires in 1 minute";
            }
        }

        if (isSweepstakes) {
            return minutes + " minutes";
        } else
            return nonClaimedReward ? minutes + " minutes remaining" : "Expires in " + minutes + " minutes";
    }


    /**
     * Hide UI Keyboard
     *
     * @param context Application Context
     * @param view    View
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
