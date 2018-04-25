# Instagram App Setup

If you do not have an Instagram app and the corresponding Client ID and Secret you can create one [here](https://www.instagram.com/developer/clients/register/) and generate the keys needed to continue.

Once you have the Client ID and Secret for your Instagram app, add the following lines to your `AndroidManifest.xml` inside the `<application>` tags:

```xml
<meta-data
    android:name="InstagramClientId"
    android:value="YOUR_INSTAGRAM_CLIENT_ID" />

<meta-data
    android:name="InstagramClientSecret"
    android:value="YOUR_INSTAGRAM_CLIENT_SECRET" />

<meta-data
    android:name="InstagramCallbackUrl"
    android:value="YOUR_INSTAGRAM_CLIENT_CALLBACK_URL" />
```

Next, [Trigger Login Flow](login_flow.md "Login Flow")

---
[Docs Home](./ "Docs Home")
