package com.example.mapboxapp.Tracking.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.mapboxapp.R;

public class PreferencesManager implements PreferencesManagerInt {
    private Context context;
    private SharedPreferences  userPreferences, sharedPreferences,serverPrefs,locationPrefs;
    private SharedPreferences.Editor sharedEdit, userEdit,serverEdit, locationEdit;

    public PreferencesManager(Context context) {
        this.context = context;
        userPreferences = context.getSharedPreferences(context.getString(R.string.userPrefs), Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        serverPrefs=context.getSharedPreferences(context.getString(R.string.serverPrefs),Context.MODE_PRIVATE);
        locationPrefs=context.getSharedPreferences(context.getString(R.string.locationPrefs),Context.MODE_PRIVATE);
    }

    @Override
    public String getPort() {
        return serverPrefs.getString("portSaved", "http://");
    }

    @Override
    public String getServer() {
        return serverPrefs.getString(context.getString(R.string.server), context.getString(R.string.googleURL));
    }

    @Override
    public String getString(String key, int shared) {
        try {
            if (shared == 1)
                return sharedPreferences.getString(key, "");
            else if(shared==2)
                return userPreferences.getString(key, "");
            else if(shared==4)
                return locationPrefs.getString(key,"");
            else
                return serverPrefs.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean getBoolean(String key, int shared) {
        try {
            if (shared == 1)
                return sharedPreferences.getBoolean(key, false);
            else if(shared==2)
                return userPreferences.getBoolean(key, false);
            else
                return serverPrefs.getBoolean(key, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void putString(String key, String valor, int shared) {
        try {
            if (shared == 1) {
                sharedEdit = sharedPreferences.edit();
                sharedEdit.putString(key, valor);
                sharedEdit.apply();

            } else if(shared==2){
                userEdit = userPreferences.edit();
                userEdit.putString(key, valor);
                userEdit.apply();
            }else if(shared==4){
                locationEdit =locationPrefs.edit();
                locationEdit.putString(key,valor);
                locationEdit.apply();
            }else{
                serverEdit=serverPrefs.edit();
                serverEdit.putString(key,valor);
                serverEdit.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putBoolean(String key, boolean valor, int shared) {
        try {
            if (shared == 1) {
                sharedEdit = sharedPreferences.edit();
                sharedEdit.putBoolean(key, valor);
                sharedEdit.apply();
            }else if(shared==2){
                userEdit = userPreferences.edit();
                userEdit.putBoolean(key, valor);
                userEdit.apply();
            }else{
                serverEdit=serverPrefs.edit();
                serverEdit.putBoolean(key,valor);
                serverEdit.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearPreferences(int shared){
        try {
            if (shared == 1) {
                sharedEdit = sharedPreferences.edit();
                sharedEdit.clear();
                sharedEdit.apply();
            }else if(shared==4) {
                locationEdit =locationPrefs.edit();
                locationEdit.clear();
                locationEdit.apply();
            }else {
                userEdit = sharedPreferences.edit();
                userEdit.clear();
                userEdit.apply();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
