package com.example.venture.Fragments.event;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.venture.R;
import com.example.venture.models.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private String id;
    private TextView titleText, locationText, dateText, timeText, descriptionText, organizerText;
    private ToggleButton actionButton;

    public EventFragment(String id) {
        // Required empty public constructor
        this.id = id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: show event with id: " + id);
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        titleText = view.findViewById(R.id.eventTitle);
        locationText = view.findViewById(R.id.eventLocation);
        dateText = view.findViewById(R.id.eventDate);
        timeText = view.findViewById(R.id.eventTime);
        descriptionText = view.findViewById(R.id.eventDescription);
        organizerText = view.findViewById(R.id.eventDescription);

        actionButton = view.findViewById(R.id.actionButton);

        if(isPastEvent()) {
            actionButton.setVisibility(View.GONE);
        }

        actionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.d(TAG, "onCheckedChanged: is going");
                } else {
                    Log.d(TAG, "onCheckedChanged: not going");
                }
            }
        });

        return view;
    }

    public Boolean isPastEvent() {
        return false;
    }
}
