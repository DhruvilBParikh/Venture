package com.example.venture.viewmodels.explore;


import android.util.Log;

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
    private String TAG = "Event View Model";

    public static ExploreEventListFragmentViewModel getInstance() {
        if (instance == null)
            instance = new ExploreEventListFragmentViewModel();
        return instance;
    }

    public void getEvents(String eventType, String location) {
        mrepo.getEvents(eventType, location);
    }

//    public void getSearchEvents(String eventType, String location) {
//        mrepo.getEvents(eventType, location).getValue();
//    }

    public void postEvents(List<Event> eventType) {
        Log.d(TAG,"inside postevents result--------"+eventType.toString() );
        resultEvents.setValue((eventType));
        getResult();
    }

    public LiveData<List<Event>> getResult() {
        Log.d(TAG,"inside get result--------"+resultEvents.getValue() );
        return resultEvents;
    }

}
