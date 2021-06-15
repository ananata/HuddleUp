package com.example.huddleup.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.huddleup.ForgotPasswordScreen;
import com.example.huddleup.R;
import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import OtherClasses.CredentialValidation;
import OtherClasses.VisualUtilities;


/**
 * A simple {@link Fragment} subclass for user login.
 */
public class LoginFragment extends Fragment {

    // Declare instance variables
    private EditText inputEmail, inputPassword;
    private String userPassword;
    private final String[] userEmail = new String[1];
    private Switch aSwitch;
    private VisualUtilities vu = new VisualUtilities();
    private CredentialValidation cv = new CredentialValidation();

    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Mounts fragment onto selected activity
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    /**
     * Declare onActivityCreated method
     * @param savedInstanceState
     *
     * Defines outcomes for two different cases
     * Case 1: Password Recovery Link is Selected
     *      Displays activity for password recovery
     * Case 2: Login Button is clicked
     *      Validates user inputs
     *      Begins user authentication process
     *
     * Update: 03/12/2020 - App will run without crashing however I'm not sure about
     * legitimacy of the authentication process (Requires further testing)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        // DOING NOTHING YET; leave
        // plan to implement this:
        //  -- if SwitchRememberMe is on when logging in, add to the user's node in Users on the database
        //  -- see Auth constructor for further details.
        //  --  Auth's userSignIn method now takes an additional boolean arg for rememberUserLogin
        // TODO: implement call to set rememberMe node for the user on the database
        final boolean[] rememberUserLogin = new boolean[]{false};

        // Remember Me Switch
        aSwitch = (Switch) getActivity().findViewById(R.id.SwitchRememberMe);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                rememberUserLogin[0] = on;
                if (rememberUserLogin[0]) {
                    vu.popToast("Switch on", getActivity());
                } else {
                    vu.popToast("Switch off", getActivity());
                }
            }
        }); // <-- end of aSwitch (View SwitchRememberMe)

        // Sign In Button
        Button signInButton = (Button) getActivity().findViewById(R.id.ButtonLogin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                // 04/17/2020 <- Just to let user know that input was read
                vu.popToast("Authenticating...", getActivity());

                // Get user inputs
                inputEmail = getActivity().findViewById(R.id.EditTextLoginEmail);
                userEmail[0] = inputEmail.getText().toString();
                inputPassword = getActivity().findViewById(R.id.EditTextLoginPassword);
                userPassword = inputPassword.getText().toString();

                final boolean[] flag = new boolean[]{true};

                // flag determines whether user is signing in with their username or email
                //  set to boolean return of validateEmail
                flag[0] = cv.validateEmail(userEmail[0].trim(), inputEmail);

                // username only useful if validateEmail = flag = false
                final String username = userEmail[0].trim().toLowerCase();

                Database.getUserUidMap().addValueEventListener(new ValueEventListener() {
                    // check the userUidMap Database section to see if it hasChild(username)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotUserUidMap) {
                        final String uid[] = new String[1];
                        if (!flag[0]) {
                            // go to child username, and get user's uid by .getValue()
                            uid[0] = (String) dataSnapshotUserUidMap.child(username).getValue();
                        }

                        // usersNodeToSearch will just look at Users on the database if user provided a valid email login
                        // else it goes to the actual user node to get the email.
                        DatabaseReference usersNodeToSearch =
                                uid[0] == null ?
                                        Database.getUsers() :
                                        Database.getUsers().child(uid[0]);
                        usersNodeToSearch.addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotUsersNodeUid) {
                                if (dataSnapshotUsersNodeUid.hasChild("email") || flag[0]) {
                                    if (!flag[0]) {
                                        // get the user's email by getValue()
                                        userEmail[0] = (String) dataSnapshotUsersNodeUid.child("email").getValue();
                                    }

                                    if (Auth.instance == null) {
                                        Auth.instance = new Auth();
                                    }

                                    Auth.instance.userSignIn(userEmail[0], userPassword, rememberUserLogin[0]).
                                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                // try to authenticate with userEmail, popToast user errors
                                                // actual userSignIn method handles other calls onComplete
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // successful login, check rememberUserLogin[0]
                                                        if (rememberUserLogin[0]) {
                                                            if (uid[0] == null) {
                                                                // using firebaseAuth.getCurrentUser instead of user.uid,
                                                                // to avoid attempting to grab User before it is set in userSignIn
                                                                uid[0] = Auth.instance.firebaseAuth.getCurrentUser().getUid();
                                                            }

                                                            // Database users uid node should write child rememberMe
                                                            Database.setRememberMeForUserLogin(uid[0], rememberUserLogin[0]);
                                                        }
                                                    } else {
                                                        // not successful either due to no account or bad password
                                                        vu.popToast("Invalid username or password", getActivity());
                                                    }
                                                }
                                            });
                                } else {
                                    //  let me know if this ever happens. (means the email node hasn't been set -- could be for unverified)
                                    Log.d("error missing child : ",
                                            "Make sure the email node for a user still " +
                                                    "exists in the Firebase console");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseErrorAtUsersNodeUid) {
                                // let me know if you see this in your run log
                                Log.d("Users error: ", databaseErrorAtUsersNodeUid.toException().getLocalizedMessage());
                            }
                        }); // <-- end of usersNodeToSearch database section listener
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseErrorAtUserUidMap) {
                        // let me know if you see this in your run log
                        Log.d("userUidMap error: ",
                                databaseErrorAtUserUidMap.toException().getLocalizedMessage());
                    }
                }); // <-- end of userUidMap database section listener.
            } // <-- end of signInButton onClick
        }); // <-- end of signInButton OnClickListener

        // Apply button action for password recovery TextView
        TextView recoverPassword = (TextView) getActivity().findViewById(R.id.TextViewLoginRecoverPassword);
        recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vu.popToast("Loading password recovery screen...", getActivity());
                showPasswordRecoveryFragment(view);
            }
        });
    }

    /**
     * Shifts screen from this to password recovery screen
     * @param view View
     */
    private void showPasswordRecoveryFragment(View view) {
        Intent intent = new Intent(getActivity(), ForgotPasswordScreen.class);
        startActivity(intent);
    }


    /**
     * Called when the fragment is no longer connected to the Activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
