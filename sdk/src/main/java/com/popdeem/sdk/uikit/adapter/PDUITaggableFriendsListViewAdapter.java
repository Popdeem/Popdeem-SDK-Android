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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDSocialMediaFriend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mikenolan on 25/06/15.
 */
public class PDUITaggableFriendsListViewAdapter extends ArrayAdapter<PDSocialMediaFriend> {

    public interface FriendCheckBoxChangedCallback {
        void friendCheckBoxChanged(String id, String name, boolean isChecked);
    }

    private ArrayList<PDSocialMediaFriend> mItems;
    private ArrayList<PDSocialMediaFriend> mSearchItems;
    private Context mContext;
    private FriendFilter mFilter;
    private FriendCheckBoxChangedCallback mCallback;

    public PDUITaggableFriendsListViewAdapter(Context context, int resource, List<PDSocialMediaFriend> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mSearchItems = this.mItems = (ArrayList<PDSocialMediaFriend>) objects;
    }

    public FriendCheckBoxChangedCallback getCallback() {
        return mCallback;
    }

    public void setCallback(FriendCheckBoxChangedCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public int getCount() {
        return this.mSearchItems.size();
    }

    @Override
    public PDSocialMediaFriend getItem(int position) {
        return this.mSearchItems.get(position);
    }

    @Override
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new FriendFilter();
        }
        return this.mFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.item_pd_taggable_friend, parent, false);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.pd_taggable_friend_check_box);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PDSocialMediaFriend item = mSearchItems.get(position);

        holder.checkBox.setText(item.getName());
        holder.checkBox.setTag(item.getTagIdentifier());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isSelected());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String tag = (String) buttonView.getTag();
                for (PDSocialMediaFriend friend : mItems) {
                    if (friend.getTagIdentifier().equalsIgnoreCase(tag)) {
                        friend.setSelected(isChecked);
                        break;
                    }
                }

                if (mCallback != null) {
                    mCallback.friendCheckBoxChanged(tag, buttonView.getText().toString(), isChecked);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private CheckBox checkBox;
    }

    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<PDSocialMediaFriend> friends = new ArrayList<>();

            if (constraint != null && constraint.length() > 0) {
                for (PDSocialMediaFriend friend : mItems) {
                    if (friend.getName().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                        friends.add(friend);
                    }
                }

                results.values = friends;
                results.count = friends.size();
            } else {
                synchronized (mItems) {
                    results.values = mItems;
                    results.count = mItems.size();
                }
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSearchItems = (ArrayList<PDSocialMediaFriend>) results.values;
            notifyDataSetChanged();
        }
    }
}
