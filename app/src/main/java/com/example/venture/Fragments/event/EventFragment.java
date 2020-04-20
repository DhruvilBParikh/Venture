package com.example.venture.Fragments.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.event.EventFragmentViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private String id;
    private TextView titleText, locationText, dateText, timeText, descriptionText, organizerText;
    private String organizerId;

    private ToggleButton actionButton;

    private SharedPreferences preferences;

    private EventFragmentViewModel eventFragmentViewModel;

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
        organizerText = view.findViewById(R.id.eventOrganiser);

        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        actionButton = view.findViewById(R.id.actionButton);

        initData();

        // set default action button state
        if(((MainActivity)getActivity()).checkSession()) {
            setDefautlActionState(preferences.getString("userId", ""), id);
        }

        actionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(((MainActivity)getActivity()).checkSession()) {
                    if (isChecked) {
                        Log.d(TAG, "onCheckedChanged: is going");
                        // add data to joined events
                    } else {
                        Log.d(TAG, "onCheckedChanged: not going");
                        // remove data from joined events
                    }
                } else {
                    Log.d(TAG, "onCheckedChanged: user is not logged in, opening LoginSignupFragment, fragment tag: " + getTag());
                    actionButton.setChecked(false);
                    actionButton.setEnabled(false);
                    ((MainActivity)getActivity()).openFragment("LOGIN");
                }
            }
        });

        return view;
    }

    public void initData() {
        Log.d(TAG, "initData: called");
        eventFragmentViewModel = EventFragmentViewModel.getInstance();
        eventFragmentViewModel.init(id);
        eventFragmentViewModel.getEvent().observe(getViewLifecycleOwner(), new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                Log.d(TAG, "onChanged: event fetched: " + event);
                if(event!=null) {
                    Log.d(TAG, "onChanged: event found with title: " + event.getTitle());
                    organizerId = event.getOrganizerId();
                    titleText.setText(event.getTitle());
                    locationText.setText(event.getLocation());
                    dateText.setText(event.getDate());
                    timeText.setText(event.getTime());
                    descriptionText.setText(event.getDetails());
                    organizerText.setText(event.getOrganizer());

                    // hide action button for past events
                    pastEventCheck(event.getDate(), event.getTime());
                }
            }
        });
    }

    public void pastEventCheck(String date, String time) {
        SimpleDateFormat eventFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date currentdate = new Date();
        Log.d(TAG, "isPastEvent: current timestamp: " + eventFormatter.format(currentdate));
        try {
            Date eventdate = eventFormatter.parse(date + " " + time);
            Log.d(TAG, "isPastEvent: event timestamp: " + eventFormatter.format(eventdate));
            if(eventdate.after(currentdate)) {
                Log.d(TAG, "isPastEvent: event is in future");

                // if current user created this event, set toggle off
                organizerCheck();

            } else {
                Log.d(TAG, "isPastEvent: event is in past");
                actionButton.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.d(TAG, "isPastEvent: exception in parsing event date: " + e.getMessage());
        }
    }

    public void organizerCheck() {
        Log.d(TAG, "organizerCheck: organizerId: " + organizerId);
        Log.d(TAG, "organizerCheck: current user id: " +preferences.getString("userId", ""));
        if(preferences.getString("userId","").equals(organizerId)) {
            Log.d(TAG, "organizerCheck: current user organized this event");
            actionButton.setChecked(true);
            actionButton.setEnabled(false);
        } else {
            actionButton.setEnabled(true);
            Log.d(TAG, "organizerCheck: event organizer different than current user");
        }
    }

    public void setDefautlActionState(String userId, String eventId) {
        // get boolean value from joined events
        if (true) {
            actionButton.setChecked(true);
        } else {
            actionButton.setChecked(false);
        }
    }
}
