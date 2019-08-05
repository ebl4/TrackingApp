package com.example.mapboxapp.Tracking.Utils;

import android.content.Context;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Model.GPSCoordinates;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingService {

    public static void getPointFromAddress(Context context, List<Point> points, GPSCoordinates gpsCoordinates, MapboxGeocoding geocoding) {
        geocoding = MapboxGeocoding.builder()
                .accessToken(context.getString(R.string.access_token))
                .query(gpsCoordinates.partida)
                .build();

        geocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();

                if (results.size() > 0) {
                    Point firstResultPoint = results.get(0).center();
                    points.add(firstResultPoint);
                    points.add(Point.fromLngLat(gpsCoordinates.destPoint.longitude(),
                            gpsCoordinates.destPoint.latitude()));
                    //Log.d(TAG, "onResponse: " + firstResultPoint.toString());
                } else {
                    // No result for your request were found.
                    //Log.d(TAG, "onResponse: No result found");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
