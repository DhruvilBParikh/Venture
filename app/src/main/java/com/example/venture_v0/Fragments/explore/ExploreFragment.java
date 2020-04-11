package com.example.venture_v0.Fragments.explore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.venture_v0.R;
import com.example.venture_v0.adapters.ExploreSectionsPageAdapter;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
