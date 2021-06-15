package com.example.huddleup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.auth.and.database.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import OtherClasses.VisualUtilities;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // Find UI elements
            Preference profileIconPreference = findPreference("update_profile_icon");
            final Preference displayNamePreference = findPreference("update_display_name");
            final Preference updateEmailPreference = findPreference("update_email");
            final Preference updatePasswordPreference = findPreference("update_password");
            final Preference updateUserBio = findPreference("update_bio");

            SwitchPreferenceCompat privacyPreference = findPreference("privacy");
            SwitchPreferenceCompat signOutPreference = findPreference("sign_out");

            final VisualUtilities vu = new VisualUtilities();

            // Add actions

            // Update user bio
            if (updateUserBio != null) {
                updateUserBio.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Context context = getContext();
                        launchCustomDialogForUserBioUpdate();
                        return true;
                    }
                });
            }



            // Launch profile icon activity
            if (profileIconPreference != null) {
                profileIconPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Context context = getContext();
                        if (context != null) {
                            Intent intent = new Intent(context, ProfileIconSelectScreen.class);
                            context.startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });
            }

            if (displayNamePreference != null) {
                displayNamePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        launchCustomDialogForDisplayNameReset();
                        return true;
                    }
                });
            }

            if (updatePasswordPreference != null) {
                updatePasswordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        launchCustomDialogForPassword();
                        return false;
                    }
                });

            }

            if (updateEmailPreference != null) {
                updateEmailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        // Update auth
                        launchCustomDialogForEmailReset();

                        return false;
                    }
                });
            }

            if (privacyPreference != null) {
                privacyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean isSwitchOn = (boolean) newValue;
                        // Get the incoming control
                        SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) preference;

                        // Set the incoming value to the incoming control (Toggle button)
                        switchPreference.setChecked(isSwitchOn);

                        //Add new node to user object - important for user search
                        // Get current users uid
                        String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
                        if (isSwitchOn) {
                            if (currentUserUid.length() != 0) {
                                Database.getUsers().child(currentUserUid).child("privacy").setValue(true);
                                vu.popToast("Updated password", getActivity());
                            }
                            else {
                                Log.d(TAG, "Unable to update privacy settings to true");
                                vu.popToast("Unable to save change", getActivity());
                            }
                            return true;
                        }
                        else {
                            if (currentUserUid.length() != 0)
                                Database.getUsers().child(currentUserUid).child("privacy").setValue(false);
                            else
                                Log.d(TAG, "Unable to update privacy settings to false");
                            return false;
                        }
                    }
                });
            }


            if (signOutPreference != null) {
                signOutPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean isSwitchOn = (Boolean) newValue;

                        // Get the incoming control
                        SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) preference;

                        // Set the incoming value to the incoming control (Toggle button)
                        switchPreference.setChecked(isSwitchOn);
                        switchPreference.setDefaultValue(!isSwitchOn);

                        // Turn switch on
                        if (isSwitchOn) {
                            VisualUtilities vu = new VisualUtilities();
                            vu.popToast("Goodbye!", getActivity());

                            // Sign-out
                            if (Auth.instance.firebaseAuth.getCurrentUser() != null) {
                                vu.popToast("Goodbye!", getActivity());

                                // Sign-out
                                FirebaseAuth.getInstance().signOut();

                                // Navigate back to Main Activity
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                getActivity().startActivity(intent);

                            }
                            else {
                                Log.d(TAG, "Unable to log out");
                            }
                        }
                        return false;
                    }
                });
            }
        }

        private void launchCustomDialogForUserBioUpdate() {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
            final LinearLayout passwordPrompt = alertLayout.findViewById(R.id.LinearLayoutCustomDialogPassword);

            // Use email edit text to get new display name
            final EditText editTextOldUsername = alertLayout.findViewById(R.id.EditTextCustomDialogEmailReset);
            final EditText editTextNewUsername = alertLayout.findViewById(R.id.EditTextCustomDialogPasswordReset);
            final TextView textViewEmailPrompt = alertLayout.findViewById(R.id.TextViewCustomDialogEmailFieldLabel);
            final TextView textViewPasswordPrompt = alertLayout.findViewById(R.id.TextViewCustomDialogPasswordFieldLabel);
            final CheckBox cbToggle = alertLayout.findViewById(R.id.cb_show_pass);

            // Hide check box and password
            cbToggle.setVisibility(View.GONE);
            textViewPasswordPrompt.setVisibility(View.GONE);
            editTextNewUsername.setVisibility(View.GONE);

            // Set edit text prompt
            textViewEmailPrompt.setText(R.string.describe_yourself);

            // Build alert dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            alert.setTitle("Tell us about yourself");

            // this is set the view from XML inside AlertDialog
            alert.setView(alertLayout);

            // Disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing;
                }
            });

            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String newBio = editTextOldUsername.getText().toString();
                    // Process information
                    final FirebaseUser currentUser = Auth.instance.firebaseAuth.getCurrentUser();

                    if (currentUser != null) {
                        // Update username in database
                        updateUserBioInDatabase(newBio);
                    }
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

        private void updateUserBioInDatabase(String newBio) {
            try {
                Database.getUsers().child(Objects.requireNonNull(Auth.instance.firebaseAuth.getCurrentUser())
                        .getUid()).child("bioInfo").setValue(newBio);
            } catch (NullPointerException ex) {
                Log.d("SettingsActivity", "Unable to update database with new bioInfo");
                new VisualUtilities().popToast("Changes were saved", getActivity());
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void launchCustomDialogForDisplayNameReset() {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
            final LinearLayout passwordPrompt = alertLayout.findViewById(R.id.LinearLayoutCustomDialogPassword);

            // Use email edit text to get new display name
            final EditText editTextOldUsername = alertLayout.findViewById(R.id.EditTextCustomDialogEmailReset);
            final EditText editTextNewUsername = alertLayout.findViewById(R.id.EditTextCustomDialogPasswordReset);
            final TextView textViewEmailPrompt = alertLayout.findViewById(R.id.TextViewCustomDialogEmailFieldLabel);
            final TextView textViewPasswordPrompt = alertLayout.findViewById(R.id.TextViewCustomDialogPasswordFieldLabel);
            final CheckBox cbToggle = alertLayout.findViewById(R.id.cb_show_pass);

            // Hide check box
            cbToggle.setVisibility(View.GONE);

            // Change text for email prompt
            textViewEmailPrompt.setText(getResources().getText(R.string.old_username));

            // Change text for password prompt
            textViewPasswordPrompt.setText(getResources().getText(R.string.new_username));


            AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            alert.setTitle("Enter new username");
            // this is set the view from XML inside AlertDialog
            alert.setView(alertLayout);
            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing;
                }
            });

            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String oldUserName = editTextOldUsername.getText().toString();
                    final String newUserName = editTextNewUsername.getText().toString();

                    // Process information
                    FirebaseUser currentUser = Auth.instance.firebaseAuth.getCurrentUser();

                    if (currentUser != null) {
                        // Update auth
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUserName).build();

                        currentUser.updateProfile(profileUpdates);

                        // Update username in database
                        updateUserNameInDatabase(oldUserName, newUserName);
                    }
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void launchCustomDialogForEmailReset() {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
            final LinearLayout passwordPrompt = alertLayout.findViewById(R.id.LinearLayoutCustomDialogPassword);
            final EditText editTextEmail = alertLayout.findViewById(R.id.EditTextCustomDialogEmailReset);
            final CheckBox cbToggle = alertLayout.findViewById(R.id.cb_show_pass);

            // Hide password textbox
            passwordPrompt.setVisibility(View.GONE);

            // Hide check box
            cbToggle.setVisibility(View.GONE);


            AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            alert.setTitle("Enter new email");
            // this is set the view from XML inside AlertDialog
            alert.setView(alertLayout);
            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing;
                }
            });

            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String email = editTextEmail.getText().toString();

                    // Process information
                    final FirebaseUser currentUser = Auth.instance.firebaseAuth.getCurrentUser();

                    if (currentUser != null) {
                        currentUser.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User email address updated.");

                                            // Update email in database
                                            updateEmailInDatabase(email);
                                            new VisualUtilities().popToast("Email updated", getActivity());
                                        }
                                        else {
                                            Log.d(TAG, "Unable to update user email address using auth.");
                                        }
                                    }
                                });

                    }
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void launchCustomDialogForPassword() {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
            final EditText editTextEmail = alertLayout.findViewById(R.id.EditTextCustomDialogEmailReset);
            final EditText editTextPassword = alertLayout.findViewById(R.id.EditTextCustomDialogPasswordReset);
            final CheckBox cbToggle = alertLayout.findViewById(R.id.cb_show_pass);

            cbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        // to encode password in dots
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        // to display the password in normal text
                        editTextPassword.setTransformationMethod(null);
                    }
                }
            });

            AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            alert.setTitle("Enter your current account information");
            // this is set the view from XML inside AlertDialog
            alert.setView(alertLayout);
            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                }
            });

            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();

                    // Process information
                    final FirebaseUser currentUser = Auth.instance.firebaseAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, password);

                    if (currentUser != null) {
                        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Auth.instance.firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                new VisualUtilities().popToast("Password reset email sent", getActivity());
                                                Log.d(TAG, "Password reset email sent");
                                            } else {
                                                new VisualUtilities().popToast("Could not send password reset email", getActivity());
                                                Log.d(TAG, "Unable to send email updated");
                                            }
                                        }
                                    });

                                } else {
                                    Log.d(TAG, "Error auth failed");
                                }
                            }
                        });
                    }

                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

        private void updateUserNameInDatabase(final String oldUserName, final String displayName) {
            // Get current users uid
            final User currentUser = Database.getUserObject(Auth.instance.firebaseAuth.getCurrentUser());

            if (currentUser != null) {

                // Update userName value in user node
                Database.getUsers().child(currentUser.getUid()).child("userName").setValue(displayName);

                // Update team information (required only for teams that user owns)
                ValueEventListener teamsValueListener = new ValueEventListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            // Do nothing
                        }
                        else {
                            // Get database data for current user
                            for (String team : currentUser.teams) {
                                if (team != null) {
                                    // Update value
                                    Database.getFirebaseDatabase().child("teams").child(team).child("owner").setValue(displayName);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO: Set database error
                    }
                };
                // Link listener to database reference
                Database.getFirebaseDatabase().child("teams").addValueEventListener(teamsValueListener);


                // Update username in Uid map
                Database.getUserUidMap().addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (oldUserName.trim().equals(displayName.trim())) {
                            new VisualUtilities().popToast("No update necessary", getActivity());
                        }
                        if (dataSnapshot.hasChild(oldUserName)) {
                            // Remove node
                            Database.getUserUidMap().child(oldUserName).removeValue();

                            // Replace node with new name
                            if (currentUser.getUid() != null ) {
                                Database.getUserUidMap().child(displayName).setValue(currentUser.getUid());
                                new VisualUtilities().popToast("Changes were saved.", getActivity());
                                Log.d(TAG, "Added " + displayName + "/" + currentUser.getUid() + " to Database");
                            }
                            else {
                                Log.d(TAG, "Unable to add " + displayName + "/" + currentUser.getUid() + " to Database");
                            }

                        }
                        else {
                            Log.d(TAG, "Unable to make changes");
                            // new VisualUtilities().popToast("Unable to update username", getActivity());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Add database error message
                        Log.d("user node error", databaseError.toException().getLocalizedMessage());
                    }
                });
            }
            else {
                Log.d(TAG, "Unable to update username");
                new VisualUtilities().popToast("Unable to save changes", getActivity());
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void updateEmailInDatabase(String email) {
            final String currentUserUid = Objects.requireNonNull(Auth.instance.firebaseAuth.getCurrentUser()).getUid();
            // Remove current value
            // Database.getUsers().child(currentUserUid).child("email").removeValue();

            // Replace value
            Database.getUsers().child(currentUserUid).child("email").setValue(Database.EncodeString(email));
        }
    }
}