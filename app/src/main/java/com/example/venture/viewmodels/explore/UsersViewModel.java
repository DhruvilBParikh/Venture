package com.example.venture.viewmodels.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.venture.models.User;
import com.example.venture.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersViewModel extends ViewModel {

    private MutableLiveData<ArrayList<User>> users;

    public void init() {
        if(users!=null) {
            return;
        }
        users = UsersRepository.getInstance().getUsers();
    }

    public LiveData<ArrayList<User>> getUsers(){
        return users;
    }

    public LiveData<User> getUser(String user) { return UsersRepository.getInstance().getUser(user); }

    public void addUser(User user) {
        UsersRepository.getInstance().addUser(user);
    }

    public void addCreatedEvent(HashMap<String, String> eventMap, String eventId, String userId) {
        UsersRepository.getInstance().addCreatedEvent(eventMap, eventId, userId);
    }
}
