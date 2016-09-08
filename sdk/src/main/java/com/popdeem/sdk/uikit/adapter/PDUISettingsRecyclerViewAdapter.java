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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.popdeem.sdk.R;
import com.popdeem.sdk.uikit.activity.PDUISettingsActivity;

import java.util.ArrayList;

/**
 * Created by mikenolan on 16/08/16.
 */
public class PDUISettingsRecyclerViewAdapter extends RecyclerView.Adapter<PDUISettingsRecyclerViewAdapter.ViewHolder> {

    public interface PDUISettingsSwitchCallback {
        void onSwitchCheckedChange(int position, boolean isChecked);
    }

    private PDUISettingsSwitchCallback mCallback;
    private ArrayList<PDUISettingsActivity.PDSettingsSocialNetwork> mItems;

    public PDUISettingsRecyclerViewAdapter(@NonNull PDUISettingsSwitchCallback callback, ArrayList<PDUISettingsActivity.PDSettingsSocialNetwork> items) {
        this.mCallback = callback;
        this.mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pd_settings, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PDUISettingsActivity.PDSettingsSocialNetwork network = mItems.get(position);
        holder.networkTextView.setText(network.getName());
        holder.networkTextView.setCompoundDrawablesWithIntrinsicBounds(network.getDrawableRes(), 0, 0, 0);

        holder.networkSwitch.setOnCheckedChangeListener(null);
        holder.networkSwitch.setChecked(network.isValidated());
        holder.networkSwitch.setOnCheckedChangeListener(holder.checkedChangeListener);
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView networkTextView;
        SwitchCompat networkSwitch;
        CompoundButton.OnCheckedChangeListener checkedChangeListener;

        public ViewHolder(View itemView) {
            super(itemView);
            networkTextView = (TextView) itemView.findViewById(R.id.pd_settings_item_social_network_text_view);
            networkSwitch = (SwitchCompat) itemView.findViewById(R.id.pd_settings_item_switch);
            checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mCallback != null) {
                        mCallback.onSwitchCheckedChange(getLayoutPosition(), isChecked);
                    }
                }
            };
            networkSwitch.setOnCheckedChangeListener(checkedChangeListener);
        }
    }

}
