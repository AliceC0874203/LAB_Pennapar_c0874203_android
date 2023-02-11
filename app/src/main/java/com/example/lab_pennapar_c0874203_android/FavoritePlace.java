package com.example.lab_pennapar_c0874203_android;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

public class FavoritePlace implements Serializable {
    public transient static final String SHARED_PREFERENCES_NAME = "favorite_place_preferences";
    public transient static final String KEY_NAME = "favoriteLocationList_json";

    private String title;
    private double lat;
    private double lng;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public FavoritePlace(String title, double lat, double lng) {
        this.title = title;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return title;
    }
}
