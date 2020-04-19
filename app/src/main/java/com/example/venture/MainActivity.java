package com.example.venture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.venture.Fragments.addevent.AddEventFragment;
import com.example.venture.Fragments.event.EventFragment;
import com.example.venture.Fragments.explore.ExploreFragment;
import com.example.venture.Fragments.history.HistoryFragment;
import com.example.venture.Fragments.loginSignup.LoginSignupFragment;
import com.example.venture.Fragments.plan.PlanFragment;
import com.example.venture.Fragments.login.LoginFragment;
import com.example.venture.Fragments.signup.SignupFragment;
import com.example.venture.Fragments.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigation;
    private Boolean isLoggedIn;

    private Fragment exploreFragment;
    private Fragment planFragment;
    private Fragment addEventFragment;
    private Fragment historyFragment;
    private Fragment profileFragment;
    private Fragment loginSignupFragment;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exploreFragment = new ExploreFragment();
        planFragment = new PlanFragment();
        addEventFragment = new AddEventFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();
        loginSignupFragment = new LoginSignupFragment();

        mAuth =FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        isLoggedIn = checkSession();
        Log.d(TAG, "onCreate: Loggedin "+isLoggedIn);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.item_explore);
//        openFragment("EXPLORE");
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser user) {
        currentUser = user;
    }

    public void openFragment(String tag) {
        Log.d(TAG, "openFragment: " + tag);
        Boolean loginPrompt = !(tag.equals("EXPLORE") || isLoggedIn);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (loginPrompt) {
            Log.d(TAG, "openFragment: user not logged in");
            transaction.replace(R.id.container, new LoginSignupFragment(), tag);
        } else {
            getSupportActionBar().setTitle(tag);
            switch(tag){
                case "EXPLORE":
                    transaction.replace(R.id.container, exploreFragment, tag);
                    break;
                case "PLAN":
                    transaction.replace(R.id.container, planFragment, tag);
                    break;
                case "ADDEVENT":
                    getSupportActionBar().setTitle("ADD EVENT");
                    transaction.replace(R.id.container, new AddEventFragment(), tag);
                    break;
                case "HISTORY":
                    transaction.replace(R.id.container, historyFragment, tag);
                    break;
                case "PROFILE":
                    transaction.replace(R.id.container, profileFragment, tag);
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
    public void openEventFragment(String eventId, String tag) {
        Log.d(TAG, "openEventFragment: opening event with id: " + eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new EventFragment(eventId), tag);
        transaction.commit();
    }

    public boolean checkSession() {
        Log.d(TAG, "isLoggedIn: checking user session "+ currentUser);

        if(currentUser!=null) {
            return true;
        } else {
            return false;
        }
    }

    public void logsIn(String tag, FirebaseUser user) {
        Log.d(TAG, "logsIn: user logs in");

        currentUser = user;
        isLoggedIn = true;

        openFragment(tag);
    }

    public void logsOut() {
        Log.d(TAG, "logsOut: user logged out");

//        handle log out code
        mAuth.signOut();
        isLoggedIn = false;

        bottomNavigation.setSelectedItemId(R.id.item_explore);
//        openFragment("EXPLORE");


    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
