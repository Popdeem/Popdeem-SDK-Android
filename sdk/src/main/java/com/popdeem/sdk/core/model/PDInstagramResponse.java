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

package com.popdeem.sdk.core.model;

/**
 * Created by mikenolan on 08/08/16.
 */
public class PDInstagramResponse {

    /*
        {
        "access_token": "3443088790.1b9912d.a1c32a9444cd46108ab70cc21aee0b96",
            "user": {
                "username": "popdeemtester1",
                "bio": "This is a test account used only for testing Popdeem's Instagram integrations",
                "website": "",
                "profile_picture": "https://scontent.cdninstagram.com/t51.2885-19/s150x150/13394975_720760244694428_156984249_a.jpg",
                "full_name": "Popdeem Tester 1",
                "id": "3443088790"
            }
        }
     */

    private String accessToken;
    private PDInstagramResponseUser user;

    public PDInstagramResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public PDInstagramResponseUser getUser() {
        return user;
    }

    public void setUser(PDInstagramResponseUser user) {
        this.user = user;
    }


    /**
     * Instagram Response User model
     */
    public class PDInstagramResponseUser {
        private String username;
        private String bio;
        private String website;
        private String profilePicture;
        private String fullName;
        private String id;

        public PDInstagramResponseUser() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
