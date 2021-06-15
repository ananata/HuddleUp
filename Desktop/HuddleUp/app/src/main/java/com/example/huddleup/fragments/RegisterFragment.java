package com.example.huddleup.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.ValueEventListener;

import OtherClasses.CredentialValidation;
import OtherClasses.VisualUtilities;

public class RegisterFragment extends Fragment {
    // Declare instance variables
    private EditText editTextRegisterFirstAndLastName, editTextRegisterUserName,
            editTextRegisterNewUserEmail, editTextRegisterPassword, editTextRegisterPasswordRetype;
    private String userFirstAndLastName, userUserName, userEmail, userPassword, userPasswordRetype;

    // Declare auth variable
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // virtual utility helper
    private VisualUtilities vu = new VisualUtilities();
    private CredentialValidation vt = new CredentialValidation();

    public RegisterFragment() {
        // Required empty public constructor
    }


    /**
     * Mount fragment onto RegisterScreen activity
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    /**
     * Declare onActivityCreated method
     *
     * @param savedInstanceState Defines outcomes for two different cases
     *                           Case 1: Password Recovery Link is Selected
     *                           Displays activity for password recovery
     *                           Case 2: Login Button is clicked
     *                           Validates user inputs
     *                           Begins user authentication process
     *                           <p>
     *                           Update: 03/12/2020 - App will run without crashing however I'm not sure about
     *                           legitimacy of the authentication process (Requires further testing)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        // Apply button action for log-in button
        Button buttonRegister = (Button) getActivity().findViewById(R.id.ButtonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get user inputs
                editTextRegisterFirstAndLastName = getActivity().findViewById(R.id.EditTextRegisterName);
                userFirstAndLastName = editTextRegisterFirstAndLastName.getText().toString();

                editTextRegisterUserName = getActivity().findViewById(R.id.EditTextRegisterUserName);
                userUserName = editTextRegisterUserName.getText().toString();
                userUserName = userUserName.toLowerCase();

                editTextRegisterNewUserEmail = getActivity().findViewById(R.id.EditTextRegisterEmail);
                userEmail = editTextRegisterNewUserEmail.getText().toString();

                editTextRegisterPassword = getActivity().findViewById(R.id.EditTextRegisterPassword);
                userPassword = editTextRegisterPassword.getText().toString();

                editTextRegisterPasswordRetype = getActivity().findViewById(R.id.EditTextRegisterPasswordRetype);
                userPasswordRetype = editTextRegisterPasswordRetype.getText().toString();

                // Create flag for if inputs are correct
                boolean flag = true;

                // Validate input formats
                if (vt.validateEmail(userEmail.trim(), editTextRegisterNewUserEmail) == false) {
                    // Set flag to false
                    flag = false;

                    // Clear text field and shake
                    editTextRegisterNewUserEmail.setText("");
                    VisualUtilities.makeMeShake(editTextRegisterNewUserEmail);

                }

                if (vt.validatePassword(userPassword.trim(), editTextRegisterPassword) == false ||
                        !userPasswordRetype.trim().equals(userPassword.trim())) {
                    // Set flag to false
                    flag = false;

                    // Clear text field and shake
                    editTextRegisterPassword.setText("");
                    VisualUtilities.makeMeShake(editTextRegisterPassword);
                    editTextRegisterPasswordRetype.setText("");
                    VisualUtilities.makeMeShake(editTextRegisterPasswordRetype);
                }


                if (flag) {
                    // flag means input has a valid email and password
                    // add ValueEventListener on userUidMap node to check for a unique username.
                    Database.getUserUidMap().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(userUserName)) {
                                // if child userUserName doesn't exist then the username is unique
                                // popToast, and call registerUser
                                vu.popToast("Creating new account...", getActivity());
                                Task registerUserTask = Auth.instance.registerUser(userEmail, userUserName, userPassword, userFirstAndLastName);
                                        registerUserTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    vu.popToast("New account created", getActivity());
                                                    // Auth calls the transition to HomeScreen or UnverifiedEmailScreen
                                                    //Intent intent = new Intent(getActivity(), HomeScreen.class);
                                                    //startActivity(intent);
                                                } else {
                                                    vu.popToast("Account could not be created", getActivity());
                                                }
                                            }
                                        });
                            } else {
                                // TODO: child userUserName exists, popToast to indicate non-unique username (appears regardless)
                                vu.popToast("Sorry, "+userUserName+" is taken.", getActivity());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("database error", databaseError.toException().getLocalizedMessage());
                        }
                    });
                }
            }
        });
    }
}

                //}
                // Executes only when all validation tests are true
                //if (flag == true && Database.checkingIsUnique != null && Database.isUnique[0]) {
                    // Create new user account
                    //vu.popToast("Creating new account...", getActivity());

                    // this should be set to the username field when it is implemented
                    // We can decide if we want to keep line 133
                    // String userUserName = userEmail.substring(0, userEmail.indexOf('@'));

                    // transition to activity if task is successful, else popToast errors
                    //Task registerUserTask = Auth.instance.registerUser(userEmail, userUserName, userPassword, userFirstAndLastName)
                            //.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                //@Override
                                //public void onComplete(@NonNull Task<AuthResult> task) {
                                //    if (task.isSuccessful()) {
                              //          vu.popToast("New account created", getActivity());
                            //            Intent intent = new Intent(getActivity(), HomeScreen.class);
                          //              startActivity(intent);
                        //            } else {
                      //                  vu.popToast("Account could not be created", getActivity());
                    //                }
                  //              }
                            //});
                //}
                //else {
                    //System.out.println(userUserName+ "!!!!!!!!!!!!! isunique returned "+Database.isUniqueUsername(userUserName.toLowerCase()));
                //}
            //}





