package com.example.venture.Fragments.history;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.venture.R;
import com.example.venture.adapters.HistoryRecyclerViewAdapter;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    private ExploreEventListFragmentViewModel mExploreEventListFragmentViewModel;

    //vars
    private HistoryRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SharedPreferences mPreferences;
    private String userId;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mRecyclerView = view.findViewById(R.id.history_list_recyclerview);
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
        mExploreEventListFragmentViewModel.getHistory(userId).observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                Log.d("----observer--", events.toString());
                mAdapter.setmEvents(events);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        Log.d(TAG, "initRecyclerView: userId:"+ userId);
        mAdapter = new HistoryRecyclerViewAdapter(getContext(), mExploreEventListFragmentViewModel.getHistory(userId).getValue());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}