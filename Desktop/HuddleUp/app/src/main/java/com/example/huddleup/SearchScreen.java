package com.example.huddleup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.auth.and.database.Team;
import com.example.huddleup.auth.and.database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import OtherClasses.VisualUtilities;

public class SearchScreen extends AppCompatActivity {
    final String[] searchForInput = new String[1];
    final ArrayList<Button> profileButtons = new ArrayList<>();
    final Map<String, String> userMap = new HashMap<>();
    public static User otherUsersProfile;
    public static Team otherTeamsProfile;
    public static boolean userOtherTeamsProfile = false;
    SearchView input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        input = findViewById(R.id.SearchInputEditText);
        Button searchButton = findViewById(R.id.SearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getQuery().toString().length() > 0) {
                    searchForInput[0] = input.getQuery().toString();
                    search();
                    // filterButtons(input.getQuery().toString());
                } else {
                    searchForInput[0] = input.getQuery().toString();
                    new VisualUtilities().popToast("Please enter a search query", SearchScreen.this);
                    // search();
                }
            }
        });

        input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (input.getQuery().toString().length() == 0) {
                    LinearLayout resultsLayout = findViewById(R.id.ResultsScrollView);
                    resultsLayout.removeAllViews();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (input.getQuery().toString().length() == 0) {
                    LinearLayout resultsLayout = findViewById(R.id.ResultsScrollView);
                    resultsLayout.removeAllViews();
                }
                return false;
            }
        });


        //searchButton.addOnClickListener();
    }

    void search() {
        final DatabaseReference teamsDb = Database.getTeams();
        final DatabaseReference usersDb = Database.getUsers();
        final LinkedList<String> results = new LinkedList<>();
        if (userMap.size() > 0) {
            userMap.clear();
        }

        // first search users
        usersDb.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Iterable<DataSnapshot> usersChildren = dataSnapshot.getChildren();
                usersChildren.forEach(dataSnap -> {
                    String child = dataSnap.getKey(); // Gets user uid
                    if (dataSnap.hasChild("privacy") && !dataSnap.child("privacy").getValue(boolean.class) || !dataSnap.hasChild("privacy")) {
                        Log.d("SearchScreen", "What is child: " + child);
                        String mapPut = child.substring(child.lastIndexOf("/") + 1);
                        String mapPutVal = (String) dataSnap.child("userName").getValue();
                        Log.d("SearchScreen", child.substring(child.indexOf("/") + 1));
                        userMap.put(child.substring(child.indexOf("/") + 1), mapPutVal);
                        userMap.put(mapPutVal, child.substring(child.indexOf("/") + 1));
                        results.add("users / " + child);
                        filterResults(results);
                    }
                    else {
                        // Do not add private users to search list
                    }
                });

                // then search teams
                teamsDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Iterable<DataSnapshot> usersChildren = dataSnapshot.getChildren();
                        usersChildren.forEach(dataSnap -> {
                            String child = dataSnap.getKey();
                            results.add("teams / "+child);
                        });

                        // last step of search is display the results
                        filterResults(results);
                        displayResults(results);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("[!] SearchScreen/teams", databaseError.toException().getLocalizedMessage());
                    }
                }); // <-- end of teams value listener
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("[!] SearchScreen/users", databaseError.toException().getLocalizedMessage());
            }
        }); // <-- end of users value listener

    }

    /**
     *
     * @param results LinkedList<String>
     */
    private void filterResults(LinkedList<String> results) {
        String query = input.getQuery().toString();
        Log.d("Search screen", "Current search query: " + query);
        LinkedList<String> copyResults = new LinkedList<>();
        for (String result : results) {
            Log.d("SearchScreen", "Current result: " + result);
            if (result.contains(query.trim().toUpperCase())) {
                Log.d("Search screen ", "The result that was added: " + result);
                copyResults.add(result);
            }
        }

        if (copyResults.size() == 0) {
            results.clear();
        } else {
            results.clear();
            results.addAll(copyResults);
        }
    }

    void displayResults(LinkedList<String> results) {
        ArrayList<View> resultsToViews = new ArrayList<>();

        ScrollView scroll = findViewById(R.id.SearchResultsScrollView);
        LinearLayout resultsLayout = findViewById(R.id.ResultsScrollView);
        if (resultsLayout.getChildCount() > 0) {
            resultsLayout.removeAllViews();
        }

        if (input.getQuery().toString().length() == 0) {
            resultsLayout.removeAllViews();
        }

        // create Buttons for the results
        if (results.size() == 0) {
            new VisualUtilities().popToast("No matches", SearchScreen.this);
            return;
        }

        for (int i = 0; i < results.size(); i++) {
            final Button b = new Button(this);
            b.setBackgroundColor(getResources().getColor(R.color.white));
            // b.setTextColor(getResources().getColor(R.color.white));
            b.setPadding(20, 20, 20, 20);
            profileButtons.add(b);
        }

        // use buttonIndex to assign for unique profileButtons
        int buttonIndex = 0;
        for (String s : results) {
            System.out.println("RESULT--------->"+s);
            if (s.toLowerCase().contains("users")) {
                // display username instead of uid
                String userUid = s.substring(s.indexOf("/")+1).trim();
                s = "users / " + userMap.get(userUid);
            }

            profileButtons.get(buttonIndex).setText(s);

            // adds and increments buttonIndex
            resultsToViews.add(profileButtons.get(buttonIndex++));

            resultsLayout.addView(resultsToViews.get(resultsToViews.size()-1));
        }

        setProfileButtonListeners(profileButtons);
    }

    void setProfileButtonListeners(final ArrayList<Button> bList) {
        final String[] profileUid = new String[1];

        for (Button b : bList) {
            b.setOnClickListener(v -> {
                String bString = b.getText().toString();

                if (bString.toLowerCase().contains("users")) {
                    // b is a button for user profile
                    int startIndex = 1 + bString.indexOf("/");
                    String userUid = bString.substring(startIndex).trim();
                    userUid = userMap.get(userUid);
                    profileUid[0] = userUid;
                    goToUserProfile(userUid);

                } else if (bString.toLowerCase().contains("teams")) {
                    // b is a button for team profile
                    int startIndex = 1 + bString.indexOf("/");
                    String teamUid = bString.substring(startIndex).trim();
                    profileUid[0] = teamUid;
                    goToTeamProfile(profileUid[0]);

                }
            });
        }
    }

    //TODO: for loadUserProfile and loadTeamProfile implement methods to transition to a profile screen
    // that displays the info according to the User/Team object passed or clicked on search

    void goToUserProfile(String uid) {
        System.out.println("---->LOAD USER PROFILE SCREEN FOR "+uid);
        DatabaseReference usersDb = Database.getUsers();
        usersDb.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.child("userName").getValue();
                String firstName = (String) dataSnapshot.child("firstName").getValue();
                String lastName = (String) dataSnapshot.child("lastName").getValue();
                String bioInfo = (String) dataSnapshot.child("bioInfo").getValue();
                String email = Database.DecodeString((String) dataSnapshot.child("email").getValue());
                Integer icon;
                if (dataSnapshot.hasChild("profileIcon")) {
                    icon = dataSnapshot.child("profileIcon").getValue(Integer.class);
                }
                else {
                    // Replace icon with blue circle
                    icon = R.drawable.blue_circle;
                }
                List<String> teamNames = new ArrayList<>();
                List<Team> teamList = new ArrayList<>();
                if (dataSnapshot.hasChild("teams")) {
                    Iterable<DataSnapshot> children = dataSnapshot.child("teams").getChildren();
                    for (DataSnapshot d : children)  {
                        if (d.getValue() != null) {
                            teamNames.add((String) d.getValue());
                        }
                    }
                }

                User ownerOfSelectedProfile = new User(username, firstName, lastName, bioInfo, teamNames);
                ownerOfSelectedProfile.setUid(dataSnapshot.getKey());
                ownerOfSelectedProfile.setProfileIcon(icon);
                ownerOfSelectedProfile.email = email;
                loadUserProfile(ownerOfSelectedProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("get user by uid ", databaseError.toException().getLocalizedMessage());
            }
        });
    }

    void loadUserProfile(User user) {
        // TODO: call screen transition, and populate user profile screen with @param user
        System.out.println("---->loadUserProfile -- "+user.username+" uid="+user.getUid());
        userOtherTeamsProfile = true;
        otherUsersProfile = user;
        Intent i = new Intent(this, MyProfileScreen.class);
        startActivity(i);
        //MyProfileScreen
    }

    void goToTeamProfile(String uid) {
        System.out.println("---->LOAD TEAM PROFILE SCREEN FOR "+uid);
        DatabaseReference teamsDb = Database.getTeams();
        teamsDb.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                String sport = (String) dataSnapshot.child("sport").getValue();
                String owner = (String) dataSnapshot.child("owner").getValue();
                String ownerUid = (String) dataSnapshot.child("ownerUid").getValue();
                String date = (String) dataSnapshot.child("date").getValue();
                String teamDesc = (String) dataSnapshot.child("teamDescription").getValue();
                Integer icon = dataSnapshot.child("imageIcon").getValue(Integer.class);
                int imageIcon = (int) icon;

                Team selectedTeamProfile = new Team(name, sport, owner, ownerUid, date, teamDesc, imageIcon);
                loadTeamProfile(selectedTeamProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("goToTeamProfile error", databaseError.toException().getLocalizedMessage());
            }
        });
    }

    void loadTeamProfile(Team team) {
        System.out.println("---->loadTeamProfile -- "+team.getTeamName() + ", "+team.getSport() +", "+team.getCreateDate() +", "+team.getTeamOwner());
        otherTeamsProfile = team;
        userOtherTeamsProfile = true;
        Intent i = new Intent(this, TeamScreen.class);
        startActivity(i);
    }
}
