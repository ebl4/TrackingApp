package com.example.mapboxapp.Tracking.Model;

import com.mapbox.geojson.Point;

public class GPSCoordinates {
    public float longitude = -1; // longitude do local atual
    public float latitude = -1; // latitude do local atual

    public String partida;
    public String destino;

    public Point destPoint; // point contendo as coordenadas do endereco de destino

    public GPSCoordinates(float theLatitude, float theLongitude) {
        longitude = theLongitude;
        latitude = theLatitude;
    }

    public GPSCoordinates(double theLatitude, double theLongitude) {
        longitude = (float) theLongitude;
        latitude = (float) theLatitude;
    }
}
