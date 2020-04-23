package com.example.venture.Fragments.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.viewmodels.explore.UsersViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int defaultImage = R.drawable.profile_pic;

    private ImageView profileImage;
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
        profileImage = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        bioDescription = view.findViewById(R.id.bioDescription);
        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        String name = mPreferences.getString("name", "");
        String bio = mPreferences.getString("bio", "");
        String profilePic = mPreferences.getString("profilePic", "");
        userName.setText(name);
        if(!bio.equals(""))
            bioDescription.setText(bio);
        Glide.with(this)
                .load(profilePic) // image url
                .placeholder(defaultImage) // any placeholder to load at start
                .error(defaultImage)  // any image in case of error
                .override(100, 100) // resizing
                .centerCrop()
                .into(profileImage);
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
                ((MainActivity)getActivity()).openEditProfileFragment("PROFILE");
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
