package com.goodcodeforfun.cleancitybattery.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Types", id = BaseColumns._ID)
public class Type extends Model {
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_CREATED = "Created";
    public static final String COLUMN_TYPE_VALUE = "TypeValue";
    @Expose
    @Column(name = COLUMN_CREATED)
    private String created;
    @Expose
    @Column(name = COLUMN_TYPE_VALUE)
    private String value;
    @Expose
    @Column(name = COLUMN_NAME, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String name;

    public Type() {
        super();
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

    public Type withCreated(String created) {
        this.created = created;
        return this;
    }

    /**
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    public Type withValue(String value) {
        this.value = value;
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

    public Type withName(String name) {
        this.name = name;
        return this;
    }
}