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
    private String desc;
    private String organizer;


//    public Event(Context current){
//        this.context = current;
//    }

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
    public Event() {


        this.id = null;
        this.imageBitmap = null;//BitmapFactory.decodeResource(getResources() ,R.drawable.sf_trail);
        this.image = null;
        this.title = null;
        this.location = null;
        this.latitude = 0;
        this.longitude = 0;
        this.date = null;
        this.time = null;
        this.desc = null;
        this.organizer = null;

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

    public String getDesc() {
        return desc;
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
}
