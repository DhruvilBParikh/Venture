package com.example.venture.models;

import android.graphics.Bitmap;

public class Event {

    private String id;
    private Bitmap imageBitmap;
    private String image;
    private String title;
    private String location;
    private double latitude;
    private double longitude;
    private String date;
    private String time;
    private String details;
    private String organizer;
    private String organizerId;
    private String city;
    private String state;
    private String desc;
    private String eventUri;

    public Event() {
        this.id = null;
        this.imageBitmap = null;
        this.image = null;
        this.title = null;
        this.location = null;
        this.latitude = 0;
        this.longitude = 0;
        this.date = null;
        this.time = null;
        this.desc = null;
        this.organizer = null;
        this.eventUri = null;
    }



    public String getId() {
        return id;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEventUri() {
        return eventUri;
    }

    public void setEventUri(String eventUri) {
        this.eventUri = eventUri;
    }
}
