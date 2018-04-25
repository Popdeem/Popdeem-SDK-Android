# Social Home

All of Popdeem's core mobile features are contained in the `Home` flow. There are various scenarios in which you may launch the Home flow.

#### Activity

The recommended way to present the Home flow is to use the standalone activity provided in the Popdeem SDK. To start this activity use one of the following lines of code depending on the type of class the call will be invoked from:

From an Activity:
```java
PopdeemSDK.showHomeFlow(this);
```

From an Fragment:
```java
PopdeemSDK.showHomeFlow(getActivity());
```

An example of this can be seen in the `navigationsample` module in the `MainActivity` class.

#### Fragment

If you wanted to present the Home flow as a fragment instead, for example as a Tab in a `TabLayout` `ViewPager`, create an instance of the `PDUIHomeFlowFragment` Fragment class using the following:
```java
PDUIHomeFlowFragment.newInstance();
```

This can be used in your `PagerAdapter` or in a `FragmentTransaction` depending on your requirements.

An example of this can be seen in the `tabbedsample` module in the `MainActivity` class.

---

To customize the look and feel of the Popdeem SDK, check out the [theming](theme.md "Theme") section.  

---
[Docs Home](./ "Docs Home")
