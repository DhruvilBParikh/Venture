package com.example.venture.Fragments.signup;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.venture.MainActivity;
import com.example.venture.R;

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

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: next fragment to open: " + getTag());
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        signupButton = view.findViewById(R.id.signup_button);
        nameText = view.findViewById(R.id.nameText);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean name = nameCheck();
                boolean email = emailCheck();
                boolean password = passwordCheck();

                if(name && email && password) {
                    // handle sign up code

                    ((MainActivity) getActivity()).logsIn(getTag());

                } else {
                    if(!name) {
                        Log.d(TAG, "onClick: invalid name message");
                        view.findViewById(R.id.invalidName).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.invalidName).setVisibility(View.GONE);
                    }
                    if(!email) {
                        Log.d(TAG, "onClick: invalid email message");
                        view.findViewById(R.id.invalidEmail).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.invalidEmail).setVisibility(View.GONE);
                    }
                    if(!password) {
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

    public boolean emailCheck()
    {
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