package com.popdeem.sdk.uikit.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;

/**
 * Created by colm on 17/07/2018.
 */

public class PDActivity extends Activity {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        FragmentManager fm = getFragmentManager();
        fm.findFragmentByTag("PDUISocialMultiLoginFragment").onActivityResult(requestCode, resultCode, data);
    }

}
