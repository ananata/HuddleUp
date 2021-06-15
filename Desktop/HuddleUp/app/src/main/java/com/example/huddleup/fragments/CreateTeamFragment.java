package com.example.huddleup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.huddleup.HomeScreen;
import com.example.huddleup.R;
import com.example.huddleup.TeamCreateScreen;
import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.auth.and.database.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import OtherClasses.VisualUtilities;

/**
 * A simple {@link Fragment} subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
public class CreateTeamFragment extends Fragment {
    private Team newTeam;
    private EditText teamDesc, teamSport, teamName, playerName, playerRole;
    private String description, username, date, owner, ownerUID, sport, name, player, role;
    private Integer icon;
    AppCompatImageButton addPlayers;
    Map<String, String> playerAndRole = new HashMap<>();

    private VisualUtilities vu = new VisualUtilities();
    private boolean flag = false;
    private boolean newPlayerAdded = false;

    public CreateTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_team, container, false);
    }

    /**
     * Declare onActivityCreated method
     * @param savedInstanceState
     *
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        // Initialize map
        final Map<String, String> playerRoleMap = new HashMap<>();

        // Get database stuff
        final String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference currentUserNode = Database.getFirebaseDatabase().child("users").child(currentUserUid);

        // Set variables to UI elements
        teamDesc = getActivity().findViewById(R.id.EditTextTeamDescription);
        teamSport = getActivity().findViewById(R.id.EditTextTeamSport);
        teamName = getActivity().findViewById(R.id.EditTextTeamName);
        playerName = getActivity().findViewById(R.id.EditTextCreateTeamPlayerUsername);
        playerRole = getActivity().findViewById(R.id.EditTextCreateTeamPlayerRole);

        // Add action to "Save Changes" Button
        Button saveTeam = getActivity().findViewById(R.id.ButtonSaveNewTeam);
        saveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPlayerAdded) {
                    newTeam = new Team(name, sport, owner, ownerUID, date, description, icon);
                    newTeam.addTeamMembers(playerAndRole);
                    if (newTeam != null) {
                        Database.addNewTeam2(newTeam);
                        vu.popToast("New Team Added", getActivity());
                    } else {
                        vu.popToast("Unable to add team", getActivity());
                    }
                }
                else {
                    // Click "+" button to get all inputs <- Quick fix
                    getActivity().findViewById(R.id.ButtonAddPlayer).performClick();
                    if (name == null) {
                        name = teamName.getText().toString();
                        sport = teamSport.getText().toString();
                        owner = Auth.instance.user.username;
                        ownerUID = Auth.instance.user.getUid();
                        date = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(new Date()) + "";
                        description = teamDesc.getText().toString();
                        icon = TeamCreateScreen.selectedSportImage;
                    }
                    newTeam = new Team(name, sport, owner, ownerUID, date, description, icon);
                    if (newTeam != null) {
                        Database.addNewTeam2(newTeam);
                        vu.popToast("New Team Added", getActivity());
                    } else {
                        vu.popToast("Unable to add team", getActivity());
                    }
                }
                // Launch new activity
                Intent intent = new Intent(getActivity(), HomeScreen.class);
                startActivity(intent);
            }
        });

        addPlayers = getActivity().findViewById(R.id.ButtonAddPlayer);
        addPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get values from Database
                Database.getFirebaseDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user inputs
                        description = teamDesc.getText().toString();
                        Log.d("CreateTeamFragment desc", description);

                        sport = teamSport.getText().toString();
                        Log.d("CreateTeamFragment spor", sport);

                        name = teamName.getText().toString();
                        Log.d("CreateTeamFragment name", name);

                        owner = dataSnapshot.child("users").child(currentUserUid).child("userName").getValue(String.class);
                        Log.d("CreateTeamFragment user", owner);

                        ownerUID = currentUserUid;
                        Log.d("CreateTeamFragment uid", ownerUID);

                        // Get today's date
                        date = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(new Date()) + "";
                        Log.d("CreateTeamFragment date", date);

                        // Get image id
                        icon = TeamCreateScreen.selectedSportImage;
                        Log.d("CreateTeamFragment icon", icon + "");

                        // Get new player information
                        player = "" + playerName.getText().toString();
                        role = "" + playerRole.getText().toString();

                        // Clear fields
                        playerName.setText("");
                        playerRole.setText("");

                        // Get new player Uid
                        if (player.length() != 0) {
                            newPlayerAdded = true;
                            String newPlayerUid = dataSnapshot.child("userUidMap").child(player).getValue(String.class);
                            if (newPlayerUid != null) {
                                // newTeam = new Team(name, sport, owner, ownerUID, date, description, icon);
                                // newTeam.addTeamMember(player, newPlayerUid, role);

                                // Store playerUid instead <- just in case user changes his/her username
                                playerAndRole.put(newPlayerUid, role);
                                flag = true;
                            }
                        }

                        if (!flag && newPlayerAdded) {  vu.popToast("Invalid username for player", getActivity()); }
                        else {
                            vu.popToast("Player added", getActivity());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Do nothing
                    }
                });
            }
        });
    }
}