package com.example.huddleup.auth.and.database;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
Team on Database:
-parent node: team name, value sport
-child: owner, value owner username + child UID, value ownerUid
-child: members, value membersList + children members, with value role and child UID
-....
*/
public class Team implements Serializable {
    String name, sport, owner, ownerUid, createDate, teamDescription;
    Integer imageIcon;
    List<String> roles;
    final String[] DEFAULT_ROLES = new String[]{"Head Coach", "Coach", "Team Member"};
    ArrayList<String> members;
    Map<String, String> memberUidMap;
    Map<String, String> memberRoleMap;
    Map<String, String> subRequestMap;
    List<String> subRequestUsernames;
    // ArrayList<String> messages;


    /**
     * Non-parameterized constructor -- required in order to read team object from database
     */
    public Team() {}

    /**
     * Parameterized constructor
     * @param name String
     * @param sport String
     * @param owner String (expects username)
     * @param ownerUid String
     * @param date String <-- will be useful when creating timeline
     * @param teamDesc String <- Short description of the team
     * @param icon Integer <- reference id to associated sport icon
     *
     */
    public Team(final String name, final String sport, final String owner, final String ownerUid, final String date,
                final String teamDesc, final Integer icon) {
        this.name = name;
        this.sport = sport;

        // owner is the owner username
        this.owner = owner;
        this.ownerUid = ownerUid;

        // Additions
        this.createDate = date;
        this.teamDescription = teamDesc;
        this.imageIcon = icon;

        memberUidMap = new HashMap<>();
        memberUidMap.put(this.owner, this.ownerUid);

        // Define roles
        roles = new ArrayList<>();
        roles.addAll(Arrays.asList(DEFAULT_ROLES));

        // Set owner role to "Head Coach" - can be changed later
        // TODO: Storing UID just in case user changes username
        memberRoleMap = new HashMap<>();
        memberRoleMap.put(this.ownerUid, DEFAULT_ROLES[0]);

        // Add head coach as first member of the team
        members = new ArrayList<>();
        members.add(owner);

        // Create a new sub-request
        subRequestMap = new HashMap<>();
        subRequestUsernames = new ArrayList<>();

        // add to Database -- This should be external to allow time to set instance variables
        // Database.addNewTeam(this);
    }

    /**
     * Add a new team member to team
     * @param username String
     * @param uid String
     * @param role String
     */
    public void addTeamMember(String username, String uid, String role) {
        // add a new member to the team with specified role.
        // TODO: Update <- store uid
        members.add(uid);
        memberRoleMap.put(uid, role);
        memberUidMap.put(username, uid);

    }

    /**
     * ADDITIONAL WAY TO ADD A TEAM MEMBER - UID can be looked up later
     * Add a new team member to team
     * @param uid String
     * @param role String
     */
    public void addTeamMember(String uid, String role) {
        // add a new member to the team with specified role.
        members.add(uid);
        memberRoleMap.put(uid, role);
    }

    /**
     * Add all players in map
     * @param playerAndRole Map
     */
    public void addTeamMembers(Map<String, String> playerAndRole) {
        memberRoleMap.putAll(playerAndRole);
        Set<String> keys = memberRoleMap.keySet();
        members.addAll(keys);
    }

    /**
     * Change the role of a member on a team
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void changeMemberRole(String user, String newRole) {
        memberRoleMap.replace(user, newRole);
    }

    /**====================================================
     * Created getters and setters for all important values
     *====================================================*/

    // public ArrayList<String> getMessages() { return messages; }

    // public void setAllMessages(ArrayList<String> newMessages) { this.messages = newMessages; }

    // public void addNewMessage(String newMessage) { this.messages.add(newMessage); }

    public String getTeamDescription() {
        return teamDescription;
    }

    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }

    public Integer getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(Integer imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getTeamName() {
        return name;
    }

    public void setTeamName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTeamOwner() {
        return owner;
    }

    public void setTeamOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public Map<String, String> getMemberUidMap() {
        return memberUidMap;
    }

    public void setMemberUidMap(Map<String, String> memberUidMap) {
        this.memberUidMap = memberUidMap;
    }

    public Map<String, String> getMemberRoleMap() {
        return memberRoleMap;
    }

    public void setMemberRoleMap(Map<String, String> memberRoleMap) {
        this.memberRoleMap = memberRoleMap;
    }

    public Map<String, String> getSubRequestMap() {
        return subRequestMap;
    }

    public void setSubRequestMap(Map<String, String> subRequestMap) {
        this.subRequestMap = subRequestMap;
    }

    public List<String> getSubRequestUsernames() {
        return subRequestUsernames;
    }

    public void setSubRequestUsernames(List<String> subRequestUsernames) {
        this.subRequestUsernames = subRequestUsernames;
    }
}