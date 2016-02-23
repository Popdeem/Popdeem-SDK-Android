/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Popdeem
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popdeem.sdk.core.utils;

import com.facebook.AccessToken;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by mikenolan on 23/02/16.
 */
public class PDSocialUtils {

    public static final String[] FACEBOOK_READ_PERMISSIONS = {"public_profile", "email", "user_birthday", "user_posts", "user_friends", "user_education_history"};
    public static final String[] FACEBOOK_PUBLISH_PERMISSIONS = {"publish_actions"};


    /**
     * Check if user has granted all Facebook Read Permissions
     *
     * @return true if permissions are granted, false if not
     */
    public static boolean hasAllFacebookReadPermissions() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        }
        Set<String> grantedPermissions = AccessToken.getCurrentAccessToken().getPermissions();
        return grantedPermissions.containsAll(Arrays.asList(FACEBOOK_READ_PERMISSIONS));
    }


    /**
     * Check if user has granted all Facebook Publish Permissions
     *
     * @return true if permissions are granted, false if not
     */
    public static boolean hasAllFacebookPublishPermissions() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        }
        Set<String> grantedPermissions = AccessToken.getCurrentAccessToken().getPermissions();
        return grantedPermissions.containsAll(Arrays.asList(FACEBOOK_PUBLISH_PERMISSIONS));
    }


    /**
     * Check if user is logged in to Facebook
     *
     * @return true if logged in, false if not
     */
    public static boolean isLoggedInToFacebook() {
        return AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired();
    }

}
