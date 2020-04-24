package com.example.venture.Fragments.login;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.models.User;
import com.example.venture.viewmodels.explore.UsersViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private View view;
    private Button loginButton;
    private TextView emailText, passwordText;
    private FirebaseAuth mAuth;
    private UsersViewModel usersViewModel;
    User loggedInUser = new User();

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: next fragment to open: " + getTag());
        view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = view.findViewById(R.id.login_button);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);
        mAuth = FirebaseAuth.getInstance();
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean emailCheck = emailCheck();
                boolean passwordCheck = passwordCheck();
                if(emailCheck && passwordCheck) {
                    // handle login code
                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();
                    signIn(email, password);
                } else {
                    if(!emailCheck) {
                        Log.d(TAG, "onClick: invalid email message");
                        view.findViewById(R.id.invalidEmail).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.invalidEmail).setVisibility(View.GONE);
                    }
                    if(!passwordCheck) {
                        Log.d(TAG, "onClick: invalid password message");
                        view.findViewById(R.id.invalidPassword).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.invalidPassword).setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    public void signIn(final String email, String password) {
        Log.d(TAG, "signIn: email:"+email+":pass:"+ password);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userId = currentUser.getUid();
                    Log.d(TAG, "onComplete: email::::::"+currentUser.getEmail());
                    Log.d(TAG, "onComplete: userId:::::"+currentUser.getUid());
                    usersViewModel.getUser(userId).observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            loggedInUser = user;
                            Log.d(TAG, "onComplete: Log in user::::::::::::::::"+ loggedInUser.getName());
                            Log.d(TAG, "onComplete: Log in user::::::::::::::::"+ loggedInUser.getId());
                            Log.d(TAG, "onComplete: Log in user::::::::::::::::"+ loggedInUser.getName());
                            Log.d(TAG, "onChanged: getting tag:::::::::::::::::::::::::"+ getTag());
                            ((MainActivity) getActivity()).logsIn(getTag(), loggedInUser);
                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public boolean passwordCheck() {
        String password = passwordText.getText().toString();
        if(password.equals("")) {
            Log.d(TAG, "passwordCheck: password is empty");
            return false;
        }
        return true;
    }

    public boolean emailCheck() {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = emailText.getText().toString();

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) {
            Log.d(TAG, "emailCheck: email is valid");
            return true;
        } else {
            Log.d(TAG, "emailCheck: email is invalid");
            return false;
        }
    }

}
