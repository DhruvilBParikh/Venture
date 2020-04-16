package com.example.venture.models;

import com.google.android.gms.maps.model.LatLng;

public class Event {

    private String id;
    private String image;
    private String title;
    private String location;
    private double latitude;
    private double longitude;
    private String date;
    private String time;
    private String details;
    private String organizer;

    public Event(){

    }
    public Event(String id, String image, String title, String location, String date, String time, String details, String organizer) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.details = details;
        this.organizer = organizer;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }


    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDetails() {
        return details;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
}
