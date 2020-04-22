package com.example.venture.repositories;

/*
 * Singleton pattern
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.venture.models.Event;
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

    private Bitmap bitmap;



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
                    newEvent.setImage("rivers.jpg");
                    allList.add(newEvent);

                }
                Log.d("-------------------", allList.toString());

                allEventsData.postValue(allList);
                getFirebaseImage(allList);

//                ExploreEventListFragmentViewModel.getInstance().postEvents(searchList);
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
                        newEvent.setImage("rivers.jpg");

                        searchList.add(newEvent);
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
//        Log.d(TAG,"========got all image values========");

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
//    private Bitmap getFirebaseImage(String imageName){
//
//        StorageReference mStorageRef;
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//        StorageReference imagesRef = mStorageRef.child(imageName);
//        Log.d(TAG, "ImageRef--- "+imagesRef.getName());
//
//        final long ONE_MEGABYTE = 1024 * 1024;
//        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                Log.d(TAG,"success-------------");
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
//        return bitmap;
//
//    }
}