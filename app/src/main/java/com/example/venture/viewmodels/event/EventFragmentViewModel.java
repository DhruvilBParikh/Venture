package com.example.venture.viewmodels.event;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.repositories.EventsRepository;

public class EventFragmentViewModel extends ViewModel {

    private static final String TAG = "EventFragmentViewModel";

    private static EventFragmentViewModel instance;
    private MutableLiveData<Event> event;
    private EventsRepository mRepo;

    public static EventFragmentViewModel getInstance() {
        if(instance==null)
            instance = new EventFragmentViewModel();
        return instance;
    }

    public void init(String id) {
        if(event==null)
            event = EventsRepository.getInstance().getEvent(id);
        mRepo = EventsRepository.getInstance();
        event = mRepo.getEvent(id);
    }

    public LiveData<Event> getEvent() {
        return event;
    }

    public void addEvent(final Event e) {
        Log.d(TAG, "addEvent: " + e.toString());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void Void) {
                super.onPostExecute(Void);

                Log.d("---currentEvent: ---", e.toString());
                event.postValue(e);
                Log.d("---end: ---", "here");

            }

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }
        }.execute();
    }

}
