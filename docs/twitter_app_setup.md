# Twitter App Setup

If you do not have a Twitter app and the corresponding consumer secret and consumer key you can create an app [here](https://apps.twitter.com/ "Twitter Apps") and generate the keys needed to continue.

Once you have the Twitter app consumer key and consumer secret add the following lines to your `AndroidMainfest.xml` inside the `<application>` tags:

```xml
<meta-data
    android:name="TwitterConsumerKey"
    android:value="YOUR_TWITTER_CONSUMER_KEY" />

<meta-data
    android:name="TwitterConsumerSecret"
    android:value="YOUR_TWITTER_CONSUMER_SECRET" />
```

---

#### NOTE: For Fabric users

If you are using [Fabric](https://get.fabric.io/ "Fabric") you will need to update the Fabric initialization in your `Application` class.      
This is due to a limitation in Fabric with Android libraries.

The Popdeem SDK provides a method to create the Twitter kit using the consumers keys added to the `AndroidManifest.xml` in the previous step.

If you are already using the a Twitter kit with Fabric you will need to replace it in your Fabric init with this method.      
If you are not using the Twitter kit already you just need to add this method to the list of kits in your Fabric init.

```java
Fabric.with(this, PDSocialUtils.getTwitterKitForFabric(this));
```

***Fabric should be initialized BEFORE initializing the Popdeem SDK***

Next, [Instagram App Setup](instagram_app_setup.md "Login Flow")

---
[Docs Home](./ "Docs Home")
