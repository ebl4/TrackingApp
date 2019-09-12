package com.example.mapboxapp.Tracking.Utils;

import android.content.Context;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Model.GPSCoordinates;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.text.DecimalFormat;
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
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static String formatDistance(double distance){
        DecimalFormat df = new DecimalFormat("#.00");
        String sufix = " m";
        if (distance >= 1000) {
            distance = distance / 1000;
            sufix = " km";
        }
        return distance >= 1 ? df.format(distance)+sufix : "0.00 m";
    }

    public static String formatResumeTime(long resumeTime){
        return formatDuration(resumeTime/1000);
    }

    public static String formatDuration(double duration){
        DecimalFormat df = new DecimalFormat("#.00");
        if(duration >= 3600){
            return df.format(duration/3600)+" hh";
        }
        else if (duration >= 60){
            return df.format(duration/60)+" mm";
        }
        else{
            return df.format(duration)+" ss";
        }
    }

    public static String changeUnidadeTempo(String unidadeDeslocamento){
        if(unidadeDeslocamento.equalsIgnoreCase("ss")){
            unidadeDeslocamento = "seg";
        }
        else if(unidadeDeslocamento.equalsIgnoreCase("mm")){
            unidadeDeslocamento = "min";
        }
        else if(unidadeDeslocamento.equalsIgnoreCase("hh")){
            unidadeDeslocamento = "h";
        }
        return unidadeDeslocamento;
    }

    public static String formatName(String name, int option){
        if(name.equalsIgnoreCase("")){
            if(option == 1){
                name = "Cliente não informado";
            }
            else{
                name = "Destino não informado";
            }
        }
        return name;
    }
}
