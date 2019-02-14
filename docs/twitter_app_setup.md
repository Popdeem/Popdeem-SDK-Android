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

Next, [Instagram App Setup](instagram_app_setup.md "Login Flow")

---
[Docs Home](./ "Docs Home")
