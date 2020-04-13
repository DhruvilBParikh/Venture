package com.example.venture_v0.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlanSectionsPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> planFragmentList = new ArrayList<>();
    private final List<String> planFragmentTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title) {
        planFragmentList.add(fragment);
        planFragmentTitleList.add(title);
    }

    public PlanSectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return planFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return planFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return planFragmentList.size();
    }
}
