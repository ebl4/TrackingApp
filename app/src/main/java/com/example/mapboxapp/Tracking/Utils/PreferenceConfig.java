package com.example.mapboxapp.Tracking.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mapboxapp.R;

public class PreferenceConfig implements PreferenceConfigInt {

    private Context context;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;

    public PreferenceConfig(Context context){
        this.context=context;
        sharedPref=context.getSharedPreferences(context.getString(R.string.userShared),Context.MODE_PRIVATE);
    }

    @Override
    public String getString(String key) {
        return sharedPref.getString(key,"");
    }

    @Override
    public int getInt(String key) {
        return sharedPref.getInt(key,1);
    }

    @Override
    public boolean getBoolean(String key) {
        return sharedPref.getBoolean(key,false);
    }

    @Override
    public void putString(String key, String valor) {
        Log.d("SaveString",key+"/"+valor);
        sharedEdit=sharedPref.edit();
        sharedEdit.putString(key,valor);
        sharedEdit.apply();
    }

    @Override
    public void putInt(String key, int valor) {
        Log.d("SaveString",key+"/"+valor);
        sharedEdit=sharedPref.edit();
        sharedEdit.putInt(key,valor);
        sharedEdit.apply();
    }

    @Override
    public void putBoolean(String key, boolean valor) {
        sharedEdit=sharedPref.edit();
        sharedEdit.putBoolean(key,valor);
        sharedEdit.apply();
    }

    @Override
    public void clearPreferences() {
        sharedEdit=sharedPref.edit();
        sharedEdit.clear();
        sharedEdit.apply();
    }
}
