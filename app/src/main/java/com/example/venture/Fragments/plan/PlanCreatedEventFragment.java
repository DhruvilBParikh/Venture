package com.example.venture.Fragments.plan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venture.R;
import com.example.venture.adapters.CreatedEventsRecyclerViewAdapter;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;

import java.util.List;

public class PlanCreatedEventFragment extends Fragment {

    private static final String TAG = "PlanCreatedEventFragment";
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;

    //vars
    private CreatedEventsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SharedPreferences mPreferences;
    private String userId;
    private TextView noEventsText;

    public PlanCreatedEventFragment() {
        // Required empty public constructor
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plan_created_event, container, false);
        mRecyclerView = view.findViewById(R.id.created_event_list_recyclerview);
        noEventsText = view.findViewById(R.id.noEvents);
        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        Log.d(TAG, "onCreateView: before initdata");
        initData();
        initRecyclerView();
        return view;
    }

    @SuppressLint("LongLogTag")
    private void initData() {

        Log.d(TAG, "initData: preparing data.");
        userId = mPreferences.getString("userId", "");
        mExploreEventListFragmentViewModel = new ViewModelProvider(this).get(ExploreEventListFragmentViewModel.class);
        mExploreEventListFragmentViewModel.getCreatedEvents(userId).observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                Log.d("----observer--", events.toString());
                if(events.isEmpty()) {
                    Log.d(TAG, "onChanged: created events is empty");
                    noEventsText.setVisibility(View.VISIBLE);
                } else {
                    noEventsText.setVisibility(View.GONE);
                }
                mAdapter.setmEvents(events);
//                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        Log.d(TAG, "initRecyclerView: userId:"+ userId);
        mAdapter = new CreatedEventsRecyclerViewAdapter(getContext(), mExploreEventListFragmentViewModel.getCreatedEvents(userId).getValue());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
