package com.example.venture.Fragments.addevent;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.example.venture.viewmodels.explore.UsersViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment {

    private static final String TAG = "AddEventFragment";

    private TextInputEditText mTitle;
    private TextInputEditText mDetails;
    private TextInputEditText mDate;
    private TextInputEditText mTime;
    private MaterialButton mAddEvent;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Calendar calendar = Calendar.getInstance();
    private AutocompleteSupportFragment autocompleteFragment;
    private int mMonth = calendar.get(Calendar.MONTH);
    private int mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    private int mYear = calendar.get(Calendar.YEAR);
    private int mHourOfDay = calendar.get(calendar.HOUR_OF_DAY);
    private int mMinute = calendar.get(calendar.MINUTE);
    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

    private Place mPlace;
    private String location;
    private LatLng latLng;
    private String city;
    private String state;
    private SharedPreferences mPreferences;

    //viewmodels
    private ExploreEventListFragmentViewModel mEventViewModel;
//    private UsersViewModel mUserViewModel;
//    private ExploreEventListFragmentViewModel mCreatedEventViewModel;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        mTitle = view.findViewById(R.id.editTitle);
        mDetails = view.findViewById(R.id.editDescription);
        mDate = view.findViewById(R.id.editDate);
        mTime = view.findViewById(R.id.editTime);
        mAddEvent = view.findViewById(R.id.buttonAddEvent);
        calendar = Calendar.getInstance();

        String apiKey = "AIzaSyCA0NaAI0q_DC1oagzC8hDnp7r1bv7j8JE";
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Add Meeting Point");


        setDate();
        setTime();
        autoSearch();

        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String userId = mPreferences.getString("userId", "");
        final String organizer = mPreferences.getString("name", "");

        mEventViewModel = new ViewModelProvider(this).get(ExploreEventListFragmentViewModel.class);
//        mUserViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
//        mCreatedEventViewModel = new ViewModelProvider(this).get(ExploreEventListFragmentViewModel.class);

        mAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Latlng"+latLng);
                Log.d(TAG, "onClick: Place Name:"+location);
                Event event = new Event();
                event.setTitle(mTitle.getText().toString());
                event.setDetails(mDetails.getText().toString());
                event.setImage("sf_trial.jpg");
                event.setLocation(location);
                event.setLatitude(latLng.latitude);
                event.setLongitude(latLng.longitude);
                event.setDate(mDate.getText().toString());
                event.setTime(mTime.getText().toString());
                event.setOrganizerId(userId);
                event.setCity(city);
                event.setState(state);
                event.setOrganizer(organizer);
                mEventViewModel.addEvent(event, userId);

//                //Adding in Created and joined events in User Node
//                HashMap<String, String> eventMap = new HashMap<String, String>();
//                eventMap.put("title", mTitle.getText().toString());
//                eventMap.put("location", location);
//                eventMap.put("date", mDate.getText().toString());
//                eventMap.put("time", mTime.getText().toString());
//                eventMap.put("image", "sf_trail.jpg");
//
//                mCreatedEventViewModel.addCreatedEvent(eventMap, eventId, userId);

                ((MainActivity)getActivity()).openFragment("EXPLORE");
            }
        });

        return view;
    }

    private void autoSearch() {
//        String apiKey = "AIzaSyCA0NaAI0q_DC1oagzC8hDnp7r1bv7j8JE";
//        if (!Places.isInitialized()) {
//            Places.initialize(getContext(), apiKey);
//        }

        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        autocompleteFragment.setHint("Add Meeting Point");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "onPlaceSelected: " + place.toString());
                if (!place.getName().isEmpty()) {
                    latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    location = place.getName();
                    Log.d(TAG, "onPlaceSelected: "+latLng);
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addresses.size()>0) {
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        Log.d(TAG, "onPlaceSelected: Geocoder "+addresses.get(0));
                        Log.d(TAG, "onPlaceSelected: Geocoder "+addresses.get(0).getLocality());
                    }
                    Log.d(TAG, "onPlaceSelected: "+place.getName());
                }
            }
            @Override
            public void onError(Status status) {
                Log.d(TAG, "onError: error occurred, status: " + status);
            }
        });

    }

    public void setDate() {
        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int year = mYear;
                int month = mMonth;
                int day = mDayOfMonth;
                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );
                dialog.getWindow();
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mMonth = month;
                month = month + 1;
                mDayOfMonth = dayOfMonth;
                mYear = year;
                String date = String.format("%02d", month)+"/"+String.format("%02d", dayOfMonth)+"/"+year;
                mDate.setText(date);
            }
        };
    }

    public void setTime() {
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hours = mHourOfDay;
                int minutes = mMinute;
                Log.d(TAG, "onClick: hours:"+hours);
                Log.d(TAG, "onClick: minutes:"+minutes);
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mTimeSetListener,
                        hours, minutes, false
                );
                timePickerDialog.getWindow();
                timePickerDialog.show();
            }
        });
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                try {
                    mHourOfDay = hourOfDay;
                    mMinute = minute;
                    String _24HourTime = hourOfDay + ":" + minute;
                    Date _24HourDt = _24HourSDF.parse(_24HourTime);
                    Log.d(TAG, "onTimeSet: " + _24HourTime);
                    assert _24HourDt != null;
                    Log.d(TAG, "onTimeSet: " + _12HourSDF.format(_24HourDt));
                    String time = _12HourSDF.format(_24HourDt);
                    mTime.setText(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
