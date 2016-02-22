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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.model.PDFeed;
import com.popdeem.sdk.uikit.adapter.PDUIFeedRecyclerViewAdapter;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIFeedFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PDUIFeedRecyclerViewAdapter mAdapter;
    private View mNoItemsView;
    private ArrayList<PDFeed> mFeedItems = new ArrayList<>();

    public PDUIFeedFragment() {
    }

    public static PDUIFeedFragment newInstance() {
        return new PDUIFeedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_feed, container, false);

        mNoItemsView = view.findViewById(R.id.pd_activity_no_items_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });

        mAdapter = new PDUIFeedRecyclerViewAdapter(mFeedItems);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pd_activity_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(mAdapter);

        refreshFeed();

        return view;
    }

    private void refreshFeed() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        PDAPIClient.instance().getFeeds(new PDAPICallback<ArrayList<PDFeed>>() {
            @Override
            public void success(ArrayList<PDFeed> pdFeeds) {
                mFeedItems.clear();
                mFeedItems.addAll(pdFeeds);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                mNoItemsView.setVisibility(mFeedItems.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void failure(int statusCode, String message) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
