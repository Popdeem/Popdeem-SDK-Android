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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.response.PDBasicResponse;
import com.popdeem.sdk.core.utils.PDLog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by mikenolan on 25/02/16.
 */
public class PDUINotificationDialogFragment extends DialogFragment {

    private boolean created = true;

    public PDUINotificationDialogFragment() {
    }

    public static void showNotificationDialog(FragmentManager fm, String title, String message, String imageUrl, String targetUrl, String deepLink, String messageId) {
        Fragment prev = fm.findFragmentByTag(PDUINotificationDialogFragment.class.getSimpleName());
        if (prev != null) {
            fm.beginTransaction().remove(prev).addToBackStack(null).commit();
        }

        PDUINotificationDialogFragment dialog = PDUINotificationDialogFragment.newInstance(title, message, imageUrl, targetUrl, deepLink, messageId);
        if (dialog.isCreated()) {
            dialog.setCancelable(false);
            dialog.show(fm, dialog.getClass().getSimpleName());
        }
    }

    public static PDUINotificationDialogFragment newInstance(String title, String message, String imageUrl, String targetUrl, String deepLink, String messageId) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("image", imageUrl);
        args.putString("targetUrl", targetUrl);
        args.putString("deepLink", deepLink);
        args.putString("messageId", messageId);

        PDUINotificationDialogFragment dialogFragment = new PDUINotificationDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
        Bundle args = getArguments();
        if (args == null) {
            created = false;
            return builder.create();
        }

        final String title = args.getString("title", null);
        final String message = args.getString("message", null);
        final String imageUrl = args.getString("image", null);
        final String targetUrl = args.getString("targetUrl", null);
        final String deepLink = args.getString("deepLink", null);
        final String messageId = args.getString("messageId", null);

        if (title == null || message == null) {
            created = false;
            return builder.create();
        }

        // Dialog View
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pd_notification_dialog, null, false);
        builder.setView(dialogView);

        // Title
        TextView textView = (TextView) dialogView.findViewById(R.id.pd_notification_dialog_title_text_view);
        textView.setText(title);

        // Message
        textView = (TextView) dialogView.findViewById(R.id.pd_notification_dialog_message_text_view);
        textView.setText(message);

        // Image
        final ImageView imageView = (ImageView) dialogView.findViewById(R.id.pd_notification_dialog_image_view);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getActivity())
                            .load(imageUrl)
                            .dontAnimate()
//                            .override(imageView.getWidth(), 1)
                            .into(imageView);
                }
            });
        }

        // Buttons
        final DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                markMessageAsRead(messageId);
            }
        };
        if (targetUrl == null && deepLink == null) {
            builder.setPositiveButton(android.R.string.ok, negativeClick);
        } else {
            builder.setPositiveButton(R.string.pd_notification_go_button_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    markMessageAsRead(messageId);

                    String url = targetUrl != null ? targetUrl : imageUrl;
                    if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, negativeClick);
        }

        return builder.create();
    }

    private void markMessageAsRead(String messageId) {
        if (messageId == null) {
            return;
        }
        PDAPIClient.instance().markMessageAsRead(messageId, new PDAPICallback<PDBasicResponse>() {
            @Override
            public void success(PDBasicResponse pdBasicResponse) {
                PDLog.d(PDUINotificationDialogFragment.class, pdBasicResponse.toString());
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.w(PDUINotificationDialogFragment.class, e.getMessage());
            }
        });
    }

    public boolean isCreated() {
        return created;
    }

}
