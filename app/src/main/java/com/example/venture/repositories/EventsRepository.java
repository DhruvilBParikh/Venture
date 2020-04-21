package com.example.venture.repositories;

/*
 * Singleton pattern
 */

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsRepository  {

    private static String TAG = "Repository";

    private static DatabaseReference mreference;
    private static EventsRepository instance;
    private static DatabaseReference mDatabase;
    private boolean initListeners = false;
    private ValueEventListener mAllValueEventListener;
    private ValueEventListener mSearchValueEventListener;

    private MutableLiveData<List<Event>> allEventsData = new MutableLiveData<>();
    private List<Event> allList = new ArrayList<>();

    private MutableLiveData<List<Event>> searchEventsData = new MutableLiveData<>();
    private List<Event> searchList = new ArrayList<>();



    public static EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Event>> getEvents(String eventType, String location) {
        if(!initListeners)
            initListeners();

        switch (eventType) {
            case "searchEvents":
                loadSearchEvents(location);
                searchEventsData.setValue(searchList);
                return searchEventsData;

            default:
                if (allList.size() == 0)
                    loadAllEvents();
                allEventsData.setValue(allList);
        }
        return allEventsData;
    }

    private void initListeners() {
        mAllValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Clearing AddList");
                allList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, snapshot.getKey());
                    Log.d(TAG, snapshot.child("location").toString());

                    final Event newEvent = new Event();
                    newEvent.setTitle(snapshot.child("title").getValue().toString());
                    newEvent.setLocation(snapshot.child("location").getValue().toString());
                    newEvent.setLatitude((Double) snapshot.child("latitude").getValue());
                    newEvent.setLongitude((Double) snapshot.child("longitude").getValue());
                    allList.add(newEvent);

                }
                Log.d("-------------------", allList.toString());

                allEventsData.postValue(allList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }


    private void loadAllEvents() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mreference = mDatabase.child("trialevents");
        mreference.addValueEventListener(mAllValueEventListener);

    }

    private void loadSearchEvents(final String location) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mreference = mDatabase.child("trialevents");
        mSearchValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "loadSearchEvents");
                searchList.clear();
                searchEventsData = new MutableLiveData<>();
                String searchLocation = location.toLowerCase();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dbCity = snapshot.child("city").getValue().toString().toLowerCase();
                    String dbState = snapshot.child("state").getValue().toString().toLowerCase();
                    Log.d("To search", location);

                    if (searchLocation.equals(dbCity) || searchLocation.equals(dbState)) {
                        final Event newEvent = new Event();
                        newEvent.setTitle(snapshot.child("title").getValue().toString());
                        newEvent.setLocation(snapshot.child("location").getValue().toString());
                        newEvent.setLatitude((Double) snapshot.child("latitude").getValue());
                        newEvent.setLongitude((Double) snapshot.child("longitude").getValue());
                        searchList.add(newEvent);
                    }
                }
                Log.d("-------------------", searchList.get(0).getTitle());
                searchEventsData.postValue(searchList);
                ExploreEventListFragmentViewModel.getInstance().postSearchEvents(searchList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mreference.addListenerForSingleValueEvent(mSearchValueEventListener);

    }
}