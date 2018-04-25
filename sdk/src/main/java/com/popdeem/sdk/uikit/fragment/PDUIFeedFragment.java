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

import android.content.Intent;
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
import com.popdeem.sdk.uikit.activity.PDUIFeedImageActivity;
import com.popdeem.sdk.uikit.adapter.PDUIFeedRecyclerViewAdapter;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;
import com.popdeem.sdk.uikit.widget.PDUISwipeRefreshLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;

import static com.popdeem.sdk.uikit.utils.PDUIImageUtils.deleteDirectoryTree;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIFeedFragment extends Fragment {

    private View mView;

    private PDUISwipeRefreshLayout mSwipeRefreshLayout;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Picasso.with(getActivity()).setLoggingEnabled(true);

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_pd_feed, container, false);

            mNoItemsView = mView.findViewById(R.id.pd_feed_no_items_view);
            mSwipeRefreshLayout = (PDUISwipeRefreshLayout) mView;
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshFeed();
                }
            });

            final RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.pd_feed_recycler_view);

            mAdapter = new PDUIFeedRecyclerViewAdapter(mFeedItems, this);
            mAdapter.setOnItemClickListener(new PDUIFeedRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    if (view.findViewById(R.id.pd_feed_shared_image_view).getVisibility() == View.VISIBLE && mFeedItems.get(position) != null) {
                        Intent intent = new Intent(getActivity(), PDUIFeedImageActivity.class);
                        intent.putExtra("userName", mFeedItems.get(position).getUserFirstName());
                        intent.putExtra("imageUrl", mFeedItems.get(position).getImageUrlString());
                        startActivity(intent);
                    }
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mSwipeRefreshLayout.addLinearLayoutManager(linearLayoutManager);

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity(), R.color.pd_feed_list_divider_color));
            recyclerView.setAdapter(mAdapter);

            loadList();
            refreshFeed();
        } else {
            container.removeView(mView);
        }

        return mView;
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
                deleteDirectoryTree(getActivity());
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(PDFeed.class);
                    }
                });

                for (int i = 0; i < pdFeeds.size(); i++) {
                    realm.beginTransaction();
                    realm.copyToRealm(pdFeeds.get(i));
                    realm.commitTransaction();
                }

                loadList();

            }

            @Override
            public void failure(int statusCode, Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
                loadList();
            }
        });
    }

    public void loadList(){

        Realm realm = Realm.getDefaultInstance();

        mFeedItems.clear();
        mFeedItems.addAll(realm.where(PDFeed.class).findAll());
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
        mNoItemsView.setVisibility(mFeedItems.size() == 0 ? View.VISIBLE : View.GONE);
    }

}
