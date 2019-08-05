package com.example.mapboxapp.Tracking.Utils;

public interface PreferencesManagerInt {

    String getPort();

    String getServer();

    String getString(String key, int shared);

    boolean getBoolean(String key, int shared);

    void putString(String key, String valor, int shared);

    void putBoolean(String key, boolean valor, int shared);

    void clearPreferences(int shared);

}
