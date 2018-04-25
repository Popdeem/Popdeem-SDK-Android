# MultiLogin Flow

To trigger the Popdeem multi-login flow, call the `enableSocialMultiLogin(@NonNull Class activityClass, int numberOfPrompts)` method of the `PopdeemSDK` class:

```java
PopdeemSDK.enableSocialMultiLogin(MainActivity.class, 3);
```

The `activityClass` parameter denotes the Activity class that you would like the multi login flow to appear in. In the example above we want the login flow to appear in the applications `MainActivity` so we pass `MainActivity.class` in method.

The `numberOfPrompts` parameter denotes how many times you wish to ask the user to log in if they have dismissed the login pop-up.

In the Activity class that you would like the multi login flow to appear, you must also override the Activity's `onActivityResult(int requestCode, int resultCode, Intent data)`. This is to allow the necessary callbacks for Twitter Login to work. Below is an example of how this should look, and can also be found in the `MainActivity.java` of the `NavigationSample`.

```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("PDUISocialMultiLoginFragment");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
```

Provided all of the items in [Facebook App Setup](facebook_app_setup.md "Facebook Setup") have been performed correctly, this login flow will result in the user being logged in.

On app launch, if the user has been previously logged in, the Popdeem SDK will take care of automatic login.

## Non-Social Users

When you initialize the Popdeem SDK, a non-social user is created and registered on Popdeem. This allows you to track which of your app users have converted to Social.  

---
[Docs Home](./ "Docs Home")