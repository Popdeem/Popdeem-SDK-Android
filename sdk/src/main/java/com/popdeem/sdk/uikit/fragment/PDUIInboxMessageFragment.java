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

package com.popdeem.sdk.uikit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDMessage;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by mikenolan on 24/02/16.
 */
public class PDUIInboxMessageFragment extends Fragment {

    public PDUIInboxMessageFragment() {
    }

    public static PDUIInboxMessageFragment newInstance(PDMessage message) {
        Bundle args = new Bundle();
        args.putString("title", message.getTitle());
        args.putString("body", message.getBody());
        args.putString("sender", message.getSenderName());
        args.putString("image", message.getImageUrl());
        args.putLong("date", message.getCreatedAt());

        PDUIInboxMessageFragment fragment = new PDUIInboxMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_inbox_message, container, false);

        Bundle args = getArguments();

        TextView textView = (TextView) view.findViewById(R.id.pd_inbox_message_title_text_view);
        textView.setText(args.getString("title", ""));

        textView = (TextView) view.findViewById(R.id.pd_inbox_message_body_text_view);
        textView.setText(args.getString("body", ""));

        textView = (TextView) view.findViewById(R.id.pd_inbox_message_sender_text_view);
        textView.setText(args.getString("sender", ""));

        textView = (TextView) view.findViewById(R.id.pd_inbox_message_date_text_view);
        long date = args.getLong("date", -1);
        if (date == -1) {
            textView.setText("");
        } else {
            textView.setText(PDUIUtils.convertUnixTimeToDate(date, PDUIUtils.PD_DATE_FORMAT));
        }

        PDUIBezelImageView imageView = (PDUIBezelImageView) view.findViewById(R.id.pd_inbox_message_image_view);
        String imageUrl = args.getString("image", "");
        if (imageUrl.isEmpty() || imageUrl.contains("default")) {
            Glide.with(getActivity())
                    .load(R.drawable.pd_ui_star_icon)
                    .dontAnimate()
                    .into(imageView);
        } else {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .dontAnimate()
                    .error(R.drawable.pd_ui_star_icon)
                    .into(imageView);
        }

        return view;
    }
}
