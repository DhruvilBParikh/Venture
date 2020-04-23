package com.example.venture.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.venture.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersRepository {

    private static final String TAG = "UsersRepository";

    static UsersRepository instance;
    private ArrayList<User> usersList =  new ArrayList<>();
    private MutableLiveData<ArrayList<User>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<User> userLiveData =new MutableLiveData<>();

    public static UsersRepository getInstance(){

        if(instance== null){
            instance = new UsersRepository();
        }

        return instance;
    }

    public MutableLiveData<ArrayList<User>> getUsers(){
        if(usersList.size()==0) loadNames();
        usersLiveData.setValue(usersList);
        return usersLiveData;
    }

    public MutableLiveData<User> getUser(final String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userExists = dataSnapshot.child(userId).exists();
                if(userExists) {
                    User user = dataSnapshot.child(userId).getValue(User.class);
                    Log.d(TAG, "onDataChange: dataSnapshot:" + dataSnapshot.child(userId).getValue());
                    user.setId(userId);
                    userLiveData.postValue(user);
//                    user.setName(dataSnapshot.);
                } else {
                    Log.d(TAG, "onDataChange: user not exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return userLiveData;
    }

    private void loadNames(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference.child("events");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: snap-----------------------------"+ dataSnapshot.child("users").child("0").getChildren());
                for (DataSnapshot snapshot1: dataSnapshot.child("users").child("0").getChildren()){
                    Log.d(TAG, "onDataChange: key1"+snapshot1.getKey());
                }
                for(DataSnapshot snapshot: dataSnapshot.child("events").getChildren()){
                    User user = new User();

//                    Log.d(TAG, "onDataChange: Title:" + snapshot.getKey());
//                    user.setDescription(((HashMap)snapshot.getValue()).get("description").toString());
//                    Log.d(TAG, "onDataChange: Description:"+ ((HashMap)snapshot.getValue()).get("description"));
//
//                    Log.d(TAG, "onDataChange: Snapshot:"+snapshot);
//                    eventsList.add(eventModel);
////                    eventsList.add(snapshot.getValue(EventModel.class));
                }
//                eventLiveData.postValue(eventsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addUser(User user){
        Log.d(TAG, "addUser: "+ user.getEmail());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        HashMap<String, String> map = new HashMap<>();
        String userId = user.getId();
        map.put("name", user.getName());
        map.put("email", user.getEmail());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: database "+ dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.d(TAG, "onCancelled: "+ databaseError.toException());
//            }
//        });
        Log.d(TAG, "addUser: map"+ map.values());
        Log.d(TAG, "addUser: database reference "+ reference.child("1").toString());
        reference.child(userId).setValue(map);

    }

    public void editUser(User user) {
        Log.d(TAG, "editUser: editing user");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        HashMap<String, String> map = new HashMap<>();
        String userId = user.getId();
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        if(!user.getBio().equals(""))
            map.put("bio", user.getBio());
        if(!user.getProfilePic().equals(""))
            map.put("profilePic", user.getProfilePic());
        reference.child(userId).setValue(map);
    }

}
