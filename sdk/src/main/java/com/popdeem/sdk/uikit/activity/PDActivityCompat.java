package com.popdeem.sdk.uikit.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by colm on 17/07/2018.
 */

public class PDActivityCompat extends AppCompatActivity {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentByTag("PDUISocialMultiLoginFragment");
        if(frag!=null){
            frag.onActivityResult(requestCode, resultCode, data);
        }
    }

}
