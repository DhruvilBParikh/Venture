package com.example.venture_v0.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExploreSectionsPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> exploreFragmentList = new ArrayList<>();
    private final List<String> exploreFragmentTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title) {
        exploreFragmentList.add(fragment);
        exploreFragmentTitleList.add(title);
    }

    public ExploreSectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return exploreFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return exploreFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return exploreFragmentList.size();
    }
}
