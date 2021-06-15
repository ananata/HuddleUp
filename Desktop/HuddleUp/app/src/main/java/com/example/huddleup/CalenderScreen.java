package com.example.huddleup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class CalenderScreen extends AppCompatActivity {
    private static Dialog popup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_screen);

        // Adds support for the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        popup = new Dialog(this);
    }

    public void showPopup(Date d)
    {
        TextView dateListed;
        TextView txtClose;
        TextView eventsListed;
        popup.setContentView(R.layout.calendarpopup);
        dateListed = popup.findViewById(R.id.dateListed);
        txtClose = (TextView) popup.findViewById(R.id.txtClose);
        eventsListed = popup.findViewById(R.id.eventsListed);

        dateListed.setText(MyCalendar.convertDate(d.getTime()));

        // add text for each event listed in the eventsListed
        txtClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popup.dismiss();
            }
        });
        popup.show();
    }

    public void showPopup(Date d, ArrayList<PlannedEvent> e)
    {
        TextView dateListed;
        TextView txtClose;
        TextView eventsListed;
        popup.setContentView(R.layout.calendarpopup);
        dateListed = popup.findViewById(R.id.dateListed);
        txtClose = (TextView) popup.findViewById(R.id.txtClose);
        eventsListed = popup.findViewById(R.id.eventsListed);

        dateListed.setText(MyCalendar.convertDate(d.getTime())); // lists the date at the top of the popup

        // add text for each event listed in the eventsListed
        String listedEvents = "";
        for (int i = 0; i < e.size(); i++)
        {
            if (e.get(i).getStringDate().contentEquals(MyCalendar.convertDate(d.getTime()))) // if the same day
            {
                Log.d("CalendarScreen", "Location retrieved " + e.get(i).getLocation());
                Log.d("CalendarScreen", "Description retrieved " + e.get(i).getDescription());
                listedEvents += "" + e.get(i).getName() + "\n" + e.get(i).getLocation() + "\n" + e.get(i).getDescription() + "\n\n";
            }
        }
        eventsListed.setText(listedEvents);

        txtClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popup.dismiss();
            }
        });
        popup.show();
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
                // Starting another activity example
                startActivity(new Intent(CalenderScreen.this, SettingsActivity.class));
                return true;
            case R.id.navCalendar:
                Toast.makeText(this, "Calendar selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navUser:
                Toast.makeText(this, "User selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(CalenderScreen.this, MyProfileScreen.class));
                return true;
//            case R.id.navMyProfile:
//                Toast.makeText(this, "My Profile selected", Toast.LENGTH_SHORT);
//                return true;
            case R.id.navProfileIcon:
                Toast.makeText(this, "Profile icon selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(CalenderScreen.this, ProfileIconSelectScreen.class));
                return true;
            case R.id.navTeam:
                Toast.makeText(this, "Team selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(CalenderScreen.this, TeamSelectScreen.class));
                return true;
//            case R.id.navTeamSelect:
//                Toast.makeText(this, "Select Team selected", Toast.LENGTH_SHORT);
//                return true;
            case R.id.navTeamCreate:
                Toast.makeText(this, "Create Team selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(CalenderScreen.this, TeamCreateScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
