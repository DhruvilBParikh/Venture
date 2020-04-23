package com.example.venture.viewmodels.explore;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.repositories.EventsRepository;

import java.util.HashMap;
import java.util.List;

public class ExploreEventListFragmentViewModel extends ViewModel {

    private MutableLiveData<List<Event>> mEvents;
    private EventsRepository mRepo;
    private static ExploreEventListFragmentViewModel instance;


    public static ExploreEventListFragmentViewModel getInstance() {
        if (instance == null) {
            instance = new ExploreEventListFragmentViewModel();
        }
        return instance;
    }


    public void init() {
        if (mEvents != null)
            return;

        mRepo = EventsRepository.getInstance();
        mEvents = mRepo.getEvents();

    }

    public LiveData<List<Event>> getEvents() {
        return mEvents;
    }

    public void addEvent(Event event, String userId, String eventId) {
        EventsRepository.getInstance().addEvent(event, userId, eventId);
    }
    
    public void addEvents(final List<Event> addevent) {
        Log.d("---currentEvent1: ---", addevent.toString());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Log.d("---currentEvent: ---", addevent.toString());
                mEvents.postValue(addevent);
                Log.d("---end: ---", "here");

            }

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }
        }.execute();
    }

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
