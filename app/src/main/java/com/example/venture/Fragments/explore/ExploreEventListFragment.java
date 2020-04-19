package com.example.venture.Fragments.explore;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.venture.R;
import com.example.venture.adapters.EventsRecyclerViewAdapter;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExploreEventListFragment} interface
 * to handle interaction events.
 * Use the {@link ExploreEventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreEventListFragment extends Fragment {

    private static final String TAG = "ExploreEventListFragment";
    private String eventType = "allEvents";


    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;

    //vars
    private EventsRecyclerViewAdapter madapter;
    private RecyclerView mrecyclerView;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ExploreEventListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreEventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreEventListFragment newInstance(String param1, String param2) {
        ExploreEventListFragment fragment = new ExploreEventListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_explore_event_list, container, false);
        mrecyclerView = view.findViewById(R.id.event_list_recyclerview);
        Log.d(TAG, "onCreateView: started.");
        initData();
        initRecyclerView();
        autoSearch();
        return view;
    }

    @SuppressLint("LongLogTag")
    private void initData() {

        Log.d(TAG, "initData: preparing data.");
//        mExploreEventListFragmentViewModel = new ViewModelProvider(getActivity()).get(ExploreEventListFragmentViewModel.class);
        mExploreEventListFragmentViewModel = ExploreEventListFragmentViewModel.getInstance();
        mExploreEventListFragmentViewModel.init(eventType, "", 0,0);
        mExploreEventListFragmentViewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                Log.d("----observer--", events.toString());
                madapter.setmEvents(events);
                madapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        madapter = new EventsRecyclerViewAdapter(getContext(), mExploreEventListFragmentViewModel.getEvents().getValue());
        mrecyclerView.setAdapter(madapter);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void autoSearch() {

        //AUTOCOMPLETE
        String apiKey = getString(R.string.google_maps_key);
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
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    String location = place.getName();
                    eventType="searchEvents";
                    mExploreEventListFragmentViewModel = ExploreEventListFragmentViewModel.getInstance();
                    mExploreEventListFragmentViewModel.init(eventType, location, latitude, longitude);
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
