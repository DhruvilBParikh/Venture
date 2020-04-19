package com.example.venture.Fragments.event;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.venture.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private String id;

    public EventFragment(String id) {
        // Required empty public constructor
        this.id = id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        Log.d(TAG, "onCreateView: show event with id: " + id);
        return view;
    }
}