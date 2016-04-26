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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
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

import com.google.android.gms.location.LocationListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.comparator.PDRewardDistanceComparator;
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.core.model.PDLocation;
import com.popdeem.sdk.core.model.PDReward;
import com.popdeem.sdk.core.model.PDUser;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.realm.PDRealmUserDetails;
import com.popdeem.sdk.core.realm.PDRealmUserLocation;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.activity.PDUIClaimActivity;
import com.popdeem.sdk.uikit.adapter.PDUIRewardsRecyclerViewAdapter;
import com.popdeem.sdk.uikit.fragment.dialog.PDUIProgressDialogFragment;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;
import com.popdeem.sdk.uikit.widget.PDUISwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import io.realm.Realm;

/**
 * Created by mikenolan on 19/02/16.
 */
public class PDUIRewardsFragment extends Fragment implements LocationListener {

    public static final String PD_LOGGED_IN_RECEIVER_FILTER = "com.popdeem.sdk.LOGGED_IN";
    private final int PD_CLAIM_REWARD_REQUEST_CODE = 65;

    private View mView;

    private PDUISwipeRefreshLayout mSwipeRefreshLayout;
    private View noItemsView;
    private PDUIRewardsRecyclerViewAdapter mRecyclerViewAdapter;
    private final ArrayList<PDReward> mRewards = new ArrayList<>();

    private PDLocationManager mLocationManager;
    private Location mLocation = null;

    public PDUIRewardsFragment() {
    }

    public static PDUIRewardsFragment newInstance() {
        return new PDUIRewardsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_pd_rewards, container, false);

            noItemsView = mView.findViewById(R.id.pd_rewards_no_items_view);

            final RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.pd_rewards_recycler_view);

            mRecyclerViewAdapter = new PDUIRewardsRecyclerViewAdapter(mRewards);
            mRecyclerViewAdapter.setOnItemClickListener(new PDUIRewardsRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view) {
                    if (PDSocialUtils.isLoggedInToFacebook() && PDUtils.getUserToken() != null) {
                        final int position = recyclerView.getChildAdapterPosition(view);

                        if (position == RecyclerView.NO_POSITION) {
                            return;
                        }

                        PDReward reward = mRewards.get(position);
                        if (reward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_NONE)) {
                            claimNoActionReward(position, reward.getId());
                        } else if (!reward.getAction().equalsIgnoreCase(PDReward.PD_REWARD_ACTION_SOCIAL_LOGIN)) {
                            Intent intent = new Intent(getActivity(), PDUIClaimActivity.class);
                            intent.putExtra("reward", new Gson().toJson(reward, PDReward.class));
                            startActivityForResult(intent, PD_CLAIM_REWARD_REQUEST_CODE);
                        }
                    } else {
                        PopdeemSDK.showSocialLogin(getActivity());
                    }
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity(), R.color.pd_reward_list_divider_color));
            recyclerView.setAdapter(mRecyclerViewAdapter);

            mSwipeRefreshLayout = (PDUISwipeRefreshLayout) mView;
            mSwipeRefreshLayout.addLinearLayoutManager(linearLayoutManager);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshRewards();
                }
            });

            refreshRewards();

            mLocationManager = new PDLocationManager(getActivity());
            mLocationManager.startLocationUpdates(this);
        } else {
            container.removeView(mView);
        }

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mLoggedInBroadcastReceiver, new IntentFilter(PD_LOGGED_IN_RECEIVER_FILTER));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mLoggedInBroadcastReceiver);
        if (mLocationManager != null) {
            mLocationManager.stop();
        }
    }

    private void claimNoActionReward(final int position, String rewardId) {
        final PDUIProgressDialogFragment progress = PDUIProgressDialogFragment.showProgressDialog(getChildFragmentManager(), getString(R.string.pd_common_please_wait_text), getString(R.string.pd_claim_claiming_reward_text), false, null);
        String lat = "", lng = "";

        Realm realm = Realm.getDefaultInstance();
        PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();
        if (userLocation != null) {
            lat = String.valueOf(userLocation.getLatitude());
            lng = String.valueOf(userLocation.getLongitude());
        }
        realm.close();

        PDAPIClient.instance().claimReward(getActivity(), null, null, null, rewardId, "", null, null, null, lng, lat, new PDAPICallback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject) {
                progress.dismiss();
                if (jsonObject == null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.pd_error_title_text)
                            .setMessage(R.string.pd_claim_something_went_wrong_string)
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                } else if (jsonObject.has("error")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.pd_error_title_text)
                            .setMessage(jsonObject.get("error").getAsString())
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                } else {
                    mRewards.remove(position);
                    mRecyclerViewAdapter.notifyItemRemoved(position);
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.pd_claim_reward_claimed_text)
                            .setMessage(R.string.pd_claim_reward_claimed_success_text)
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                progress.dismiss();
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.pd_error_title_text)
                        .setMessage(R.string.pd_claim_something_went_wrong_string)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });
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
                noItemsView.setVisibility(mRewards.size() == 0 ? View.VISIBLE : View.GONE);

                if (mLocation == null) {
                    Realm realm = Realm.getDefaultInstance();
                    PDRealmUserLocation userLocation = realm.where(PDRealmUserLocation.class).findFirst();
                    if (userLocation != null) {
                        mLocation = new Location("");
                        mLocation.setLongitude(userLocation.getLongitude());
                        mLocation.setLatitude(userLocation.getLatitude());
                    }
                    realm.close();
                }
                if (mLocation != null) {
                    updateListDistances(mLocation);
                }
            }

            @Override
            public void failure(int statusCode, Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateSavedUserLocation(Location location) {
        PDRealmUserLocation userLocation = new PDRealmUserLocation(location.getLatitude(), location.getLongitude());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userLocation);
        realm.commitTransaction();
        realm.close();
    }

    private void updateListDistances(final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Location rewardLocation;
                for (PDReward reward : mRewards) {
                    for (PDLocation loc : reward.getLocations()) {
                        double lat = PDNumberUtils.toDouble(loc.getLatitude(), -1);
                        double lng = PDNumberUtils.toDouble(loc.getLongitude(), -1);

                        if (lat == -1 || lng == -1) {
                            if (reward.getDistanceFromUser() <= 0) {
                                reward.setDistanceFromUser(-1);
                            }
                            continue;
                        }

                        rewardLocation = new Location("");
                        rewardLocation.setLatitude(lat);
                        rewardLocation.setLongitude(lng);

                        float distanceInMeters = location.distanceTo(rewardLocation);
                        if (reward.getDistanceFromUser() == 0 || reward.getDistanceFromUser() > distanceInMeters) {
                            reward.setDistanceFromUser(distanceInMeters);
                        }
                    }
                }

                synchronized (mRewards) {
                    Collections.sort(mRewards, new PDRewardDistanceComparator());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mRecyclerViewAdapter != null) {
                                    mRecyclerViewAdapter.notifyItemRangeChanged(0, mRewards.size() - 1);
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private final BroadcastReceiver mLoggedInBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PDLog.i(PDUIRewardsFragment.class, "LoggedIn broadcast onReceive");
            refreshRewards();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PD_CLAIM_REWARD_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Reward claimed successfully. Remove from list.
            String id = data.getStringExtra("id");
            if (id != null) {
                for (Iterator<PDReward> it = mRewards.iterator(); it.hasNext(); ) {
                    PDReward r = it.next();
                    if (r.getId().equalsIgnoreCase(id)) {
                        int position = mRewards.indexOf(r);
                        it.remove();
                        mRecyclerViewAdapter.notifyItemRemoved(position);
                        break;
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.mLocation = location;
            mLocationManager.stop();

            PDLog.d(getClass(), "location: " + location.toString());
            updateSavedUserLocation(location);

            if (mRecyclerViewAdapter.getItemCount() > 0) {
                updateListDistances(location);
            }

            Realm realm = Realm.getDefaultInstance();
            PDRealmUserDetails userDetails = realm.where(PDRealmUserDetails.class).findFirst();
            String id = userDetails == null ? null : userDetails.getId();

            PDRealmGCM gcmRealm = realm.where(PDRealmGCM.class).findFirst();
            String token = gcmRealm == null ? "" : gcmRealm.getRegistrationToken();

            realm.close();

            if (id != null) {
                PDAPIClient.instance().updateUserLocationAndDeviceToken(id, token, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new PDAPICallback<PDUser>() {
                    @Override
                    public void success(PDUser user) {
                        PDLog.d(PDUIRewardsFragment.class, "user: " + user.toString());
                    }

                    @Override
                    public void failure(int statusCode, Exception e) {
                        PDLog.w(PDUIRewardsFragment.class, "code=" + statusCode + ", e=" + e.getMessage());
                    }
                });
            }
        }
    }
}
