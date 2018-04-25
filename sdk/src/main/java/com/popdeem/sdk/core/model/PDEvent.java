package com.popdeem.sdk.core.model;

/**
 * Created by colm on 02/03/2018.
 *
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PDEvent {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("from_tier")
    @Expose
    private int fromTier;
    @SerializedName("to_tier")
    @Expose
    private int toTier;
    @SerializedName("readed")
    @Expose
    private boolean readed;
    @SerializedName("date")
    @Expose
    private long date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromTier() {
        return fromTier;
    }

    public void setFromTier(int fromTier) {
        this.fromTier = fromTier;
    }

    public int getToTier() {
        return toTier;
    }

    public void setToTier(int toTier) {
        this.toTier = toTier;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

}