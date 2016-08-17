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

package com.popdeem.sdk.uikit.fragment.dialog;

import android.support.v4.app.DialogFragment;

/**
 * Created by mikenolan on 05/08/16.
 */
@Deprecated
public class PDUIConnectSocialAccountDialogFragment extends DialogFragment {

    //    public interface ConnectSocialAccountCallback {
//        void connectClick();
//
//        void dialogDismissed();
//    }
//
////    @IntDef({PD_CONNECT_FACEBOOK_DIALOG, PD_CONNECT_TWITTER_DIALOG, PD_CONNECT_INSTAGRAM_DIALOG})
////    @Retention(RetentionPolicy.SOURCE)
////    public @interface PDConnectSocialAccountType {
////    }
////
////    public static final int PD_CONNECT_FACEBOOK_DIALOG = 0;
////    public static final int PD_CONNECT_TWITTER_DIALOG = 1;
////    public static final int PD_CONNECT_INSTAGRAM_DIALOG = 2;
//
//    public static void showDialog(FragmentManager fm, /*@PDConnectSocialAccountType*/ int type, @NonNull ConnectSocialAccountCallback callback) {
//        Fragment prev = fm.findFragmentByTag(getName());
//        if (prev != null) {
//            fm.beginTransaction().remove(prev).addToBackStack(null).commit();
//        }
//
//        PDUIConnectSocialAccountDialogFragment dialog = PDUIConnectSocialAccountDialogFragment.newInstance(type, callback);
//        dialog.show(fm, getName());
//    }
//
//    public static PDUIConnectSocialAccountDialogFragment newInstance(/*@PDConnectSocialAccountType*/ int type, @NonNull ConnectSocialAccountCallback callback) {
//        Bundle args = new Bundle();
//        args.putInt("type", type);
//
//        PDUIConnectSocialAccountDialogFragment fragment = new PDUIConnectSocialAccountDialogFragment();
//        fragment.setCallback(callback);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
////    private int mType = PD_CONNECT_FACEBOOK_DIALOG;
//    private ConnectSocialAccountCallback mCallback;
//    private boolean mUserClickConnect = false;
//
    public PDUIConnectSocialAccountDialogFragment() {
    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        Bundle args = getArguments();
////        if (args != null) {
////            mType = args.getInt("type", PD_CONNECT_FACEBOOK_DIALOG);
////        }
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pd_connect_social_account_dialog, null, false);
//        TextView messageTextView = (TextView) dialogView.findViewById(R.id.pd_connect_dialog_title_text_view);
//        messageTextView.setText(getMessageText());
//
//        ImageView imageView = (ImageView) dialogView.findViewById(R.id.pd_connect_dialog_network_image_view);
//        imageView.setImageResource(getImageViewDrawable());
//
//        final Button button = (Button) dialogView.findViewById(R.id.pd_connect_dialog_button);
//        button.setText(getButtonText());
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
//                .setView(dialogView);
//
//        final AlertDialog dialog = builder.create();
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mUserClickConnect = true;
//                dialog.dismiss();
//                mCallback.connectClick();
//            }
//        });
//
//        return dialog;
//    }
//
//    public void setCallback(@NonNull ConnectSocialAccountCallback callback) {
//        this.mCallback = callback;
//    }
//
//    private String getMessageText() {
//        String network = "";
////        if (mType == PD_CONNECT_FACEBOOK_DIALOG) {
////            network = getString(R.string.pd_connect_facebook_title);
////        } else if (mType == PD_CONNECT_TWITTER_DIALOG) {
////            network = getString(R.string.pd_connect_twitter_title);
////        } else if (mType == PD_CONNECT_INSTAGRAM_DIALOG) {
////            network = getString(R.string.pd_connect_instagram_title);
////        }
//        return getString(R.string.pd_connect_dialog_message_text, network);
//    }
//
//    @DrawableRes
//    private int getImageViewDrawable() {
////        if (mType == PD_CONNECT_FACEBOOK_DIALOG) {
////            return R.drawable.pd_facebook_icon;
////        } else if (mType == PD_CONNECT_TWITTER_DIALOG) {
////            return R.drawable.pd_twitter_icon;
////        } else {
////            return R.drawable.pd_instagram_icon;
////        }
//        return 0;
//    }
//
//    private String getButtonText() {
//        String network = "";
////        if (mType == PD_CONNECT_FACEBOOK_DIALOG) {
////            network = getString(R.string.pd_connect_facebook_title);
////        } else if (mType == PD_CONNECT_TWITTER_DIALOG) {
////            network = getString(R.string.pd_connect_twitter_title);
////        } else if (mType == PD_CONNECT_INSTAGRAM_DIALOG) {
////            network = getString(R.string.pd_connect_instagram_title);
////        }
//        return getString(R.string.pd_connect_dialog_button_text, network);
//    }
//
//    public static String getName() {
//        return PDUIConnectSocialAccountDialogFragment.class.getSimpleName();
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        if (!mUserClickConnect) {
//            mCallback.dialogDismissed();
//        }
//    }

}
