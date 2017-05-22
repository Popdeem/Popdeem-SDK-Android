# Customize String values in the Podpeem SDK

Customizing Strings in the Popdeem SDK is similar to customizing colors.

You can customize the Popdeem SDK's UI strings to help with integration.

All you need to do it declare the string in your applications `strings.xml` file.

_The strings must be named the same as they appear below_

The list of strings that can be changed are as follows:

```xml
<!-- Common -->
<string name="pd_common_please_wait_text">Please wait</string>
<string name="pd_common_sorry_text">Sorry</string>
<string name="pd_common_something_wrong_text">Something went wrong. Please try again later.</string>
<string name="pd_common_logout_text">Logout</string>
<string name="pd_common_logout_message_text">Are you sure you want to log out?</string>
<string name="pd_common_facebook_login_cancelled_title_text">Login Cancelled</string>
<string name="pd_common_facebook_login_cancelled_message_text">You may browse the app before connecting your Facebook, but you must log in with Facebook to avail of social rewards.</string>


<!-- Social Login -->
<string name="pd_social_login_success_text">Connected!</string>
<string name="pd_social_login_success_description_text">Rewards are now unlocked. You will be notified when new rewards are available.</string>
<string name="pd_social_login_heading_text">Connect your Facebook to earn additional Rewards</string>
<string name="pd_social_login_body_text">Connect your Facebook account to turn social features on. This will give you access to exclusive content and new social rewards.</string>
<string name="pd_social_login_footer_text">Powered by Popdeem</string>
<string name="pd_social_login_continue_text">Continue to app</string>
<string name="pd_social_login_tagline_text">NEW: SOCIAL FEATURES</string>
<string name="pd_social_login_terms_text">By signing in with Facebook you accept the terms of our privacy policy.</string>


<!-- Home Flow -->
<string name="pd_home_banner_text">Share your Popdeem experience on social networks to earn more rewards.</string>


<!-- Rewards -->
<string name="pd_rewards_not_available_text">There are no rewards available right now. Please check back later.</string>


<!-- Tab Titles -->
<string name="pd_rewards_title">Rewards</string>
<string name="pd_activity_title">Activity</string>
<string name="pd_wallet_title">Wallet</string>


<!-- Claim -->
<string name="pd_claim_title">Claim</string>
<string name="pd_claim_get_reward_text">Share and Redeem</string>
<string name="pd_claim_verify_location_failed_title_text">Not at location</string>
<string name="pd_claim_verify_location_failed_text">You must be at this location to claim this reward.\nPlease come back later.</string>

<string name="pd_claim_message_placeholder_text">What are you up to?</string>

<string name="pd_claim_cannot_deselect_text">Cannot Deselect</string>
<string name="pd_claim_facebook_forced_message_string">This reward must be claimed with a Facebook post. You can also post to Twitter if you wish.</string>
<string name="pd_claim_twitter_forced_message_string">This reward must be claimed with a Twitter post. You can also post to Facebook if you wish.</string>

<string name="pd_claim_reward_claimed_text">Reward Claimed!</string>
<string name="pd_claim_reward_claimed_success_text">You can view your reward in your wallet</string>
<string name="pd_claim_claiming_reward_text">Claiming your reward</string>
<string name="pd_claim_tweet_too_long_text">You are over the character limit for Twitter. Please edit your message.</string>
<string name="pd_claim_no_network_selected_title_text">No Network Selected</string>
<string name="pd_claim_no_network_selected_message_text">You must select at least one social network in order to complete this action</string>
<string name="pd_claim_photo_required_text">Photo Required</string>
<string name="pd_claim_photo_required_message_text">A photo is required for this action. Please add a photo</string>

<string name="pd_claim_action_tweet_checkin">Check-in or Tweet Required</string>
<string name="pd_claim_action_photo">Photo required</string>
<string name="pd_claim_action_none">Instant Coupon</string>
<string name="pd_claim_action_checkin">Check-in Required</string>
<string name="pd_claim_action_tweet">Tweet Required</string>
<string name="pd_claim_action_tweet_photo">Tweet with Photo Required</string>

<!-- Claim Add Image Dialog -->
<string name="pd_claim_add_photo_title_text">Add Photo</string>
<string name="pd_claim_add_photo_gallery_text">Gallery</string>
<string name="pd_claim_add_photo_camera_text">Camera</string>

<!-- Claim Tag Friends -->
<string name="pd_claim_choose_friends_title">Choose Friends</string>
<string name="pd_claim_choose_friends_search_text">Search</string>


<!-- Inbox -->
<string name="pd_inbox_title">Inbox</string>
<string name="pd_inbox_message_sender_tag_text">Sender:</string>
<string name="pd_inbox_message_date_tag_text">Date:</string>
<string name="pd_inbox_message_body_tag_text">Body:</string>
<string name="pd_inbox_message_title_tag_text">Title:</string>
<string name="pd_inbox_no_messages_text">You have no message right now…</string>


<!-- Wallet -->
<string name="pd_wallet_no_items_text">You have no items in your Wallet right now.</string>
<string name="pd_wallet_sweepstake_redeem_text">You will be notified if you are the winner.</string>
<string name="pd_wallet_credit_reward_text">was added to your balance.</string>
<string name="pd_wallet_coupon_info_message_text">- Once you\'re ready to redeem your reward, tap \"Redeem\".\n\n- After tapping \"Redeem\" you have %1$s minutes to get the reward.\n\n- You must show the cashier the following screen within %2$s minutes.</string>
<string name="pd_redeem_sweepstake_reward_info_title_string">Sweepstake Reward</string>
<string name="pd_redeem_sweepstake_reward_info_message_string">- You are now in the draw.\n\n- You will be notified if you are the winner.</string>
<string name="pd_redeem_sweepstake_reward_no_date_message_string">\n\n- There is no date set for this draw.</string>
<string name="pd_draw_takes_place_in_string">Draw takes place in</string>


<!-- Feed -->
<string name="pd_feed_no_items_text">There is no Feed available right now. Please check back later.</string>


<!-- Redeem -->
<string name="pd_redeem_title">Redeem</string>
<string name="pd_redeem_done_alert_title_text">Have you redeemed your reward?</string>
<string name="pd_redeem_done_alert_body_text">You will not be able to redeem your reward after leaving this screen</string>
<string name="pd_redeem_timer_finished_text">Timer Finished</string>

<string name="pd_are_you_sure_string">Are you sure?</string>
<string name="pd_redeem_button_string">Redeem</string>
<string name="pd_redeem_reward_info_title_string">How to Redeem</string>

<string name="pd_redeem_info_text">Share this screen at the location\nbefore the timer runs out</string>
<string name="pd_redeem_done_text">Done</string>


<!-- Notification Dialog -->
<string name="pd_notification_go_button_text">GO</string>


<!-- Location Permissions -->
<string name="pd_location_disabled_title_text">Location Disabled</string>
<string name="pd_location_disabled_message_text">Location Services need to be enabled to continue. We use your location to deliver local rewards. Would you like to be taken Location Settings?</string>
<string name="pd_location_permission_title_text">Locations Permission</string>
<string name="pd_location_permission_rationale_text">We use your location to deliver local rewards</string>
<string name="pd_location_permission_are_you_sure_text">We use your location to deliver local rewards. Would you like to give permission to access your location?</string>


<!-- Storage Permissions -->
<string name="pd_storage_permissions_title_string">Storage Permissions</string>
<string name="pd_storage_permission_rationale_string">We need permission to save the image you take to your phone. Would you like to give permission?</string>
<string name="pd_storage_permissions_denied_string">This app needs access to your storage to share an image.</string>


<!-- Suspended Account -->
<string name="pd_suspended_title_string">Suspended</string>
<string name="pd_suspended_message_string">You have been suspended. You cannot claim any rewards at this time.</string>
<string name="pd_suspended_message_with_date_string">You have been suspended. Your account will become active on %1s</string>
```

Next, [MultiLogin Flow](MultiLoginFlow.md "MultiLogin Flow")

---
[Docs Home](./ "Docs Home")
