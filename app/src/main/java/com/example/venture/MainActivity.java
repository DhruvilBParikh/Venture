package com.example.venture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.venture.Fragments.addevent.AddEventFragment;
import com.example.venture.Fragments.explore.ExploreFragment;
import com.example.venture.Fragments.history.HistoryFragment;
import com.example.venture.Fragments.loginSignup.LoginSignupFragment;
import com.example.venture.Fragments.plan.PlanFragment;
import com.example.venture.Fragments.login.LoginFragment;
import com.example.venture.Fragments.signup.SignupFragment;
import com.example.venture.Fragments.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigation;
    private Boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        isLoggedIn = checkSession();
        isLoggedIn = false;

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.item_explore);
//        openFragment("EXPLORE");
    }

    public void openFragment(String tag) {
        Log.d(TAG, "openFragment: " + tag);
        Boolean loginPrompt = !(tag.equals("EXPLORE") || isLoggedIn);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(loginPrompt) {
            Log.d(TAG, "openFragment: user not logged in");
            transaction.replace(R.id.container, new LoginSignupFragment(), tag);
        } else {
            getSupportActionBar().setTitle(tag);
            switch(tag){
                case "EXPLORE":
                    transaction.replace(R.id.container, new ExploreFragment(), tag);
                    break;
                case "PLAN":
                    transaction.replace(R.id.container, new PlanFragment(), tag);
                    break;
                case "ADDEVENT":
                    getSupportActionBar().setTitle("ADD EVENT");
                    transaction.replace(R.id.container, new AddEventFragment(), tag);
                    break;
                case "HISTORY":
                    transaction.replace(R.id.container, new HistoryFragment(), tag);
                    break;
                case "PROFILE":
                    transaction.replace(R.id.container, new ProfileFragment(), tag);
                    break;
            }
        }
        transaction.commit();
    }

    public void openLoginFragment(String tag) {
        Log.d(TAG, "openLoginFragment: opening login fragment");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new LoginFragment(), tag);
        transaction.commit();
    }

    public void openSignupFragment(String tag) {
        Log.d(TAG, "openLoginFragment: opening login fragment");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new SignupFragment(), tag);
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
        isLoggedIn = false;
        bottomNavigation.setSelectedItemId(R.id.item_explore);
//        openFragment("EXPLORE");


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
