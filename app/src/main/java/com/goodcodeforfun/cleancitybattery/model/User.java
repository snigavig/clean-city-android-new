package com.goodcodeforfun.cleancitybattery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//Not used and most probably will never be needed
@Deprecated
public class User {

    @SerializedName("_id")
    @Expose
    private String apiId;
    @Expose
    private String displayName;

    /**
     * @return The Id
     */
    @SuppressWarnings("unused")
    public String getApiId() {
        return apiId;
    }

    /**
     * @param apiId The _id
     */
    @SuppressWarnings("unused")
    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    @SuppressWarnings("unused")
    public User withId(String apiId) {
        this.apiId = apiId;
        return this;
    }

    /**
     * @return The displayName
     */
    @SuppressWarnings("unused")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName The displayName
     */
    @SuppressWarnings("unused")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @SuppressWarnings("unused")
    public User withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

}