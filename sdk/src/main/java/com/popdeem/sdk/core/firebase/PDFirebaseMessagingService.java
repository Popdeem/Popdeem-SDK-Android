package com.popdeem.sdk.core.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.popdeem.sdk.BuildConfig;
import com.popdeem.sdk.R;
import com.popdeem.sdk.core.PopdeemSDK;
import com.popdeem.sdk.core.gcm.GCMIntentService;
import com.popdeem.sdk.core.gcm.PDGCMUtils;
import com.popdeem.sdk.core.realm.PDRealmGCM;
import com.popdeem.sdk.core.utils.PDLog;
import com.popdeem.sdk.core.utils.PDNumberUtils;
import com.popdeem.sdk.core.utils.PDUtils;
import com.popdeem.sdk.uikit.fragment.dialog.PDUINotificationDialogFragment;

import java.io.IOException;
import java.util.Map;

import io.realm.Realm;

public class PDFirebaseMessagingService extends FirebaseMessagingService {

    private static final String PD_SENDER_VALUE = "popdeem";
    private static final String PD_KEY_SENDER = "sender";
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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleMessage(remoteMessage);

    }



    private void handleMessage(RemoteMessage remoteMessage) {
//        Bundle extras = intent.getExtras();
        Map<String,String> map = remoteMessage.getData();
        String sender = map.get(PD_KEY_SENDER);

        if (sender != null && sender.equalsIgnoreCase(PD_SENDER_VALUE)) {

            String title = map.get(PD_KEY_TITLE);
            if(title == null || title.length() == 0){
                title = getString(R.string.app_name);
            }
            String message = map.get(PD_KEY_MESSAGE);
            String imageUrl = map.get(PD_KEY_IMAGE_URL);
            String targetUrl = map.get(PD_KEY_TARGET_URL);
            String deepLink = map.get(PD_KEY_DEEP_LINK);
            String messageId = map.get(PD_KEY_MESSAGE_ID);

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("POPDEEM", "Popdeem", NotificationManager.IMPORTANCE_DEFAULT);
            Notification notification = new NotificationCompat.Builder(this, "POPDEEM")
                        .setSmallIcon(R.drawable.ic_pd_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);

            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.notify(id, notification);
        }else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_pd_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.notify(id, notificationBuilder.build());
        }




    }
//    /**
//     * Called if InstanceID token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the InstanceID token
//     * is initially generated so this is where you would retrieve the token.
//     */
//    @Override
//    public void onNewToken(String token) {
//        PDLog.d(this.getClass(), "Refreshed token: " + token);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//
//    }

    /**
     * Initialise GCM.
     * If Google Play Services is available and a registration token is needed, one will be requested.
     *
     * @param context  Application context
     * @param callback Callback for GCM registration
     */
    public static void initGCM(Context context, PDGCMUtils.PDGCMRegistrationCallback callback) {
        if (isGooglePlayServicesAvailable(context)) {
            String token = getRegistrationToken(context);
            if (token == null || token.isEmpty()) {
                registerInBackground(context, callback);
            }
        } else {
            PDLog.d(PDGCMUtils.class, "Google Play Services is not available");
        }
    }

    /**
     * Check if Google Play Services is available
     *
     * @param context Application Context
     * @return true if Play Services are available, false if not
     */
    public static boolean isGooglePlayServicesAvailable(Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }


    private static String getGCMSenderID(Context context) {
        String senderID = PDUtils.getStringFromMetaData(context, "GCMSenderID");
        PDLog.d(PDGCMUtils.class, "senderID:" + senderID);
        return senderID;
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Save GCM Registration Token
     *
     * @param registrationToken Registration Token {@link String} to save
     * @param appVersion        Current app version
     */
    private static void saveRegistrationID(String registrationToken, int appVersion) {
        PDRealmGCM gcmRealm = new PDRealmGCM();
        gcmRealm.setId(0);
        gcmRealm.setRegistrationToken(registrationToken);
        gcmRealm.setAppVersion(appVersion);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(gcmRealm);
        realm.commitTransaction();
        realm.close();
    }


    /**
     * Get GCM Registration Token
     *
     * @param context Application context
     * @return Registration Token if there is one present for the current version of the app, empty String otherwise.
     */
    public static String getRegistrationToken(Context context) {
        Realm realm = Realm.getDefaultInstance();
        PDRealmGCM gcmRealm = realm.where(PDRealmGCM.class).findFirst();

        if (gcmRealm == null) {
            PDLog.i(PDGCMUtils.class, "no registration token saved");
            return "";
        }

        int registeredVersion = gcmRealm.getAppVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            PDLog.i(PDGCMUtils.class, "App version changed.");
            return "";
        }

        String token = gcmRealm.getRegistrationToken();
        realm.close();
        return token;
    }


    /**
     * Register GCM in background thread
     *
     * @param context  Application Context
     * @param callback Callback for registration result
     */
    private static void registerInBackground(final Context context, final PDGCMUtils.PDGCMRegistrationCallback callback) {
//        final String gcmSenderID = getGCMSenderID(context);
//        if (gcmSenderID == null) {
//            PDLog.d(PopdeemSDK.class, "Cannot register for push. GCM Sender ID was not found in AndroidManifest.xml.\n" +
//                    "Add this string resource to your project \"<string name=\"google_app_id\">YOUR_SENDER_ID</string>\" Check that: <meta-data android:name=\"" + GCM_SENDER_ID_META_DATA_PROPERTY_NAME + "\" android:value=\"@string/google_app_id\" /> is in the <application> element of your app's AndroidManifest.xml.");
//        } else {
//            new PDGCMUtils.PDGCMRegisterAsync(context, gcmSenderID, callback).execute();

        try {

            String token = FirebaseInstanceId.getInstance().getToken();
            PDLog.i(PDGCMUtils.class, "token: " + token);

            if (token != null && !token.isEmpty()) {
                saveRegistrationID(token, getAppVersion(context));
                if (callback != null) {
                    callback.success(token);
                }
            }
        }catch(Exception jo) {
            jo.printStackTrace();
        }
//
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        try {
//                            String token = task.getResult().getToken();
//                            PDLog.i(PDGCMUtils.class, "token: " + token);
//
//                            if (token != null && !token.isEmpty()) {
//                                saveRegistrationID(token, getAppVersion(context));
//                                if (callback != null) {
//                                    callback.success(token);
//                                }
//                            }
//                        }catch(Exception jo) {
//                            jo.printStackTrace();
//                        }
//                    }
//                });
//        }
    }

}
