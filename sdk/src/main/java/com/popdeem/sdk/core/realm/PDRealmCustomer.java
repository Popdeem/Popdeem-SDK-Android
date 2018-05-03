package com.popdeem.sdk.core.realm;

import com.google.gson.JsonObject;

import io.realm.RealmObject;

/**
 * Created by colm on 28/02/2018.
 */

public class PDRealmCustomer extends RealmObject {
    public static final String NAME = "name";
    public static final String FB_APP_ID = "fb_app_id";
    public static final String FB_APP_ACCESS_TOKEN = "fb_app_access_token";
    public static final String FACEBOOK_NAMESPACE = "facebook_namespace";
    public static final String TWITTER_CONSUMER_KEY = "twitter_consumer_key";
    public static final String TWITTER_CONSUMER_SECRET = "twitter_consumer_secret";
    public static final String TWITTER_HANDLE = "twitter_handle";
    public static final String INSTAGRAM_CLIENT_ID = "instagram_client_id";
    public static final String INSTAGRAM_CLIENT_SECRET = "instagram_client_secret";
    public static final String COUNTDOWN_TIMER = "countdown_timer";
    public static final String INCREMENT_ADVOCACY_POINTS = "increment_advocacy_points";
    public static final String DECREMENT_ADVOCACY_POINTS = "decrement_advocacy_points";
        
    private String name;
    private String fb_app_id;
    private String fb_app_access_token;
    private String facebook_namespace;
    private String twitter_consumer_key;
    private String twitter_consumer_secret;
    private String twitter_handle;
    private String instagram_client_id;
    private String instagram_client_secret;
    private int countdown_timer;
    private int increment_advocacy_points;
    private int decrement_advocacy_points;

    public static PDRealmCustomer fromJson(JsonObject jsonObject){
//        setName()
        PDRealmCustomer ret = new PDRealmCustomer();

        ret.setName(jsonObject.get(NAME).getAsString());
        ret.setFb_app_id(jsonObject.get(FB_APP_ID).getAsString());
        ret.setFb_app_access_token(jsonObject.get(FB_APP_ACCESS_TOKEN).getAsString());
        ret.setFacebook_namespace(jsonObject.get(FACEBOOK_NAMESPACE).getAsString());
        if(!jsonObject.get(TWITTER_CONSUMER_KEY).isJsonNull() && !jsonObject.get(TWITTER_CONSUMER_KEY).isJsonNull() && jsonObject.get(TWITTER_CONSUMER_KEY)!=null && jsonObject.get(TWITTER_CONSUMER_SECRET)!=null) {
            ret.setTwitter_consumer_key(jsonObject.get(TWITTER_CONSUMER_KEY).getAsString());
            ret.setTwitter_consumer_secret(jsonObject.get(TWITTER_CONSUMER_SECRET).getAsString());
        }else{
            ret.setTwitter_consumer_key("");
            ret.setTwitter_consumer_secret("");
        }
        ret.setTwitter_handle(jsonObject.get(TWITTER_HANDLE).getAsString());
        ret.setInstagram_client_id(jsonObject.get(INSTAGRAM_CLIENT_ID).getAsString());
        ret.setInstagram_client_secret(jsonObject.get(INSTAGRAM_CLIENT_SECRET).getAsString());
        ret.setCountdown_timer(jsonObject.get(COUNTDOWN_TIMER).getAsInt());
        if(jsonObject.get(INCREMENT_ADVOCACY_POINTS)!=null) {
            ret.setIncrement_advocacy_points(jsonObject.get(INCREMENT_ADVOCACY_POINTS).getAsInt());
        }else{
            ret.setIncrement_advocacy_points(-1);
        }
        if(jsonObject.get(INCREMENT_ADVOCACY_POINTS)!=null) {
            ret.setDecrement_advocacy_points(jsonObject.get(DECREMENT_ADVOCACY_POINTS).getAsInt());
        }else{
            ret.setDecrement_advocacy_points(-1);
        }
        
        return ret;
    }

    public PDRealmCustomer(){ }

    public PDRealmCustomer setName(String name){
        this.name = name;
        return this;
    }
    public String getName(){
        return this.name;
    }
    public PDRealmCustomer setFb_app_id(String fb_app_id){
        this.fb_app_id = fb_app_id;
        return this;
    }
    public String getFb_app_id(){
        return this.fb_app_id;
    }
    public PDRealmCustomer setFb_app_access_token(String fb_app_access_token){
        this.fb_app_access_token = fb_app_access_token;
        return this;
    }
    public String getFb_app_access_token(){
        return this.fb_app_access_token;
    }
    public PDRealmCustomer setFacebook_namespace(String facebook_namespace){
        this.facebook_namespace = facebook_namespace;
        return this;
    }
    public String getFacebook_namespace(){
        return this.facebook_namespace;
    }
    public PDRealmCustomer setTwitter_consumer_key(String twitter_consumer_key){
        this.twitter_consumer_key = twitter_consumer_key;
        return this;
    }
    public String getTwitter_consumer_key(){
        return this.twitter_consumer_key;
    }
    public PDRealmCustomer setTwitter_consumer_secret(String twitter_consumer_secret){
        this.twitter_consumer_secret = twitter_consumer_secret;
        return this;
    }
    public String getTwitter_consumer_secret(){
        return this.twitter_consumer_secret;
    }
    public PDRealmCustomer setTwitter_handle(String twitter_handle){
        this.twitter_handle = twitter_handle;
        return this;
    }
    public String getTwitter_handle(){
        return this.twitter_handle;
    }
    public PDRealmCustomer setInstagram_client_id(String instagram_client_id){
        this.instagram_client_id = instagram_client_id;
        return this;
    }
    public String getInstagram_client_id(){
        return this.instagram_client_id;
    }
    public PDRealmCustomer setInstagram_client_secret(String instagram_client_secret){
        this.instagram_client_secret = instagram_client_secret;
        return this;
    }
    public String getInstagram_client_secret(){
        return this.instagram_client_secret;
    }
    public PDRealmCustomer setCountdown_timer(int countdown_timer){
        this.countdown_timer = countdown_timer;
        return this;
    }
    public int getCountdown_timer(){
        return this.countdown_timer;
    }
    public PDRealmCustomer setIncrement_advocacy_points(int increment_advocacy_points){
        this.increment_advocacy_points = increment_advocacy_points;
        return this;
    }
    public int getIncrement_advocacy_points(){
        return this.increment_advocacy_points;
    }
    public PDRealmCustomer setDecrement_advocacy_points(int decrement_advocacy_points){
        this.decrement_advocacy_points = decrement_advocacy_points;
        return this;
    }
    public int getDecrement_advocacy_points(){
        return this.decrement_advocacy_points;
    }


    public boolean usesAmbassadorFeatures(){
        if(getIncrement_advocacy_points()>0)
            return true;
        return false;
    }
}