package com.example.mapboxapp.Reroute.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapboxapp.R;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class NavigationFragment extends Fragment implements OnNavigationReadyCallback, NavigationListener,
        ProgressChangeListener {

    private static final double ORIGIN_LONGITUDE = -3.714873;
    private static final double ORIGIN_LATITUDE = 40.397389;
    private static final double DESTINATION_LONGITUDE = -3.712331;
    private static final double DESTINATION_LATITUDE = 40.401686;

    private NavigationView navigationView;
    private DirectionsRoute directionsRoute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateNightMode();
        navigationView = view.findViewById(R.id.navigation_view_fragment);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            navigationView.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navigationView.onDestroy();
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        Point origin = Point.fromLngLat(ORIGIN_LONGITUDE, ORIGIN_LATITUDE);
        Point destination = Point.fromLngLat(DESTINATION_LONGITUDE, DESTINATION_LATITUDE);
        fetchRoute(origin, destination);
    }

    @Override
    public void onCancelNavigation() {
        navigationView.stopNavigation();
        stopNavigation();
    }

    @Override
    public void onNavigationFinished() {
        // no-op
    }

    @Override
    public void onNavigationRunning() {
        // no-op
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        boolean isInTunnel = routeProgress.inTunnel();
        boolean wasInTunnel = wasInTunnel();
        if (isInTunnel) {
            if (!wasInTunnel) {
                updateWasInTunnel(true);
                updateCurrentNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else {
            if (wasInTunnel) {
                updateWasInTunnel(false);
                updateCurrentNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
            }
        }
    }

    private void updateNightMode() {
        if (wasNavigationStopped()) {
            updateWasNavigationStopped(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
            getActivity().recreate();
        }
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(getContext())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        directionsRoute = response.body().routes().get(0);
                        startNavigation();
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
    }

    private void startNavigation() {
        if (directionsRoute == null) {
            return;
        }
        NavigationViewOptions options = NavigationViewOptions.builder()
                .directionsRoute(directionsRoute)
                .shouldSimulateRoute(true)
                .navigationListener(NavigationFragment.this)
                .progressChangeListener(this)
                .build();
        navigationView.startNavigation(options);
    }

    private void stopNavigation() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
//            FragmentNavigationActivity fragmentNavigationActivity = (FragmentNavigationActivity) activity;
//            fragmentNavigationActivity.showPlaceholderFragment();
//            fragmentNavigationActivity.showNavigationFab();
            updateWasNavigationStopped(true);
            updateWasInTunnel(false);
        }
    }

    private boolean wasInTunnel() {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.was_in_tunnel), false);
    }

    private void updateWasInTunnel(boolean wasInTunnel) {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.was_in_tunnel), wasInTunnel);
        editor.apply();
    }

    private void updateCurrentNightMode(int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);
        getActivity().recreate();
    }

    private boolean wasNavigationStopped() {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(getString(R.string.was_navigation_stopped), false);
    }

    public void updateWasNavigationStopped(boolean wasNavigationStopped) {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.was_navigation_stopped), wasNavigationStopped);
        editor.apply();
    }
}
