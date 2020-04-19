package com.example.venture.repositories;

/*
 * Singleton pattern
 */

import android.nfc.Tag;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.venture.models.Event;
import com.example.venture.models.User;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.example.venture.viewmodels.explore.ExploreMapFragmentViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class EventsRepository {

    private String TAG = "-----EventsRepo------";

    private List<Event> addList = new ArrayList<>();
    private static EventsRepository instance;
    private ArrayList<Event> allEventsdataSet = new ArrayList<>();
    private ArrayList<Event> dataSet = new ArrayList<>();
    private static DatabaseReference mDatabase;
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;
    private ExploreMapFragmentViewModel mExloreMapFragmentViewModel;


    public static EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public MutableLiveData<List<Event>> getEvents() {

        if (dataSet.size() == 0)
            loadEvents();
        MutableLiveData<List<Event>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;

    }

    public String addEvent(Event event, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("events");
        Log.d(TAG, "addEvent: "+event.getTitle());
        reference.push().setValue(event);
        String eventId = reference.push().getKey();
        Log.d(TAG, "addEvent: event id is::::" + eventId);

        HashMap<String, String> eventMap = new HashMap<>();
        eventMap.put("title", event.getTitle());
        eventMap.put("location", event.getLocation());
        eventMap.put("date", event.getDate());
        eventMap.put("time", event.getTime());
        eventMap.put("image", event.getImage());

        addCreatedEvent(eventMap,eventId,userId);
        return eventId;
    }

    public void addCreatedEvent(HashMap<String, String> eventMap, String eventId, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("createdEvents").child(userId).child(eventId).setValue(eventMap);
        reference.child("joinedEvents").child(userId).child(eventId).setValue(eventMap);
    }

    public List<Event> getEventList() {
        return dataSet;
    }

    private void loadEvents() {
        DatabaseReference mreference = mDatabase.child("events");
        mExploreEventListFragmentViewModel = ExploreEventListFragmentViewModel.getInstance();
        mExloreMapFragmentViewModel = ExploreMapFragmentViewModel.getInstance();
        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Clearing AddList");
                addList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final Event newEvent = new Event();

                    Log.d(TAG, "onDataChange: " + snapshot.getKey());
                    Log.d(TAG, "onDataChange: " + snapshot.child("title").getValue());
                    Log.d(TAG, "onDataChange: " + snapshot.child("location").getValue());
                    Log.d(TAG, "onDataChange: " + snapshot.child("latitude").getValue());
                    Log.d(TAG, "onDataChange: " + snapshot.child("longitude").getValue());

                    newEvent.setId(snapshot.getKey());
                    newEvent.setTitle(snapshot.child("title").getValue().toString());
                    newEvent.setLocation(snapshot.child("location").getValue().toString());
                    newEvent.setLatitude(Double.parseDouble(snapshot.child("latitude").getValue().toString()));
                    newEvent.setLongitude(Double.parseDouble(snapshot.child("longitude").getValue().toString()));

                    addList.add(newEvent);

                }
                Log.d("-------------------", addList.toString());
                mExploreEventListFragmentViewModel.addEvents(addList);
                mExloreMapFragmentViewModel.addEvents(addList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public MutableLiveData<List<Event>> getCreatedEvents() {

        if (dataSet.size() == 0)
            loadEvents();
        MutableLiveData<List<Event>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;

    }



}
