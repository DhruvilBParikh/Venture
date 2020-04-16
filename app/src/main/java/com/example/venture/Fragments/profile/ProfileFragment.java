package com.example.venture.Fragments.profile;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.models.User;
import com.example.venture.viewmodels.explore.UsersViewModel;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView userName;
    private TextView bioDescription;
    private UsersViewModel usersViewModel;
    private SharedPreferences mPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        userName = view.findViewById(R.id.userName);
        bioDescription = view.findViewById(R.id.bioDescription);
        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        String name = mPreferences.getString("name", "");
        String bio = mPreferences.getString("bio", "");
        userName.setText(name);
        if(bio!="")
            bioDescription.setText(bio);
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_logout_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                ((MainActivity)getActivity()).logsOut();
                return true;
            }
            case R.id.edit_profile: {

//                openFragment("editProfileFragment");
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
