package com.example.mapboxapp.Tracking.View;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.TrackingService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.INVISIBLE;
import static com.example.mapboxapp.Tracking.View.VisitActivity.gpsCoordinates;

public class TrackingActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, RouteListener, ProgressChangeListener {

    private static final String TAG = "TrackingActivity";
    private NavigationView navigationView;
    private boolean dropoffDialogShown, navigationCanceled;
    private Location lastKnownLocation;
    private List<Point> points = new ArrayList<>();
    private MapboxGeocoding geocoding;
    private RouteProgress routeProgress;
    FloatingActionButton fabFinish;

    private PreferenceConfig prefConfig;
    private Double totalDistanceTraveled;
    long tempoInicioViagem, tempoFimViagem;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.access_token));
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        navigationCanceled = false;
        prefConfig = new PreferenceConfig(this);
        totalDistanceTraveled = 0.0;

        prefConfig.putString(getString(R.string.partida), gpsCoordinates.partida);
        prefConfig.putString(getString(R.string.destino), gpsCoordinates.destino);
        TrackingService.getPointFromAddress(this, points, gpsCoordinates, geocoding);

        setContentView(R.layout.activity_navigation);

        fabFinish = findViewById(R.id.fabFinish);
        navigationView = findViewById(R.id.nvView);
        navigationView.onCreate(savedInstanceState);

        navigationView.initialize(this);
        tempoInicioViagem = System.currentTimeMillis();

        fabFinish.setVisibility(View.INVISIBLE);
        fabFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationFinished();
            }
        });
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
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.alertInterrupted));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                (dialogInterface, in) -> {
                    // If the navigation view didn't need to do anything, call super
                    if (!navigationView.onBackPressed()) {
                        //super.onBackPressed();
                        navigationCanceled = true;
                        if(navigationView != null){
                            navigationView.stopNavigation();
                        }
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dropoff_dialog_negative_text),
                (dialogInterface, in) -> {
                    // Do nothing
                });
        alertDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
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
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
        finish();
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        if(points.size() >= 2) {
            fetchRoute(points.remove(0), points.remove(0));
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage(getString(R.string.dropoff_dialog_text));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.error_address_navigation),
                    (dialogInterface, in) -> finish());
            alertDialog.show();
        }
        //navigationView.findViewById(R.id.soundFab).setVisibility(View.GONE);
    }

    @Override
    public void onCancelNavigation() {
        // Navigation canceled, finish the activity
        navigationCanceled = true;
        Intent intent = new Intent(this, CancelNavigationActivity.class);
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onNavigationFinished() {
        // Intentionally empty
        saveData();
        prefConfig.putString(getString(R.string.navigationStatus), navigationCanceled ?
                getString(R.string.viagemCancelada) : getString(R.string.viagemConcluida));
        Intent intent = new Intent(this, ResumeTrafficActivity.class);
        startActivity(intent);
        navigationView.stopNavigation();
        finish();
    }

    public void saveData(){
        tempoFimViagem = System.currentTimeMillis();
        if(routeProgress != null){
            prefConfig.putString(getString(R.string.distanceTraveled), formatDistance(totalDistanceTraveled));
        }
        prefConfig.putString(getString(R.string.resumeTime), formatResumeTime(tempoFimViagem - tempoInicioViagem));
    }

    @Override
    public void onNavigationRunning() {
        // Intentionally empty
    }

    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        return true;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {
    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {
        if (!dropoffDialogShown && !points.isEmpty()) {
            showDropoffDialog();
            dropoffDialogShown = true; // Accounts for multiple arrival events
            //Toast.makeText(this, "You have arrived! ", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Distância: " +formatDistance(routeProgress.distanceTraveled()), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        lastKnownLocation = location;
        this.routeProgress = routeProgress;
        if(totalDistanceTraveled <= routeProgress.distanceTraveled()){
            totalDistanceTraveled = routeProgress.distanceTraveled();
        }
        else{
            totalDistanceTraveled += routeProgress.distanceTraveled();
        }

        if(routeProgress.distanceRemaining() < 50){
            fabFinish.setVisibility(View.VISIBLE);
        }
        Toast.makeText(getApplicationContext(), "Distância: " +
                formatDistance(totalDistanceTraveled), Toast.LENGTH_SHORT).show();
    }

    public String formatDistance(double distance){
        DecimalFormat df = new DecimalFormat("#.00");
        String sufix = " m";
        if (distance >= 1000) {
            distance = distance / 1000;
            sufix = " km";
        }
        return df.format(distance)+sufix;
    }

    public String formatResumeTime(long resumeTime){
        if(resumeTime >= 60000){
            return (resumeTime/60000)+" min";
        }
        else{
            return (resumeTime/1000)+" seg";
        }
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions navigationViewOptions = setupOptions(directionsRoute);
        navigationView.startNavigation(navigationViewOptions);
    }

    private void showDropoffDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.dropoff_dialog_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dropoff_dialog_positive_text),
                (dialogInterface, in) -> fetchRoute(getLastKnownLocation(), points.remove(0)));
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dropoff_dialog_negative_text),
                (dialogInterface, in) -> {
                    // Do nothing
                });

        alertDialog.show();
    }

    private void fetchRoute(Point origin, Point destination) {
        Locale ptLocale = new Locale("pt-BR");
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .language(ptLocale)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        startNavigation(response.body().routes().get(0));
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    private NavigationViewOptions setupOptions(DirectionsRoute directionsRoute) {
        dropoffDialogShown = false;

        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(true);
        return options.build();
    }

    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }
}

