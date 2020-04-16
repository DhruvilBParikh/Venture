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

    public List<Event> getEventList() {
        return dataSet;
    }


    private void loadEvents() {

        DatabaseReference mreference = mDatabase.child("trialevents");
        mExploreEventListFragmentViewModel = ExploreEventListFragmentViewModel.getInstance();

        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Clearing AddList");
                addList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, dataSnapshot.getKey());
                    Log.d(TAG, dataSnapshot.child("location").toString());

                    final Event newEvent = new Event();
                    newEvent.setTitle(snapshot.child("title").getValue().toString());
                    newEvent.setLocation(snapshot.child("location").getValue().toString());
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
}
