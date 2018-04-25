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

package com.popdeem.sdk.core.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.uikit.fragment.dialog.PDUINotificationDialogFragment;

/**
 * Created by mikenolan on 24/02/16.
 */
public class GCMIntentService extends IntentService {

    private static final String PD_SENDER_VALUE = "popdeem";
    private static final String PD_KEY_TITLE = "title";
    private static final String PD_KEY_MESSAGE = "message";
    private static final String PD_KEY_TARGET_URL = "target_url";
    private static final String PD_KEY_MESSAGE_ID = "message_id";
    private static final String PD_KEY_DEEP_LINK = "deep_link";
    private static final String PD_KEY_IMAGE_URL = "image_url";

    public static final String PD_NOTIFICATION_INTENT_MESSAGE_ID_KEY = PD_SENDER_VALUE + "." + PD_KEY_MESSAGE_ID;
    public static final String PD_NOTIFICATION_INTENT_URL_KEY = PD_SENDER_VALUE + "." + PD_KEY_TARGET_URL;
    public static final String PD_NOTIFICATION_INTENT_IMAGE_URL_KEY = PD_SENDER_VALUE + "." + PD_KEY_IMAGE_URL;
    public static final String PD_NOTIFICATION_INTENT_TITLE_KEY = PD_SENDER_VALUE + "." + PD_KEY_TITLE;
    public static final String PD_NOTIFICATION_INTENT_MESSAGE_KEY = PD_SENDER_VALUE + "." + PD_KEY_MESSAGE;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GCMIntentService() {
        super(GCMIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PDLog.d(GCMIntentService.class, "onHandleIntent");
        if (intent == null) {
            return;
        }

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty() && messageType.equalsIgnoreCase(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
            handleMessage(intent);
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void handleMessage(Intent intent) {
        Bundle extras = intent.getExtras();

        final String sender = extras.getString("sender", "");
        if (sender.equalsIgnoreCase(PD_SENDER_VALUE)) {
            PDLog.d(GCMIntentService.class, extras.toString());

            // Get extras
            String title = extras.getString(PD_KEY_TITLE, getString(R.string.app_name));
            String message = extras.getString(PD_KEY_MESSAGE, "");
            String imageUrl = extras.getString(PD_KEY_IMAGE_URL, "");
            String targetUrl = extras.getString(PD_KEY_TARGET_URL, null);
            String deepLink = extras.getString(PD_KEY_DEEP_LINK, null);
            String messageId = extras.getString(PD_KEY_MESSAGE_ID, null);

            // If app is open, show a dialog, else show a notification
            if (PopdeemSDK.currentActivity() != null) {
                if (PopdeemSDK.currentActivity() instanceof FragmentActivity || PopdeemSDK.currentActivity() instanceof AppCompatActivity) {
                    FragmentManager fm = ((FragmentActivity) PopdeemSDK.currentActivity()).getSupportFragmentManager();
                    PDUINotificationDialogFragment.showNotificationDialog(fm, title, message, imageUrl, targetUrl, deepLink, messageId);
                }
            } else {
                // Create Pending Intent for Notification
                PackageManager pm = getPackageManager();
                Intent openAppIntent = pm.getLaunchIntentForPackage(getPackageName());
                openAppIntent.putExtra(PD_NOTIFICATION_INTENT_MESSAGE_ID_KEY, messageId);
                openAppIntent.putExtra(PD_NOTIFICATION_INTENT_URL_KEY, targetUrl != null ? targetUrl : deepLink);
                openAppIntent.putExtra(PD_NOTIFICATION_INTENT_IMAGE_URL_KEY, imageUrl);
                openAppIntent.putExtra(PD_NOTIFICATION_INTENT_TITLE_KEY, title);
                openAppIntent.putExtra(PD_NOTIFICATION_INTENT_MESSAGE_KEY, message);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, 0);

                int id = (int) PDNumberUtils.toLong(messageId, 1);
                sendNotification(title, message, pendingIntent, id);
            }
        }
    }

    private void sendNotification(String title, String message, PendingIntent pendingIntent, int id) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pd_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notificationBuilder.build());
    }
}
