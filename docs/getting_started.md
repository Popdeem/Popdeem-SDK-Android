## Getting Started

### Dependency

The Popdeem Android SDK is available through jcenter and mavenCentral.

In your ***top-level*** `build.gradle` file add the lines marked in the sample below:

```java
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" } // Add this line
    }
}
```

Add the following between the `buildscripts` and the `allprojects`: 

```java
ext {
    compileSdkVersion = 28
    supportLibVersion = "28.0.0"
}
```

These should be set to the same level as your project sdk version and support library. To avoid conflicts we recommend setting all support libraries using the following:

```java
    implementation "com.android.support:appcompat-v7:${rootProject.ext.supportLibVersion}"
```


Add the following line to the `dependencies` block of your applications `build.gradle` file and Gradle Sync your project to download the SDK dependency:

```java
implementation 'com.popdeem.sdk:sdk:1.3.26'
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

To use the Popdeem Push Notification and Broadcast feature, your application must have Firebase setup.
Go to [Firebase](https://firebase.google.com/ "Firebase") and create a project for your application. Add your application details to the cloud messaging and place the `google-services.json` in the same folder as your app level `build.gradle` 

In the `buildscripts->dependencies` add the following: 

```java
classpath 'com.google.gms:google-services:4.0.1'
```

At the bottom of the applications `build.gradle` add the following line:

```java
apply plugin: 'com.google.gms.google-services'
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
