package com.survey.hujuhj.Model;

public class location {

    double lat;
    double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public location() {
    }

    public location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
