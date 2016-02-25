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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.uikit.activity.PDUIRedeemActivity;
import com.popdeem.sdk.uikit.adapter.PDUIWalletRecyclerViewAdapter;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIWalletFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PDUIWalletRecyclerViewAdapter mAdapter;
    private ArrayList<PDReward> mRewards = new ArrayList<>();
    private View mNoItemsInWalletView;

    public PDUIWalletFragment() {
    }

    public static PDUIWalletFragment newInstance() {
        return new PDUIWalletFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_wallet, container, false);

        mNoItemsInWalletView = view.findViewById(R.id.pd_wallet_no_items_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWallet();
            }
        });

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pd_wallet_recycler_view);

        mAdapter = new PDUIWalletRecyclerViewAdapter(mRewards);
        mAdapter.setOnItemClickListener(new PDUIWalletRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                final int position = recyclerView.getChildAdapterPosition(v);
                final PDReward reward = mRewards.get(position);

                if (!reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
                    final long REDEMPTION_TIMER = 1000 * 60 * 10 + 500;
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are you sure?")
                            .setMessage("You will have " + PDUIUtils.millisecondsToMinutes(REDEMPTION_TIMER) + " minutes to show the next screen at the shop")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    redeemReward(reward, position);
                                    Intent intent = new Intent(getActivity(), PDUIRedeemActivity.class);
                                    intent.putExtra("imageUrl", reward.getCoverImage());
                                    intent.putExtra("reward", reward.getDescription());
                                    intent.putExtra("rules", reward.getRules());
                                    intent.putExtra("isSweepstakes", reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE));
                                    intent.putExtra("time", reward.getAvailableUntilInSeconds());
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .create()
                            .show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(mAdapter);

        refreshWallet();

        return view;
    }

    private void redeemReward(PDReward reward, int position) {
        PDAPIClient.instance().redeemReward(reward.getId(), new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {

            }

            @Override
            public void failure(int statusCode, Exception e) {

            }
        });
    }

    private void refreshWallet() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        PDAPIClient.instance().getRewardsInWallet(new PDAPICallback<ArrayList<PDReward>>() {
            @Override
            public void success(ArrayList<PDReward> pdRewards) {
                mRewards.clear();
                mRewards.addAll(pdRewards);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                mNoItemsInWalletView.setVisibility(mRewards.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
