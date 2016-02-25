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
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.uikit.fragment.dialog.PDUINotificationDialogFragment;

/**
 * Created by mikenolan on 24/02/16.
 */
public class GCMIntentService extends IntentService {

    private final String NOTIFICATION_KEYS_PREFIX = "gcm.notification.";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GCMIntentService() {
        super(GCMIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(GCMIntentService.class.getSimpleName(), "onHandleIntent");
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

        String sender = extras.getString("sender", extras.getString(NOTIFICATION_KEYS_PREFIX + "sender", ""));
        if (sender.equalsIgnoreCase("popdeem")) {
            Log.d(GCMIntentService.class.getSimpleName(), extras.toString());

            String title = extras.getString("title", extras.getString(NOTIFICATION_KEYS_PREFIX + "title", getString(R.string.app_name)));
            String message = extras.getString("message", extras.getString(NOTIFICATION_KEYS_PREFIX + "message", null));
            sendNotification(title, message, 1);

            if (PopdeemSDK.currentActivity() != null) {
                // App is in the Foreground. Show Dialog.
//                Log.d(GCMIntentService.class.getSimpleName(), "app in fg");
                if (PopdeemSDK.currentActivity() instanceof AppCompatActivity) {
                    FragmentManager fm = ((AppCompatActivity) PopdeemSDK.currentActivity()).getSupportFragmentManager();
                    Fragment prev = fm.findFragmentByTag(PDUINotificationDialogFragment.class.getSimpleName());
                    if (prev != null) {
                        fm.beginTransaction().remove(prev).addToBackStack(null).commit();
                    }

                    PDUINotificationDialogFragment dialog = PDUINotificationDialogFragment.newInstance(title, message);
                    if (dialog.isCreated()) {
                        dialog.show(fm, PDUINotificationDialogFragment.class.getSimpleName());
                    }
                }
            }
//            else {
//                // App is in the background
//                Log.d(GCMIntentService.class.getSimpleName(), "app in bg");
//            }
        }
    }

    private void sendNotification(String title, String message, int id) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pd_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
//                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notificationBuilder.build());
    }
}
