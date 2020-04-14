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

import java.util.ArrayList;
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
        initData(view);
        return view;
    }

    @SuppressLint("LongLogTag")
    private void initData(View view) {
        Log.d(TAG, "initData: preparing data.");
        mExploreEventListFragmentViewModel = new ViewModelProvider(this).get(ExploreEventListFragmentViewModel.class);
        mExploreEventListFragmentViewModel.init();
        mExploreEventListFragmentViewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                madapter.notifyDataSetChanged();

            }
        });

        initRecyclerView(view);
    }

    @SuppressLint("LongLogTag")
    private void initRecyclerView(View view) {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        madapter = new EventsRecyclerViewAdapter(getContext(), mExploreEventListFragmentViewModel.getEvents().getValue());
        mrecyclerView.setAdapter(madapter);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


}
