## Getting Started

### Dependency

The Popdeem Android SDK is available through jcenter and mavenCentral.
Add the following line to your dependencies block of your applications `build.gradle` file and Gradle Sync your project to download the SDK dependency:

```
compile 'com.popdeem.sdk:sdk:0.1.20'
```

### Initialise SDK

#### API Key

Add your Popdeem API Key to your applications `AndroidManifest.xml`  inside the `<application>` tags:

```
<meta-data
    android:name="com.popdeem.sdk.ApiKey"
    android:value="YOUR_POPDEEM_API_KEY" />
```

#### Permissions

Add the following permissions to your applications `AndroidManifest.xml` if they are not already declared.

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

#### Broadcast Features

To use the Popdeem Push Notification and Broadcast feature, your application must have GCM implemented.
You can follow this [tutorial](https://developers.google.com/cloud-messaging/android/start "Android GCM") to implement GCM in your application.

Once you have implemented GCM, include the following:

Add your Google App ID to your applications `strings.xml`.
_This can be found in your [Google Console Dashboard](https://console.cloud.google.com/home/dashboard "Google Console") for your app under the **Project Number** heading._

```
<string name="google_app_id">YOUR_GOOGLE_APP_ID</string>
```

Then reference this in your `AndroidManifest.xml` inside the `<application>` tags:
```
<meta-data
    android:name="GCMSenderID"
    android:value="@string/google_app_id" />
```

Add the following `receiver` and `service` after the lines above:
```
<receiver
    android:name="com.popdeem.sdk.core.gcm.GCMBroadcastReceiver"
    android:exported="true"
    android:permission="com.google.android.c2dm.permission.SEND">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>
</receiver>

<service
    android:name="com.popdeem.sdk.core.gcm.GCMIntentService"
    android:exported="true" />
```

#### Initialise Popdeem SDK

To initialise the Popdeem SDK add the following line to your `Application` class:
```
PopdeemSDK.initializeSDK(this);
```

Next, set up your [Facebook App](https://github.com/Popdeem/Popdeem-SDK-Android/tree/master/docs/facebook_app_setup.md "Facebook App")

---
[Docs Home](https://github.com/Popdeem/Popdeem-SDK-Android/tree/master/docs/README.md "Docs Home")
