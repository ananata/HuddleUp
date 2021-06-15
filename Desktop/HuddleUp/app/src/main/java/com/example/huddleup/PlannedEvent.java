package com.example.huddleup;

import android.util.Log;

import com.example.huddleup.auth.and.database.Database;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlannedEvent implements Comparable<PlannedEvent>, Serializable
{
   public String ownerUid;
   private String name; // Stores the name of the event
   private String stringDate; // Stores event date
   private long longDate;
   private String stringTime;
   private long longTime; // Store the time of the event in milliseconds since January 1, 1970
   private String location; // The name of the location the event takes place at
   private String description; // A description of what the event is
   private ArrayList<String> teams = new ArrayList<>(); // Stores the teams who use this event
   private ArrayList<String> users; // Stores the users who use this event
   private String eventUid;

   // this is the default constructor
   public PlannedEvent() {
   }

    // constructor with all required inputs
    public PlannedEvent(String eventName, long eventTime, String eventLocation, String eventDescription)
    {
        name = eventName;
        longTime = eventTime;
        location = eventLocation;
        description = eventDescription;
       teams = new ArrayList<String>();
       users = new ArrayList<String>();

       // Database.createEvent(this);
    }


   // constructor with all required inputs
   public PlannedEvent(String eventName, String eventTime, String eventLocation, String eventDescription)
   {
      name = eventName;
      longTime = convertDate(eventTime);
      location = eventDescription;
      description = eventDescription;
      teams = new ArrayList<String>();
      users = new ArrayList<String>();

      // Database.createEvent(this);
   }

   public PlannedEvent(String name, String location, String date, long time, String description, String ownerUid) {
      this.name = name;
      this.location = location;
      this.stringDate = date;
      this.longTime = time;
      this.description = description;
      this.ownerUid = ownerUid;
   }

   public String getOwnerUid() {
      return ownerUid;
   }

   public void setOwnerUid(String ownerUid) {
      this.ownerUid = ownerUid;
   }

   public String getStringDate() {
      return stringDate;
   }

   public void setStringDate(String stringDate) {
      this.stringDate = stringDate;
   }

   public long getLongDate() {
      return longDate;
   }

   public void setLongDate(long longDate) {
      this.longDate = longDate;
   }

   public String getStringTime() {
      return stringTime;
   }

   public void setStringTime(String stringTime) {
      this.stringTime = stringTime;
   }

   public long getLongTime() {
      return longTime;
   }

   public void setLongTime(long longTime) {
      this.longTime = longTime;
   }

   public String getEventUid() {
      return eventUid;
   }

   public void setEventUid(String eventUid) {
      this.eventUid = eventUid;
   }

   public PlannedEvent(String nameOfEvent, String locationOfEvent, String dateOfEvent, long timeOfEvent, String eventDescription, String ownerUid, String eventUid) {
      this.name = nameOfEvent;
      this.location = locationOfEvent;
      this.stringDate = dateOfEvent;
      this.longTime = timeOfEvent;
      this.description = eventDescription;
      this.ownerUid = ownerUid;
      this.eventUid = eventUid;
      // Get teams
      // Database.createEvent(this);
   }

   // getter methods
   public String getName()
   {
      return name;
   }

   public long getTime() { return longTime; }

   public String getDate() // returns the time in a month/date/year format
   {
     return convertDate(getTime());
   }

   public String getLocation()
   {
      return location;
   }

   public String getDescription()
   {
      return description;
   }

   public ArrayList<String> getTeams()
   {
      return teams;
   }

   public ArrayList<String> getUsers()
   {
      return users;
   }

   // setter methods
   public void setName(String n)
   {
      name = n;
   }

   public void setTime(long t)
   {
      longTime = t;
   }

   public void setLocation(String l)
   {
      location = l;
   }

   public void setDescription(String d)
   {
      description = d;
   }

   public void setTeams(ArrayList<String> ts) // this method is unlikely to be used, but is here just in case a duplication of data from events is wanted later
   {
      teams = ts;
   }

   public void setUsers(ArrayList<String> us) // this method is unlikely to be used, but is here just in case a duplication of data from events is wanted later
   {
      users = us;
   }

   // editor methods
   public void addTeam(String t)
   {
      if (t != null) {
         teams.add(t);
         Database.addTeamToEvent(this, t);
      }
      else {
         Log.d("PlannedEvent", "Null team passed to addTeam() method");
      }
   }

   public void setTeamNames(ArrayList<String> teamNames) {
      this.teams = teamNames;
   }

   public ArrayList<String> getTeamNames() {
      return teams;
   }

   public void addUser(String u)
   {
      users.add(u);
   }

   public void removeTeam(String t)
   {
      teams.remove(t);
   }

   public void removeUser(String u)
   {
      users.remove(u);
   }


   // utility or helper methods
   private String convertDate(long milli)
   {
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
      String dateString = formatter.format(new Date(milli));
      return dateString;
   }
   private long convertDate(String d)
   {
      Date day = new Date(d);
      return day.getTime();
   }

   public String toString()
   {
      String output = "";
      output += getName() + "\n\t" + getDate() + "\n\t" + getLocation() + "\n\t" + getDescription();
      return output;
   }

   @Override
   public int compareTo(PlannedEvent plannedEvent) {
      final String DATE_FORMAT = "MMMM dd, yyyy";
      Date eventOne = null, eventTwo = null;
      if (getStringDate() == null || plannedEvent.getStringDate() == null) {
         return 0;
      }

      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
      try {
         eventOne = sdf.parse(getStringDate());
      } catch (ParseException e) {
         e.printStackTrace();
      }
      try {
         eventTwo = sdf.parse(plannedEvent.getStringDate());
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return eventOne.compareTo(eventTwo);
   }
}
