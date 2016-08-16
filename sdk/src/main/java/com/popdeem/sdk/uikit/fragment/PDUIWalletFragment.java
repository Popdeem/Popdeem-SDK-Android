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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.uikit.activity.PDUIRedeemActivity;
import com.popdeem.sdk.uikit.adapter.PDUIWalletRecyclerViewAdapter;
import com.popdeem.sdk.uikit.utils.PDUIDialogUtils;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;
import com.popdeem.sdk.uikit.widget.PDUISwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIWalletFragment extends Fragment {

    private View mView;

    private PDUISwipeRefreshLayout mSwipeRefreshLayout;
    private PDUIWalletRecyclerViewAdapter mAdapter;
    private ArrayList<PDReward> mRewards = new ArrayList<>();
    private View mNoItemsInWalletView;

    private String mAutoVerifyRewardId = null;

    public PDUIWalletFragment() {
    }

    public static PDUIWalletFragment newInstance() {
        return new PDUIWalletFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_pd_wallet, container, false);

            mNoItemsInWalletView = mView.findViewById(R.id.pd_wallet_no_items_view);
            mSwipeRefreshLayout = (PDUISwipeRefreshLayout) mView;
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshWallet();
                }
            });

            final RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.pd_wallet_recycler_view);

            mAdapter = new PDUIWalletRecyclerViewAdapter(mRewards);
            mAdapter.setOnItemClickListener(new PDUIWalletRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v) {
                    final int position = recyclerView.getChildAdapterPosition(v);
                    final PDReward reward = mRewards.get(position);
                    if (reward.claimedUsingNetwork(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM) && !reward.isInstagramVerified()) {
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    if (reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
                        long availableUntil = PDNumberUtils.toLong(reward.getAvailableUntilInSeconds(), 0);
                        String availabilityString;
                        if (availableUntil <= 0) {
                            availabilityString = getString(R.string.pd_redeem_sweepstake_reward_no_date_message_string);
                        } else {
                            availabilityString = String.format(Locale.getDefault(), "\n\n- Draw in %1s", PDUIUtils.timeUntil(availableUntil, false, true));
                        }

                        String message = String.format(Locale.getDefault(), "%1s%2s", getString(R.string.pd_redeem_sweepstake_reward_info_message_string), availabilityString);

                        builder.setTitle(R.string.pd_redeem_sweepstake_reward_info_title_string)
                                .setMessage(message)
                                .setPositiveButton(android.R.string.ok, null);
                        builder.create().show();
                    } else if (!reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_CREDIT)) {
                        final long REDEMPTION_TIMER = (reward.getCountdownTimer() * 1000) + 500;
                        String minutes = PDUIUtils.millisecondsToMinutes(REDEMPTION_TIMER);
                        String message = String.format(Locale.getDefault(), getString(R.string.pd_wallet_coupon_info_message_text), minutes, minutes);

                        builder.setTitle(R.string.pd_redeem_reward_info_title_string)
                                .setMessage(message)
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(R.string.pd_redeem_button_string, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        redeemReward(reward, position);
                                        Intent intent = new Intent(getActivity(), PDUIRedeemActivity.class);
                                        intent.putExtra("imageUrl", reward.getCoverImage());
                                        intent.putExtra("reward", reward.getDescription());
                                        intent.putExtra("rules", reward.getRules());
                                        intent.putExtra("isSweepstakes", reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE));
                                        intent.putExtra("time", reward.getAvailableUntilInSeconds());
                                        intent.putExtra("countdown", reward.getCountdownTimer());
                                        startActivity(intent);
                                    }
                                });
                        builder.create().show();
                    }
                }

                @Override
                public void onVerifyClick(int position) {
                    PDReward reward = mRewards.get(position);
                    reward.setVerifying(true);
                    mAdapter.notifyItemChanged(position);
                    verifyReward(position);
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
            mSwipeRefreshLayout.addLinearLayoutManager(linearLayoutManager);

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity(), R.color.pd_wallet_list_divider_color));
            recyclerView.setAdapter(mAdapter);

            refreshWallet();
        } else {
            container.removeView(mView);
        }

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        PDLog.d(getClass(), "wallet_onResume");
        getActivity().registerReceiver(mLoggedInBroadcastReceiver, new IntentFilter(PDUIRewardsFragment.PD_LOGGED_IN_RECEIVER_FILTER));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mLoggedInBroadcastReceiver);
    }

    private final BroadcastReceiver mLoggedInBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PDLog.i(PDUIWalletFragment.class, "LoggedIn broadcast onReceive");
            refreshWallet();
        }
    };

    private void redeemReward(PDReward reward, final int position) {
        PDAPIClient.instance().redeemReward(reward.getId(), new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
//                PDLog.d(PDUIWalletFragment.class, "redeem success: " + jsonObject.toString());
                mRewards.remove(position);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void failure(int statusCode, Exception e) {
//                PDLog.d(PDUIWalletFragment.class, "redeem failed: code=" + statusCode + ", message=" + e.getMessage());
            }
        });
    }

    private void verifyReward(final int position) {
        final PDReward reward = mRewards.get(position);
        PDAPIClient.instance().verifyInstagramPostForReward(reward.getId(), new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                PDLog.d(PDUIWalletFragment.class, "verify success: " + jsonObject.toString());
                boolean success = false;
                if (jsonObject.has("data")) {
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");
                    if (dataObject.has("status")) {
                        success = dataObject.get("status").getAsString().equalsIgnoreCase("success");
                    }
                }
                if (success) {
                    reward.setInstagramVerified(true);
                } else {
                    String dialogMessage = String.format(Locale.getDefault(), "Please ensure your Instagram post includes the required hashtag '%1s'. You may edit the post and come back here to verify. Unverified rewards expire in 24 hours.", reward.getInstagramOptions().getForcedTag());
                    PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), "Instagram post not verified", dialogMessage);
                }
                reward.setVerifying(false);
                mAdapter.notifyItemChanged(position);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUIWalletFragment.class, "verify failed: code=" + statusCode + ", message=" + e.getMessage());
                reward.setVerifying(false);
                mAdapter.notifyItemChanged(position);
                String dialogMessage = String.format(Locale.getDefault(), "Please ensure your Instagram post includes the required hashtag '%1s'. You may edit the post and come back here to verify. Unverified rewards expire in 24 hours.", reward.getInstagramOptions().getForcedTag());
                PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), "Instagram post not verified", dialogMessage);
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
                // If a reward has been revoked, remove it from the users wallet
                int verifyingRewardIndex = -1;
                for (Iterator<PDReward> iterator = pdRewards.iterator(); iterator.hasNext(); ) {
                    PDReward r = iterator.next();
                    if (r.getRevoked().equalsIgnoreCase("true")) {
                        iterator.remove();
                        continue;
                    }

                    if (mAutoVerifyRewardId != null && r.getId().equalsIgnoreCase(mAutoVerifyRewardId) && !r.isInstagramVerified()) {
                        r.setVerifying(true);
                        verifyingRewardIndex = pdRewards.indexOf(r);
                        mAutoVerifyRewardId = null;
                    }
                }

                mRewards.clear();
                mRewards.addAll(pdRewards);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                mNoItemsInWalletView.setVisibility(mRewards.size() == 0 ? View.VISIBLE : View.GONE);

                if (verifyingRewardIndex != -1) {
                    verifyReward(verifyingRewardIndex);
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void autoVerifyReward(String rewardId) {
        mAutoVerifyRewardId = rewardId;
        if (!mSwipeRefreshLayout.isRefreshing()) {
            refreshWallet();
        }
    }

}
