package com.example.venture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.venture.Fragments.addevent.AddEventFragment;
import com.example.venture.Fragments.event.EventFragment;
import com.example.venture.Fragments.explore.ExploreFragment;
import com.example.venture.Fragments.history.HistoryFragment;
import com.example.venture.Fragments.login.LoginFragment;
import com.example.venture.Fragments.loginSignup.LoginSignupFragment;
import com.example.venture.Fragments.plan.PlanFragment;
import com.example.venture.Fragments.login.LoginFragment;
import com.example.venture.Fragments.profile.EditProfileFragment;
import com.example.venture.Fragments.signup.SignupFragment;
import com.example.venture.Fragments.profile.ProfileFragment;
import com.example.venture.Fragments.signup.SignupFragment;
import com.example.venture.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String CHANNEL_ID = "NewEventChannel";

    private BottomNavigationView bottomNavigation;
    private Boolean isLoggedIn;

    private Fragment exploreFragment;
    private Fragment planFragment;
    private Fragment addEventFragment;
    private Fragment historyFragment;
    private Fragment profileFragment;
    private Fragment loginSignupFragment;
    private FirebaseAuth mAuth;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //fragments
        exploreFragment = new ExploreFragment();
        planFragment = new PlanFragment();
        addEventFragment = new AddEventFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();
        loginSignupFragment = new LoginSignupFragment();

        //auth
        mAuth =FirebaseAuth.getInstance();

        //preferences
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        mPreferencesEditor = mPreferences.edit();

        //check session
        isLoggedIn = checkSession();
        Log.d(TAG, "onCreate: Loggedin "+isLoggedIn);


        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.item_explore);

    }

    public boolean checkSession() {
        Log.d(TAG, "isLoggedIn: checking user session ");

        if(mPreferences.getString("userId","") == "") {
            return false;
        } else {
            Log.d(TAG, "checkSession: userId"+mPreferences.getString("userId",""));
            Log.d(TAG, "checkSession: email"+mPreferences.getString("email",""));
            Log.d(TAG, "checkSession: name"+mPreferences.getString("name",""));
            Log.d(TAG, "checkSession: bio"+mPreferences.getString("bio",""));
            return true;
        }
    }

    public void logsIn(String tag, User user) {
        Log.d(TAG, "logsIn: user logs in");
        setPreferences(user);
        isLoggedIn = true;
        openFragment(tag);
    }

    public void setPreferences(User user) {
        mPreferencesEditor.putString("userId", user.getId());
        mPreferencesEditor.putString("email", user.getEmail());
        mPreferencesEditor.putString("name", user.getName());
        mPreferencesEditor.putString("bio", user.getBio());
        mPreferencesEditor.putString("profilePic", user.getProfilePic());
        mPreferencesEditor.commit();
    }

    public void logsOut() {
        Log.d(TAG, "logsOut: user logged out");

//        handle log out code
        mPreferencesEditor.remove("userId");
        mPreferencesEditor.remove("email");
        mPreferencesEditor.remove("name");
        mPreferencesEditor.remove("profilePic");
        mPreferencesEditor.remove("bio");

        mPreferencesEditor.commit();
        mAuth.signOut();
        isLoggedIn = false;
        bottomNavigation.setSelectedItemId(R.id.item_explore);
    }

    public void openFragment(String tag) {
        Log.d(TAG, "openFragment: " + tag);
        Boolean loginPrompt = !(tag.equals("EXPLORE") || isLoggedIn);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (loginPrompt) {
            Log.d(TAG, "openFragment: user not logged in");
            getSupportActionBar().setTitle("LOGIN-SIGNUP");
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
                default:
                    getSupportActionBar().setTitle("EVENT");
                    transaction.replace(R.id.container, new EventFragment(tag), "EXPLORE");
                    break;
            }
        }
        transaction.commit();
    }

    public void openLoginFragment(String tag) {
        Log.d(TAG, "openLoginFragment: opening login fragment");
        getSupportActionBar().setTitle("LOGIN");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new LoginFragment(), tag);
        transaction.commit();
    }

    public void openSignupFragment(String tag) {
        Log.d(TAG, "openLoginFragment: opening login fragment");
        getSupportActionBar().setTitle("SIGNUP");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new SignupFragment(), tag);
        transaction.commit();
    }
    public void openEventFragment(String eventId, String tag) {
        Log.d(TAG, "openEventFragment: opening event with id: " + eventId);
        getSupportActionBar().setTitle("EVENT");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new EventFragment(eventId), tag);
        transaction.commit();
    }

    public void openEditProfileFragment(String tag) {
        Log.d(TAG, "openEditProfile: opening an edit profile fragment "+mPreferences.getString("userId",""));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportActionBar().setTitle("EDIT PROFILE");
        transaction.replace(R.id.container, new EditProfileFragment(), tag);
        transaction.commit();
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

    @Override
    public boolean onSupportNavigateUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        onBackPressed();
        return true;
    }

    public void sendNotification() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("A new event was organized around you")
                .setContentText("Check out!")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        manager.notify(0, notification);
    }

}
