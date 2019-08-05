package com.example.mapboxapp.Reroute.activity;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Reroute.fragment.NavigationFragment;
import com.mapbox.mapboxsdk.Mapbox;

public class NavigationActivity extends AppCompatActivity {
    NavigationFragment navigationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_navigation);

        navigationFragment = new NavigationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.replace(R.id.frame_conteudo, navigationFragment).commit();
    }
}
