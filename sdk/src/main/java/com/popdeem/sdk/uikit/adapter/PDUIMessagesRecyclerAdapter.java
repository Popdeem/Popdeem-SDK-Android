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

package com.popdeem.sdk.uikit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDMessage;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIBezelImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDUIMessagesRecyclerAdapter extends RecyclerView.Adapter<PDUIMessagesRecyclerAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(View view);
    }

    private OnItemClickListener mListener;
    private ArrayList<PDMessage> mItems;

    public PDUIMessagesRecyclerAdapter(ArrayList<PDMessage> mItems) {
        this.mItems = mItems;
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pd_message, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PDMessage message = this.mItems.get(position);

        holder.readIndicatorView.setVisibility(message.isRead() ? View.INVISIBLE : View.VISIBLE);
        holder.bodyTextView.setText(message.getBody());
        holder.dateTextView.setText(PDUIUtils.convertUnixTimeToDate(message.getCreatedAt(), PDUIUtils.PD_DATE_FORMAT));

        if (message.getImageUrl() == null || message.getImageUrl().isEmpty() || message.getImageUrl().contains("default")) {
            Picasso.with(holder.context)
                    .load(R.drawable.pd_ui_star_icon)
                    .into(holder.imageView);
        } else {
            Picasso.with(holder.context)
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.pd_ui_star_icon)
                    .error(R.drawable.pd_ui_star_icon)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        PDUIBezelImageView imageView;
        TextView dateTextView;
        TextView bodyTextView;
        View readIndicatorView;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(v);
                    }
                }
            });
            this.context = context;
            this.imageView = (PDUIBezelImageView) itemView.findViewById(R.id.pd_message_image_view);
            this.dateTextView = (TextView) itemView.findViewById(R.id.pd_message_date_text_view);
            this.bodyTextView = (TextView) itemView.findViewById(R.id.pd_message_body_text_view);
            this.readIndicatorView = itemView.findViewById(R.id.pd_message_read_indicator_view);
        }
    }

}
