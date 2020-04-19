package com.example.venture.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.venture.models.CreatedEvent;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.CreatedEventViewModel;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.example.venture.viewmodels.explore.ExploreMapFragmentViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreatedEventsRepository {

    private static final String TAG = "CreatedEventsRepository";

    private List<CreatedEvent> addList = new ArrayList<>();
    private static CreatedEventsRepository instance;
    private ArrayList<CreatedEvent> allEventsdataSet = new ArrayList<>();
    private ArrayList<CreatedEvent> dataSet = new ArrayList<>();
    private static DatabaseReference mDatabase;
    private CreatedEventViewModel mCreatedEventViewModel;

    public static CreatedEventsRepository getInstance() {
        if (instance == null) {
            instance = new CreatedEventsRepository();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public MutableLiveData<List<CreatedEvent>> getEvents() {

        if (dataSet.size() == 0)
            loadEvents();
        MutableLiveData<List<CreatedEvent>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;

    }

    public void addEvent(HashMap<String, String> eventMap, String eventId, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("createdEvents");
        reference.child(userId).child(eventId).setValue(eventMap);
        reference.child(userId).child(eventId).setValue(eventMap);
    }

    public List<CreatedEvent> getEventList() {
        return dataSet;
    }

    private void loadEvents() {
        DatabaseReference mreference = mDatabase.child("events");
        mCreatedEventViewModel = CreatedEventViewModel.getInstance();
//        mreference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "Clearing AddList");
//                addList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    final Event newEvent = new Event();
//
//                    Log.d(TAG, "onDataChange: " + snapshot.getKey());
//                    Log.d(TAG, "onDataChange: " + snapshot.child("title").getValue());
//                    Log.d(TAG, "onDataChange: " + snapshot.child("location").getValue());
//                    Log.d(TAG, "onDataChange: " + snapshot.child("latitude").getValue());
//                    Log.d(TAG, "onDataChange: " + snapshot.child("longitude").getValue());
//
//                    newEvent.setId(snapshot.getKey());
//                    newEvent.setTitle(snapshot.child("title").getValue().toString());
//                    newEvent.setLocation(snapshot.child("location").getValue().toString());
//                    newEvent.setLatitude(Double.parseDouble(snapshot.child("latitude").getValue().toString()));
//                    newEvent.setLongitude(Double.parseDouble(snapshot.child("longitude").getValue().toString()));
//
//                    addList.add(newEvent);
//
//                }
//                Log.d("-------------------", addList.toString());
//                mExploreEventListFragmentViewModel.addEvents(addList);
//                mExloreMapFragmentViewModel.addEvents(addList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

}
