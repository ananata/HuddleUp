package com.example.huddleup.auth.and.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.huddleup.PlannedEvent;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database
{
    final static DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    final static DatabaseReference users = firebaseDatabase.child("users");
    private static Object iface;
    public static DatabaseReference getUsers() { return users; }
    final static DatabaseReference teams = firebaseDatabase.child("teams");
    public static DatabaseReference getTeams() { return teams; }

    final static DatabaseReference events = firebaseDatabase.child("events");

    final static DatabaseReference userUidMap = firebaseDatabase.child("userUidMap");
    public static DatabaseReference getUserUidMap() { return userUidMap; }

    public static PlannedEvent eventBeingCreating;

    //final static DatabaseReference userProfileIcon = firebaseDatabase.child("userIcon");
    // ----> Make a child node per user (under users.child(user.getUid()))
    //           with key=profileIcon, and value=index of the selected icon

    public Database () {
    }

    public static void addTeamToEvent(final PlannedEvent event, final String teamName) {
        Log.d("Database owner", "Owner of event: " + event.getOwnerUid());
        events.child(event.getEventUid()).child("teams").child(teamName).setValue(teamName);
        // event.addTeam(teamName);
    }

    public static void createEvent(final PlannedEvent event) {
        // Store name of event and date in user node
        Log.d("Database owner", "Owner of event: " + event.getOwnerUid());
        Log.d("Database owner", "Owner of event: " + event.getEventUid());
        users.child(event.getOwnerUid()).child("events").child(event.getEventUid()).setValue(event.getName());

        // Call next method to write event details to event node
        finishCreateEvent(event.getEventUid());
        eventBeingCreating = event;
    }

    public static void createNewTeamEvent(final PlannedEvent event) {
        if (event.getEventUid() != null) {
            events.child(event.getEventUid()).child("eventUid").setValue(event.getEventUid());
            Log.d("Database event", "Event uid: " + event.getEventUid());
        }
        if (event.getOwnerUid() != null) {
            events.child(event.getEventUid()).child("ownerUid").setValue(event.getOwnerUid());
            users.child(event.getOwnerUid()).child("events").child(event.getEventUid()).setValue(event.getName());
            Log.d("Database event", "Owner uid: " + event.getOwnerUid());
        }
        if (event.getStringDate() != null) {
            events.child(event.getEventUid()).child("date").setValue(event.getStringDate());
            Log.d("Database event", "Event date: " + event.getStringDate());
        }
        if (event.getDescription() != null) {
            events.child(event.getEventUid()).child("description").setValue(event.getDescription());
            Log.d("Database event", "Event desc: " + event.getDescription());
        }
        if (event.getLocation() != null) {
            events.child(event.getEventUid()).child("location").setValue(event.getLocation());
            Log.d("Database event", "Event location: " + event.getLocation());
        }
        if (event.getName() != null) {
            events.child(event.getEventUid()).child("name").setValue(event.getName());
            Log.d("Database event", "Event name: " + event.getName());
        }
        if (event.getLongTime() != 0) {
            events.child(event.getEventUid()).child("time").setValue(event.getLongTime());
            Log.d("Database event", "Event time: " + event.getLongTime());
        }


        for (String team : event.getTeams()) {
            teams.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (team != null && dataSnapshot.hasChild(team)) {
                        notifyPlayersOfNewEvent(team, event);
                    }
                    // Else: do nothing (the team doesn't exist in database)
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private static void finishCreateEvent(final String eventUid) {
        // events.child(eventUid).setValue(eventBeingCreating.getName());

        DatabaseReference eventNode = events.child(eventUid);
        eventNode.child("name").setValue(eventBeingCreating.getName());
        eventNode.child("location").setValue(eventBeingCreating.getLocation());
        eventNode.child("date").setValue(eventBeingCreating.getDate());
        eventNode.child("time").setValue(eventBeingCreating.getTime());
        eventNode.child("description").setValue(eventBeingCreating.getDescription());
        eventNode.child("ownerUid").setValue(eventBeingCreating.ownerUid);

        // eventNode.child("teams").setValue("teams-involved");
        DatabaseReference eventTeamsNode = eventNode.child("teams");
        int i = 0;
        for (String teamUid : eventBeingCreating.getTeams()) {
            // Check to see if team exists in database before adding
            eventTeamsNode.child(String.valueOf(i++)).setValue(teamUid);
        }

//        eventNode.child("users").setValue("users-involved");
//        DatabaseReference eventUsersNode = eventNode.child("users");
//        i = 0;
//        for (String userUid : event.getUsers()) {
//            eventUsersNode.child(String.valueOf(i++)).setValue(userUid);
//        }

        Auth.instance.user.events.add(eventBeingCreating);
    }

    public static PlannedEvent getEvent(final String eventUidFromUser) {
        final PlannedEvent[] event = new PlannedEvent[1];
        events.child(eventUidFromUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                String location = (String) dataSnapshot.child("location").getValue();
                String date = (String) dataSnapshot.child("date").getValue();
                long time = (long) dataSnapshot.child("time").getValue();
                String description = (String) dataSnapshot.child("description").getValue();
                String ownerUid = (String) dataSnapshot.child("ownerUid").getValue();
                // String eventUid = eventUidFromUser;
                event[0] = new PlannedEvent(name, location, date, time, description, ownerUid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return event[0];
    }

    /**
     *
     * @param user
     */
    public static void addNewUserToDatabase(final User user) {
        users.child(user.getUid()).setValue(EncodeString(user.email));
        users.child(user.getUid()).child("userName").setValue(user.username);
        users.child(user.getUid()).child("firstName").setValue(user.firstName);
        users.child(user.getUid()).child("lastName").setValue(user.lastName);
        users.child(user.getUid()).child("bioInfo").setValue(user.bioInfo);

        // Set default privacy setting
        users.child(user.getUid()).child("privacy").setValue(false);

        // set notifications, store as key=index,value=notification message
        users.child(user.getUid()).child("notifications").setValue("notificationsList");
        for (int i = 0; i < user.notifications.size(); i++) {
            // users.child(user.getUid()).child("notifications").child(""+i).setValue(user.notifications.get(i));
            Message userMessage = user.notifications.get(i);

            // Get message object
            users.child(user.getUid()).child("notifications").child("AccountUpdate"+i).child("subject").setValue(userMessage.getSubject());
            users.child(user.getUid()).child("notifications").child("AccountUpdate"+i).child("message").setValue(userMessage.getMessage());
            users.child(user.getUid()).child("notifications").child("AccountUpdate"+i).child("dateSent").setValue(userMessage.getDateSent());
            users.child(user.getUid()).child("notifications").child("AccountUpdate"+i).child("timeSent").setValue(userMessage.getDateTimeSent());

        }

        // Create a child node under the user for teams
        users.child(user.getUid()).child("teams").setValue("teams-list");
        for (Object teamName : user.teams) {
            users.child(user.getUid()).child("teams").child((String) teamName).setValue("member");
        }

        // All users are set as public by default
        users.child(user.getUid()).child("privacy").setValue(false);

        // add username with value uid to userUidMap
        addToUserUidMap(user);

        // addValueEventListener for errors
        users.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("users update", "New user; "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("users update error", "New user error; "+databaseError.toException().getLocalizedMessage());
            }
        });
    }
    
    /**
     *
     * @param uid
     * @param rememberLogin
     */
    public static void setRememberMeForUserLogin(final String uid, final boolean rememberLogin) {
        // incomplete
        users.child(uid).child("rememberMe").setValue(rememberLogin);
    }

    /**
     *
     * @param uid
     * @return
     */
    public static boolean getRememberMeForUserLogin(final String uid) {
        final boolean[] rememberLogin = new boolean[]{false};

        // incomplete
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~"+uid+" ");
        users.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("rememberMe")) {
                    rememberLogin[0] = (boolean) dataSnapshot.child("rememberMe").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("no user?", databaseError.getDetails());
            }
        });

        return rememberLogin[0];
    }

    
    /**
     * Use to get current user's information from database
     * @param firebaseUser
     * @return
     */
    public static User getUserObject(final FirebaseUser firebaseUser) {
        final User user = new User(firebaseUser);
        final boolean[] notSetEmail = new boolean[1];
        final List<String> eventUidStrings = new ArrayList<>();
        //final DatabaseReference users = firebaseDatabase.child("users");

        // get info from the user's node in users
        users.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("user node", String.valueOf(dataSnapshot));
                if (firebaseUser.isEmailVerified()) {
                    // update email node if does not exist
                    if (!dataSnapshot.hasChild("email")) {
                        users.child(firebaseUser.getUid()).child("email").setValue(EncodeString(firebaseUser.getEmail()));
                    }

                    // Get team list
                    List<String> savedTeams = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.child("teams").getChildren()) {
                        Log.d("Database", "Got Team: " + snapshot.getKey());
                        savedTeams.add(snapshot.getKey());
                    }

                    user.teams = savedTeams;

                    user.firstName = (String) dataSnapshot.child("firstName").getValue();
                    user.lastName = (String) dataSnapshot.child("lastName").getValue();
                    user.username = (String) dataSnapshot.child("userName").getValue();
                    // user.isPrivate = (boolean) dataSnapshot.child("privacy").getValue();
                    user.notifications = new ArrayList<>();
                    //if (dataSnapshot.hasChild("events")) {
                    for (DataSnapshot d : dataSnapshot.child("events").getChildren()) {
                        //PlannedEvent eventToAdd = getEvent(d.getValue().toString());
                        eventUidStrings.add(d.getKey());

                        System.out.println(d.getKey()+"USER EVENT ");
                        //user.events.add(getEvent(d.getValue().toString().trim()));
                    }

                    user.userEventUids = eventUidStrings;

                    for(String s : eventUidStrings) {
                        System.out.println("~~~~~~~~~~~~~~~~~~~~" + s);
                        //user.events.add(getEvent(s));
                    }

                    // Get all events
                    events.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (String eventUid : eventUidStrings) {
                                DataSnapshot snapshot = dataSnapshot.child(eventUid);
                                String name = null;

                                // Instantiate new planned event object
                                PlannedEvent currentPlannedEvent = new PlannedEvent();

                                if (snapshot.hasChild("name")) {
                                    name = snapshot.child("name").getValue(String.class);
                                    currentPlannedEvent.setName(name);
                                }
                                Log.d("Database", "Event name to store: " + name);

                                String location = null;
                                if (snapshot.hasChild("location")) {
                                    location = snapshot.child("location").getValue(String.class);
                                    currentPlannedEvent.setLocation(location);
                                }
                                Log.d("Database", "Event location to store: " + location);

                                String date = null;
                                if (snapshot.hasChild("date")) {
                                    date = snapshot.child("date").getValue(String.class);
                                    currentPlannedEvent.setStringDate(date);
                                }
                                Log.d("Database", "Event date to store: " + date);

                                long time = 0;
                                if (snapshot.hasChild("time")) {
                                    time = snapshot.child("time").getValue(long.class);
                                    currentPlannedEvent.setLongTime(time);
                                }
                                Log.d("Database", "Event time to store: " + time);

                                String description = null;
                                if (snapshot.hasChild("description")) {
                                    description = snapshot.child("description").getValue(String.class);
                                    currentPlannedEvent.setDescription(description);
                                }
                                Log.d("Database", "Event description to store: " + description);

                                // Create new event (checked the most important thing)
                                if (currentPlannedEvent.getDate() != null)
                                    user.events.add(currentPlannedEvent);
                                // ... Nothing else is needed for calendar
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                        events.child(s).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String name = (String) dataSnapshot.child("name").getValue();
//                                String location = (String) dataSnapshot.child("location").getValue();
//                                String date = (String) dataSnapshot.child("date").getValue();
//                                long time = dataSnapshot.child("time").getValue(long.class);
//                                String description = (String) dataSnapshot.child("description").getValue();
//                                String ownerUid = (String) dataSnapshot.child("ownerUid").getValue();
//                                String eventUid = s;
//                                eventUidFromUser[0] = new PlannedEvent(name, location, date, time, description, ownerUid, eventUid);
//                                user.events.add(eventUidFromUser[0]);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                            }
//                        });
//                    }

                    //}

                    // TODO: Test with new account
                    // getUserNotifications(firebaseUser.getUid(), user);

                    // add username with value uid to userUidMap
                    addToUserUidMap(user);
                } else {
                    firebaseUser.sendEmailVerification();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("user node error", databaseError.toException().getLocalizedMessage());
            }
        });

        //System.out.println("_____________________________"+user.events.size());
        return user;
    }

    public static void getUserEvents() {
    }


//    /**
//     * Pass user (it can be empty) object to method to store all messages
//     * Must provide userUid in order to use method
//     * - Verify that uid is valid before attempting to call method
//     * @param userUid String
//     * @param temp User
//     */
//    public static void getUserNotifications(String userUid, final User temp) {
//        users.child(userUid).child("notifications").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChildren()) {
//                    Iterable<DataSnapshot> notificationsList = dataSnapshot.child("notifications").getChildren();
//                    for (DataSnapshot data : notificationsList) {
//                        // Recreate message objects
//                        int index = Integer.parseInt(data.getKey());
//                        String subject = data.child("subject").getValue(String.class);
//                        String message = data.child("message").getValue(String.class);
//                        String dateSent = data.child("dateSent").getValue(String.class);
//                        long timeSent = data.child("timeSent").getValue(long.class);
//
//                        Message newMessage = new Message(subject, message, dateSent, timeSent);
//                        temp.addNotification(newMessage);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


    /**
     * Add New Team in database teams node
     * Update 04/06/2020 - Added additional parameters for team
     * icon added to add image to list view (i.e. design purpose)
     * createDate added for  timeline (we have a start date)
     * @param team Team
     */
    public static void addNewTeam(final Team team) {
        teams.child(team.getTeamName()).setValue(team.getSport());
        teams.child(team.getTeamName()).child("owner").setValue(team.getTeamOwner());
        teams.child(team.getTeamName()).child("owner").child("UID").setValue(team.getOwnerUid());
        teams.child(team.getTeamName()).child("owner").child("description").setValue(team.getTeamDescription());
        teams.child(team.getTeamName()).child("owner").child("sport").setValue(team.getSport());
        teams.child(team.getTeamName()).child("owner").child("icon").setValue(team.getImageIcon());
        teams.child(team.getTeamName()).child("owner").child("createDate").setValue(team.getCreateDate());

        teams.child(team.getTeamName()).child("members").setValue("membersList");
        for (String member : team.members) {
            teams.child(team.getTeamName()).child("members").child(member).setValue(team.memberRoleMap.get(member));
            teams.child(team.getTeamName()).child("members").child(member).child("UID").setValue(team.memberUidMap.get(member));
        }

        // on create no subscription requests should be present,
        // so just make inbox node with children sub-requests, and event-requests
        teams.child(team.getTeamName()).child("inbox").setValue("teamInbox");
        teams.child(team.getTeamName()).child("inbox").child("sub-requests").setValue("subscriptionRequestList");
        teams.child(team.getTeamName()).child("inbox").child("event-requests").setValue("eventRequestList");

        // addValueEventListener for errors
        teams.child(team.getTeamName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("teams update", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("teams update error", databaseError.toException().getLocalizedMessage());
            }
        });
    }

    public static void addNewMemberToExistingTeam(final String teamName, final String newPlayerUsername, final String newPlayerRole) {
        // Make sure that team exists in database
        teams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(teamName)) {
                    // Check to see if current user is authorized to make changes to the team
                    String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
                    boolean firstCondition = dataSnapshot.child(teamName).child("ownerUid").getValue(String.class).trim().equals(currentUserUid.trim());
                    boolean secondCondition = false;
                    if ( dataSnapshot.child(teamName).child("members").hasChild(currentUserUid))
                        secondCondition = dataSnapshot.child(teamName).child("members").child(currentUserUid).getValue(String.class).trim().toLowerCase().contains("coach");
                    if (firstCondition || secondCondition) {
                        // Get player's Uid
                        userUidMap.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(newPlayerUsername)) {
                                    String playerUid = dataSnapshot.child(newPlayerUsername).getValue(String.class);

                                    if (playerUid != null) {
                                        // Add member
                                        teams.child(teamName).child("members").child(playerUid).setValue(newPlayerRole);

                                        // Send a message to the new team member
                                        // Date and time is set internally
                                        String subject = "Congratulations!";
                                        String message = "You were recently added to a team called " + teamName;
                                        String toUser = playerUid;

                                        // Record notification to database
                                        Message teamUpdate = new Message(subject, message, toUser);
                                        DatabaseReference node = users.child(teamUpdate.getToUserUid()).child("notifications").push();
                                        String newKey = node.getKey();
                                        addNotificationToPlayer(teamName, teamUpdate, newKey);
                                    }
                                }
                                else {
                                    Log.d("Database", "Unable to store uid in empty user object");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Database Error", databaseError.getDetails());
                            }
                        });
                    }
                    else {
                        // Send message to team owner
                        String ownerUid = dataSnapshot.child(teamName).child("ownerUid").getValue(String.class);
                        String subject = "Join team request";
                        String message = newPlayerUsername + " requested to join " + teamName + " as a " + newPlayerRole;
                        message = message + "\nAdd " + newPlayerUsername + " to team or ignore notification";

                        Message teamUpdate = new Message(subject, message, ownerUid);
                        DatabaseReference node = users.child(ownerUid).child("notifications").push();
                        String newKey = node.getKey();
                        addNotificationToPlayer(teamName, teamUpdate, newKey);
                        Log.d("Add new player", "Notification sent");
                    }
                }
                // Else: Do nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database Error", databaseError.getDetails());
            }
        });
    }

    public static void notifyPlayersOfNewEvent(final String teamName, final PlannedEvent teamEvent) {
        // Make sure that team exists in database
        teams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(teamName)) {
                    // Loop through members of the team
                    for (DataSnapshot snapshot : dataSnapshot.child(teamName).child("members").getChildren()) {
                        String playerUid = snapshot.getKey();
                        if (playerUid != null) {
                            // Create message
                            String subject = "New Event for " + teamName;
                            String message = "New event posted for " + teamEvent.getStringDate() + "\n" + teamEvent.getDescription();
                            String toUser = playerUid;

                            // Record notifications to database <- players are notified one at a time
                            Message teamUpdate = new Message(subject, message, toUser);
                            DatabaseReference node = users.child(teamUpdate.getToUserUid()).child("notifications").push();
                            String newKey = node.getKey();
                            addNotificationToPlayer(teamName, teamUpdate, newKey);
                        }
                    }
                }
                // Else: Do nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database Error", databaseError.getDetails());
            }
        });
    }


    /**
     * New method to use
     * Add New Team in database teams node
     * Update 04/06/2020 - Added additional parameters for team
     * icon added to add image to list view (i.e. design purpose)
     * createDate added for  timeline (we have a start date)
     * @param team Team
     */
    public static void addNewTeam2(final Team team) {
        teams.child(team.getTeamName()).child("name").setValue(team.getTeamName());
        teams.child(team.getTeamName()).child("sport").setValue(team.getSport());
        teams.child(team.getTeamName()).child("date").setValue(team.getCreateDate());
        teams.child(team.getTeamName()).child("owner").setValue(team.getTeamOwner());
        teams.child(team.getTeamName()).child("ownerUid").setValue(team.getOwnerUid());
        teams.child(team.getTeamName()).child("teamDescription").setValue(team.getTeamDescription());
        teams.child(team.getTeamName()).child("imageIcon").setValue(team.getImageIcon());

        // Add team to user list
        users.child(team.getOwnerUid()).child("teams").child(team.getTeamName()).setValue(team.getSport());

        if (team.getMembers().size() == 0) {
            Log.d("Database", "No players on " + team.getTeamName());
        }
        for (String member : team.getMembers()) {
            teams.child(team.getTeamName()).child("members").child(member).setValue(team.memberRoleMap.get(member));
        }

        // on create no subscription requests should be present,
        // so just make inbox node with children sub-requests, and event-requests
        //teams.child(team.getTeamName()).child("inbox").setValue("teamInbox");
        //teams.child(team.getTeamName()).child("inbox").child("sub-requests").setValue("subscriptionRequestList");
        //teams.child(team.getTeamName()).child("inbox").child("event-requests").setValue("eventRequestList");

        // addValueEventListener for errors
        teams.child(team.getTeamName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("teams update", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("teams update error", databaseError.toException().getLocalizedMessage());
            }
        });

        // Handle errors
        users.child(team.getOwnerUid()).child("teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("user update", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("user could not update", databaseError.toString());
            }
        });
    }

    /**
     * Add User to User UID Map -- userUidMap on database
     * @param user
     */
    public static void addToUserUidMap(final User user) {
        userUidMap.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user.username)) {
                    userUidMap.child(user.username).setValue(user.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("userUidMap update error", databaseError.toException().getLocalizedMessage());
            }
        });
    }

    /**
     * Set User Profile Icon -- sets child node "profileIcon" to the index of selected avatar.
     * @param user User
     */
    public static void setUserProfileIcon(User user) {
        users.child(user.getUid()).child("profileIcon").setValue(user.getProfileIcon());
        users.child(user.getUid()).child("profileIcon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("user updated", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("user update error", databaseError.getDetails());
            }
        });
    }


    /**
     * Add Notification to User -- adds to user's notifications on the database
     * - may need some adjustments unless notifications are also sent/made one by one...
     * Use this method for app notifications and use addNotificationToPlayer for team updates
     * @param user User
     * @param notification Message
     */
    public static void addNotificationToUser(User user, Message notification) {
        users.child(user.getUid()).child("notifications").child(""+(user.notifications.size()-1)).child("subject").setValue(notification.getSubject());
        users.child(user.getUid()).child("notifications").child(""+(user.notifications.size()-1)).child("message").setValue(notification.getMessage());
        users.child(user.getUid()).child("notifications").child(""+(user.notifications.size()-1)).child("dateSent").setValue(notification.getDateSent());
        users.child(user.getUid()).child("notifications").child(""+(user.notifications.size()-1)).child("timeSent").setValue(notification.getDateTimeSent());

        users.child(user.getUid()).child("notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("notifications update", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("notifications upd error", databaseError.toException().getLocalizedMessage());
            }
        });
    }

    /**
     * Sends notifications to players of a team
     * playerUid is obtained from Message object
     * @param teamName String
     * @param notification Message
     */
    public static void addNotificationToPlayer(final String teamName, final Message notification, final String newKey) {
        // Consider using .setValueAsync(notification) to set message object to database in entirety <- Attempted but did not work;
        users.child(notification.getToUserUid()).child("notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // TODO: Send email to target user (refer to https://stackoverflow.com/questions/2197741/how-to-send-emails-from-my-android-application)
                // Send email when this method is called
                Log.d("notifications update", dataSnapshot.toString());

                // Store counter in database
//                int messageCount = 1;
//                if (dataSnapshot.hasChild("numUpdates")) {
//                    // Get current count
//                    try {
//                        messageCount = dataSnapshot.child("numUpdates").getValue(int.class);
//                        Log.d("Database messCount", "This is the current value for numUpdates: " + messageCount);
//                        messageCount = messageCount + 1; // Increment to store new message
//                    } catch (NullPointerException e) {
//                        Log.d("Database error", "Unable to read messageCount node");
//                    }
//                }
//                else {
//                    // Assume this is the first notification sent from team to player and create a counter (sets numUpdates at 1)
//                    // Save to database
//                    users.child(notification.getToUserUid()).child("notifications").child("numUpdates").setValue(messageCount);
//                }

                // Write new message to database
                Log.d("Database new node", "Created new notification node: " + newKey);
                users.child(notification.getToUserUid()).child("notifications").child(newKey).child("subject").setValue(notification.getSubject());
                users.child(notification.getToUserUid()).child("notifications").child(newKey).child("message").setValue(notification.getMessage());
                users.child(notification.getToUserUid()).child("notifications").child(newKey).child("dateSent").setValue(notification.getDateSent());
                users.child(notification.getToUserUid()).child("notifications").child(newKey).child("timeSent").setValue(notification.getDateTimeSent());

                // Store incremented numUpdates to database
                // users.child(notification.getToUserUid()).child("notifications").child("numUpdates").setValue(messageCount);
                // Log.d("Database increment", "Incremented numUpdates in database");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("notifications upd error", databaseError.toException().getLocalizedMessage());
            }
        });
    }

    /**
     * Update: I feel a lot better not being able to access the database directly
     * @return DatabaseReference
     */
    public static DatabaseReference getFirebaseDatabase() {
        return firebaseDatabase;
    }

    /**
     * Attempting to avoid - Firebase Database paths must not contain '.', '#', '$', '[', or ']' Error
     * Caused by storing email address with periods
     * @param string
     * @return
     */
    public static String EncodeString(String string) {
        //if (string == null) { return ""; }
        return string == null ? "" :
                string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        //if (string == null) { return ""; }
        return string == null ? "" :
                string.replace(",", ".");
    }
}
