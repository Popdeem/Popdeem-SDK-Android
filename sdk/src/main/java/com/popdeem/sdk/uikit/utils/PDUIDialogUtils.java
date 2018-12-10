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

package com.popdeem.sdk.uikit.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.popdeem.sdk.R;

/**
 * Created by mikenolan on 10/08/16.
 */
public class PDUIDialogUtils {

    public static void showBasicOKAlertDialog(Context context, @StringRes int titleRes, String message) {
        if (context == null) {
            return;
        }
        String title = context.getString(titleRes);
        showBasicOKAlertDialog(context, title, message);
    }

    public static void showBasicOKAlertDialog(Context context, @StringRes int titleRes, @StringRes int messageRes) {
        if (context == null) {
            return;
        }
        String title = context.getString(titleRes);
        String message = context.getString(messageRes);
        showBasicOKAlertDialog(context, title, message);
    }

    public static void showBasicOKAlertDialog(Context context, String title, String message) {
        if (context == null) {
            return;
        }
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    public static void showBasicOKAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener listener ) {
        if (context == null) {
            return;
        }
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .create()
                .show();
    }

    public static Dialog setMargins(Dialog dialog, int marginLeft, int marginTop, int marginRight, int marginBottom )
    {
        Window window = dialog.getWindow();
        if ( window == null )
        {
            // dialog window is not available, cannot apply margins
            return dialog;
        }
        Context context = dialog.getContext();

        // set dialog to fullscreen
        RelativeLayout root = new RelativeLayout( context );
        root.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( root );
        // set background to get rid of additional margins
        window.setBackgroundDrawable( new ColorDrawable( Color.WHITE ) );

        // apply left and top margin directly
        window.setGravity( Gravity.LEFT | Gravity.TOP );
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = dpToPx(marginLeft);
        attributes.y = dpToPx(marginTop);
        window.setAttributes( attributes );

        // set right and bottom margin implicitly by calculating width and height of dialog
        Point displaySize = getDisplayDimensions( context );
        int width = displaySize.x - dpToPx(marginLeft) - dpToPx(marginRight);
        int height = displaySize.y - dpToPx(marginTop) - dpToPx(marginBottom);
        window.setLayout( width, height );

        return dialog;
    }

    @NonNull
    public static Point getDisplayDimensions( Context context )
    {
        WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics( metrics );
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // find out if status bar has already been subtracted from screenHeight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics( metrics );
        }
        int physicalHeight = metrics.heightPixels;
        int statusBarHeight = getStatusBarHeight( context );
        int navigationBarHeight = getNavigationBarHeight( context );
        int heightDelta = physicalHeight - screenHeight;
        if ( heightDelta == 0 || heightDelta == navigationBarHeight )
        {
            screenHeight -= statusBarHeight;
        }

        return new Point( screenWidth, screenHeight );
    }

    public static int getStatusBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }

    public static int getNavigationBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }


    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
