package com.example.mapboxapp.Tracking.View;

import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import java.util.ArrayList;
import java.util.List;

/* ==========================================================================
    - Class to store selected route data
    - Rhayden
 ========================================================================== */

public class Route implements Parcelable {
    private List<DirectionsRoute> routesList;
    private int routeSelectedID;

    public Route(List<DirectionsRoute> routesList, int routeSelectedID) {
        this.routesList = routesList;
        this.routeSelectedID = routeSelectedID;
    }

    private Route(Parcel in) {
        routesList = new ArrayList<>();
        in.readList(routesList, Route.class.getClassLoader());
        routeSelectedID = in.readInt();
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    /*
    private void makeReverseGeocode(final LatLng latLng, Context context) {
        try {
            // Build a Mapbox reverse geocoding request
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(context.getString(R.string.access_token))
                    .query(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(@NotNull Call<GeocodingResponse> call,
                                       @NotNull Response<GeocodingResponse> response)
                {
                    List<CarmenFeature> results = Objects.requireNonNull(response.body()).features();
                    if (results.size() > 0) {
                        // Get the first Feature from the successful geocoding response
                        CarmenFeature feature = results.get(0);
                    } // else, no results...
                }

                @Override
                public void onFailure(@NotNull Call<GeocodingResponse> call,
                                      @NotNull Throwable throwable)
                {
                    Timber.d("onFailure: %s", throwable.getMessage());
                }
            });

        } catch (ServicesException servicesException) {
            Timber.d("ServiceException: %s", servicesException.getMessage());
        }
    }
    */

    // ==========================================================
    // Getters and Setters
    // ==========================================================
    public List<DirectionsRoute> getRoutes() {
        return(this.routesList);
    }

    public int getRouteSelectedID() {
        return(this.routeSelectedID);
    }
    // ==========================================================

    // ==========================================================
    // Implements
    // ==========================================================
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(routesList);
        dest.writeInt(routeSelectedID);
    }
    // ==========================================================
}
