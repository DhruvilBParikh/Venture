package com.example.venture.viewmodels.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.repositories.EventsRepository;

import java.util.List;

public class ExploreEventListFragmentViewModel extends ViewModel {

    private MutableLiveData<List<Event>> mEvents;
    private EventsRepository mRepo;

    public void init() {
        if (mEvents != null)
            return;

        mRepo = EventsRepository.getInstance();
        mEvents = mRepo.getEvents();

    }

    public LiveData<List<Event>> getEvents() {
        return mEvents;
    }
}
