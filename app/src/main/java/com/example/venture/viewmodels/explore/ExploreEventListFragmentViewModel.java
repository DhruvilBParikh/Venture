package com.example.venture.viewmodels.explore;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.repositories.EventsRepository;

import java.util.List;


public class ExploreEventListFragmentViewModel extends ViewModel {

    EventsRepository mrepo = EventsRepository.getInstance();
    MutableLiveData<List<Event>> resultEvents = mrepo.getEvents("allEvents", "");
    private static ExploreEventListFragmentViewModel instance;

    public static ExploreEventListFragmentViewModel getInstance() {
        if (instance == null)
            instance = new ExploreEventListFragmentViewModel();
        return instance;
    }

    public LiveData<List<Event>> getEvents(String eventType, String location) {
        return mrepo.getEvents(eventType, location);
    }

    public void getSearchEvents(String eventType, String location) {
        mrepo.getEvents(eventType, location).getValue();
    }

    public void postSearchEvents(List<Event> eventType) {
        resultEvents.setValue((eventType));
        getResult();
    }

    public LiveData<List<Event>> getResult() {
        return resultEvents;
    }

}
