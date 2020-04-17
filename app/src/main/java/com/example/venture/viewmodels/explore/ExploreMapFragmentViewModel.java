package com.example.venture.viewmodels.explore;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.repositories.EventsRepository;

import java.util.List;

public class ExploreMapFragmentViewModel extends ViewModel {

    private static final String TAG = "ExploreMapFragmentVModel";

    private MutableLiveData<List<Event>> mEvents;
    private EventsRepository mRepo;
    private static ExploreMapFragmentViewModel instance;

    public static ExploreMapFragmentViewModel getInstance() {
        if (instance == null) {
            instance = new ExploreMapFragmentViewModel();
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

//    public String addEvent(Event event) {
//        return EventsRepository.getInstance().addEvent(event);
//    }

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

}
