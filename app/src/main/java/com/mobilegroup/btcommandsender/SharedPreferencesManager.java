package com.mobilegroup.btcommandsender;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "btCommandSenderPrefs";

    private static final String LAST_KNOWN_MAC_KEY = "last_known_mac_key";



    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesManager instance;
    private Context context;

    // Private constructor to prevent direct instantiation
    public SharedPreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Create a public static method to get the singleton instance
    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }


    public void saveMac( String value) {
        editor.putString(LAST_KNOWN_MAC_KEY, value);
        editor.apply();
    }

    public String getMac() {
        return sharedPreferences.getString(LAST_KNOWN_MAC_KEY, "");
    }
}
