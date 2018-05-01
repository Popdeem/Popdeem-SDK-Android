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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.popdeem.sdk.R;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.api.PDAPICallback;
import com.popdeem.sdk.core.api.PDAPIClient;
import com.popdeem.sdk.core.api.response.PDBasicResponse;
import com.popdeem.sdk.core.model.PDMessage;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDSocialUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.adapter.PDUIMessagesRecyclerAdapter;
import com.popdeem.sdk.uikit.widget.PDUIDividerItemDecoration;
import com.popdeem.sdk.uikit.widget.PDUISwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PDUIInboxFragment extends Fragment {

    public interface InboxItemClickListener {
        void itemClicked(PDMessage message);
    }

    private PDUISwipeRefreshLayout mSwipeRefreshLayout;
    private PDUIMessagesRecyclerAdapter mAdapter;
    private ArrayList<PDMessage> mMessages = new ArrayList<>();
    private View mNoMessagesView;

    private InboxItemClickListener mListener;

    public PDUIInboxFragment() {
        // Required empty public constructor
    }

    public static PDUIInboxFragment newInstance(InboxItemClickListener listener) {
        PDUIInboxFragment fragment = new PDUIInboxFragment();
        fragment.addInboxItemClickListener(listener);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_inbox, container, false);

        mNoMessagesView = view.findViewById(R.id.pd_inbox_no_items_view);
        mSwipeRefreshLayout = (PDUISwipeRefreshLayout) view.findViewById(R.id.pd_inbox_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMessages();
            }
        });

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pd_inbox_recycler_view);

        mAdapter = new PDUIMessagesRecyclerAdapter(mMessages);
        mAdapter.setOnItemClickListener(new PDUIMessagesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                final int position = recyclerView.getChildAdapterPosition(view);

                PDMessage message = mMessages.get(position);

                markMessageAsRead(message.getId());
                message.setRead(true);
                mAdapter.notifyItemChanged(position);

                if (mListener != null) {
                    mListener.itemClicked(message);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        mSwipeRefreshLayout.addLinearLayoutManager(linearLayoutManager);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new PDUIDividerItemDecoration(getActivity(), R.color.pd_inbox_list_divider_color));
        recyclerView.setAdapter(mAdapter);

        refreshMessages();

        return view;
    }

    private void addInboxItemClickListener(InboxItemClickListener listener) {
        this.mListener = listener;
    }

    private void markMessageAsRead(long messageId) {
        PDAPIClient.instance().markMessageAsRead(String.valueOf(messageId), new PDAPICallback<PDBasicResponse>() {
            @Override
            public void success(PDBasicResponse response) {
                PDLog.d(PDUIInboxFragment.class, "message read: " + response.toString());
            }

            @Override
            public void failure(int statusCode, Exception e) {
                PDLog.d(PDUIInboxFragment.class, "message read failed: code=" + statusCode + ", message=" + e.getMessage());
            }
        });
    }

    private final Comparator<PDMessage> MESSAGES_COMPARATOR = new Comparator<PDMessage>() {
        @Override
        public int compare(PDMessage lhs, PDMessage rhs) {
            return (int) (rhs.getCreatedAt() - lhs.getCreatedAt());
        }
    };

    private void refreshMessages() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        PDAPIClient.instance().getPopdeemMessages(new PDAPICallback<ArrayList<PDMessage>>() {
            @Override
            public void success(ArrayList<PDMessage> messages) {
                PDLog.d(PDUIInboxFragment.class, "message count: " + messages.size());
                mMessages.clear();
                mMessages.addAll(messages);
                Collections.sort(mMessages, MESSAGES_COMPARATOR);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                mNoMessagesView.setVisibility(mMessages.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void failure(int statusCode, Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (PDUtils.getUserToken() != null && PDSocialUtils.isLoggedInToFacebook()) {
            inflater.inflate(R.menu.menu_pd_inbox, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_pd_logout) {
            new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                    .setTitle(R.string.pd_common_logout_text)
                    .setMessage(R.string.pd_common_logout_message_text)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PopdeemSDK.logout(getActivity());
                            getActivity().finish();
                        }
                    })
                    .create()
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
