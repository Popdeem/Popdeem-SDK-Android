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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.popdeem.sdk.core.model.PDSocialMediaFriend;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDUITagFriendsFragment extends Fragment {

    public PDUITagFriendsFragment() {
    }

    public static PDUITagFriendsFragment newInstance() {
        return new PDUITagFriendsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_tag_friends, container, false);

        makeTaggableFriendsRequest();

        return view;
    }

//    private TextWatcher searchTextWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            friendsAdapter.getFilter().filter(s);
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//        }
//    };
//
//    private void updateTaggableFriends() {
//        ArrayList<PDSocialMediaFriend> friends = user.getTaggableFriends();
//        if (friends == null) {
//            makeTaggableFriendsRequest();
//
//            friends = new ArrayList<>();
//            user.setTaggableFriends(friends);
//            PDDataManager.savePDUser(this, user);
//        }
//
//        for (PDSocialMediaFriend f : friends) {
//            for (PDSocialMediaFriend f1 : this.friends) {
//                if (f1.getTagIdentifier().equalsIgnoreCase(f.getTagIdentifier())) {
//                    f.setSelected(f1.isSelected());
//                }
//            }
//        }
//
//        this.friends.clear();
//        this.friends.addAll(friends);
//        this.friendsAdapter.notifyDataSetChanged();
//    }

    private void makeTaggableFriendsRequest() {
        PDRealmUserDetails userDetails = Realm.getDefaultInstance().where(PDRealmUserDetails.class).findFirst();

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
                                        .registerTypeAdapter(type, new PDSocialMediaFriend.PDSocialMediaFriendDeserializer())
                                        .create();

//                                ArrayList<PDSocialMediaFriend> friends = gson.fromJson(json, type);
//                                user.setTaggableFriends(friends);
//                                PDDataManager.savePDUser(ClaimActivity.this, user);
                            }
                            Log.d(PDUITagFriendsFragment.class.getSimpleName(), "friends: " + json.toString());
                        }

//                        updateTaggableFriends();
                    }
                });
        taggableFriendsRequest.setParameters(parameters);
        taggableFriendsRequest.executeAsync();
    }

}
