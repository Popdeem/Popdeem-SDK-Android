## Getting Started

### Dependency

The Popdeem Android SDK is available through jcenter and mavenCentral.
Add the following line to the `dependencies` block of your applications `build.gradle` file and Gradle Sync your project to download the SDK dependency:

```java
compile 'com.popdeem.sdk:sdk:1.2.2'
```

To test against the staging environment append `-STAGING` to the dependency:

```java
compile 'com.popdeem.sdk:sdk:1.2.2-STAGING'
```

---
#### NOTE: For projects that do NOT use Fabric

The Popdeem SDK uses [Fabric](https://get.fabric.io/ "Fabric") for the official Twitter SDK but due to a limitation in Fabric regarding use in Android Libraries these steps will need to be taken. Fabric have stated that official support for this is upcoming.      
_If you already use Fabric in your application, these steps do not apply to you. You can skip to the Initialise SDK section below._

If you do not use Fabric in your project you will need to add the following or you will get an error when syncing project files:

In your ***top-level*** `build.gradle` file add the lines marked in the sample below:

```java
buildscript {
    repositories {
        jcenter()
        maven { url 'https://maven.fabric.io/public' } // Add this line
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.1.0'
        classpath 'io.fabric.tools:gradle:1.+' // Add this line
    }
}

allprojects {
    repositories {
        jcenter()
        maven { url 'https://maven.fabric.io/public' } // Add this line
    }
}
```

Then in your ***application-level*** `build.gradle` file add the line marked below:

```java
apply plugin: 'com.android.application'
apply plugin: 'io.fabric' // Add this line
```
---
### Initialise SDK

#### API Key

Add your Popdeem API Key to your applications `AndroidManifest.xml`  inside the `<application>` tags:

```xml
<meta-data
    android:name="com.popdeem.sdk.ApiKey"
    android:value="YOUR_POPDEEM_API_KEY" />
```

#### Permissions

Add the following permissions to your applications `AndroidManifest.xml` if they are not already declared.

```xml
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

```xml
<string name="google_app_id">YOUR_GOOGLE_APP_ID</string>
```

Then reference this in your `AndroidManifest.xml` inside the `<application>` tags:
```xml
<meta-data
    android:name="GCMSenderID"
    android:value="@string/google_app_id" />
```

Add the following `receiver` and `service` after the lines above:
```xml
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
```java
PopdeemSDK.initializeSDK(this);
```


#### For Applications that use Realm

If your application uses [Realm](https://realm.io/) you will need to add the Popdeem SDK's schema to your Realm Configuration. You can read about this [here](https://realm.io/docs/java/latest/#schemas) in the Realm documentation.

The Popdeem SDK's Realm Module class is `PDRealmModule`. A sample RealmConfiguration might look like this when including Popdeem's Realm Module:

```java
RealmConfiguration config = new RealmConfiguration.Builder(this)
        .schemaVersion(1)
        .modules(Realm.getDefaultModule(), new PDRealmModule())
        .name("yourdbname.realm")
        .build();
Realm.setDefaultConfiguration(config);
```

---

Next, set up your [Facebook App](facebook_app_setup.md "Facebook App")

---
[Docs Home](./ "Docs Home")
