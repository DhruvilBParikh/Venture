package com.example.venture.models;

import com.google.android.gms.maps.model.LatLng;

public class Event {

    private String id;
    private String image;
    private String title;
    private String location;
    private double latitude;
    private double longitude;
//    private LatLng latLng;
    private String date;
    private String time;
    private String details;
    private String organizer;
    private String organizerId;
    private String city;
    private String state;


//    private String desc;
    //Dhruvil's Change
     public Event(){

     }
    // public Event(String id, String image, String title, String location, String date, String time, String details, String organizer) {
    //     this.id = id;
    //     this.image = image;
    //     this.title = title;
    //     this.location = location;
    //     this.date = date;
    //     this.time = time;
    //     this.details = details;
    //     this.organizer = organizer;


    //Devanshi's Change
//    public Event(String id, String image, String title, String location, LatLng latLng, String date, String time, String desc, String organizer) {
//        this.id = id;
//        this.image = image;
//        this.title = title;
//        this.location = location;
//        this.latLng = latLng;
//        this.date = date;
//        this.time = time;
//        this.desc = desc;
//        this.organizer = organizer;
//    }
//    public Event() {
//        this.id = null;
//        this.image = null;
//        this.title = null;
//        this.location = null;
//        this.latLng = null;
//        this.date = null;
//        this.time = null;
//        this.desc = null;
//        this.organizer = null;
//
//    }

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

//    public LatLng getLatLng() {
//        return latLng;
//    }

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

//    public void setLatLng(LatLng latLng) {
//        this.latLng = latLng;
//    }

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

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
