package com.example.venture.Fragments.explore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.venture.R;
import com.example.venture.adapters.ExploreSectionsPageAdapter;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";
    private ExploreSectionsPageAdapter exploreSectionsPageAdapter;
    private ViewPager exploreViewPager;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        exploreSectionsPageAdapter = new ExploreSectionsPageAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        exploreViewPager = view.findViewById(R.id.explore_viewpager);
        setupViewPager(exploreViewPager);

        TabLayout tabLayout = view.findViewById(R.id.explore_tabs);
        tabLayout.setupWithViewPager(exploreViewPager);


        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreSectionsPageAdapter adapter = new ExploreSectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new ExploreEventListFragment(), "Events");
        adapter.addFragment(new ExploreMapFragment(), "Map");
        viewPager.setAdapter(adapter);
    }

}
