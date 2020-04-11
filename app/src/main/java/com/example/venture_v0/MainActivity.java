package com.example.venture_v0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.venture_v0.Fragments.addevent.AddEventFragment;
import com.example.venture_v0.Fragments.explore.ExploreFragment;
import com.example.venture_v0.Fragments.history.HistoryFragment;
import com.example.venture_v0.Fragments.loginSignup.LoginSignupFragment;
import com.example.venture_v0.Fragments.plan.PlanFragment;
import com.example.venture_v0.Fragments.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigation;
    private Boolean isLoggedIn;
    private Fragment exploreFragment;
    private Fragment planFragment ;
    private Fragment addEventFragment;
    private Fragment historyFragment;
    private Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exploreFragment = ExploreFragment.newInstance("", "");
        planFragment = PlanFragment.newInstance("", "");
        addEventFragment = AddEventFragment.newInstance("", "");
        historyFragment = HistoryFragment.newInstance("", "");
        profileFragment = ProfileFragment.newInstance("", "");

        setContentView(R.layout.activity_main);

        isLoggedIn = checkSession();
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment("EXPLORE");
    }

    public void openFragment(String tag) {
        Log.d(TAG, "openFragment: " + tag);
        Boolean loginPrompt = !(tag.equals("EXPLORE") || isLoggedIn);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(loginPrompt) {
            Log.d(TAG, "openFragment: user not logged in");
            transaction.replace(R.id.container, LoginSignupFragment.newInstance("", ""), tag);
        } else {
            switch(tag){
                case "EXPLORE":
                    transaction.replace(R.id.container, exploreFragment, tag);
                    break;
                case "PLAN":
                    transaction.replace(R.id.container, planFragment, tag);
                    break;
                case "ADDEVENT":
                    transaction.replace(R.id.container, addEventFragment, tag);
                    break;
                case "HISTORY":
                    transaction.replace(R.id.container, historyFragment, tag);
                    break;
                case "PROFILE":
                    transaction.replace(R.id.container, profileFragment, tag);:equals(q)
                    break;
            }
        }
        transaction.commit();
    }

    public boolean checkSession() {
        Log.d(TAG, "isLoggedIn: checking user session");

//        check session code

        return false;
    }

    public void logsIn(String tag) {
        Log.d(TAG, "logsIn: user logs in");

//        handle login code

        isLoggedIn = true;

        openFragment(tag);
    }

    public void logsOut() {
        Log.d(TAG, "logsOut: user logged out");

//        handle log out code

        openFragment("EXPLORE");

        isLoggedIn = false;
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_explore:
                            Log.d(TAG, "onNavigationItemSelected: explore clicked");
                            openFragment("EXPLORE");
                            return true;
                        case R.id.item_plan:
                            Log.d(TAG, "onNavigationItemSelected: plan clicked");
                            openFragment("PLAN");
                            return true;
                        case R.id.item_add_event:
                            Log.d(TAG, "onNavigationItemSelected: add event clicked");
                            openFragment("ADDEVENT");
                            return true;
                        case R.id.item_history:
                            Log.d(TAG, "onNavigationItemSelected: history clicked");
                            openFragment("HISTORY");
                            return true;
                        case R.id.item_profile:
                            Log.d(TAG, "onNavigationItemSelected: profile clicked");
                            openFragment("PROFILE");
                            return true;
                    }
                    return false;
                }
            };

}
