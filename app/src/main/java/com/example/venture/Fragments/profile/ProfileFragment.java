package com.example.venture.Fragments.profile;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

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

    private TextView userName;
    private TextView bioDescription;
    private UsersViewModel usersViewModel;

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
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        usersViewModel.init();
        String userId = ((MainActivity)getActivity()).getCurrentUser().getUid();
        usersViewModel.getUser(userId).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                userName.setText(user.getName());
                if(user.getBio()!=null)
                    bioDescription.setText(user.getBio());
            }
        });
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
