package com.goodcodeforfun.cleancitybattery.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Table(name = "Locations", id = BaseColumns._ID)
public class Location extends Model {

    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGTITUDE = "Longtitude";

    @SerializedName("_id")
    @Expose
    private String Id;
    @Expose
    private User user;
    @SerializedName("__v")
    @Expose
    private int V;
    @Expose
    private String created;
    @Expose
    @Column(name = COLUMN_LATITUDE)
    private String latitude;
    @Expose
    @Column(name = COLUMN_LONGTITUDE)
    private String longitude;
    @Expose
    private String name;

    /**
     * @return The Id
     */
    public String getApiId() {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setApiId(String Id) {
        this.Id = Id;
    }

    public Location withId(String Id) {
        this.Id = Id;
        return this;
    }

    /**
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    public Location withUser(User user) {
        this.user = user;
        return this;
    }

    /**
     * @return The V
     */
    public int getV() {
        return V;
    }

    /**
     * @param V The __v
     */
    public void setV(int V) {
        this.V = V;
    }

    public Location withV(int V) {
        this.V = V;
        return this;
    }

    /**
     * @return The created
     */
    public String getCreated() {
        return created;
    }

    /**
     * @param created The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    public Location withCreated(String created) {
        this.created = created;
        return this;
    }

    /**
     * @return The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Location withLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * @return The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Location withLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public Location withName(String name) {
        this.name = name;
        return this;
    }

}