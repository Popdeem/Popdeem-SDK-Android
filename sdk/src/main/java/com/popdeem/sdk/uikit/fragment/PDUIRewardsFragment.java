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
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.uikit.adapter.PDUIRewardsRecyclerViewAdapter;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIRewardsFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PDUIRewardsRecyclerViewAdapter mRecyclerViewAdapter;
    private ArrayList<PDReward> mRewards = new ArrayList<>();

    public PDUIRewardsFragment() {
    }

    public static PDUIRewardsFragment newInstance() {
        return new PDUIRewardsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_rewards, container, false);

        mRecyclerViewAdapter = new PDUIRewardsRecyclerViewAdapter(mRewards);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pd_rewards_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(mRecyclerViewAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRewards();
            }
        });

        refreshRewards();

        return view;
    }

    private void refreshRewards() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        PDAPIClient.instance().getAllRewards(new PDAPICallback<ArrayList<PDReward>>() {
            @Override
            public void success(ArrayList<PDReward> pdRewards) {
                mRewards.clear();
                mRewards.addAll(pdRewards);
                mRecyclerViewAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(int statusCode, String message) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
