package com.example.huddleup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.auth.and.database.Team;
import com.example.huddleup.auth.and.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import OtherClasses.PlayerAdapter;
import OtherClasses.RecyclerItemClickListener;
import OtherClasses.TeamAdapter;
import OtherClasses.VisualUtilities;

public class TeamScreen extends AppCompatActivity {
    ImageView sportIconImageView;
    Button joinButton, eventsButton;
    TextView teamNameTextView, teamSportTextView, teamDateCreatedTextView, teamDescTextView;
    Team currentTeam;
    ProgressDialog progressDialog;
    RecyclerView userRecyclerView;
    PlayerAdapter adapter;
    VisualUtilities vu = new VisualUtilities();
    List<User> finalPlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_screen);

        // Adds support for the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get current team from intent
        Bundle passedObject = getIntent().getExtras();
        if (passedObject != null && !SearchScreen.userOtherTeamsProfile) {
            currentTeam = (Team)passedObject.getSerializable("CURRENT_TEAM");
        }
        else if (SearchScreen.userOtherTeamsProfile) {
            currentTeam = SearchScreen.otherTeamsProfile;
        }
        else {
            Log.d("TeamScreen", "Unable to get value from previous screen");
        }

        // Lookup the recyclerview in activity layout
        userRecyclerView = (RecyclerView) findViewById(R.id.RecyclerViewTeamScreen);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(TeamScreen.this, LinearLayoutManager.VERTICAL));
        userRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext(), RecyclerView.VERTICAL, false);
        userRecyclerView.setLayoutManager(layoutManager);

        // Get information about team
        teamNameTextView = findViewById(R.id.TextViewTeamScreenTeamName);
        teamSportTextView = findViewById(R.id.TextViewTeamScreenTeamSport);
        teamDateCreatedTextView = findViewById(R.id.TextViewTeamScreenDateCreated);
        teamDescTextView = findViewById(R.id.TextViewTeamScreenDescription);
        sportIconImageView = findViewById(R.id.ImageViewTeamScreenSportIcon);

        // Set values
        teamNameTextView.setText(currentTeam.getTeamName());
        teamSportTextView.setText(currentTeam.getSport());
        String dateCreated = "Created " + currentTeam.getCreateDate();
        teamDateCreatedTextView.setText(dateCreated);
        teamDescTextView.setText(currentTeam.getTeamDescription());
        //Drawable res = getResources().getDrawable(currentTeam.getImageIcon());
        //sportIconImageView.setImageDrawable(res);
        if (currentTeam.getImageIcon() != null) {
            Log.d("TeamScreen", "This is the icon: " + currentTeam.getImageIcon());
            Drawable res = getResources().getDrawable(currentTeam.getImageIcon());
            sportIconImageView.setImageDrawable(res);
        }
        else {
            Log.d("TeamScreen", "Unable to set team icon: " + currentTeam.getImageIcon());
        }

        // Mount player information in recyclerview object
        final String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
        if (currentUserUid != null) {
            Log.d("TeamScreen", "currentUserUid is: " + currentUserUid);
            getAllPlayers(currentUserUid, TeamScreen.this, userRecyclerView);
        }
        else {
            Log.d("TeamScreen", "currentUserUid is null");
        }

        // Add onClick activity to buttons
        joinButton = findViewById(R.id.ButtonTeamScreenRequestToJoin);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TeamScreen.this);
                Context context = TeamScreen.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                alert.setTitle("Provide Player Information");

                // Add a EditText for new player username
                final EditText usernameEditText = new EditText(context);
                usernameEditText.setHint("Username");
                usernameEditText.setPadding(20, 20, 20, 20);
                layout.addView(usernameEditText);

                // Add another EditText here for new player role
                final EditText roleEditText = new EditText(context);
                roleEditText.setHint("Position on Team");
                roleEditText.setPadding(20, 20, 20, 20);
                layout.addView(roleEditText);

                alert.setView(layout); // Mount objects onto dialog box

                alert.setPositiveButton("SEND REQUEST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Execute database method
                        String playerUserName = usernameEditText.getText().toString();
                        String playerRole = roleEditText.getText().toString();
                        String teamName = teamNameTextView.getText().toString();
                        Database.addNewMemberToExistingTeam(teamName, playerUserName, playerRole);

                        // TODO: Send email notification to team owner
                        // Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                        // intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                        // intent.putExtra(Intent.EXTRA_TEXT, "Body of email");
                        // intent.setData(Uri.parse("mailto:default@recipient.com")); // or just "mailto:" for blank
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                        // startActivity(intent);

                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Clear text fields for next time
                        usernameEditText.setText("");
                        roleEditText.setText("");
                    }
                });
                alert.show();
            }
        });

        eventsButton = findViewById(R.id.ButtonTeamScreenViewTeamEvents);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pass current team name to timeline screen
                String currentTeamName = currentTeam.getTeamName();
                Intent intent = intent = new Intent(TeamScreen.this, TimelineScreen.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("LOADED_TEAM", currentTeamName);
                intent.putExtras(bundle);
                TeamScreen.this.startActivity(intent);
                // Intent intent = new Intent(TeamScreen.this, TimelineScreen.class);
                vu.popToast("Navigating to team timeline...", TeamScreen.this);
            }
        });

        // Add onClick listener to elements in RecycleView
        userRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), userRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // vu.popToast("The team at position " + position + " was pressed for a short time", (Activity) view.getContext());
                        // TODO: Allow users to visit players profile page
                        //Intent intent = intent = new Intent(TeamScreen.this, MyProfileScreen.class);
                        //TeamScreen.this.startActivity(intent);

                        // Get user object from list
                        User clickedPlayer = finalPlayers.get(position);

                        // Pass user object as serializable item to MyProfileScreen
                        if (clickedPlayer != null) {
                            Intent intent = intent = new Intent(TeamScreen.this, MyProfileScreen.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("CLICKED_PLAYER", clickedPlayer);
                            intent.putExtras(bundle);
                            TeamScreen.this.startActivity(intent);
                        }
                        else {
                            Log.d("TeamScreen", "Unable to pass clicked user object to MyProfileScreen");
                        }


                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        // No action associated with click
                    }
                })
        );

    }


    public void getAllPlayers(final String userUid, final Context context, final RecyclerView teamRecyclerView) {
        // Launch Progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading Data from Firebase Database");
        progressDialog.show();

        ValueEventListener playerValueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> players = new ArrayList<>();
                List<String> playerUids = new ArrayList<String>(currentTeam.getMemberRoleMap().keySet());
                Log.d("TeamScreen", "There are " + playerUids.size());

                if (playerUids.size() == 0 || dataSnapshot.getChildrenCount() == 0) {
                    vu.popToast("Could not retrieve member data", TeamScreen.this);
                }
                else {
                    for (String uid : playerUids) {
                        DataSnapshot snapshot = dataSnapshot.child(uid);

                        if (snapshot != null) {
                            // Get information about user and add to new user object
                            User currentPlayer = new User();
                            String userName = snapshot.child("firstName").getValue(String.class).trim() + " "
                                    + snapshot.child("lastName").getValue(String.class).trim();
                            currentPlayer.setFullName(userName);

                            Integer userIcon;
                            if (snapshot.hasChild("profileIcon")) {
                                // Get user's profile Icon if defined
                                userIcon = snapshot.child("profileIcon").getValue(int.class);
                                currentPlayer.setProfileIcon(userIcon);
                            }
                            else {
                                // A blue circle will be placed instead
                                userIcon = R.drawable.blue_circle;
                                currentPlayer.setProfileIcon(userIcon);
                            }

                            // Get player's email <- for onClick action
                            if (snapshot.hasChild("email")) {
                                currentPlayer.email = Database.DecodeString(snapshot.child("email").getValue(String.class));
                            }

                            // Get player's bio <- for onClick action
                            if (snapshot.hasChild("bioInfo")) {
                                currentPlayer.bioInfo = snapshot.child("bioInfo").getValue(String.class);
                            }

                            // Get player's username <- for onClick action
                            if (snapshot.hasChild("userName")) {
                                currentPlayer.username = snapshot.child("userName").getValue(String.class);
                            }

                            // Get player's uid
                            currentPlayer.setUid(snapshot.getKey());

                            // Get current player's role
                            Map<String, String> roleMap = currentTeam.getMemberRoleMap();
                            String role = roleMap.get(snapshot.getKey());
                            Log.d("TeamScreen role", role);
                            currentPlayer.setRole(role);

                            // Add user object to array list
                            players.add(currentPlayer);
                        }
                        else {
                            Log.d("TeamScreen", "Unable to get valid snapshot");
                        }
                    }
                }

                if (players.size() != 0) {
                    // Pass data to adapter
                    finalPlayers = players;
                    progressDialog.dismiss();
                    adapter = new PlayerAdapter(players);
                    userRecyclerView.setAdapter(adapter);

                }
                else {
                    vu.popToast("Unable to load members from database", TeamScreen.this);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException(); // Don't ignore errors
            }

        };
        final DatabaseReference teamNode = Database.getFirebaseDatabase().child("users");
        teamNode.addValueEventListener(playerValueEventListener);
    }

    // adds the menu for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigationbar, menu);
        return true;
    }

    // for what happens when the menu parts are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.navHome:
                Intent i = new Intent(this, HomeScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            case R.id.navSettings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navCalendar:
                Toast.makeText(this, "Calendar selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navUser:
                Toast.makeText(this, "User selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navMyProfile:
                Toast.makeText(this, "My Profile selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navProfileIcon:
                Toast.makeText(this, "Profile Icon selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navTeam:
                Toast.makeText(this, "Team selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navTeamSelect:
                Toast.makeText(this, "Select Team selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navTeamCreate:
                Toast.makeText(this, "Create Team selected", Toast.LENGTH_SHORT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}