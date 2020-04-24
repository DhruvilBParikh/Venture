package com.example.venture.Fragments.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venture.R;
import com.example.venture.adapters.EventsRecyclerViewAdapter;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExploreEventListFragment} interface
 * to handle interaction events.
 */
public class ExploreEventListFragment extends Fragment {

    private static final String TAG = "ExploreEventFragment";
    private String eventType = "allEvents";
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;
    MutableLiveData<List<Event>> searchResult;

    //vars
    private RecyclerView mRecyclerView;
    private TextView noEventsText;
    private EventsRecyclerViewAdapter mAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore_event_list, container, false);
        mRecyclerView = view.findViewById(R.id.event_list_recyclerview);
        noEventsText = view.findViewById(R.id.noEvents);
        Log.d(TAG, "onCreateView: started.");
        initData();
        initRecyclerView();
        setSearchFragment();
        return view;
    }



    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        mAdapter = new EventsRecyclerViewAdapter(getContext(), mExploreEventListFragmentViewModel.getResult().getValue());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initData() {

        Log.d(TAG, "initData: preparing data.");
        mExploreEventListFragmentViewModel = new ViewModelProvider(getActivity()).get(ExploreEventListFragmentViewModel.class);
        mExploreEventListFragmentViewModel.getEvents("allEvents","California");
        mExploreEventListFragmentViewModel.getResult().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                Log.d(TAG, "--------all data observer" + events.toString());
                if(events.isEmpty()) {
                    Log.d(TAG, "onChanged: nearby events is empty");
                    noEventsText.setVisibility(View.VISIBLE);
                } else {
                    noEventsText.setVisibility(View.GONE);

                }
                mAdapter.setmEvents(events);
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    private void setSearchFragment() {

        //AUTOCOMPLETE
        String apiKey = "AIzaSyBAA7PGAMSIVpwIJ478qNdiE8VzjL39hOs";
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("Place", place.getName());
                System.out.println(place.toString());
                if (!place.getName().isEmpty()) {
                    String searchLocation = place.getName();
                    eventType = "searchEvents";
                    mExploreEventListFragmentViewModel.getEvents(eventType,searchLocation);
                }
            }


            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });

    }

}
