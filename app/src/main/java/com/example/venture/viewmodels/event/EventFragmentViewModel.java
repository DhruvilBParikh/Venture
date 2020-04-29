package com.example.venture.viewmodels.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.repositories.EventsRepository;

import java.util.HashMap;

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

    public LiveData<Boolean> hasJoinedEvent(String userId, String eventId) {
        return EventsRepository.getInstance().hasJoinedEvent(userId, eventId);
    }

    public LiveData<String> getEventUri(String userId, String eventId) {
        return EventsRepository.getInstance().getEventUri(userId, eventId);
    }

    public void addJoinedEvent(String userId, String eventId,  HashMap<String, String> eventObj) {
        EventsRepository.getInstance().addJoinedEvent(userId, eventId, eventObj);
    }

    public void removeJoinedEvent(String userId, String eventId) {
        EventsRepository.getInstance().removeJoinedEvent(userId, eventId);
    }

}
