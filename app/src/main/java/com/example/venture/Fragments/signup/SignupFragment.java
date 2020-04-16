package com.example.venture.Fragments.signup;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class SignupFragment extends Fragment {

    private static final String TAG = "SignupFragment";

    private View view;
    private Button signupButton;
    private EditText nameText, emailText, passwordText;
    private FirebaseAuth mAuth;
    private UsersViewModel usersViewModel;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: next fragment to open: " + getTag());
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        //Set widgets
        signupButton = view.findViewById(R.id.signup_button);
        nameText = view.findViewById(R.id.nameText);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);

        //firebase setup
        mAuth = FirebaseAuth.getInstance();

        //mvvm viewmodel
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        //Signup onClick
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean nameCheck = nameCheck();
                boolean emailCheck = emailCheck();
                boolean passwordCheck = passwordCheck();

                if(nameCheck && emailCheck && passwordCheck) {
                    // handle sign up code
                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();
                    String name = nameText.getText().toString();
                    createAccount(name, email, password);
                } else {
                    if(!nameCheck) {
                        Log.d(TAG, "onClick: invalid name message");
                        view.findViewById(R.id.invalidName).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.invalidName).setVisibility(View.GONE);
                    }
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

    public void createAccount(final String name, final String email, String password) {
        Log.d(TAG, "createAccount: Creating an account:email:"+email+":pass:"+ password);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                User user = new User();
                                user.setId(currentUser.getUid());
                                user.setEmail(email);
                                user.setName(name);
                                usersViewModel.addUser(user);

                                ((MainActivity) getActivity()).logsIn(getTag(), user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
    }

    public boolean nameCheck() {
        String name = nameText.getText().toString().trim();
        if(name.equals("")) {
            Log.d(TAG, "onFocusChange: name in empty");
            return false;
        }
        return true;
    }

    public boolean passwordCheck() {
        String password = passwordText.getText().toString();
        if(password.equals("")) {
            Log.d(TAG, "passwordCheck: password is empty");
            return false;
        }
        return true;
    }

    public boolean emailCheck(){
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