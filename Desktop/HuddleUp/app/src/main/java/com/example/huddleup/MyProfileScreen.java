package com.example.huddleup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.auth.and.database.Team;
import com.example.huddleup.auth.and.database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import OtherClasses.RecyclerItemClickListener;
import OtherClasses.TeamAdapter;
import OtherClasses.VisualUtilities;

public class MyProfileScreen extends AppCompatActivity {
    ImageView profileIconImageView;
    ImageButton changeProfileIconImageButton;
    TextView nameTextView, usernameTextView, bioInfoTextView, emailTextView;
    String name, username;
    DatabaseReference teamDatabaseReference;
    ProgressDialog progressDialog;
    RecyclerView teamRecyclerView;
    TeamAdapter adapter;
    VisualUtilities vu = new VisualUtilities();
    User teamPlayer;
    boolean loadPlayer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_screen);

        // adds support for the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get current team from intent
        Bundle passedObject = getIntent().getExtras();
        if (passedObject != null) {
            teamPlayer = (User)passedObject.getSerializable("CLICKED_PLAYER");
            loadPlayer = true;
        }
        else if (SearchScreen.userOtherTeamsProfile && SearchScreen.otherUsersProfile != null) {
            teamPlayer = SearchScreen.otherUsersProfile;
            loadPlayer = true;
        }
        else {
            Log.d("TeamScreen", "Unable to get value from previous screen");
        }

        // Get database stuff
        final String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference currentUserNode = Database.getFirebaseDatabase().child("users").child(currentUserUid);

        currentUserNode.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (loadPlayer) {
                    // Hide change profile icon button
                    changeProfileIconImageButton.setVisibility(View.GONE);
                    loadClickedPlayerProfile(dataSnapshot, teamPlayer);
                }
                else {
                    loadCurrentUserProfile(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeProfileIconImageButton = findViewById(R.id.ImageButtonMyProfileChangeProfileIcon);
        changeProfileIconImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ProfileIconSelectScreen.class);
                context.startActivity(intent);
            }
        });

        // Lookup the recyclerview in activity layout
        teamRecyclerView = (RecyclerView) findViewById(R.id.RecyclerViewMyProfileScreen);
        teamRecyclerView.addItemDecoration(new DividerItemDecoration(MyProfileScreen.this, LinearLayoutManager.VERTICAL));
        teamRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext(), RecyclerView.VERTICAL, false);
        teamRecyclerView.setLayoutManager(layoutManager);

        // Display teams
        if (!loadPlayer) {
            TeamSelectScreen tss = new TeamSelectScreen();
            tss.getAllTeams(currentUserUid, MyProfileScreen.this, teamRecyclerView);
        }
        else {
            // Use other player's uid
            TeamSelectScreen tss = new TeamSelectScreen();
            Log.d("MyProfileScreen", "Current user's uid is: " + teamPlayer.getUid());
            tss.getAllTeams(teamPlayer.getUid(), MyProfileScreen.this, teamRecyclerView);
        }

        // Add onClick listener to elements in RecycleView
        teamRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), teamRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TeamAdapter adapter = (TeamAdapter)teamRecyclerView.getAdapter();
                        if (adapter != null) {
                            Team clickedTeam = adapter.getTeamList().get(position);
                            if (clickedTeam != null) {
                                Intent intent = intent = new Intent(MyProfileScreen.this, TeamScreen.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("CURRENT_TEAM", clickedTeam);
                                intent.putExtras(bundle);
                                MyProfileScreen.this.startActivity(intent);
                                // vu.popToast("You can pass this object to next activity", MyProfileScreen.this);
                            }
                            else {
                                Log.d("TeamScreen", "Unable to pass clicked team object to TeamScreen");
                            }
                        }
                        else {
                            Log.d("MyProfileScreen", "Unable to get adapter from recyclerview");
                        }
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        // No action associated with click
                    }
                })
        );
    }

    private void loadClickedPlayerProfile(DataSnapshot dataSnapshot, User teamPlayer) {
        // Get player's name
        name = teamPlayer.getFullName();
        nameTextView = findViewById(R.id.TextViewMyProfileUserName);
        nameTextView.setText(name);

        // Get player's username
        username = teamPlayer.getUsername();
        usernameTextView = findViewById(R.id.TextViewMyProfileUserUsername);
        usernameTextView.setText(username);

        // Get player's information
        String bioInfo = teamPlayer.bioInfo;
        if (bioInfo.equals("N/a")) { bioInfo = "Not much here"; };
        bioInfoTextView = findViewById(R.id.TextViewMyProfileUserBio);
        bioInfoTextView.setText(bioInfo);

        // Get player's profile
        profileIconImageView = findViewById(R.id.ImageViewMyProfileScreenProfileIcon);
        if (teamPlayer.getProfileIcon() != null) {
            Drawable res = getResources().getDrawable(teamPlayer.getProfileIcon());
            profileIconImageView.setImageDrawable(res);
        }

        // Get player's email
        String email = teamPlayer.email;
        emailTextView = findViewById(R.id.TextViewMyProfileUserEmail);
        emailTextView.setText(email);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadCurrentUserProfile(DataSnapshot dataSnapshot) {
        String firstname = Objects.requireNonNull(dataSnapshot.child("firstName").getValue(String.class)).trim();
        String lastname = Objects.requireNonNull(dataSnapshot.child("lastName").getValue(String.class)).trim();
        name = firstname + " " + lastname;
        nameTextView = findViewById(R.id.TextViewMyProfileUserName);
        nameTextView.setText(name);

        username = dataSnapshot.child("userName").getValue(String.class);
        usernameTextView = findViewById(R.id.TextViewMyProfileUserUsername);
        usernameTextView.setText(username);

        String bioInfo = dataSnapshot.child("bioInfo").getValue(String.class);
        if (bioInfo.equals("N/a")) { bioInfo = "Not much here"; };
        bioInfoTextView = findViewById(R.id.TextViewMyProfileUserBio);
        bioInfoTextView.setText(bioInfo);

        profileIconImageView = findViewById(R.id.ImageViewMyProfileScreenProfileIcon);
        if (dataSnapshot.hasChild("profileIcon")) {
            Drawable res = getResources().getDrawable(dataSnapshot.child("profileIcon").getValue(Integer.class));
            profileIconImageView.setImageDrawable(res);
        }

        String email = Database.DecodeString(dataSnapshot.child("email").getValue(String.class));
        emailTextView = findViewById(R.id.TextViewMyProfileUserEmail);
        emailTextView.setText(email);
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
            case R.id.navHome: // goes back to the home activity
                Intent i = new Intent(this, HomeScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            case R.id.navSettings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT);
                /* Starting another activity example
                startActivity(new Intent(HomeScreen.this, SettingsScreen.class));
                 */
                return true;
            case R.id.navCalendar:
                Toast.makeText(this, "Calendar selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(this, CalenderScreen.class));
                return true;
            case R.id.navSearch:
                Toast.makeText(this, "Search selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(this, SearchScreen.class));
                return true;
            case R.id.navUser:
                Toast.makeText(this, "User selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navMyProfile:
                Toast.makeText(this, "My Profile selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(this, MyProfileScreen.class));
                return true;
            case R.id.navProfileIcon:
                Toast.makeText(this, "Profile Icon selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(this, ProfileIconSelectScreen.class));
                return true;
            case R.id.navTeam:
                Toast.makeText(this, "Team selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navTeamSelect:
                Toast.makeText(this, "Select Team selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(this, TeamSelectScreen.class));
                return true;
            case R.id.navTeamCreate:
                Toast.makeText(this, "Create Team selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(this, TeamCreateScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}