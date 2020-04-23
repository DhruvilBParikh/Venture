package com.example.venture.Fragments.plan;

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
import com.example.venture.adapters.JoinedEventsRecyclerViewAdapter;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;

import java.util.List;

public class PlanJoinedEventFragment extends Fragment {

    private static final String TAG = "PlanJoinedEventFragment";
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;

    //vars
    private JoinedEventsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SharedPreferences mPreferences;
    private String userId;
    private TextView noEventsText;

    public PlanJoinedEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan_joined_event, container, false);
        mRecyclerView = view.findViewById(R.id.joined_event_list_recyclerview);
        noEventsText = view.findViewById(R.id.noEvents);
        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        Log.d(TAG, "onCreateView: before init data");
        initData();
        initRecyclerView();
        return view;
    }

    private void initData() {
        Log.d(TAG, "initData: preparing data.");
        userId = mPreferences.getString("userId", "");
        mExploreEventListFragmentViewModel = new ViewModelProvider(this).get(ExploreEventListFragmentViewModel.class);
        mExploreEventListFragmentViewModel.getJoinedEvents(userId).observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                Log.d("----observer--", events.toString());
                if(events.isEmpty()) {
                    Log.d(TAG, "onChanged: joined events is empty");
                    noEventsText.setVisibility(View.VISIBLE);
                } else {
                    noEventsText.setVisibility(View.GONE);
                }
                mAdapter.setmEvents(events);
//                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        Log.d(TAG, "initRecyclerView: userId:"+ userId);
        mAdapter = new JoinedEventsRecyclerViewAdapter(getContext(), mExploreEventListFragmentViewModel.getJoinedEvents(userId).getValue());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
