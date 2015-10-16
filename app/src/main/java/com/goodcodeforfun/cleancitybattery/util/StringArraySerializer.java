package com.goodcodeforfun.cleancitybattery.util;

import com.activeandroid.serializer.TypeSerializer;

/**
 * Created by snigavig on 04.10.15.
 */
public class StringArraySerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return String[].class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public String serialize(Object data) {
        if (data == null) {
            return null;
        }
        return toString((String[]) data);
    }

    @Override
    public String[] deserialize(Object data) {
        if (data == null || data.toString().length() == 0) {
            return null;
        }
        return toArray((String) data);
    }


    private String[] toArray(String value) {
        String[] values = value.split(",");
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = String.valueOf(values[i]);
        }
        return result;
    }

    private String toString(String[] values) {
        String result = "";
        for (int i = 0; i < values.length; i++) {
            result += String.valueOf(values[i]);
            if (i < values.length - 1) {
                result += ",";
            }
        }
        return result;
    }
}