package com.example.huddleup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.auth.and.database.Message;
import com.example.huddleup.auth.and.database.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import OtherClasses.MessageAdapter;
import OtherClasses.PlannedEventAdapter;
import OtherClasses.VisualUtilities;

public class TimelineScreen extends AppCompatActivity {
    DatabaseReference teamDatabaseReference;
    ProgressDialog progressDialog;
    RecyclerView timelineRecyclerView;
    PlannedEventAdapter adapter;
    List<PlannedEvent> finalEventList = new ArrayList<>();
    VisualUtilities vu = new VisualUtilities();
    final String TAG = "TimelineScreen";
    String currentTeam = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_screen);
        // adds support for the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get current teamName from intent
        Bundle passedObject = getIntent().getExtras();
        if (passedObject != null) {
            currentTeam = (String)passedObject.getSerializable("LOADED_TEAM");
            vu.popToast("Loading timeline for " + currentTeam, TimelineScreen.this);
        }
        else {
            vu.popToast("Loading timeline for all team events", TimelineScreen.this);
            Log.d("TeamScreen", "Unable to get value from previous screen, loading all events");
        }

        // Lookup the recyclerview in activity layout
        timelineRecyclerView = (RecyclerView) findViewById(R.id.RecyclerViewTimelineScreen);
        timelineRecyclerView.addItemDecoration(new DividerItemDecoration(TimelineScreen.this, LinearLayoutManager.VERTICAL));
        timelineRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext(), RecyclerView.VERTICAL, false);
        timelineRecyclerView.setLayoutManager(layoutManager);

        // Get database stuff
        final String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference currentUserNode = Database.getFirebaseDatabase().child("users").child(currentUserUid);

        // Check data
        if (currentUserUid != null) {
            Log.d("MessagesScreen", "This is the current user's uid" + currentUserUid);
            // getAllTeams(currentUserUid, TeamSelectScreen.this, teamRecyclerView);
        } else {
            Log.d("MessagesScreen", "Unable to get current uid");
        }


        Database.getUsers().child(Auth.instance.firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            List<String> userTeamEvents = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("events")) {
                    for ( DataSnapshot event : dataSnapshot.child("events").getChildren()) {
                        String currentEventUid = event.getKey();
                        if (currentEventUid != null) {
                            Log.d(TAG, "Current eventUid: " + currentEventUid);
                            userTeamEvents.add(currentEventUid);
                        }
                        else {
                            Log.d(TAG, "Could not obtain eventUid from user node");
                        }
                    }

                    getTeamEvent(TimelineScreen.this, userTeamEvents);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing
            }
        });
    }

    private void getTeamEvent(Activity context, List<String> eventUids) {
        ValueEventListener plannedEventValueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PlannedEvent> eventLog = new ArrayList<>();
                for (String eventUid : eventUids) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        vu.popToast("No events at the moment", context);
                    }
                    else {
                        if (eventUid != null) {
                            // Load event from database into event object
                            PlannedEvent currentEvent = new PlannedEvent();
                            DataSnapshot snapshot = dataSnapshot.child(eventUid);
                            Log.d("TimelineScreen", "Current node: " + eventUid);

                            String date = null;
                            if (snapshot.hasChild("date")) {
                                // Get subject of message
                                date = snapshot.child("date").getValue(String.class);
                                currentEvent.setStringDate(date);
                                Log.d(TAG, "event date " + date);
                            }
                            Log.d(TAG, "event subject " + date);

                            String description = null;
                            if (snapshot.hasChild("description")) {
                                description = snapshot.child("description").getValue(String.class);
                                Log.d(TAG, "event description " + description);
                                currentEvent.setDescription(description);
                            }
                            Log.d(TAG, "event body " + description);

                            String name = null;
                            if (snapshot.hasChild("name")) {
                                name = snapshot.child("name").getValue(String.class);
                                Log.d(TAG, "event name " + name);
                                currentEvent.setName(name);
                            }
                            Log.d(TAG, "event name " + name);

                            long time = 0;
                            if (snapshot.hasChild("time")) {
                                time = snapshot.child("time").getValue(long.class);
                                Log.d(TAG, "event time " + time);
                                currentEvent.setLongTime(time);
                            }
                            Log.d(TAG, "event time " + time);

                            ArrayList<String> teams = new ArrayList<>();
                            if (snapshot.hasChild("teams")) {
                                for (DataSnapshot teamSnapshot : snapshot.child("teams").getChildren()) {
                                    teams.add(teamSnapshot.getKey());
                                }
                                if (teams.size() != 0)
                                    currentEvent.setTeamNames(teams);
                            }

                            // Add event object to array list
                            eventLog.add(currentEvent);
                        } else {
                            Log.d("TimelineScreen", "Unable to get valid snapshot");
                        }
                    }
                }

                // Modify list so only current team's events are shown
                eventLog = getCurrentTeamEvents(eventLog);
                if (eventLog.size() != 0) {
                    // Pass data to adapter
                    Log.d(TAG, "event adapter " + eventLog.size());
                    finalEventList = eventLog;

                    // Sort list to allow for chronological listing
                    Collections.sort(finalEventList);
                    Log.d(TAG, "First value in finalEventList: " + finalEventList.get(0) + "");

                    adapter = new PlannedEventAdapter(context, finalEventList);
                    timelineRecyclerView.setAdapter(adapter);
                }
                else {
                    vu.popToast("No events sent yet", context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException(); // Don't ignore errors
            }
        };

        // Link listener to database reference
        Database.getFirebaseDatabase().child("events").addValueEventListener(plannedEventValueEventListener);
    }

    // Remove events not affiliated with current team
    private List<PlannedEvent> getCurrentTeamEvents(List<PlannedEvent> finalEventList) {
        if (currentTeam == null) {
            Log.d(TAG, "No changes made to finalEventList");
            return finalEventList;
        }
        List<PlannedEvent> copyfinalEventList = new LinkedList<>();
        for (PlannedEvent thisEvent : finalEventList) {
            if (thisEvent.getTeamNames().contains(currentTeam.trim())) {
                copyfinalEventList.add(thisEvent);
                Log.d(TAG, "Removed the plannedEvent " + thisEvent);
            }
        }

        return copyfinalEventList;
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
                Toast.makeText(this, "Back selected", Toast.LENGTH_SHORT);
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