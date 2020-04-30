package com.example.venture.repositories;

/*
 * Singleton pattern
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.venture.models.Event;
import com.example.venture.viewmodels.event.EventFragmentViewModel;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EventsRepository  {


    private boolean initListeners = false;
    private ValueEventListener mAllValueEventListener;
    private ValueEventListener mSearchValueEventListener;

    private MutableLiveData<List<Event>> allEventsData = new MutableLiveData<>();
    private List<Event> allList = new ArrayList<>();

    private MutableLiveData<List<Event>> searchEventsData = new MutableLiveData<>();
    private List<Event> searchList = new ArrayList<>();

    private Bitmap bitmap;

    private static final String TAG = "EventsRepository";

    private Event event;
    private static EventsRepository instance;
    private static DatabaseReference mDatabase;
    private static DatabaseReference mreference;
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;
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
    
    private MutableLiveData<Boolean> joinedEventCheck = new MutableLiveData<>();
    private MutableLiveData<String> eventUri = new MutableLiveData<>();


    public static EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public void addEvent(final Event event, final String userId, final String eventId, String eventUri) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("events");
        Log.d(TAG, "addEvent: "+event.getTitle());
//        final String[] eventId = {""};
        reference.child(eventId).setValue(event);
//                eventId[0] = databaseReference.getKey();
        Log.d(TAG, "addEvent: event id is::::" + eventId);
        HashMap<String, String> eventMap = new HashMap<>();
        eventMap.put("title", event.getTitle());
        eventMap.put("location", event.getLocation());
        eventMap.put("date", event.getDate());
        eventMap.put("time", event.getTime());
        eventMap.put("image", event.getImage());

        addCreatedEvent(eventMap, eventId,userId, eventUri);


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

                    String stringDate = snapshot.child("date").getValue() + " " + snapshot.child("time").getValue();
                    Date date = null;
                    try {
                        date = sdf.parse(stringDate);
                        Log.d(TAG, "onDataChange: date "+date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date.after(new Date())) {


                        final Event newEvent = new Event();

                        Log.d(TAG, "onDataChange: " + snapshot.getKey());
                        Log.d(TAG, "onDataChange: " + snapshot.child("title").getValue());
                        Log.d(TAG, "onDataChange: " + snapshot.child("location").getValue());
                        Log.d(TAG, "onDataChange: " + snapshot.child("latitude").getValue());
                        Log.d(TAG, "onDataChange: " + snapshot.child("longitude").getValue());

                        newEvent.setId(snapshot.getKey());
                        newEvent.setTitle(snapshot.child("title").getValue().toString());
                        newEvent.setLocation(snapshot.child("location").getValue().toString());
                        newEvent.setLatitude((Double) snapshot.child("latitude").getValue());
                        newEvent.setLongitude((Double) snapshot.child("longitude").getValue());
                        //                    newEvent.setImage("rivers.jpg");
                        if (snapshot.hasChild("image"))
                            newEvent.setImage(snapshot.child("image").getValue().toString());
                        else
                            newEvent.setImage("default.png");
                        allList.add(newEvent);
                    }

                }
                Log.d("-------------------", allList.toString());

                allEventsData.postValue(allList);
                getFirebaseImage(allList);  
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    private void loadAllEvents() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mreference = mDatabase.child("trialevents");
        mreference = mDatabase.child("events");
        mreference.addValueEventListener(mAllValueEventListener);

    }

    private void loadSearchEvents(final String location) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mreference = mDatabase.child("trialevents");
        mreference = mDatabase.child("events");
        mSearchValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "loadSearchEvents");
                searchList.clear();
                searchEventsData = new MutableLiveData<>();
                String searchLocation = location.toLowerCase();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String stringDate = snapshot.child("date").getValue() + " " + snapshot.child("time").getValue();
                    Date date = null;
                    try {
                        date = sdf.parse(stringDate);
                        Log.d(TAG, "onDataChange: date "+date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date.after(new Date())) {


                        String dbCity = snapshot.child("city").getValue().toString().toLowerCase();
                        String dbState = snapshot.child("state").getValue().toString().toLowerCase();
                        Log.d("To search", location);

                        if (searchLocation.equals(dbCity) || searchLocation.equals(dbState)) {
                            final Event newEvent = new Event();
                            newEvent.setId(snapshot.getKey());
                            newEvent.setTitle(snapshot.child("title").getValue().toString());
                            newEvent.setLocation(snapshot.child("location").getValue().toString());
                            newEvent.setLatitude((Double) snapshot.child("latitude").getValue());
                            newEvent.setLongitude((Double) snapshot.child("longitude").getValue());
                            //                        newEvent.setImage("rivers.jpg");
                            if (snapshot.hasChild("image"))
                                newEvent.setImage(snapshot.child("image").getValue().toString());
                            else
                                newEvent.setImage("default.png");

                            searchList.add(newEvent);
                        }
                    }
                }
                getFirebaseImage(searchList);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mreference.addListenerForSingleValueEvent(mSearchValueEventListener);


    }

    private void getFirebaseImage(List<Event> eventList){
        iterateThroughEvents(eventList, 0); 
    }

    private void iterateThroughEvents(final List<Event> eventList, final int position){
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final long ONE_MEGABYTE = 1024 * 1024;

        if(position >=eventList.size()) {
            finaldone(eventList);
            return;
        }

        StorageReference imagesRef = mStorageRef.child(eventList.get(position).getImage());

        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                eventList.get(position).setImageBitmap(bitmap);
                Log.d(TAG,"success-------------"+eventList.get(position).getImageBitmap());
                iterateThroughEvents(eventList,position+1);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void finaldone(List<Event> eventList){

        Log.d(TAG,"========got all image values========"+eventList.toString());
        ExploreEventListFragmentViewModel.getInstance().postEvents(eventList);

    }

    public void addCreatedEvent(HashMap<String, String> eventMap, String eventId, String userId, String eventUri) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("createdEvents").child(userId).child(eventId).setValue(eventMap);
        eventMap.put("eventUri", eventUri);
        reference.child("joinedEvents").child(userId).child(eventId).setValue(eventMap);
    }

    public MutableLiveData<Event> getEvent(String id) {
        loadEvent(id);
        return currentEvent;
    }

    public MutableLiveData<List<Event>> getCreatedEvents(String userId) {
        loadCreatedEvents(userId);
        createdEventsData.setValue(createdList);
        return createdEventsData;
    }

    private void loadCreatedEvents(String userId) {
        DatabaseReference reference = mDatabase.child("createdEvents").child(userId);
        Log.d(TAG, "loadCreatedEvents: seraching created events for user: " + userId);
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
                        if(snapshot.hasChild("image"))
                            newEvent.setImage(snapshot.child("image").getValue().toString());
                        else
                            newEvent.setImage("default.png");
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
//                        Log.d(TAG, "onDataChange: joined location" + snapshot.child("image").getValue());
                        newEvent.setId(snapshot.getKey());
                        newEvent.setTitle(snapshot.child("title").getValue().toString());
                        newEvent.setLocation(snapshot.child("location").getValue().toString());
                        if(snapshot.hasChild("image"))
                            newEvent.setImage(snapshot.child("image").getValue().toString());
                        else
                            newEvent.setImage("default.png");
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
                        if(snapshot.hasChild("image"))
                            newEvent.setImage(snapshot.child("image").getValue().toString());
                        else
                            newEvent.setImage("default.png");
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

                event.setOrganizerId(((HashMap)dataSnapshot.getValue()).get("organizerId").toString());
                event.setOrganizer(((HashMap)dataSnapshot.getValue()).get("organizer").toString());
                event.setLatitude(Double.parseDouble(((HashMap)dataSnapshot.getValue()).get("latitude").toString()));
                event.setLongitude(Double.parseDouble(((HashMap)dataSnapshot.getValue()).get("longitude").toString()));
                event.setDetails(((HashMap)dataSnapshot.getValue()).get("details").toString());
                event.setLocation(((HashMap)dataSnapshot.getValue()).get("location").toString());
                event.setTime(((HashMap)dataSnapshot.getValue()).get("time").toString());
                event.setTitle(((HashMap)dataSnapshot.getValue()).get("title").toString());
//                event.setEventUri(((HashMap)dataSnapshot.getValue()).get("eventUri").toString());
                if(dataSnapshot.hasChild("image"))
                    event.setImage(((HashMap)dataSnapshot.getValue()).get("image").toString());
                else
                    event.setImage("default.png");

                currentEvent.postValue(event);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public MutableLiveData<Boolean> hasJoinedEvent(final String userId, final String eventId) {
        Log.d(TAG, "hasJoinedEvent: checking if user: " + userId + " has joined event: " + eventId);
        final DatabaseReference mreference = mDatabase.child("joinedEvents");
        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: snapshot"+dataSnapshot);
                if(dataSnapshot.hasChild(userId)) {
                    Log.d(TAG, "onDataChange: user key exists");
                    if(dataSnapshot.child(userId).hasChild(eventId)) {
                        Log.d(TAG, "hasJoinedEvent: event key exists");
                        joinedEventCheck.postValue(true);
                    } else {
                        Log.d(TAG, "hasJoinedEvent: event key does not exists");
                        joinedEventCheck.postValue(false);
                    }
                } else {
                    Log.d(TAG, "hasJoinedEvent: user key does not exists");
                    joinedEventCheck.postValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return joinedEventCheck;
    }

    public MutableLiveData<String> getEventUri(final String userId, final String eventId) {
        Log.d(TAG, "getEventUri:  getting event uri: from " + userId + " and event: " + eventId);

        mDatabase.child("joinedEvents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: "+((HashMap)dataSnapshot.child(userId).child(eventId).getValue()).get("eventUri"));


                Log.d(TAG, "onDataChange: snapshot in getting uri::::"+ dataSnapshot);
                if(dataSnapshot.hasChild(userId)){
                    if(dataSnapshot.child(userId).hasChild(eventId)){
                        if(((HashMap)dataSnapshot.child(userId).child(eventId).getValue()).containsKey("eventUri")) {
                            Log.d(TAG, "onDataChange: event uri::::"+(    (HashMap)dataSnapshot.child(userId).child(eventId).getValue() ).get("eventUri").toString()) ;
                            eventUri.postValue((    (HashMap)dataSnapshot.child(userId).child(eventId).getValue() ).get("eventUri").toString());
                            Log.d(TAG, "onDataChange: user key exists"+eventUri);

                        } else {
                            eventUri.postValue("");
                            Log.d(TAG, "onDataChange: eventuri:::::"+ eventUri);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return eventUri;
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