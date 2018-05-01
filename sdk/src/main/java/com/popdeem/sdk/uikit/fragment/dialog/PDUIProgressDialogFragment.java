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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.popdeem.sdk.R;

/**
 * Created by mikenolan on 10/03/16.
 */
public class PDUIProgressDialogFragment extends DialogFragment {

    private static final String MESSAGE_KEY = "message";
    private static final String TITLE_KEY = "title";

    private DialogInterface.OnCancelListener mOnCancelListener = null;

    public PDUIProgressDialogFragment() {
    }

    public static PDUIProgressDialogFragment showProgressDialog(FragmentManager fm, String title, String message, boolean isCancelable, DialogInterface.OnCancelListener onCancelListener) {
        Fragment prev = fm.findFragmentByTag(PDUIProgressDialogFragment.class.getSimpleName());
        if (prev != null) {
            fm.beginTransaction().remove(prev).addToBackStack(null).commit();
        }

        PDUIProgressDialogFragment dialog = PDUIProgressDialogFragment.newInstance(title, message);
        dialog.setCancelable(isCancelable);
        dialog.setOnCancelListener(onCancelListener);
        dialog.show(fm, PDUIProgressDialogFragment.class.getSimpleName());
        return dialog;
    }

    public static PDUIProgressDialogFragment newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MESSAGE_KEY, message);

        PDUIProgressDialogFragment fragment = new PDUIProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pd_progress_dialog, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
        builder.setView(view);
        builder.setOnCancelListener(mOnCancelListener);

        TextView textView = (TextView) view.findViewById(R.id.pd_progress_dialog_title);
        textView.setText(args.getString(TITLE_KEY, getString(R.string.pd_common_please_wait_text)));

        textView = (TextView) view.findViewById(R.id.pd_progress_dialog_message);
        textView.setText(args.getString(MESSAGE_KEY, getString(R.string.pd_claim_claiming_reward_text)));

        return builder.create();
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

}
