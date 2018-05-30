package com.example.biespiel_pc.and_2.Model;

/**
 * Created by Res Non Verba on 04/01/2018.
 */

public class LocationModel {
    private double latitude, longitude;

    public LocationModel() {
    }

    public LocationModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
