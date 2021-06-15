package com.example.huddleup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class TeamEventScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_event_screen);

        // Adds support for the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
