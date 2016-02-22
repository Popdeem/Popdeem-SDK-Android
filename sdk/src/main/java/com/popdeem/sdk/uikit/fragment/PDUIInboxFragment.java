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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;

import java.util.ArrayList;

public class PDUIInboxFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    //    private PDUIWalletRecyclerViewAdapter mAdapter;
    private ArrayList<String> mRewards = new ArrayList<>();
    private View mNoMessagesView;

    public PDUIInboxFragment() {
        // Required empty public constructor
    }

    public static PDUIInboxFragment newInstance() {
        return new PDUIInboxFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_inbox, container, false);

        mNoMessagesView = view.findViewById(R.id.pd_inbox_no_items_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pd_inbox_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMessages();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pd_inbox_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity()));
//        recyclerView.setAdapter(mAdapter);

        refreshMessages();

        return view;
    }

    private void refreshMessages() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        PDAPIClient.instance().getPopdeemMessages(new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                Log.d(PDUIInboxFragment.class.getSimpleName(), "json: " + jsonObject.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(int statusCode, String message) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

        }
        return false;
    }

}
