# Login Flow

To trigger the Popdeem login flow, call the `enableSocialLogin(@NonNull Class activityClass, int numberOfPrompts)` method of the `PopdeemSDK` class:

```java
PopdeemSDK.enableSocialLogin(MainActivity.class, 3);
```

The `activityClass` parameter denotes the Activity class that you would like the login flow to appear in. In the example above we want the login flow to appear in the applications `MainActivity` so we pass `MainActivity.class` in method.

The `numberOfPrompts` parameter denotes how many times you wish to ask the user to log in if they have dismissed the login pop-up.

Provided all of the items in [Facebook App Setup](facebook_app_setup.md "Facebook Setup") have been performed correctly, this login flow will result in the user being logged in.

On app launch, if the user has been previously logged in, the Popdeem SDK will take care of automatic login.

## Non-Social Users

When you initialize the Popdeem SDK, a non-social user is created and registered on Popdeem. This allows you to track which of your app users have converted to Social.  

Next, [Trigger Home Flow](home_flow.md "Home Flow")  

---
[Docs Home](./ "Docs Home")
