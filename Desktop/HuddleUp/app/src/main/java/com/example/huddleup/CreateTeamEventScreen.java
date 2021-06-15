package com.example.huddleup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import OtherClasses.VisualUtilities;

public class CreateTeamEventScreen extends AppCompatActivity {

    EditText teamNameEditText,
            eventNameEditText,
            eventDescriptionEditText,
            locationDescriptionEditText;

    List<String> storedTeams;
    String nameOfEvent, dateOfEvent, locationOfEvent, eventDescription;
    long timeOfEvent;

    Button saveEventButton, setDateButton, setTimeButton;
    ImageButton addTeamButton;
    final Calendar dateCalendar = Calendar.getInstance();
    final Calendar timeCalendar = Calendar.getInstance();
    boolean accept, isTimePicked, isDatePicked;

    PlannedEvent newEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team_event_screen);

        // Get UI elements
        saveEventButton = findViewById(R.id.ButtonSaveNewTeamEvent);
        addTeamButton = findViewById(R.id.ButtonCreateTeamEventAddTeam);
        setDateButton = findViewById(R.id.ButtonCreateTeamEventSelectDate);
        setTimeButton = findViewById(R.id.ButtonCreateTeamEventSelectTime);
        teamNameEditText = findViewById(R.id.EditTextCreateTeamEventTeamName);
        eventNameEditText = findViewById(R.id.EditTextCreateTeamEventEventTitle);
        eventDescriptionEditText = findViewById(R.id.EditTextCreateTeamEventDescription);
        locationDescriptionEditText = findViewById(R.id.EditTextCreateTeamEventLocation);

        // Save values
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storedTeams.size() != 0) {
                    createEvent();

                    // Navigate to home-screen
                    Intent intent = new Intent(CreateTeamEventScreen.this, HomeScreen.class);
                    CreateTeamEventScreen.this.startActivity(intent);
                }
                else {
                    VisualUtilities vu = new VisualUtilities();
                    vu.popToast("Unable to save", CreateTeamEventScreen.this);
                    vu.makeMeShake(teamNameEditText);
                    vu.popToast("At least one team or user must be provided", CreateTeamEventScreen.this);
                }
            }
        });

        // Sets up the listener for the add team button
        storedTeams = new ArrayList<>();
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToTeamList();
            }
        });

        // Sets up the listener for launching the set date widget
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, monthOfYear);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }
        };

        // Launch date picker widget when button is selected
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(CreateTeamEventScreen.this, date, dateCalendar
                        .get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DAY_OF_MONTH));
                // Disable all dates in the past
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

        // Sets up the listener for launching set time widget
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                timeCalendar.set(Calendar.HOUR_OF_DAY, hour);
                timeCalendar.set(Calendar.MINUTE, minute);
                updateTimeLabel();
            }
        };

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = timeCalendar.get(Calendar.MINUTE);
                TimePickerDialog timePicker = new TimePickerDialog(CreateTeamEventScreen.this, time,
                        hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });
    }

    private void createEvent() {
        nameOfEvent = eventNameEditText.getText().toString();
        locationOfEvent = locationDescriptionEditText.getText().toString();
        eventDescription = eventDescriptionEditText.getText().toString();
        if (!isDatePicked) {
            Log.d("CreateTeamScreen", ">>Final date picked<<: " + dateOfEvent);
            updateDateLabel(); // Uses default date (01/01/1970)
        }
        if (!isTimePicked) {
            updateTimeLabel(); // Uses current time
        }

        // Generate a reference to a new location and add some data using push()
        DatabaseReference pushedEventRef = Database.getFirebaseDatabase().child("events").push();

        // Get the unique ID generated by a push()
        String postId = pushedEventRef.getKey();

        Log.d("CreateTeamScreen", ">>Final date picked<<: " + dateOfEvent);
        Log.d("CreateTeamScreen", ">>Final date picked<<: " + dateOfEvent);
        newEvent = new PlannedEvent(nameOfEvent, locationOfEvent, dateOfEvent, timeOfEvent,
                eventDescription, Auth.instance.firebaseAuth.getCurrentUser().getUid(),
                postId);

        // Clear text edits
        eventNameEditText.setText("");
        locationDescriptionEditText.setText("");
        eventDescriptionEditText.setText("");


        // Add associated teams
        for (String team : storedTeams) {
            if (team != null)
                Log.d("CreateTeamEventScreen", "This is the current team that is being added: " + team);
                newEvent.addTeam(team);
        }

        // Add event to database
        Database.createNewTeamEvent(newEvent);
    }

    public void addToTeamList() {
        // At least one team has to exist on huddleup
        Database.getTeams().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = teamNameEditText.getText().toString();
                if (!dataSnapshot.hasChild(name.trim())) {
                    new VisualUtilities().popToast("The team name you provided could not be found", CreateTeamEventScreen.this);
                    launchWarningDialog();
                    if (accept) {
                        storedTeams.add(name);
                        new VisualUtilities().popToast("Team added", CreateTeamEventScreen.this);
                    }
                    else {
                        new VisualUtilities().popToast("Please enter a valid team name", CreateTeamEventScreen.this);
                    }
                }
                else {
                    teamNameEditText.setText(" ");
                    storedTeams.add(name);
                    new VisualUtilities().popToast("Team added", CreateTeamEventScreen.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing
            }
        });


    }

    private void launchWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("An unknown team was entered")
                .setMessage("Selecting 'Yes' will write the unknown team to your event")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        accept = true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                }).show();
    }

    private void updateDateLabel() {
        final String DATE_FORMAT = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US); // Initialize SimpleDateFormat

        // Set date
        dateOfEvent = sdf.format(dateCalendar.getTime());

        // Prevent dateOfEvent from changing when myCalendar is re-instantiated for time
        isDatePicked = true;
        Log.d("CreateTeamEventScreen", "Picked day: " + dateOfEvent );
        new VisualUtilities().popToast("Date saved", CreateTeamEventScreen.this);
    }

    private void updateTimeLabel() {
        final String TIME_FORMAT = "h:mm a";
         SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.US);

         // Convert from string to long
        String stringTime = sdf.format(timeCalendar.getTime());
        try {
            Date d = sdf.parse(stringTime);
            timeOfEvent = d.getTime();
            isTimePicked = true;
            new VisualUtilities().popToast("Time saved", CreateTeamEventScreen.this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("CreateTeamEventScreen", "Picked time: " + timeOfEvent);
    }

//    public void createEventFromUI() {
//
//        // if it doesn't work, it might be these lines below
//        nameOfEvent = eventNameEditText.getText().toString();
//        time = .getText().toString();
//        location = locationDescription.getText().toString();
//        description = eventDescription.getText().toString();
//
//        // System.out.println("TeamName: " + name + "\nEventName: " + nameOfEvent + "\nDate: " + time + "\nLocation: " + location + "\nDescription: " + description); // for testing
//
//        PlannedEvent event = new PlannedEvent(nameOfEvent, time, location, description);
//        for (int i = 0; i < storedTeams.size(); i++)
//        {
//            event.addTeam(storedTeams.get(i));
//        }
//        storedTeams.clear(); // clears the teams so a new event can be created with new teams
//        // add each member from the team to the event as well
//    }

}
