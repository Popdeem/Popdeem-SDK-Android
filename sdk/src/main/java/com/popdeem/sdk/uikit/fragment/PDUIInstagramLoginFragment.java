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

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.model.PDInstagramResponse;
import com.popdeem.sdk.core.realm.PDRealmInstagramConfig;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.uikit.utils.PDUIColorUtils;

import java.io.IOException;
import java.util.Locale;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mikenolan on 05/08/16.
 */
public class PDUIInstagramLoginFragment extends Fragment {

    private boolean notCanceled = false;

    public interface PDInstagramLoginCallback {
        void loggedIn(PDInstagramResponse response);

        void error(String message);

        void canceled();
    }

    public static PDUIInstagramLoginFragment newInstance(@NonNull PDInstagramLoginCallback callback) {
        Bundle args = new Bundle();

        PDUIInstagramLoginFragment fragment = new PDUIInstagramLoginFragment();
        fragment.setCallback(callback);
        fragment.setArguments(args);
        return fragment;
    }

    private WebView mWebView;
    private ProgressBar mProgress;
    private String mClientId;
    private String mClientSecret;
    private String mCallbackUrl;
    private PDInstagramLoginCallback mCallback;

    public PDUIInstagramLoginFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm realm = Realm.getDefaultInstance();
        PDRealmInstagramConfig config = realm.where(PDRealmInstagramConfig.class).findFirst();
        if (config != null) {
            mClientId = config.getInstagramClientId();
            mClientSecret = config.getInstagramClientSecret();
            mCallbackUrl = config.getInstagramCallbackUrl();
        }
        realm.close();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pd_instagram_login, container, false);

        mProgress = (ProgressBar) view.findViewById(R.id.pd_instagram_login_progress_bar);
        mWebView = (WebView) view.findViewById(R.id.pd_instagram_login_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new InstagramWebViewClient());

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ActionBar actionBar = null;
            if(getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            }else{
//                toolbar.getContext().setTheme(R.style.PopdeemSDKTheme_AppBarOverlay);
                toolbar.setTitleTextAppearance(getActivity(),R.style.ToolbarTitleText);
                String title = " " + getString(R.string.pd_claim_connect_instagram_title);
                toolbar.setTitle(title);
                toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.pd_toolbar_text_color));

                int inset = (int) getResources().getDimension(R.dimen.toolbarPadding);
                for (int i = 0; i < 35; i++) {
                    Log.i("INSET", "onCreateView: " + inset);
                }
                toolbar.setContentInsetsRelative(inset, inset);
                toolbar.setContentInsetStartWithNavigation(inset);
                toolbar.setNavigationIcon(PDUIColorUtils.getTintedDrawable(getActivity(),R.drawable.pd_ic_arrow_back,R.color.pd_toolbar_text_color, false));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
            }
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(R.string.pd_claim_connect_instagram_title);
            }

        }

        loadInstagramUrl();

        return view;
    }



    public void setCallback(@NonNull PDInstagramLoginCallback callback) {
        this.mCallback = callback;
    }

    private void loadInstagramUrl() {
        clearCookies();
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.loadUrl(String.format(Locale.getDefault(),
                "https://api.instagram.com/oauth/authorize/?client_id=%1s&redirect_uri=%2s&response_type=code&scope=public_content+likes+comments+basic",
                mClientId, mCallbackUrl));
    }

    @SuppressWarnings("deprecation")
    private void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(getActivity());
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }

    private void authenticateInstagram(String code) {
        mProgress.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.INVISIBLE);

        PDLog.d(getClass(), "code=" + code);

        OkHttpClient httpClient = new OkHttpClient();
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("client_id", mClientId)
                .add("client_secret", mClientSecret)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", mCallbackUrl)
                .add("code", code);

        final Request request = new Request.Builder()
                .url("https://api.instagram.com/oauth/access_token")
                .post(bodyBuilder.build())
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PDLog.e(getClass(), "error:" + e.getLocalizedMessage());
                            mCallback.error(e.getLocalizedMessage());
                            notCanceled = true;
                            removeThisFragment();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                PDLog.d(getClass(), "response: " + responseBody);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create();
                            PDInstagramResponse instagramResponse = gson.fromJson(responseBody, PDInstagramResponse.class);
                            notCanceled = true;
                            mCallback.loggedIn(instagramResponse);
                            removeThisFragment();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onPause() {
        if(!notCanceled){
            mCallback.canceled();
        }
        super.onPause();
    }

    private class InstagramWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgress.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            PDLog.d(getClass(), url);
            if (url.startsWith(mCallbackUrl)) {
                Uri uri = Uri.parse(url);
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    authenticateInstagram(code);
                } else {
                    mCallback.error("Error logging in. Please try again.");
                }
                return true;
            }
            mProgress.setVisibility(View.VISIBLE);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public static String getName() {
        return PDUIInstagramLoginFragment.class.getSimpleName();
    }

    public void removeThisFragment() {
        if (!isAdded()) {
            return;
        }
        getActivity().getSupportFragmentManager().popBackStack(getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
