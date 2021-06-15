package com.example.huddleup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import OtherClasses.RecyclerItemClickListener;
import OtherClasses.TeamAdapter;
import OtherClasses.VisualUtilities;

public class TeamSelectScreen extends AppCompatActivity {
    DatabaseReference teamDatabaseReference;
    ProgressDialog progressDialog;
    RecyclerView teamRecyclerView;
    TeamAdapter adapter;
    List<Team> finalTeamList = new ArrayList<>();
    VisualUtilities vu = new VisualUtilities();
    FloatingActionButton addTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select_screen);

        // Lookup the recyclerview in activity layout
        teamRecyclerView = (RecyclerView) findViewById(R.id.RecyclerViewTeamSelectScreen);
        teamRecyclerView.addItemDecoration(new DividerItemDecoration(TeamSelectScreen.this, LinearLayoutManager.VERTICAL));
        teamRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext(), RecyclerView.VERTICAL, false);
        teamRecyclerView.setLayoutManager(layoutManager);

        // Get database stuff
        final String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference currentUserNode = Database.getFirebaseDatabase().child("users").child(currentUserUid);

        // Get data
        if (currentUserUid != null) {
            Log.d("TeamSelectScreen", "This is the current user's uid" + currentUserUid);
            getAllTeams(currentUserUid, TeamSelectScreen.this, teamRecyclerView);
        } else {
            Log.d("TeamSelectScreen", "Unable to get current uid");
        }

        // Adds support for the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add button activity to floating action button
        addTeam = findViewById(R.id.FloatingActionButtonNewTeam);
        addTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, TeamCreateScreen.class);
                context.startActivity(intent);
            }
        });

        // Add onClick listener to elements in RecycleView
        teamRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), teamRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onItemClick(View view, int position) {
                        //TeamAdapter adapter = (TeamAdapter) teamRecyclerView.getAdapter();
                        //List<Team> teams = adapter.getArrayList();
                        //if (teams == null) {
                        //   Log.d("TeamSelectScreen", "Unable to get team object from adapter");
                        //}
                        Team currentTeam = finalTeamList.get(position);

                        // Pass team object as extra in intent
                        if (currentTeam != null) {
                            Intent intent = intent = new Intent(TeamSelectScreen.this, TeamScreen.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("CURRENT_TEAM", currentTeam);
                            intent.putExtras(bundle);
                            TeamSelectScreen.this.startActivity(intent);
                        } else {
                            Log.d("TeamSelectScreen", "Unable to pass team object to TeamScreen");
                        }

                        // vu.popToast("The team at position " + position + " was pressed for a short time", (Activity) view.getContext());
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // No action associated with click
                    }
                })
        );
    }

    // TODO: Replace with user specific teams
    public void getAllTeams(final String userUid, final Context context, final RecyclerView teamRecyclerView) {
        // Launch Progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading Data from Firebase Database");
        progressDialog.show();

        // Read team objects from database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Team> teams = new ArrayList<>();
                if (dataSnapshot.getChildrenCount() == 0) {
                    vu.popToast("No teams could be found in database", (Activity) context);
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Check to see if user owns or is a member of the current team node
                        if (snapshot.child("ownerUid").getValue(String.class).equals(userUid) || snapshot.child("members").hasChild("userUid")) {
                            Integer icon = snapshot.child("imageIcon").getValue(Integer.class);
                            String name = snapshot.child("name").getValue(String.class);
                            String date = snapshot.child("date").getValue(String.class);
                            String owner = snapshot.child("owner").getValue(String.class);
                            String description = snapshot.child("teamDescription").getValue(String.class);
                            String sport = snapshot.child("sport").getValue(String.class);
                            String ownerUid = snapshot.child("ownerUid").getValue(String.class);

                            // Get members of players team (needed for TeamScreen)
                            Map<String, String> memberRoleMap = new HashMap<>();
                            for (DataSnapshot playerSnapshot : snapshot.child("members").getChildren()) {
                                Log.d("TeamSelectScreen", "Current child: " + playerSnapshot.getKey());
                                memberRoleMap.put(playerSnapshot.getKey(), playerSnapshot.getValue(String.class));
                            }
                            // Create new team then add to adapter
                            Team myTeam = new Team(name, sport, owner, ownerUid, date, description, icon);
                            myTeam.addTeamMembers(memberRoleMap);
                            teams.add(myTeam);
                        }
                    }
                }

                if (teams.size() != 0) {
                    // Pass data to adapter
                    adapter = new TeamAdapter(teams);
                    teamRecyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                    // Update global variable
                    finalTeamList = teams;
                } else {
                    vu.popToast("No teams linked to your account", (Activity) context);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                throw databaseError.toException(); //Don't ignore errors
            }
        };
        teamDatabaseReference = Database.getFirebaseDatabase().child("teams");
        teamDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }
}

//    // for what happens when the menu parts are clicked
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.navHome: // goes back to the home activity
//                Intent i = new Intent(this, HomeScreen.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                return true;
//            case R.id.navSettings:
//                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT);
//                /* Starting another activity example
//                startActivity(new Intent(HomeScreen.this, SettingsScreen.class));
//                 */
//                return true;
//            case R.id.navCalendar:
//                Toast.makeText(this, "Calendar selected", Toast.LENGTH_SHORT);
//                startActivity(new Intent(this, CalenderScreen.class));
//                return true;
//            case R.id.navSearch:
//                Toast.makeText(this, "Search selected", Toast.LENGTH_SHORT);
//                startActivity(new Intent(this, SearchScreen.class));
//                return true;
//            case R.id.navUser:
//                Toast.makeText(this, "User selected", Toast.LENGTH_SHORT);
//                return true;
//            case R.id.navMyProfile:
//                Toast.makeText(this, "My Profile selected", Toast.LENGTH_SHORT);
//                startActivity(new Intent(this, MyProfileScreen.class));
//                return true;
//            case R.id.navProfileIcon:
//                Toast.makeText(this, "Profile Icon selected", Toast.LENGTH_SHORT);
//                startActivity(new Intent(this, ProfileIconSelectScreen.class));
//                return true;
//            case R.id.navTeam:
//                Toast.makeText(this, "Team selected", Toast.LENGTH_SHORT);
//                return true;
//            case R.id.navTeamSelect:
//                Toast.makeText(this, "Select Team selected", Toast.LENGTH_SHORT);
//                startActivity(new Intent(this, TeamSelectScreen.class));
//                return true;
//            case R.id.navTeamCreate:
//                Toast.makeText(this, "Create Team selected", Toast.LENGTH_SHORT);
//                startActivity(new Intent(this, TeamCreateScreen.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}




