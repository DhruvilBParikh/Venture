package com.example.venture.repositories;

/*
 * Singleton pattern
 */
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EventsRepository {

    private String TAG = "-----EventsRepo------";

    private Event event;
    private static EventsRepository instance;
    private static DatabaseReference mDatabase;
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;
    private ExploreMapFragmentViewModel mExloreMapFragmentViewModel;
    private EventFragmentViewModel eventFragmentViewModel;

    //Lists
    private List<Event> addList = new ArrayList<>();
    private List<Event> createdList = new ArrayList<>();
    private List<Event> joinedList = new ArrayList<>();
    private List<Event> historyList = new ArrayList<>();

    //MutableLiveData
    private MutableLiveData<List<Event>> exploreData = new MutableLiveData<>();
    private MutableLiveData<List<Event>> createdEventsData = new MutableLiveData<>();
    private MutableLiveData<List<Event>> joinedEventsData = new MutableLiveData<>();
    private MutableLiveData<List<Event>> historyData = new MutableLiveData<>();
    private MutableLiveData<Event> currentEvent = new MutableLiveData<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    
    private boolean joinedEventCheck;

//    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;
//    private ExploreMapFragmentViewModel mExloreMapFragmentViewModel;

    public static EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public void addEvent(final Event event, final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("events");
        Log.d(TAG, "addEvent: "+event.getTitle());
        reference.push().setValue(event);
        final String[] eventId = {""};
        reference.push().setValue(event, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                eventId[0] = databaseReference.getKey();
                Log.d(TAG, "addEvent: event id is::::" + eventId[0]);
                HashMap<String, String> eventMap = new HashMap<>();
                eventMap.put("title", event.getTitle());
                eventMap.put("location", event.getLocation());
                eventMap.put("date", event.getDate());
                eventMap.put("time", event.getTime());
                eventMap.put("image", event.getImage());
                addCreatedEvent(eventMap, eventId[0], userId);
            }
        });
    }

    public void addCreatedEvent(HashMap<String, String> eventMap, String eventId, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("createdEvents").child(userId).child(eventId).setValue(eventMap);
        reference.child("joinedEvents").child(userId).child(eventId).setValue(eventMap);
    }

    public MutableLiveData<Event> getEvent(String id) {
        loadEvent(id);
        return currentEvent;
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
//                        newEvent.setImage(snapshot.child("image").getValue().toString());
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

    public MutableLiveData<List<Event>> getHistory(String userId) {
        if (historyList.size() == 0)
            loadHistory(userId);
        historyData.setValue(historyList);
        return historyData;
    }

    private void loadHistory(String userId) {
        DatabaseReference reference = mDatabase.child("joinedEvents").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Clearing AddList");
                historyList.clear();
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
                    if(date.before(new Date())){
                        Log.d(TAG, "onDataChange: event "+ snapshot.child("title").getValue().toString() + " was in the past");
                        final Event newEvent = new Event();
                        Log.d(TAG, "onDataChange: history key" + snapshot.getKey());
                        Log.d(TAG, "onDataChange: history title" + snapshot.child("title").getValue());
                        Log.d(TAG, "onDataChange: history location" + snapshot.child("location").getValue());
                        newEvent.setId(snapshot.getKey());
                        newEvent.setTitle(snapshot.child("title").getValue().toString());
                        newEvent.setLocation(snapshot.child("location").getValue().toString());
                        newEvent.setImage(snapshot.child("image").getValue().toString());
                        historyList.add(newEvent);
                    }
                }
                historyData.postValue(historyList);
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

                currentEvent.postValue(event);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean hasJoinedEvent(final String userId, final String eventId) {
        Log.d(TAG, "hasJoinedEvent: checking if user: " + userId + " has joined event: " + eventId);

        mDatabase.child("joinedEvents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId)) {
                    Log.d(TAG, "onDataChange: user key exists");
                    if(dataSnapshot.child(userId).hasChild(eventId)) {
                        Log.d(TAG, "hasJoinedEvent: event key exists");
                        joinedEventCheck = true;
                    } else {
                        Log.d(TAG, "hasJoinedEvent: event key does not exists");
                        joinedEventCheck = false;
                    }
                } else {
                    Log.d(TAG, "hasJoinedEvent: user key does not exists");
                    joinedEventCheck = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return joinedEventCheck;
    }

    public void addJoinedEvent(String userId, String eventId, HashMap<String, String> eventObj) {
        Log.d(TAG, "addJoinedEvent: adding to joined events");
        Log.d(TAG, "addJoinedEvent: userid: " + userId);
        Log.d(TAG, "addJoinedEvent: eventid: " + eventId);
        Log.d(TAG, "addJoinedEvent: event: " + eventObj);
        mDatabase.child("joinedEvents").child(userId).child(eventId).setValue(eventObj);
    }

    public void removeJoinedEvent(String userId, String eventId) {
        Log.d(TAG, "onCheckedChanged: removing event from joined events: " + event + " for user: " + userId);
        mDatabase.child("joinedEvents").child(userId).child(eventId).removeValue();
    }
}
