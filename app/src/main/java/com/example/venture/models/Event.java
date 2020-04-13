package com.example.venture.models;

public class Event {

    private String id;
    private String image;
    private String title;
    private String location;
    private String date;
    private String time;
    private String desc;
    private String organizer;

    public Event(String id, String image, String title, String location, String date, String time, String desc, String organizer) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.organizer = organizer;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
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
