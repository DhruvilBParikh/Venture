package com.example.venture.repositories;

/*
 * Singleton pattern
 */

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.venture.models.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventsRepository {

    private static final String TAG = "EventsRepository";

    private static EventsRepository instance;
    private ArrayList<Event> dataSet = new ArrayList<>();

    public static EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Event>> getEvents() {
        setEvents();
        MutableLiveData<List<Event>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;

    }

    public void addEvent(Event event) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("events");
        Log.d(TAG, "addEvent: "+event.getTitle());
        reference.push().setValue(event);
    }

    private void setEvents() {
        dataSet.add(
                new Event("1", "sf_trail.jpg", "Trail1", "Santa Cruz", "10 may 2020", "9 am", "This is a trail", "organizer1")
        );
        dataSet.add(
                new Event("2", "trial_image.jpg", "Trail1", "RedWoods", "10 may 2020", "9 am", "This is a trail", "organizer2")
        );
        dataSet.add(
                new Event("3", "sf_trail.jpg", "Trail1", "SF", "10 may 2020", "9 am", "This is a trail", "organizer3")
        );
    }
}
