package com.example.venture_v0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.venture_v0.ExploreFragment.newInstance;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigation;
    private Boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    transaction.replace(R.id.container, ExploreFragment.newInstance("", ""), tag);
                    break;
                case "PLAN":
                    transaction.replace(R.id.container, PlanFragment.newInstance("", ""), tag);
                    break;
                case "ADDEVENT":
                    transaction.replace(R.id.container, AddEventFragment.newInstance("", ""), tag);
                    break;
                case "HISTORY":
                    transaction.replace(R.id.container, HistoryFragment.newInstance("", ""), tag);
                    break;
                case "PROFILE":
                    transaction.replace(R.id.container, ProfileFragment.newInstance("", ""), tag);
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
