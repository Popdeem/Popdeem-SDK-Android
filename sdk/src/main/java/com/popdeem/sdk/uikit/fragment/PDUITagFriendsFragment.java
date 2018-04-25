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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.deserializer.PDSocialMediaFriendsDeserializer;
import com.popdeem.sdk.core.model.PDSocialMediaFriend;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.uikit.adapter.PDUITaggableFriendsListViewAdapter;
import com.popdeem.sdk.uikit.utils.PDUIUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDUITagFriendsFragment extends Fragment {

    public interface TagFriendsConfirmedCallback {
        void taggedFriendsUpdated(@NonNull ArrayList<String> taggedNames, @NonNull ArrayList<String> taggedIds);
    }

    private TagFriendsConfirmedCallback mConfirmCallback;

    private ArrayList<PDSocialMediaFriend> friends = new ArrayList<>();

    private PDUITaggableFriendsListViewAdapter mAdapter;
    private ProgressBar mProgress;
    private TextView mTaggedFriendsTextView;

    private ArrayList<String> mTaggedNames = new ArrayList<>();
    private ArrayList<String> mTaggedIds = new ArrayList<>();

    public PDUITagFriendsFragment() {
    }

    public static PDUITagFriendsFragment newInstance(@NonNull TagFriendsConfirmedCallback callback, @NonNull ArrayList<String> taggedNames, @NonNull ArrayList<String> taggedIds) {
        Bundle args = new Bundle();
        args.putStringArrayList("names", taggedNames);
        args.putStringArrayList("ids", taggedIds);

        PDUITagFriendsFragment fragment = new PDUITagFriendsFragment();
        fragment.setConfirmCallback(callback);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_tag_friends, container, false);

        mTaggedNames = getArguments().getStringArrayList("names");
        mTaggedIds = getArguments().getStringArrayList("ids");

        mProgress = (ProgressBar) view.findViewById(R.id.pd_progress_bar);
        mTaggedFriendsTextView = (TextView) view.findViewById(R.id.pd_tagged_friends_text_view);

        view.findViewById(R.id.pd_tagged_friends_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmCallback.taggedFriendsUpdated(mTaggedNames, mTaggedIds);
                getActivity().getSupportFragmentManager().popBackStack(PDUITagFriendsFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        final EditText searchEditText = (EditText) view.findViewById(R.id.pd_tag_friends_search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    PDUIUtils.hideKeyboard(getActivity(), searchEditText);
                    return true;
                }
                return false;
            }
        });

        mAdapter = new PDUITaggableFriendsListViewAdapter(getActivity(), R.layout.item_pd_taggable_friend, friends);
        mAdapter.setCallback(mFriendsCheckBoxChangedCallback);

        ListView listView = (ListView) view.findViewById(R.id.pd_tag_friends_list_view);
        listView.setAdapter(mAdapter);

        updateTaggedFriendsButton();
        makeTaggableFriendsRequest();

        return view;
    }

    public void setConfirmCallback(TagFriendsConfirmedCallback callback) {
        this.mConfirmCallback = callback;
    }

    private void updateTaggedFriendsButton() {
        boolean anyCheckBoxChecked = mTaggedIds.size() > 0;
        mTaggedFriendsTextView.setVisibility(anyCheckBoxChecked ? View.VISIBLE : View.INVISIBLE);
        if (anyCheckBoxChecked) {
            StringBuilder builder = new StringBuilder("");
            for (int i = 0; i < mTaggedNames.size(); i++) {
                if (i == 0) {
                    builder.append(mTaggedNames.get(i));
                } else {
                    builder.append(", ");
                    builder.append(mTaggedNames.get(i));
                }
            }
            mTaggedFriendsTextView.setText(builder.toString());
        } else {
            mTaggedFriendsTextView.setText("");
        }
    }

    private PDUITaggableFriendsListViewAdapter.FriendCheckBoxChangedCallback mFriendsCheckBoxChangedCallback = new PDUITaggableFriendsListViewAdapter.FriendCheckBoxChangedCallback() {
        @Override
        public void friendCheckBoxChanged(String id, String name, boolean isChecked) {
            if (isChecked && !mTaggedNames.contains(name)) {
                mTaggedNames.add(name);
                mTaggedIds.add(id);
            } else if (mTaggedNames.contains(name)) {
                mTaggedIds.remove(mTaggedNames.indexOf(name));
                mTaggedNames.remove(mTaggedNames.indexOf(name));
            }

            if (mTaggedNames.isEmpty() || mTaggedIds.isEmpty()) {
                mConfirmCallback.taggedFriendsUpdated(mTaggedNames, mTaggedIds);
            }

            PDLog.d(PDUITagFriendsFragment.class, "mTaggedIds: " + mTaggedIds.toString());
            PDLog.d(PDUITagFriendsFragment.class, "mTaggedNames: " + mTaggedNames.toString());

            updateTaggedFriendsButton();
        }

    };


    private void updateTaggableFriends(ArrayList<PDSocialMediaFriend> friends) {
        for (PDSocialMediaFriend f : friends) {
            for (String fid : this.mTaggedNames) {
                if (f.getName().equals(fid)) {
                    f.setSelected(true);
                }
            }
        }
        this.friends.clear();
        this.friends.addAll(friends);
        mProgress.setVisibility(View.GONE);
        this.mAdapter.notifyDataSetChanged();
    }

    private void makeTaggableFriendsRequest() {
        Bundle parameters = new Bundle();
        parameters.putString("limit", "5000");
        GraphRequest taggableFriendsRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                "/me/taggable_friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        FacebookRequestError error = graphResponse.getError();
                        if (error != null) {
//                            DialogUtils.showBasicDialog(ClaimActivity.this, error.getErrorUserTitle(), error.getErrorUserMessage(),
//                                    android.R.string.ok, 0, null, null);
                        } else {
                            JsonParser parser = new JsonParser();
                            JsonElement json = parser.parse(graphResponse.getJSONObject().toString());
                            if (json.getAsJsonObject().has("data")) {
                                Type type = new TypeToken<ArrayList<PDSocialMediaFriend>>() {
                                }.getType();

                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(type, new PDSocialMediaFriendsDeserializer())
                                        .create();

                                ArrayList<PDSocialMediaFriend> friends = gson.fromJson(json, type);
                                updateTaggableFriends(friends);
                            }
                            PDLog.d(PDUITagFriendsFragment.class, "friends: " + json.toString());
                        }
                    }
                });
        taggableFriendsRequest.setParameters(parameters);
        taggableFriendsRequest.executeAsync();
    }

}
