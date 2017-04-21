package com.popdeem.sdk.uikit.fragment.multilogin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.api.abra.PDAbraConfig;
import com.popdeem.sdk.core.api.abra.PDAbraLogEvent;
import com.popdeem.sdk.core.api.abra.PDAbraProperties;
import com.popdeem.sdk.core.location.PDLocationManager;
import com.popdeem.sdk.uikit.utils.PDUIColorUtils;

/**
 * Created by dave on 21/04/2017.
 * Project: Popdeem-SDK-Android
 */

public class PDUISocialMultiLoginFragment extends Fragment implements View.OnClickListener {

    private static String TAG = PDUISocialMultiLoginFragment.class.getSimpleName();

    private View view;

    private final int LOCATION_PERMISSION_REQUEST = 90;

    private PDLocationManager mLocationManager;

    private ProgressBar mProgressFacebook;
    private ProgressBar mProgressTwitter;
    private ProgressBar mProgressInstagram;

    private TextView mRewardsInfoTextView;

    private Button mContinueButton;

    private Button mFacebookLoginButton;
    private Button mTwitterLoginButton;
    private Button mInstaLoginButton;

    private CallbackManager mCallbackManager;

    private boolean mAskForPermission = true;

    public PDUISocialMultiLoginFragment() {
    }

    public static PDUISocialMultiLoginFragment newInstance() {
        return new PDUISocialMultiLoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pd_social_multi_login, container, false);


        setupBackButton();
        setupSocialButtons();






        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_PAGE_VIEWED, new PDAbraProperties.Builder()
                .add(PDAbraConfig.ABRA_PROPERTYNAME_SOURCE_PAGE, PDAbraConfig.ABRA_PROPERTYVALUE_PAGE_LOGINTAKEOVER)
                .create());
    }

    ////////////////////////////////////////////////////
    // Social Login Buttons
    //////////////////////////////////////////////////

    private void setupSocialButtons(){
        mFacebookLoginButton = (Button) view.findViewById(R.id.pd_facebook_login_button);
        mTwitterLoginButton = (Button) view.findViewById(R.id.pd_twitter_login_button);
        mInstaLoginButton = (Button) view.findViewById(R.id.pd_instagram_login_button);

        mProgressFacebook = (ProgressBar) view.findViewById(R.id.pd_progress_bar);
        mProgressTwitter = (ProgressBar) view.findViewById(R.id.pd_progress_bar_twitter);
        mProgressInstagram = (ProgressBar) view.findViewById(R.id.pd_progress_bar_instagram);
    }


    ////////////////////////////////////////////////////
    // Social Login Buttons Click Listeners
    //////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        final int ID = v.getId();

        if (ID == R.id.pd_facebook_login_button){
            Log.i(TAG, "onClick: Facebook Login Selected");

        } else if (ID == R.id.pd_twitter_login_button){
            Log.i(TAG, "onClick: Twitter Login Selected");

        } else if (ID == R.id.pd_instagram_login_button){
            Log.i(TAG, "onClick: Instagram Login Selected");
        }
    }

    ////////////////////////////////////////////////////
    // Back Button
    //////////////////////////////////////////////////

    private void setupBackButton(){
        ImageButton backButton = (ImageButton) view.findViewById(R.id.pd_social_login_back_button);
        backButton.setImageDrawable(PDUIColorUtils.getSocialLoginBackButtonIcon(getActivity()));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgressFacebook.getVisibility() == View.GONE && mProgressTwitter.getVisibility() == View.GONE && mProgressInstagram.getVisibility() == View.GONE) {
                    PDAbraLogEvent.log(PDAbraConfig.ABRA_EVENT_CLICKED_CLOSE_LOGIN_TAKEOVER, new PDAbraProperties.Builder()
                            .add("Source", "Dismiss Button")
                            .create());
                    removeThisFragment();
                }
            }
        });
    }

    ////////////////////////////////////////////////////
    // Pop this fragment off the stack               //
    //////////////////////////////////////////////////

    public void removeThisFragment() {
        getActivity().getSupportFragmentManager().popBackStack(PDUISocialMultiLoginFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
