package com.example.huddleup.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huddleup.R;
import com.example.huddleup.auth.and.database.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;


public class ForgotPasswordFragment extends Fragment {
    // Declare instance variables
    private EditText recoveryEmail;
    private String userEmail;
    private Button getPasswordButton;

    // Declare auth variable
    private FirebaseAuth mAuth;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Send reset link to user
     * @param savedInstanceState Bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        // Apply button action for log-in button
        getPasswordButton = (Button) getActivity().findViewById(R.id.ButtonRecoverPassword);
        getPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoveryEmail = (EditText) getActivity().findViewById(R.id.EditTextRecoverWithEmail);
                userEmail = recoveryEmail.getText().toString();
                sendPasswordResetEmail(userEmail);
            }
        });

    }

    /**
     * Sends password reset link to registered user
     * Case 1: Email is sent to user
     * Case 2: Email is not sent to user because...
     *      User is not registered
     *      Email is not formatted correctly
     * @param email String
     */
    private void sendPasswordResetEmail(final String email) {
        // initializes Auth instance
        FirebaseApp myFirebaseApp = FirebaseApp.initializeApp(getActivity());
        //mAuth = new Auth(myFirebaseApp);
        mAuth = Auth.instance.firebaseAuth;
        // Validate email format
        if (validateEmail(email, (EditText)getActivity().findViewById(R.id.EditTextRecoverWithEmail))) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Reset password", "Email sent.");
                                popToast("Email sent.");
                            } else {
                                Log.d("Unable to send link", "Email was not sent.");
                                popToast("Unable to send link");
                            }
                        }
                    });
        }
        else {
            // Clear field
            EditText inputEmail = (EditText)getActivity().findViewById(R.id.EditTextRecoverWithEmail);
            makeMeShake(inputEmail);
            inputEmail.setText("");
        }

    }

    /**
     * Minor animation
     * Adapted from Windless on:
     * https://stackoverflow.com/questions/9448732/shaking-wobble-view-animation-in-android
     * @param view      View that will be animated
     *
     */
    public static void makeMeShake(View view) {
        Animation anim = new TranslateAnimation(-5, 5,0,0);
        anim.setDuration(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(5);
        view.startAnimation(anim);
    }

    /**
     * Verify that email is formatted correctly
     * @param email String
     * @return  boolean
     * TRUE: Email is valid (matches expected format)
     * FALSE: Email is invalid
     */
    private boolean validateEmail(final String email, EditText inputEmail) {
        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

        // Check for empty strings or null
        if (email.length() == 0) {
            inputEmail.setError("Required field");
            return false;
        }

        // Check email format
        // Check email format
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    /**
     * Generates custom pop-up toast that display information
     * temporarily.
     * @param message String
     */
    private void popToast(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        View view = toast.getView();

        // Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark),
                PorterDuff.Mode.SRC_IN);

        // Show toast
        toast.show();
    }

    /**
     * Mounts fragment to activity
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }
}
