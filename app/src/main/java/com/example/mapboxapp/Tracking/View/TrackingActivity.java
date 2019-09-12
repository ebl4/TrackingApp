package com.example.mapboxapp.Tracking.View;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Presenter.TrackingPresenter;
import com.example.mapboxapp.Tracking.Presenter.TrackingPresenterInt;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.TrackingService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.mapboxapp.Tracking.View.Fragments.VisitFragment.gpsCoordinates;

public class TrackingActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, RouteListener, ProgressChangeListener {

    private static final String TAG = "TrackingActivity";
    private NavigationView navigationView;
    private boolean dropoffDialogShown, navigationCanceled, arrived, rerouteAlong;
    private Location lastKnownLocation;
    private List<Point> points = new ArrayList<>();
    private MapboxGeocoding geocoding;
    private RouteProgress routeProgress;

    FloatingActionButton fabFinish;
    private TextView txtDistance;

    private PreferenceConfig prefConfig;
    private TrackingPresenterInt presenter;
    private Double totalDistanceTraveled;
    long tempoInicioViagem, tempoFimViagem;
    private Route route;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.access_token));
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);

        initComponents();
        setContentView(R.layout.activity_navigation);

        txtDistance = findViewById(R.id.txtDistance);
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
        startNavigation(route.getRoutes().get(route.getRouteSelectedID()));
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
        Intent intent = new Intent(this, ResumeTrafficView.class);
        startActivity(intent);
        navigationView.stopNavigation();
        finish();
    }

    public void saveData(){
        tempoFimViagem = System.currentTimeMillis();
        if(routeProgress != null){
            prefConfig.putString(getString(R.string.distanceTraveled), TrackingService.formatDistance(totalDistanceTraveled));
        }
        prefConfig.putString(getString(R.string.resumeTime), TrackingService.formatResumeTime(tempoFimViagem - tempoInicioViagem));
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
        Log.w("TrackingActivity", "Testando rerota");
    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {

        if (!dropoffDialogShown && !points.isEmpty()) {
            //showDropoffDialog();
            arrived = true;
            rerouteAlong = false;
            dropoffDialogShown = true; // Accounts for multiple arrival events
            //Toast.makeText(this, "You have arrived! ", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Distância: " +formatDistance(routeProgress.distanceTraveled()), Toast.LENGTH_SHORT).show();
        }
    }

    public void verifyFinishNavigation(Location location, double distanceTo){
        float[] result = new float[1];
        if(!rerouteAlong && distanceTo > 0) {
            Location.distanceBetween(gpsCoordinates.destPoint.latitude(), gpsCoordinates.destPoint.longitude(),
                    location.getLatitude(), location.getLongitude(), result);
            if (result[0] > 50) {
                fetchRoute(getLastKnownLocation(), gpsCoordinates.destPoint);
                rerouteAlong = true;
                arrived = true;
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        double distanceTo = 0;
        this.routeProgress = routeProgress;
        if(lastKnownLocation == null){
            distanceTo = 0.0;
        }
        else{
            distanceTo = location.distanceTo(lastKnownLocation);
        }
        totalDistanceTraveled += distanceTo;
        if(arrived){
            verifyFinishNavigation(location, distanceTo);
        }

        lastKnownLocation = location;
        navigationView.resumeCamera(location);

        if(routeProgress.distanceRemaining() < 50){
            fabFinish.setVisibility(View.VISIBLE);
        }
        txtDistance.setText(TrackingService.formatDistance(totalDistanceTraveled));
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
        arrived = false;
        rerouteAlong = false;

        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this);
        return options.build();
    }

    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }

    public void initComponents(){
        navigationCanceled = false;
        prefConfig = new PreferenceConfig(this);
        presenter = new TrackingPresenter();
        totalDistanceTraveled = 0.0;

        prefConfig.putString(getString(R.string.partida), gpsCoordinates.partida);
        prefConfig.putString(getString(R.string.destino), gpsCoordinates.destino);
        TrackingService.getPointFromAddress(this, points, gpsCoordinates, geocoding);

        route = (Route) getIntent().getParcelableExtra("route_obj");
    }
}

