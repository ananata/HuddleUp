package com.example.huddleup.auth.and.database;

import androidx.annotation.Keep;

import com.example.huddleup.PlannedEvent;
import com.example.huddleup.R;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Keep
public class User implements Serializable
{
    FirebaseUser firebaseUser;

    public boolean isEmailVerified;
    private String uid;
    public String email, username, password;
    public String fullName;
    public String lastName, firstName, bioInfo;
    public String role;
    public boolean isPrivate;

    private int profileIcon;

    public List<String> teams;
    public List<Team> teamList;
    List<Message> notifications;

    public List<String> userEventUids = new ArrayList<>();
    public ArrayList<PlannedEvent> events = new ArrayList<>();

    public User() {
    }

    public User(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
        email = firebaseUser.getEmail();
        isEmailVerified = firebaseUser.isEmailVerified();
        uid = firebaseUser.getUid();
        teams = new ArrayList<>();

        firstName = "N/a";
        lastName = "N/a";
        bioInfo = "N/a";

        // Add first notification to user account
        notifications = new ArrayList<>();
    }

    // using this constructor when visiting other user's  profiles from SearchScreen
    public User(String username, String firstName, String lastName, String bioInfo, List<String> teamNames) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bioInfo = bioInfo;
        this.teams = teamNames;
    }

    void createNewUserNotifications() {
        notifications = new ArrayList<>();
        // Nana <- Had to remove period also (sorry)
        // Create message object
        String subject = "Welcome to HuddleUp!";
        String message = "If you haven't already, please make sure to verify your email\n "
        + "We hope you find our app useful\nBest Wishes,\nHuddleUp Team";
        Integer icon = R.mipmap.white_round_huddleup_icon;

        Message greeting = new Message(subject, message, "HuddleUp!", icon);
        notifications.add(greeting);
    }

    // Does not write to database <- saves value locally
    public void setProfileIcon(int icon) {
        profileIcon = icon;
    }

    public void setUserProfileIconIndex(int index) {
        profileIcon = index;
        Database.setUserProfileIcon(this);
    }

    /**
     * Add to Notifications -- calls notifications.add and Database.addNotificationToUser
     * - some adjustments may be made in the future to make this more efficient..
     * @param notification Message
     */
    void addToNotifications(Message notification) {
        notifications.add(notification);
        Database.addNotificationToUser(this, notification);
    }

    public void addToNewTeam(String teamName) {
        teams.add(teamName);
    }

    /**
     * Add getters
     */
    public String getUsername() {
        return username;
    }
    public String getUid() { return uid; }
    public void setUid(String newUid) { this.uid = newUid; }
    public Integer getProfileIcon() { return profileIcon; }
    public String getRole() {return role;}
    public void setRole(String role) {this.role = role; }
    public String getFullName() {return this.fullName;}
    public void setFullName(String name) { this.fullName = name;}
    public void addNotification(Message message) { this.notifications.add(message);}
}