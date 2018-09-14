package com.service.iscon.vcr.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 13/9/18.
 */

public class ChantingPrefSettings {

    private static final String CHANTING_PREF = "ChantingPref";
    private static ChantingPrefSettings instance = new ChantingPrefSettings();
    private final Object object = new Object();
    private SharedPreferences sharedPreferences;

    private ChantingPrefSettings() {
    }

    public static void init(Context context) {
        instance.sharedPreferences = context.getSharedPreferences(CHANTING_PREF, context.MODE_PRIVATE);
    }
    public static ChantingPrefSettings getInstance() {
        return instance;
    }

    public void setLocation(String location) {
        synchronized (object) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Location", location);
            editor.apply();
        }
    }

    public String getLocation() {
        synchronized (object) {
            return sharedPreferences.getString("Location", "");
        }
    }

}
