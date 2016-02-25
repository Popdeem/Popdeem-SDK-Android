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

package com.popdeem.sdk.uikit.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.popdeem.sdk.core.PopdeemSDK;

/**
 * Created by mikenolan on 25/02/16.
 */
public class PDUINotificationDialogFragment extends DialogFragment {

    private boolean created = true;

    public PDUINotificationDialogFragment() {
    }

    public static PDUINotificationDialogFragment newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);

        PDUINotificationDialogFragment dialogFragment = new PDUINotificationDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PopdeemSDK.currentActivity());

        if (getArguments() == null) {
            created = false;
            return builder.create();
        }

        String title = getArguments().getString("title", null);
        String message = getArguments().getString("message", null);
        if (title == null || message == null) {
            created = false;
            return builder.create();
        }

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

    public boolean isCreated() {
        return created;
    }
}
