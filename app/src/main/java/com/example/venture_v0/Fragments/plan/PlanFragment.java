package com.example.venture_v0.Fragments.plan;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.venture_v0.Fragments.explore.ExploreEventListFragment;
import com.example.venture_v0.Fragments.explore.ExploreMapFragment;
import com.example.venture_v0.R;
import com.example.venture_v0.adapters.ExploreSectionsPageAdapter;
import com.example.venture_v0.adapters.PlanSectionsPageAdapter;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanFragment extends Fragment {

    private static final String TAG = "PlanFragment";
    private PlanSectionsPageAdapter planSectionsPageAdapter;
    private ViewPager planViewPager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlanFragment newInstance(String param1, String param2) {
        PlanFragment fragment = new PlanFragment();
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
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        planSectionsPageAdapter = new PlanSectionsPageAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        planViewPager = view.findViewById(R.id.plan_viewpager);
        setupViewPager(planViewPager);

        TabLayout tabLayout = view.findViewById(R.id.plan_tabs);
        tabLayout.setupWithViewPager(planViewPager);


        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        PlanSectionsPageAdapter adapter = new PlanSectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new PlanJoinedEventFragment(), "Joined Events");
        adapter.addFragment(new PlanCreatedEventFragment(), "Created Events");
        viewPager.setAdapter(adapter);
    }

}
