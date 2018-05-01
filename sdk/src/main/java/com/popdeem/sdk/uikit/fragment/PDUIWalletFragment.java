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
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.comparator.PDRewardComparator;
import com.popdeem.sdk.core.deserializer.PDRewardDeserializer;
import com.popdeem.sdk.core.model.PDEvent;
import com.popdeem.sdk.core.model.PDMessage;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.uikit.activity.PDUIRedeemActivity;
import com.popdeem.sdk.uikit.adapter.PDUIWalletRecyclerViewAdapter;
import com.popdeem.sdk.uikit.fragment.dialog.PDUIGratitudeDialog;
import com.popdeem.sdk.uikit.utils.PDUIDialogUtils;
import com.popdeem.sdk.uikit.utils.PDUIUtils;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;
import com.popdeem.sdk.uikit.widget.PDUILinearLayoutManager;
import com.popdeem.sdk.uikit.widget.PDUISwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import io.realm.Realm;

import static com.popdeem.sdk.uikit.utils.PDUIImageUtils.deleteDirectoryTree;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIWalletFragment extends Fragment {


    private View mView;
    private Realm realm;

    private PDUISwipeRefreshLayout mSwipeRefreshLayout;
    private PDUIWalletRecyclerViewAdapter mAdapter;
    private ArrayList<Object> mRewards = new ArrayList<>();
    private View mNoItemsInWalletView;

    private String mAutoVerifyRewardId = null;
    private boolean finishedWallet = false;
    private boolean finishedMessages = false;

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
            mNoItemsInWalletView.setVisibility(View.GONE);
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
                    final int position = recyclerView.getChildAdapterPosition(v) - 1;

                    if(mRewards.get(position) instanceof PDReward) {
                        final PDReward reward = (PDReward) mRewards.get(position);
                        if (reward.claimedUsingNetwork(PDReward.PD_SOCIAL_MEDIA_TYPE_INSTAGRAM) && !reward.isInstagramVerified()) {
                            return;
                        }

                        if (reward.getCredit() != null && reward.getCredit().length() > 0) {
                            return;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
                        if (reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_SWEEPSTAKE)) {
                            String message = String.format(Locale.getDefault(), "%1s", getString(R.string.pd_redeem_sweepstake_reward_info_message_string));
                            builder.setTitle(R.string.pd_redeem_sweepstake_reward_info_title_string)
                                    .setMessage(message)
                                    .setPositiveButton(android.R.string.ok, null);
                            builder.create().show();
                        } else if (!reward.getRewardType().equalsIgnoreCase(PDReward.PD_REWARD_TYPE_CREDIT)) {
                            String message = getString(R.string.pd_wallet_coupon_info_message_text);
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
                }

                @Override
                public void onVerifyClick(int position) {
                    PDReward reward = (PDReward) mRewards.get(position);
                    reward.setVerifying(true);
//                    mAdapter.notifyItemChanged(position);
                    mAdapter.notifyDataSetChanged();
                    verifyReward(position);
                }
            });

//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
            PDUILinearLayoutManager linearLayoutManager = new PDUILinearLayoutManager(container.getContext());
            mSwipeRefreshLayout.addLinearLayoutManager(linearLayoutManager);

            recyclerView.setLayoutManager(linearLayoutManager);
//            recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity(), R.color.pd_wallet_list_divider_color, false));
            recyclerView.setAdapter(mAdapter);

//            refreshWallet();
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
        refreshWallet();
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

    private void redeemReward(final PDReward reward, final int position) {
        PDAPIClient.instance().redeemReward(reward.getId(), new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_REDEEMED_REWARD, new PDAbraProperties.Builder()
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_REWARD_NAME, reward.getDescription())
                        .add(PDAbraConfig.ABRA_PROPERTYNAME_REWARD_ID, reward.getId())
                        .create());
                mRewards.remove(position);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void failure(int statusCode, Exception e) {
            }
        });
    }

    private void verifyReward(final int position) {
        final PDReward reward = (PDReward) mRewards.get(position);
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
//                mAdapter.notifyItemChanged(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUIWalletFragment.class, "verify failed: code=" + statusCode + ", message=" + e.getMessage());
                reward.setVerifying(false);
//                mAdapter.notifyItemChanged(position);
                mAdapter.notifyDataSetChanged();
                String dialogMessage = String.format(Locale.getDefault(), "Please ensure your Instagram post includes the required hashtag '%1s'. You may edit the post and come back here to verify. Unverified rewards expire in 24 hours.", reward.getInstagramOptions().getForcedTag());
                PDUIDialogUtils.showBasicOKAlertDialog(getActivity(), "Instagram post not verified", dialogMessage);
            }
        });
    }

    private void refreshWallet() {
//        deleteDirectoryTree(getActivity());
        Log.i("PDUIWalletFragment", "Refreshing Wallet");
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        finishedWallet = false;
        finishedMessages = false;

        final PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
        if (userDetails == null) {
            Log.i("PDUIWalletFragment", "refreshWallet: user is Null, clearing list");
            mRewards.clear();
            mAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
//            mNoItemsInWalletView.setVisibility(mRewards.size() == 0 ? View.VISIBLE : View.GONE);
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("PDUIWalletFragment", "Removing refresh");
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            Log.i("PDUIWalletFragment", "refreshWallet: user exists");
//            PDAPIClient.instance().getRewardsInWallet(new PDAPICallback<ArrayList<PDReward>>() {

            PDAPIClient.instance().getUserDetailsForId(userDetails.getId(), new PDAPICallback<JsonObject>() {

                @Override
                public void success(JsonObject jsonObject) {
                    if(jsonObject.get("status").getAsString().equalsIgnoreCase("User Data")){
                        JsonObject user = jsonObject.getAsJsonObject("user");
                        float score = Float.valueOf(user.get("advocacy_score").getAsString());

                        realm.beginTransaction();
                        userDetails.setAdvocacyScore(score);
                        realm.commitTransaction();
                        mAdapter.notifyDataSetChanged();
                    }


                }

                @Override
                public void failure(int statusCode, Exception e) {

                }
            });
            PDAPIClient.instance().getRewardsInWallet(new PDAPICallback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject) {
                    JsonArray jsonArray = jsonObject.getAsJsonArray("rewards");
                    ArrayList<PDReward> pdRewards = new ArrayList<PDReward>();
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(PDReward.class, new PDRewardDeserializer());

                    Gson gson = gsonBuilder.create();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        PDReward reward = gson.fromJson(jsonArray.get(i), PDReward.class);
                        pdRewards.add(reward);
                    }

                    // Sort rewards
                    Collections.sort(pdRewards, new PDRewardComparator(PDRewardComparator.CLAIMED_AT_COMPARATOR));

                    // If a reward has been revoked, remove it from the users wallet
                    int verifyingRewardIndex = -1;
                    for (Iterator<PDReward> iterator = pdRewards.iterator(); iterator.hasNext(); ) {
                        PDReward r = iterator.next();
                        if (r.getRevoked().equalsIgnoreCase("true")) {
                            iterator.remove();
                            continue;
                        }

//                        if (mAutoVerifyRewardId != null && r.getId().equalsIgnoreCase(mAutoVerifyRewardId) && !r.isInstagramVerified()) {
//                            r.setVerifying(true);
//                            verifyingRewardIndex = pdRewards.indexOf(r);
//                            mAutoVerifyRewardId = null;
//                        }
                    }

                    JsonArray jsonArrayTiers = jsonObject.getAsJsonArray("tiers");
                    ArrayList<PDEvent> pdEvents = new ArrayList<PDEvent>();

                    Gson gsonEvents = new Gson();

                    for (int i = 0; i < jsonArrayTiers.size(); i++) {
                        PDEvent event = gsonEvents.fromJson(jsonArrayTiers.get(i), PDEvent.class);
                        pdEvents.add(event);
                    }


                    mRewards.clear();
                    mRewards.addAll(pdEvents);
                    mRewards.addAll(pdRewards);

                    Collections.sort(mRewards, new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            long time1 = 0;
                            long time2 = 0;

                            if(o1 instanceof PDReward){
                                PDReward reward = (PDReward) o1;
                                time1 = reward.getClaimedAt();
                            }else{
                                PDEvent reward = (PDEvent) o1;
                                time1 = reward.getDate();
                            }

                            if(o2 instanceof PDReward){
                                PDReward reward = (PDReward) o2;
                                time2 = reward.getClaimedAt();
                            }else{
                                PDEvent reward = (PDEvent) o2;
                                time2 = reward.getDate();
                            }

                            if(time1<time2)
                                return 1;
                            else if(time1>time2)
                                return -1;

                            return 0;
                        }

                    });


                    mAdapter.notifyDataSetChanged();
                    finishedWallet = true;
                    if(finishedMessages&&finishedWallet) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
//                    mNoItemsInWalletView.setVisibility(mRewards.size() == 0 ? View.VISIBLE : View.GONE);

//                    if (verifyingRewardIndex != -1) {
//                        verifyReward(verifyingRewardIndex);
//                    }else{
//                        PDUIGratitudeDialog.showGratitudeDialog(getActivity(), "share");
//                    }
                }

                @Override
                public void failure(int statusCode, Exception e) {
                    finishedWallet = true;
                    if(finishedMessages&&finishedWallet) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }

        PDAPIClient.instance().getPopdeemMessages(new PDAPICallback<ArrayList<PDMessage>>() {
            @Override
            public void success(ArrayList<PDMessage> messages) {
                PDLog.d(PDUIInboxFragment.class, "message count: " + messages.size());
                ArrayList<PDMessage> mMessages = new ArrayList<>();
                mMessages.addAll(messages);
                int unread = 0;
                for (int i = 0; i < messages.size(); i++) {
                    if(!messages.get(i).isRead()){
                        unread++;
                    }
                }
                setBadges(unread);

                finishedMessages = true;
                if(finishedMessages&&finishedWallet) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                finishedMessages = true;
                if(finishedMessages&&finishedWallet) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void setBadges(int badges) {

        if (getParentFragment() != null && getParentFragment() instanceof PDUIHomeFlowFragment) {
            PDUIHomeFlowFragment parent = (PDUIHomeFlowFragment) getParentFragment();
            parent.setProfileBadge(badges);
        }

        mAdapter.setMessagesCount(badges);

        mAdapter.notifyDataSetChanged();
    }



    public void autoVerifyReward(String rewardId) {
//        mAutoVerifyRewardId = rewardId;
        if (!mSwipeRefreshLayout.isRefreshing()) {
            refreshWallet();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }


}
