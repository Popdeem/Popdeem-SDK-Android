# Facebook App Setup

You will need to set up a Facebook application at the Facebook Dev Centre. You can do so by following this [Tutorial](https://developers.facebook.com/docs/apps/register "Facebook Tutorial").

Popdeem SDK already includes the Facebook SDK dependency _(v4.5.0)_ so your application will inherit this.

---

When you have created your Facebook Application, you will need to set up some custom Open Graph Objects in order to use Popdeem correctly. You will need two objects:

* Brand Location, which inherits from **Place** and has an attribute **Geopoints**  
* Photo, which inherits from **Photo**

You then need to create *two* Open Graph stories using these objects:

* **Check in** at **Brand Location**. This has action type **Check In**, object type **Brand Location** and uses the **Map** attachments, with **brand_location.geopoints** highlighted.
* **Share** a **Photo**. This has action type **Share**, and object type **Photo**.

---

When you have your Facebook app set up, make note of your Facebook App ID. Add the following to your Android applications `string.xml` file:
```xml
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
```

Then in your `AndroidManifest.xml` file add these lines inside the `<application>` tags:
```xml
<meta-data
    android:name="com.facebook.sdk.ApplicationId"
    android:value="@string/facebook_app_id" />

<activity
    android:name="com.facebook.FacebookActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:label="@string/app_name"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

---

Next, [Twitter App Setup](twitter_app_setup.md "Login Flow")

---
[Docs Home](./ "Docs Home")
