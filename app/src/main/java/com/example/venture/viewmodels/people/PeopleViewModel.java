package com.example.venture.viewmodels.people;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.Event;
import com.example.venture.models.People;
import com.example.venture.repositories.EventsRepository;
import com.example.venture.repositories.PeopleRepository;
import com.example.venture.viewmodels.event.EventFragmentViewModel;

import java.util.HashMap;
import java.util.List;

public class PeopleViewModel extends ViewModel {

    private static final String TAG = "PeopleViewModel";

    private static PeopleViewModel instance;
    private MutableLiveData<List<People>> peopleData;
    private PeopleRepository mRepo;

    public static PeopleViewModel getInstance() {
        if(instance==null)
            instance = new PeopleViewModel();
        return instance;
    }

    public void init(String id) {
        if(peopleData==null)
            peopleData = PeopleRepository.getInstance().getPeopleByEventId(id);
        mRepo = PeopleRepository.getInstance();
        peopleData = mRepo.getPeopleByEventId(id);
    }

    public LiveData<List<People>> getPeople(String id) {
        return peopleData;
    }

    public void addPeople(String eventId, String personId, HashMap<String, String> personDetail) {
        PeopleRepository.getInstance().addPeople(eventId, personId, personDetail);
    }

    public void removePeople(String eventId, String personId) {
        PeopleRepository.getInstance().removePeople(eventId, personId);
    }

}
