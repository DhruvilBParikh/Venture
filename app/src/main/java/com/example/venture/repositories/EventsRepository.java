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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    private DatabaseReference mreference;


    public static EventsRepository getInstance() {
        if (instance == null) {
            instance = new EventsRepository();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public MutableLiveData<List<Event>> getEvents(String eventType, String location, double latitude, double longitude) {
        mreference = mDatabase.child("trialevents");
        mExploreEventListFragmentViewModel = ExploreEventListFragmentViewModel.getInstance();

        switch (eventType){
            case "allEvents": loadAllEvents();
                                break;
            case "searchEvents": Log.d("inside switch case", eventType);
                loadSearchEvents(location, latitude, longitude);
                                break;

            default: loadAllEvents();
        }

        MutableLiveData<List<Event>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;

    }

    public List<Event> getEventList() {
        return dataSet;
    }


    private void loadAllEvents() {

        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Clearing AddList");
                addList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, snapshot.getKey());
                    Log.d(TAG, snapshot.child("location").toString());
                    Log.d(TAG, String.valueOf(snapshot.child("longitude").getValue()));


                    final Event newEvent = new Event();
                    newEvent.setTitle(snapshot.child("title").getValue().toString());
                    newEvent.setLocation(snapshot.child("location").getValue().toString());
                    newEvent.setLatitude((Double) snapshot.child("latitude").getValue());
                    newEvent.setLongitude((Double) snapshot.child("longitude").getValue());
                    addList.add(newEvent);

                }
                Log.d("-------------------", addList.toString());
                mExploreEventListFragmentViewModel.addEvents(addList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadSearchEvents(final String location, double latitude, double longitude) {

        mreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "loadSearchEvents");
                addList.clear();
                String searchLocation = location.toLowerCase();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dbCity = snapshot.child("city").getValue().toString().toLowerCase();
                    String dbState = snapshot.child("state").getValue().toString().toLowerCase();
                    Log.d("To search", location);

                    if(searchLocation.equals(dbCity) || searchLocation.equals(dbState))
                    {
                        final Event newEvent = new Event();
                        newEvent.setTitle(snapshot.child("title").getValue().toString());
                        newEvent.setLocation(snapshot.child("location").getValue().toString());
                        newEvent.setLatitude((Double) snapshot.child("latitude").getValue());
                        newEvent.setLongitude((Double) snapshot.child("longitude").getValue());
                        addList.add(newEvent);
                    }
                }
                Log.d("---------ans----------", addList.get(0).getTitle());
                mExploreEventListFragmentViewModel.addEvents(addList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
