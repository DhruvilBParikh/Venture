package com.example.venture.viewmodels.explore;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.repositories.EventsRepository;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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


    public void init(String eventType, String location, double latitude, double longitude) {
//        if (mEvents != null)
//            return;
        Log.d("inside INIT", eventType);
        mRepo = EventsRepository.getInstance();
        mEvents = mRepo.getEvents(eventType, location, latitude, longitude);

    }

    public LiveData<List<Event>> getEvents() {
        return mEvents;
    }

    public void addEvents(final List<Event> addevent) {
        
        mEvents.setValue(addevent);
//        Log.d("---currentEvent1: ---", addevent.toString());
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//
//                Log.d("---currentEvent: ---", addevent.get(0).getTitle());
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
    }
}
