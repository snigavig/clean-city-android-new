package com.goodcodeforfun.cleancitybattery.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by snigavig on 05.09.15.
 */
public class SharedPreferencesHelper {
    public static final String LAST_UPDATE_TIME_KEY = "LAST_UPDATE_TIME";


    private final SharedPreferences prefs;


    public SharedPreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void clearAll() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
    }


    public SharedPreferences getPrefs() {
        return prefs;
    }


    public long getLastUpdateTime() {
        return prefs.getLong(LAST_UPDATE_TIME_KEY, -1);
    }


    public void setLastUpdateTime(long value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(LAST_UPDATE_TIME_KEY, value);
        editor.apply();
    }
}
