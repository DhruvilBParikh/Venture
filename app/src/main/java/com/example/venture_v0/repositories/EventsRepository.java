package com.example.venture_v0.repositories;

/*
 * Singleton pattern
 */

import androidx.lifecycle.MutableLiveData;

import com.example.venture_v0.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventsRepository {

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
