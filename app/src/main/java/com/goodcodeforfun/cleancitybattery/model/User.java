package com.goodcodeforfun.cleancitybattery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    @Expose
    private String apiId;
    @Expose
    private String displayName;

    /**
     * @return The Id
     */
    public String getApiId() {
        return apiId;
    }

    /**
     * @param apiId The _id
     */
    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public User withId(String apiId) {
        this.apiId = apiId;
        return this;
    }

    /**
     * @return The displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName The displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public User withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

}