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

    public void postEvents(List<Event> eventType) {
        Log.d(TAG,"inside postevents result--------"+eventType.toString() );
        resultEvents.setValue((eventType));
        getResult();
    }

    public LiveData<List<Event>> getResult() {
        Log.d(TAG,"inside get result--------"+resultEvents.getValue() );
        return resultEvents;
    }

    public void addEvent(Event event, String userId, String eventId, String eventUri) {
        EventsRepository.getInstance().addEvent(event, userId, eventId, eventUri);
    }
    
//    public void addEvents(final List<Event> addevent) {
//        Log.d("---currentEvent1: ---", addevent.toString());
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//
//                Log.d("---currentEvent: ---", addevent.toString());
//                mEvents.postValue(addevent);
//                Log.d("---end: ---", "here");
//
//            }
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                return null;
//            }
//        }.execute();
//    }

    public LiveData<List<Event>> getCreatedEvents(String userId) {
        return EventsRepository.getInstance().getCreatedEvents(userId);
    }

    public LiveData<List<Event>> getJoinedEvents(String userId) {
        return EventsRepository.getInstance().getJoinedEvents(userId);
    }

    public LiveData<List<Event>> getHistory(String userId) {
        return EventsRepository.getInstance().getHistory(userId);
    }

}
