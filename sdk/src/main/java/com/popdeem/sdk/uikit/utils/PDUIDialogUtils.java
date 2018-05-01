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
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.popdeem.sdk.R;

/**
 * Created by mikenolan on 10/08/16.
 */
public class PDUIDialogUtils {

    public static void showBasicOKAlertDialog(Context context, @StringRes int titleRes, String message) {
        if (context == null) {
            return;
        }
        String title = context.getString(titleRes);
        showBasicOKAlertDialog(context, title, message);
    }

    public static void showBasicOKAlertDialog(Context context, @StringRes int titleRes, @StringRes int messageRes) {
        if (context == null) {
            return;
        }
        String title = context.getString(titleRes);
        String message = context.getString(messageRes);
        showBasicOKAlertDialog(context, title, message);
    }

    public static void showBasicOKAlertDialog(Context context, String title, String message) {
        if (context == null) {
            return;
        }
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    public static void showBasicOKAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener listener ) {
        if (context == null) {
            return;
        }
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .create()
                .show();
    }

}
