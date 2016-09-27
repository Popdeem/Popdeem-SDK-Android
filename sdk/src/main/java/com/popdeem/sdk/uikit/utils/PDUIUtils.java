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

import com.popdeem.sdk.core.utils.PDLocaleUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIUtils {

    public static final String PD_DATE_FORMAT = "EEE dd MMM kk:mm";

    private static final DecimalFormat DF_TWO_PLACES = new DecimalFormat(".##");

    /**
     * Method to calculate the time until (Needs refactoring)
     *
     * @param expiryTimeInSeconds {@link Long} value for expiry time in seconds
     * @param nonClaimedReward    {@link Boolean}
     * @param isSweepstakes       {@link Boolean}
     * @return {@link String} value for time until
     */
    public static String timeUntil(long expiryTimeInSeconds, boolean nonClaimedReward, boolean isSweepstakes) {
        final DateTime now = DateTime.now();
        final DateTime expiry = new DateTime(expiryTimeInSeconds * 1000);

        // Check if reward has expired
        if (expiry.isBefore(now)) {
            return "Reward has expired";
        }

        // Days
        int days = Days.daysBetween(now, expiry).getDays();
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

        // Hours
        int hours = Hours.hoursBetween(now, expiry).getHours();
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

        // Minutes
        int minutes = Minutes.minutesBetween(now, expiry).getMinutes();
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


    public static String convertTimeToDayAndMonth(long expiryTimeInSeconds) {
        if (expiryTimeInSeconds <= 0) {
            return "";
        }

        return convertUnixTimeToDate(expiryTimeInSeconds, "d MMM");
    }


    /**
     * Get a time in milliseconds in Timer format
     *
     * @param millis Time in milliseconds
     * @return String in timer format
     */
    public static String millisecondsToTimer(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }


    public static String millisecondsToMinutes(long millis) {
        return String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millis));
    }


    /**
     * Format a Unix Time stamp for the given format
     *
     * @param date   Unix Time stamp in seconds
     * @param format Format to use when converting.
     * @return Formatted date String
     */
    public static String convertUnixTimeToDate(long date, String format) {
        DateTime dateTime = new DateTime(date * 1000);
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        return formatter.print(dateTime);
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


    public static String formatDistance(float distanceInMeters) {
        if (PDLocaleUtils.useMetricSystem()) {
            double distanceInKm = distanceInMeters / 1000;
            return String.format(Locale.getDefault(), "%1skm", DF_TWO_PLACES.format(distanceInKm));
        } else {
            double miles = distanceInMeters / 1609.344;
            return String.format(Locale.getDefault(), "%1smiles", DF_TWO_PLACES.format(miles));
        }
    }

}
