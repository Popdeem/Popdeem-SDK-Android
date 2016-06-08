# Customize the PopdeemSDK Theme

### Images
There are a few images that you can override to suit your application.

##### Notification Icon
To customize the notification icon that is shown when your app receives a push notification from Popdeem add an image to your drawable folders with this name:
```
ic_pd_notification.png
```
_We recommend using the built in Image Asset creator in Android Studio to do this as it will add an image for all densities._   
_To do this use File -> New -> Image Asset and choose "Notification Icons"_

##### Social Login Header Image
To customize the header image shown in the Social Login view add an image to your drawable folders called:
```
pd_social_login_header.png
```


##### Home Flow Banner Image
To customize the header / banner image shown in the Home Flow, add an image to your drawable folders called:
```
pd_home_banner.png
```

---

### Colors

You can customize the Popdeem SDK's UI colors to help with integration.

All you need to do it declare the color in your applications `colors.xml` file.

_The colors must be named the same as they appear below_

The list of colors that can be changed are as follows:

```xml
<!-- Toolbar Colors -->
<color name="pd_toolbar_color">#da4d59</color>
<color name="pd_toolbar_color_dark">#cc4752</color>
<color name="pd_toolbar_text_color">#ffffff</color>
<color name="pd_accent">#dba7ac</color>
<color name="pd_back_button_color">#ffffff</color>
<!-- Can be used if your social login header image clashes with the back button color for the rest of your app. Otherwise it defaults to pd_back_button_color -->
<color name="pd_social_login_back_button_color">@color/pd_back_button_color</color>


<!-- Inbox Floating Action Button Colors -->
<color name="pd_inbox_button_bg_color">#da4d59</color>
<color name="pd_inbox_button_icon_color">#ffffff</color>


<!-- TabLayout Colors -->
<color name="pd_tab_background_color">#da4d59</color>
<color name="pd_tab_selected_text_color">#ffffff</color>
<color name="pd_tab_unselected_text_color">#aaaaaa</color>
<color name="pd_tab_indicator_color">#dba7ac</color>


<!-- Home Flow -->
<color name="pd_home_flow_banner_text_color">#ffffff</color>
<color name="pd_home_flow_banner_bg_color">#da4d59</color>


<!-- Inbox / Message Center Colors -->
<color name="pd_inbox_background_color">#ffffff</color>
<color name="pd_inbox_no_messages_text_color">#000000</color>
<!-- Inbox List Item Colors -->
<color name="pd_inbox_list_divider_color">#dba7ac</color>
<color name="pd_inbox_list_item_background_color">#ffffff</color>
<color name="pd_inbox_list_item_body_text_color">#000000</color>
<color name="pd_inbox_list_item_time_text_color">#A2A2A1</color>
<color name="pd_inbox_list_item_unread_indicator_color">#2b85d3</color>


<!-- Social Login Colors -->
<color name="pd_login_continue_button_background_color">#3c9915</color>
<color name="pd_login_continue_button_text_color">#ffffff</color>
<color name="pd_login_tagline_text_color">#000000</color>
<color name="pd_login_heading_text_color">#000000</color>
<color name="pd_login_body_text_color">#000000</color>
<color name="pd_login_terms_text_color">#000000</color>


<!-- Rewards Colors -->
<color name="pd_rewards_background_color">#ffffff</color>
<color name="pd_rewards_no_rewards_text_color">#000000</color>
<!-- Rewards List Item Colors -->
<color name="pd_reward_list_divider_color">#dba7ac</color>
<color name="pd_reward_list_background_color">#ffffff</color>
<color name="pd_reward_list_item_title_text_color">#000000</color>
<color name="pd_reward_list_item_subtitle_text_color">#000000</color>
<color name="pd_reward_list_item_info_text_color">#A2A2A1</color>


<!-- Feed List Item Colors -->
<color name="pd_feed_background">#ffffff</color>
<color name="pd_feed_no_feed_text_color">#000000</color>
<color name="pd_feed_list_background_color">#ffffff</color>
<color name="pd_feed_list_divider_color">#dba7ac</color>
<color name="pd_feed_item_default_text_color">#A2A2A1</color>
<color name="pd_feed_item_name_text_color">#000000</color>
<color name="pd_feed_item_comment_text_color">#888888</color>
<color name="pd_feed_item_time_text_color">#A2A2A1</color>
<color name="pd_feed_item_title_text_color">#da4d59</color>


<!-- Wallet List Item Colors -->
<color name="pd_wallet_background_color">#ffffff</color>
<color name="pd_wallet_no_rewards_text_color">#000000</color>
<color name="pd_wallet_item_background_color">#ffffff</color>
<color name="pd_wallet_list_divider_color">#dba7ac</color>
<color name="pd_wallet_item_default_text_color">#000000</color>


<!-- Redeem Colors -->
<color name="pd_redeem_background_colour">#dddddd</color>
<color name="pd_redeem_content_background_colour">#ffffff</color>
<color name="pd_redeem_content_border_colour">#aaaaaa</color>
<color name="pd_redeem_done_button_background_colour">#da4d59</color>
<color name="pd_redeem_done_button_text_colour">#ffffff</color>
<color name="pd_redeem_timer_text_colour">#da4d59</color>
<color name="pd_redeem_title_text_colour">#000000</color>
<color name="pd_redeem_description_text_colour">#000000</color>


<!-- Claim Colors -->
<color name="pd_claim_background_color">#dddddd</color>
<color name="pd_claim_over_character_limit_text_color">#ff0000</color>
<color name="pd_claim_share_button_background_color">#3b5999</color>
<color name="pd_claim_share_button_text_color">#ffffff</color>
<color name="pd_claim_tagged_friends_text_color">#da4d59</color>
<color name="pd_claim_location_verification_background_color">#d8d8d8</color>
<color name="pd_claim_location_verification_text_color">#000000</color>
<color name="pd_claim_location_verification_tick_color">#777777</color>

<color name="pd_tag_friends_background_color">#ffffff</color>

<color name="pd_tagged_friends_text_color">#ffffff</color>
<color name="pd_tagged_friends_color">#3DA8F2</color>
<color name="pd_tagged_friends_color_pressed">#ff2d7db5</color>


<!-- Horizontal Progress Colors -->
<color name="pd_horizontal_progress_color">#bb4f53</color>
<color name="pd_horizontal_progress_background_color">#dba7ac</color>


<!-- Notification Dialog Colors -->
<color name="pd_notification_dialog_title_text_color">#000000</color>
<color name="pd_notification_dialog_message_text_color">#000000</color>


<!-- Swipe Refresh Colors. Should contain between 1...n colors. -->
<!-- If an empty array is provided the swipe refresh color defaults to pd_toolbar_color -->
<array name="pd_swipe_refresh_colors_array">
    <item>@color/pd_rainbow_color_1</item>
    <item>@color/pd_rainbow_color_2</item>
    <item>@color/pd_rainbow_color_3</item>
    <item>@color/pd_rainbow_color_4</item>
</array>
```

---

Next, you may want to customize the [strings](strings.md "strings") throughout the PopdeemSDK.  

---
[Docs Home](./ "Docs Home")
