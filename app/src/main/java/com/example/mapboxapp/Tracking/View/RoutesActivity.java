package com.example.mapboxapp.Tracking.View;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;
import com.example.mapboxapp.Tracking.Utils.TrackingService;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import org.jetbrains.annotations.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.mapboxapp.Tracking.View.Fragments.VisitFragment.gpsCoordinates;
import static com.mapbox.core.constants.Constants.PRECISION_6;

/* ===================================================================================
-   Generate alternative routes for choice , and drawing on the map each user choice
-   Rhayden
 =================================================================================== */

public class RoutesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Style styleG;
    private Response<DirectionsResponse> routes;
    private DirectionsRoute minRoute;
    private MarkerViewManager markerViewManager;
    private int routeSelectedID;
    private boolean routeIsSet, routeFetched;

    private PreferenceConfigInt prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_routes);

        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        prefs = new PreferenceConfig(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ============================================================
        // Create map
        // ============================================================
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(Style.LIGHT, style -> {

                markerViewManager = new MarkerViewManager(mapView, mapboxMap);

                // ============================================================
                // Add the marker image to map
                // ============================================================
                style.addImage("marker-icon-id",
                        BitmapFactory.decodeResource(
                                RoutesActivity.this.getResources(), R.drawable.round_gps_fixed_black_18dp));

                GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(
                        Point.fromLngLat(gpsCoordinates.longitude, gpsCoordinates.latitude)));
                style.addSource(geoJsonSource);

                SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
                symbolLayer.withProperties(PropertyFactory.iconImage("marker-icon-id"));
                style.addLayer(symbolLayer);

                // ---------------------------------------------------------------------------
                // Adding annotation marker on map
                // ---------------------------------------------------------------------------
                // Use an XML layout to create a View object
                View customView = LayoutInflater.from(RoutesActivity.this).inflate(
                        R.layout.marker_view_bubble, null);
                customView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

                TextView titleTextView = customView.findViewById(R.id.marker_window_title);
                titleTextView.setText(getString(R.string.string_seu_local));

                TextView snippetTextView = customView.findViewById(R.id.marker_window_snippet);
                snippetTextView.setText(gpsCoordinates.partida);

                // Use the View to create a MarkerView which will eventually be given to
                // the plugin's MarkerViewManager class
                MarkerView markerView = new MarkerView(new LatLng(gpsCoordinates.latitude, gpsCoordinates.longitude), customView);
                markerViewManager.addMarker(markerView);
                // ---------------------------------------------------------------------------

                // -------------------

                style.addImage("marker-icon-id-2",
                        BitmapFactory.decodeResource(
                                RoutesActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));

                geoJsonSource = new GeoJsonSource("source-id-2", Feature.fromGeometry(
                        Point.fromLngLat(gpsCoordinates.destPoint.longitude(), gpsCoordinates.destPoint.latitude())));
                style.addSource(geoJsonSource);

                symbolLayer = new SymbolLayer("layer-id-2", "source-id-2");
                symbolLayer.withProperties(PropertyFactory.iconImage("marker-icon-id-2"));
                style.addLayer(symbolLayer);

                // ---------------------------------------------------------------------------
                // Adding annotation marker on map
                // ---------------------------------------------------------------------------
                // Use an XML layout to create a View object
                View customView2 = LayoutInflater.from(RoutesActivity.this).inflate(
                        R.layout.marker_view_bubble, null);
                customView2.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

                TextView titleTextView2 = customView2.findViewById(R.id.marker_window_title);
                titleTextView2.setText(getString(R.string.string_seu_destino));

                TextView snippetTextView2 = customView2.findViewById(R.id.marker_window_snippet);
                snippetTextView2.setText(gpsCoordinates.destino);

                // Use the View to create a MarkerView which will eventually be given to
                // the plugin's MarkerViewManager class
                MarkerView markerView2 = new MarkerView(new LatLng(gpsCoordinates.destPoint.latitude(),
                        gpsCoordinates.destPoint.longitude()), customView2);

                markerViewManager.addMarker(markerView2);
                // ---------------------------------------------------------------------------

                // ============================================================

                // ============================================================
                // Map settings
                // ============================================================
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);
                this.mapboxMap = mapboxMap;
                this.styleG = style;
                this.routeIsSet = false;
                routeFetched = false;
                updateCameraPosition();
                // ============================================================
            });
        });
        // ============================================================

        setNavigationDrawer();
    }

    private void setNavigationDrawer() {
        // =====================================================
        // Build routes on navigationView Menu
        // =====================================================
        Menu menu = navigationView.getMenu();

        NavigationRoute.builder(this)
                .accessToken(getString(R.string.access_token))
                .origin(Point.fromLngLat(gpsCoordinates.longitude, gpsCoordinates.latitude))
                .destination(gpsCoordinates.destPoint)
                .alternatives(Boolean.TRUE)
                .language(new Locale("pt-BR"))
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<DirectionsResponse> call,
                                           @NotNull Response<DirectionsResponse> response)
                    {
                        DecimalFormat numberFormat = new DecimalFormat("#.00"); // work only with 2 decimal places
                        // For each route create a menu item with data of the route
                        for(int i = 0; i < Objects.requireNonNull(response.body()).routes().size(); ++i)
                        {
                            if(!routeFetched){
                                minRoute = response.body().routes().get(i);
                                routeFetched = true;
                            }
                            if(response.body().routes().get(i).distance() < minRoute.distance()){
                                minRoute = response.body().routes().get(i);
                            }
                            double durationMin = (response.body().routes().get(i).duration()); // route duration
                            double distance = (response.body().routes().get(i).distance()); // route distance

                            // Menu title
                            menu.add(i, i, Menu.NONE,
                                    "Rota " + (i+1) + ":  " + TrackingService.formatDistance(distance))
                                    .setIcon(R.drawable.ic_navigation);

                            // Menu info with estimated time
                            menu.add(i, i, Menu.NONE,
                                    "Tempo estimado: " + TrackingService.formatDuration(durationMin))
                                    .setIcon(R.drawable.ic_car).setEnabled(false);

                            // Menu with most specific place name for this route (DEVELOPING)
                        }

                        // Retrieve the directions routes from the API response
                        routes = response;
                        prefs.putString(getString(R.string.minRouteDistance),  TrackingService.formatDistance(minRoute.distance()));
                        prefs.putString(getString(R.string.minRouteTime),  TrackingService.formatDuration(minRoute.duration()));
                        if(minRoute.legs() != null)
                            prefs.putString(getString(R.string.minRouteAddress), Objects.requireNonNull(minRoute.legs()).get(0).summary());
                    }

                    @Override
                    public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable t) {
                        Timber.d("onFailure: %s", t.getMessage());
                    }
                });
        // =====================================================
    }

    private void drawRoute(int itemID) {
        // ============================================================
        // Drawing selected route on map
        // ============================================================
        styleG.removeLayer("routeLayer"); // Remove last line
        styleG.removeSource("line-source-route"); // Remove last source

        // Get each coordinates from route
        LineString lineString = LineString.fromPolyline(
                Objects.requireNonNull(Objects.requireNonNull(
                        routes.body()).routes().get(itemID).geometry()), PRECISION_6);

        List<Point> coordinates = lineString.coordinates();

        List<Point> routeCoordinates = new ArrayList<>(); // arrayList to save each waypoint in this route
        for(int i = 0; i < coordinates.size(); ++i) {
            // adding each coordinates in this route on the List
            routeCoordinates.add(Point.fromLngLat(
                    coordinates.get(i).longitude(), coordinates.get(i).latitude()));
        }

        // Create the LineString from the list of coordinates and then make a GeoJSON
        // FeatureCollection so we can add the line to our map as a layer.
        styleG.addSource(new GeoJsonSource("line-source-route",
                FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
                        LineString.fromLngLats(routeCoordinates)
                )})));

        // The layer properties for our line. This is where we make the line dotted, set the color, etc.
        styleG.addLayer(new LineLayer("routeLayer", "line-source-route").withProperties(
                PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
        ));

        routeSelectedID = itemID;
        this.routeIsSet = true;
        updateCameraPosition();
        // ============================================================
    }

    public void startNavigation() {
        startNavigationActivity();
    }

    public void startNavigationActivity() {
        Route routeModel = new Route(Objects.requireNonNull(routes.body()).routes(), routeSelectedID);
        Intent intent = new Intent(this, TrackingActivity.class);
        intent.putExtra("route_obj", (Parcelable) routeModel);
        startActivity(intent);
    }

    public void updateCameraPosition() {
        // Update camera to the start position
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(gpsCoordinates.latitude, gpsCoordinates.longitude))
                .zoom(12)
                .tilt(20)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 10);
    }

    // ============================================================
    // Implements
    // ============================================================
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, "Rota " + (item.getItemId() + 1), Toast.LENGTH_SHORT).show();
        drawRoute(item.getItemId()); // Drawing selected route on map
        drawerLayout.closeDrawer(GravityCompat.START);
        return(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_nav, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        if (id == R.id.btn_start_navigation) {
            if(this.routeIsSet) { // Check if route is set
                startNavigation();
            } else {
                Toast.makeText(this, "Selecione uma rota antes de iniciar a navegação.",
                        Toast.LENGTH_LONG).show();
            }

            return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
    // ============================================================

    // ============================================================
    // Mapbox
    // ============================================================
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
