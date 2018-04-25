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

package com.popdeem.sdk.core.realm;

import com.popdeem.sdk.core.model.PDInstagramOptions;

import android.content.Context;

import com.popdeem.sdk.core.model.PDTweetOptions;
import com.popdeem.sdk.core.model.PDUser;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by mikenolan on 18/02/16.
 */
public class PDRealmUtils {

    private static final int REALM_SCHEMA_VERSION = 20;

    public static void initRealmDB(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("popdeemrealm.realm")
                .modules(new PDRealmModule())
                .schemaVersion(REALM_SCHEMA_VERSION)
                .migration(REALM_MIGRATION)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        Realm.compactRealm(realmConfiguration);
    }

    private static final RealmMigration REALM_MIGRATION = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            //Update for migrating user to allow advocacy score.
            RealmSchema schema = realm.getSchema();


            if(oldVersion < REALM_SCHEMA_VERSION){
                RealmObjectSchema userSchema = schema.get("PDRealmUserDetails");
                if(userSchema!=null){
                    if(userSchema.hasField("advocacyScore")) {
                        userSchema.renameField("advocacyScore", "advocacy_score");
                    }
                    if(!userSchema.hasField("advocacy_score")) {
                        userSchema.addField("advocacy_score", float.class, null);
                    }
                    if(userSchema.hasField("advocacyScore")) {
                        userSchema.removeField("advocacyScore");
                    }
                    if(!schema.contains("PDRealmCustomer")){
                        schema.create("PDRealmCustomer")
                                .addField(PDRealmCustomer.NAME, String.class, null)
                                .addField(PDRealmCustomer.FB_APP_ID, String.class, null)
                                .addField(PDRealmCustomer.FB_APP_ACCESS_TOKEN, String.class, null)
                                .addField(PDRealmCustomer.FACEBOOK_NAMESPACE, String.class, null)
                                .addField(PDRealmCustomer.TWITTER_CONSUMER_KEY, String.class, null)
                                .addField(PDRealmCustomer.TWITTER_CONSUMER_SECRET, String.class, null)
                                .addField(PDRealmCustomer.TWITTER_HANDLE, String.class, null)
                                .addField(PDRealmCustomer.INSTAGRAM_CLIENT_ID, String.class, null)
                                .addField(PDRealmCustomer.INSTAGRAM_CLIENT_SECRET, String.class, null)
                                .addField(PDRealmCustomer.COUNTDOWN_TIMER, int.class, null)
                                .addField(PDRealmCustomer.INCREMENT_ADVOCACY_POINTS, int.class, null)
                                .addField(PDRealmCustomer.DECREMENT_ADVOCACY_POINTS, int.class, null);
                    }


                    if(!schema.contains("PDFeed")){
                        schema.create("PDFeed")
                                .addField("userId", int.class, null)
                                .addField("brandLogoUrlString", String.class, null)
                                .addField("brandName", String.class, null)
                                .addField("imageUrlString", String.class, null)
                                .addField("rewardTypeString", String.class, null)
                                .addField("userProfilePicUrlString", String.class, null)
                                .addField("userFirstName", String.class, null)
                                .addField("userLastName", String.class, null)
                                .addField("actionText", String.class, null)
                                .addField("timeAgo", String.class, null)
                                .addField("descriptionString", String.class, null)
                                .addField("caption", String.class, null);
                    }



                    if(!schema.contains("PDRealmLocation")) {
                        schema.create("PDRealmLocation")
                                .addField("id", String.class)
                                .addField("latitude", String.class)
                                .addField("longitude", String.class)
                                .addField("address", String.class)
                                .addField("twitterPageId", String.class)
                                .addField("fbPageId", String.class)
                                .addField("fbPageUrl", String.class)
                                .addField("numberOfRewards", String.class)
                                .addField("brandIdentifier", String.class)
                                .addField("brandName", String.class);
                    }

                    if(!schema.contains("PDRealmTweetOptions")) {
                        schema.create("PDRealmTweetOptions")
                                .addField("prefill", boolean.class)
                                .addField("forceTag", boolean.class)
                                .addField("freeForm", boolean.class)
                                .addField("prefilledMessage", String.class)
                                .addField("forcedTag", String.class)
                                .addField("includeDownloadLink", String.class);
                    }

                    if(!schema.contains("PDRealmInstagramOptions")) {
                        schema.create("PDRealmInstagramOptions")
                                .addField("prefill", boolean.class)
                                .addField("forceTag", boolean.class)
                                .addField("freeForm", boolean.class)
                                .addField("prefilledMessage", String.class)
                                .addField("forcedTag", String.class)
                                .addField("includeDownloadLink", String.class);
                    }

                    if(!schema.contains("PDRealmRewardClaimingSocialNetwork")) {
                        schema.create("PDRealmRewardClaimingSocialNetwork")
                                .addField("name", String.class)
                                .addField("socialAccountId", int.class)
                                .addField("createdAt", String.class)
                                .addField("updatedAt", String.class);
                    }

                    if(schema.contains("PDRealmReward")) {
                        schema.remove("PDRealmReward");
                    }

                    if(!schema.contains("PDRealmReward")) {
                            schema.create("PDRealmReward")
                                .addField("id", String.class)
                                .addField("rewardType", String.class)
                                .addField("description", String.class)
                                .addField("picture", String.class)
                                .addField("blurredPicture", String.class)
                                .addField("coverImage", String.class)
                                .addField("rules", String.class)
                                .addField("remainingCount", int.class)
                                .addField("status", String.class)
                                .addField("action", String.class)
                                .addField("createdAt", long.class)
                                .addRealmListField("locations", schema.get("PDRealmLocation"))
                                .addRealmListField("claimingSocialNetworks", schema.get("PDRealmRewardClaimingSocialNetwork"))
                                .addField("availableUntilInSeconds", String.class)
                                .addField("availableNextInSeconds", String.class)
                                .addField("revoked", String.class)
                                .addField("twitterMediaCharacters", String.class)
                                .addRealmListField("socialMediaTypes", String.class)
                                .addField("disableLocationVerification", String.class)
                                .addField("credit", String.class)
                                .addRealmObjectField("tweetOptions", schema.get("PDRealmTweetOptions"))
                                .addRealmObjectField("instagramOptions", schema.get("PDRealmInstagramOptions"))
                                .addField("countdownTimer", long.class)
                                .addField("distanceFromUser", float.class)
                                .addField("instagramVerified", boolean.class)
                                .addField("claimedAt", long.class)
                                .addField("recurrence", String.class)
                                .addField("verifying", boolean.class)
                                .addField("globalHashtag", String.class)
                                .addField("noTimeLimit", String.class);
                    }

                }
//                if(schema.contains("PDFeed")){
//                    schema.remove("PDFeed")", String.class)
//                }
                oldVersion = REALM_SCHEMA_VERSION;

            }
        }
    };
}
