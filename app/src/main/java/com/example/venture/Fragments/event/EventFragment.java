package com.example.venture.Fragments.event;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.adapters.PeopleExpandableAdapter;
import com.example.venture.models.Event;
import com.example.venture.models.People;
import com.example.venture.viewmodels.event.EventFragmentViewModel;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.example.venture.viewmodels.people.PeopleViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private String id, title, location, date, time, organizerId, image;
    private TextView titleText, locationText, dateText, timeText, descriptionText, organizerText;

    private ToggleButton actionButton;
    private ImageView eventImage;

    private ExpandableListView mExpandablePeople;

    private SharedPreferences preferences;

    private EventFragmentViewModel eventFragmentViewModel;
    private PeopleViewModel peopleViewModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    private String stringUri="";
    private String userId = "";
    private String userName = "";
    boolean joined = false;

    List<People> peopleList;
    PeopleExpandableAdapter peopleAdapter;

    private static final int REQUEST_CODE = 1;

    public EventFragment(String id) {
        this.id = id;
    }

    public EventFragment(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: show event with id: " + id);

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        //set widgets
        titleText = view.findViewById(R.id.eventTitle);
        locationText = view.findViewById(R.id.eventLocation);
        dateText = view.findViewById(R.id.eventDate);
        timeText = view.findViewById(R.id.eventTime);
        descriptionText = view.findViewById(R.id.eventDescription);
        organizerText = view.findViewById(R.id.eventOrganiser);
        eventImage = view.findViewById(R.id.eventImage);
        mExpandablePeople = view.findViewById(R.id.peopleList);
        actionButton = view.findViewById(R.id.actionButton);

        //get user from preferences
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        userId = preferences.getString("userId","");
        userName = preferences.getString("name","");

        //initialize viewmodel
        eventFragmentViewModel = new ViewModelProvider(this).get(EventFragmentViewModel.class);
        peopleViewModel = new ViewModelProvider(this).get(PeopleViewModel.class);

        peopleList = new ArrayList<>();
//        init event data
        initData();
//        verifyPermissions();

        actionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(((MainActivity)getActivity()).checkSession()) {

                    String[] permissions = {Manifest.permission.WRITE_CALENDAR,Manifest.permission.READ_CALENDAR};
                    if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                                    permissions[1]) == PackageManager.PERMISSION_GRANTED) {
                        final ContentResolver cr = getActivity().getContentResolver();
                        ContentValues cv = new ContentValues();
                        Calendar start = Calendar.getInstance();
                        Calendar end = Calendar.getInstance();

                        if (isChecked) {
                            Log.d(TAG, "onCheckedChanged: is going");
                            // add data to joined events
                            HashMap<String, String> eventObject = new HashMap<>();
                            eventObject.put("title", title);
                            eventObject.put("location", location);
                            eventObject.put("date", date);
                            eventObject.put("time", time);
                            eventObject.put("image", image);

                            HashMap<String, String> people = new HashMap<>();
                            people.put("attendeeName", userName);

                            if (!joined) {
                                //setting the start and end dates
                                try {
                                    Date dateFromSdf = sdf.parse(date + " " + time);
                                    start.setTime(dateFromSdf);
                                    end.setTime(dateFromSdf);
                                    end.add(Calendar.MINUTE, 60);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //setting calendar columns
                                cv.put(CalendarContract.Events.CALENDAR_ID, 3);
                                cv.put(CalendarContract.Events.TITLE, title);
                                cv.put(CalendarContract.Events.DESCRIPTION, descriptionText.getText().toString());
                                cv.put(CalendarContract.Events.EVENT_LOCATION, location);
                                cv.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
                                cv.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
                                cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

                                Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);

                                ContentValues cv1 = new ContentValues();
                                cv1.put(CalendarContract.Reminders.EVENT_ID, eventUri.getLastPathSegment());
                                cv1.put(CalendarContract.Reminders.MINUTES, 60);

                                cr.insert(CalendarContract.Reminders.CONTENT_URI, cv1);

                                Log.d(TAG, "onClick: uri " + eventUri);

                                Log.d(TAG, "onCheckedChanged: adding event to joined events: " + eventObject);

                                eventObject.put("eventUri", eventUri.toString());
                                joined = true;

                                peopleViewModel.addPeople(id, userId, people);
                                eventFragmentViewModel.addJoinedEvent(userId, id, eventObject);
//                                ((MainActivity) getActivity()).sendNotification();
                            }
                        } else {
                            Log.d(TAG, "onCheckedChanged: not going");
                            // remove data from joined events
                            EventFragmentViewModel.getInstance().getEventUri(userId, id).observe(getViewLifecycleOwner(), new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    if (joined) {
                                        stringUri =s;
                                        Log.d(TAG, "onCheckedChanged: stringUri " + stringUri);
                                        if (stringUri != null && !stringUri.equals("")) {
                                            Log.d(TAG, "onCheckedChanged: removing event from calendar");
                                            cr.delete(Uri.parse(stringUri), null, null);
                                        }
                                    }
                                    Log.d(TAG, "onCheckedChanged: removing event from joined events: " + id);
                                    joined = false;

                                    peopleViewModel.removePeople(id, userId);
                                    eventFragmentViewModel.removeJoinedEvent(preferences.getString("userId", ""), id);
                                }
                            });

                        }

                    } else {
                        requestPermissions(permissions, REQUEST_CODE);
                    }
                } else {
                    Log.d(TAG, "onCheckedChanged: user is not logged in, opening LoginSignupFragment, fragment tag: " + getTag());
                    actionButton.setChecked(false);
                    actionButton.setEnabled(false);
                    ((MainActivity)getActivity()).openFragment(id);
                }
            }
        });

        return view;
    }

    public void initData() {
        Log.d(TAG, "initData: called");
        eventFragmentViewModel.init(id);
        eventFragmentViewModel.getEvent().observe(getViewLifecycleOwner(), new Observer<Event>() {
            @Override
            public void onChanged(final Event event) {
                Log.d(TAG, "onChanged: event fetched: " + event);
                if(event!=null) {
                    Log.d(TAG, "onChanged: event found with title: " + event.getTitle());

                    /////////////////IMAGE////////////////
                    StorageReference mStorageRef;
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    final long ONE_MEGABYTE = 1024 * 1024;
                    StorageReference imagesRef = mStorageRef.child(event.getImage());

                    imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            eventImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                    //////////////////////////////////////
                    organizerId = event.getOrganizerId();
                    title = event.getTitle();
                    location = event.getLocation();
                    date = event.getDate();
                    time = event.getTime();
                    image = event.getImage();
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
        Date currentDate = new Date();
        Log.d(TAG, "isPastEvent: current timestamp: " + sdf.format(currentDate));
        try {
            Date eventDate = sdf.parse(date + " " + time);
            Log.d(TAG, "isPastEvent: event timestamp: " + sdf.format(eventDate));
            if(eventDate.after(currentDate)) {
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
        Log.d(TAG, "organizerCheck: current user id: " +userId);

        peopleViewModel.init(id);
        peopleViewModel.getPeople(id).observe(getViewLifecycleOwner(), new Observer<List<People>>() {
            @Override
            public void onChanged(List<People> people) {

//                ViewGroup.LayoutParams params = mExpandablePeople.getLayoutParams();
//                params.height = people.size() * 20 + 100;
//                mExpandablePeople.setLayoutParams(params);
//                mExpandablePeople.requestLayout();

                peopleAdapter = new PeopleExpandableAdapter(getContext(), people);
                mExpandablePeople.setAdapter(peopleAdapter);
                peopleAdapter.notifyDataSetChanged();
            }
        });

        if(userId.equals(organizerId)) {
            Log.d(TAG, "organizerCheck: current user organized this event");
            Log.d(TAG, "organizerCheck: Going Button");
            joined = true;
            actionButton.setChecked(true);
            actionButton.setEnabled(false);
            organizerText.setText("You");
        } else {
            actionButton.setEnabled(true);
            Log.d(TAG, "organizerCheck: event organizer different than current user");
            // set default action button state
            setDefaultActionState();
        }

    }

    public void setDefaultActionState() {

        eventFragmentViewModel.hasJoinedEvent(userId, id).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                joined = aBoolean;
                Log.d(TAG, "setDefaultActionState: joined::::::::::" + joined);
                if (joined) {
                    Log.d(TAG, "setDefaultActionState: user has joined the event");
                    Log.d(TAG, "setDefaultActionState: Going Button");
                    actionButton.setChecked(true);
                } else {
                    Log.d(TAG, "setDefaultActionState: user has not joined the event");
                    Log.d(TAG, "setDefaultActionState: Join Button");
                    actionButton.setChecked(false);
                }
            }
        });

    }

//    private void verifyPermissions(){
//        Log.d(TAG, "verifyPermissions: asking user for permissions");
//        String[] permissions = {Manifest.permission.WRITE_CALENDAR,Manifest.permission.READ_CALENDAR};
//        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
//                permissions[0])== PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
//                        permissions[1])== PackageManager.PERMISSION_GRANTED) {
//            initData();
//        } else {
//            requestPermissions(permissions,REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        verifyPermissions();
//    }
}
