package com.example.venture.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.lifecycle.MutableLiveData;

import com.example.venture.models.Event;
import com.example.venture.models.People;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeopleRepository {

    private static final String TAG = "PeopleRepository";

    private MutableLiveData<List<People>> peopleData = new MutableLiveData<>();
    private List<People> peopleList = new ArrayList<>();

    private static PeopleRepository instance;
    private static DatabaseReference mDatabase;

    public static PeopleRepository getInstance() {
        if (instance == null) {
            instance = new PeopleRepository();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public MutableLiveData<List<People>> getPeopleByEventId(final String eventId){
        Log.d(TAG, "getPeopleByEventId: getting people by event id");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                peopleList.clear();
                if(dataSnapshot.hasChild("people/"+eventId)){
                    Log.d(TAG, "onDataChange: getting people data snapshot: "+dataSnapshot.child("people/"+eventId));
                    for (DataSnapshot snapshot : dataSnapshot.child("people/"+eventId).getChildren()){
                        Log.d(TAG, "onDataChange: snapshot person id"+ snapshot.getKey());
                        Log.d(TAG, "onDataChange: snapshot person name"+ ((HashMap) snapshot.getValue()).get("attendeeName") );
                        People people = new People();
                        people.setUserId(snapshot.getKey());
                        people.setUserName(((HashMap) snapshot.getValue()).get("attendeeName").toString());
                        peopleList.add(people);
                    }
                    peopleData.postValue(peopleList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return peopleData;
    }

    public void addPeople(String eventId, String personId, HashMap<String, String> personDetail) {
        Log.d(TAG, "addPeople: adding person to the event");
        mDatabase.child("people").child(eventId).child(personId).setValue(personDetail);
    }

    public void removePeople(String eventId, String personId) {
        Log.d(TAG, "addPeople: adding person to the event");
        mDatabase.child("people").child(eventId).child(personId).removeValue();
    }
}
