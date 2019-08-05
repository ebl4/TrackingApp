package com.example.mapboxapp.Tracking.Utils;

public interface PreferenceConfigInt {

    String getString(String key);

    int getInt(String key);

    boolean getBoolean(String key);

    void putString(String key, String valor);

    void putInt(String key, int valor);

    void putBoolean(String key, boolean valor);

    void clearPreferences();
}
