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
import com.example.venture.viewmodels.event.EventFragmentViewModel;
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
    private Event event;
    private static EventsRepository instance;
    private ArrayList<Event> allEventsdataSet = new ArrayList<>();
    private ArrayList<Event> dataSet = new ArrayList<>();
    private static DatabaseReference mDatabase;
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;
    private ExploreMapFragmentViewModel mExloreMapFragmentViewModel;
    private EventFragmentViewModel eventFragmentViewModel;


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

    public String addEvent(Event event) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("events");
        Log.d(TAG, "addEvent: "+event.getTitle());
        reference.push().setValue(event);
        String eventId = reference.push().getKey();
        Log.d(TAG, "addEvent: event id is::::" + eventId);
        return eventId;
    }
    public List<Event> getEventList() {
        return dataSet;
    }

    public MutableLiveData<Event> getEvent(String id) {
        loadEvent(id);
        MutableLiveData<Event> e = new MutableLiveData<>();
        e.setValue(event);
        return e;
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

    public void loadEvent(String id) {
        final DatabaseReference mreference = mDatabase.child("events").child(id);
        eventFragmentViewModel = EventFragmentViewModel.getInstance();

        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: found 1 event--------------");
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("date"));
//                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("image"));
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("organizer"));
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("latitude"));
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("longitude"));
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("details"));
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("location"));
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("time"));
                Log.d(TAG, "onDataChange: " + ((HashMap)dataSnapshot.getValue()).get("title"));

                event = new Event();

                event.setDate(((HashMap)dataSnapshot.getValue()).get("date").toString());
//                event.setImage(((HashMap)dataSnapshot.getValue()).get("image").toString());
                event.setOrganizerId(((HashMap)dataSnapshot.getValue()).get("organizerId").toString());
                event.setOrganizer(((HashMap)dataSnapshot.getValue()).get("organizer").toString());
                event.setLatitude(Double.parseDouble(((HashMap)dataSnapshot.getValue()).get("latitude").toString()));
                event.setLongitude(Double.parseDouble(((HashMap)dataSnapshot.getValue()).get("longitude").toString()));
                event.setDetails(((HashMap)dataSnapshot.getValue()).get("details").toString());
                event.setLocation(((HashMap)dataSnapshot.getValue()).get("location").toString());
                event.setTime(((HashMap)dataSnapshot.getValue()).get("time").toString());
                event.setTitle(((HashMap)dataSnapshot.getValue()).get("title").toString());

                eventFragmentViewModel.addEvent(event);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
