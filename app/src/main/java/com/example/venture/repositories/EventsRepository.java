package com.example.venture.repositories;

/*
 * Singleton pattern
 */
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.venture.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EventsRepository {

    private String TAG = "-----EventsRepo------";

    private static EventsRepository instance;
    private static DatabaseReference mDatabase;

    //Lists
    private List<Event> addList = new ArrayList<>();
    private List<Event> createdList = new ArrayList<>();
    private List<Event> joinedList = new ArrayList<>();

    //MutableLiveData
    private MutableLiveData<List<Event>> exploreData = new MutableLiveData<>();
    private MutableLiveData<List<Event>> createdEventsData = new MutableLiveData<>();
    private MutableLiveData<List<Event>> joinedEventsData = new MutableLiveData<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

//    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;
//    private ExploreMapFragmentViewModel mExloreMapFragmentViewModel;

    public static EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance;
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

    public MutableLiveData<List<Event>> getEvents() {
        if (addList.size() == 0)
            loadEvents();
        exploreData.setValue(addList);
        return exploreData;
    }

    private void loadEvents() {
        DatabaseReference mreference = mDatabase.child("events");
//        mExploreEventListFragmentViewModel = ExploreEventListFragmentViewModel.getInstance();
//        mExloreMapFragmentViewModel = ExploreMapFragmentViewModel.getInstance();
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
                exploreData.postValue(addList);
//                mExploreEventListFragmentViewModel.addEvents(addList);
//                mExloreMapFragmentViewModel.addEvents(addList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public MutableLiveData<List<Event>> getCreatedEvents(String userId) {
        if (createdList.size() == 0)
            loadCreatedEvents(userId);
        createdEventsData.setValue(createdList);
        return createdEventsData;
    }

    private void loadCreatedEvents(String userId) {
        DatabaseReference reference = mDatabase.child("createdEvents").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Clearing AddList");
                createdList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String stringDate = snapshot.child("date").getValue() + " " + snapshot.child("time").getValue();
                    Date date = null;
                    try {
                        date = sdf.parse(stringDate);
                        Log.d(TAG, "onDataChange: date "+date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date.after(new Date())){
                        final Event newEvent = new Event();
                        Log.d(TAG, "onDataChange: key" + snapshot.getKey());
                        Log.d(TAG, "onDataChange: title" + snapshot.child("title").getValue());
                        Log.d(TAG, "onDataChange: location" + snapshot.child("location").getValue());
                        newEvent.setId(snapshot.getKey());
                        newEvent.setTitle(snapshot.child("title").getValue().toString());
                        newEvent.setLocation(snapshot.child("location").getValue().toString());
                        newEvent.setImage(snapshot.child("image").getValue().toString());
                        createdList.add(newEvent);
                    }
                }
                createdEventsData.postValue(createdList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public MutableLiveData<List<Event>> getJoinedEvents(String userId) {
        if (joinedList.size() == 0)
            loadJoinedEvents(userId);
        joinedEventsData.setValue(joinedList);
        return joinedEventsData;
    }

    private void loadJoinedEvents(String userId) {
        DatabaseReference reference = mDatabase.child("joinedEvents").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Clearing AddList");
                joinedList.clear();
                Log.d(TAG, "onDataChange: datasnapshot of joined "+ dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String stringDate = snapshot.child("date").getValue() + " " + snapshot.child("time").getValue();
                    Date date = null;
                    try {
                        date = sdf.parse(stringDate);
                        Log.d(TAG, "onDataChange: date "+date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date.after(new Date())){
                        Log.d(TAG, "onDataChange: event "+ snapshot.child("title").getValue().toString() + " will be in future");
                        final Event newEvent = new Event();
                        Log.d(TAG, "onDataChange: joined key" + snapshot.getKey());
                        Log.d(TAG, "onDataChange: joined title" + snapshot.child("title").getValue());
                        Log.d(TAG, "onDataChange: joined location" + snapshot.child("location").getValue());
                        newEvent.setId(snapshot.getKey());
                        newEvent.setTitle(snapshot.child("title").getValue().toString());
                        newEvent.setLocation(snapshot.child("location").getValue().toString());
                        newEvent.setImage(snapshot.child("image").getValue().toString());
                        joinedList.add(newEvent);
                    }
                }
                joinedEventsData.postValue(joinedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
