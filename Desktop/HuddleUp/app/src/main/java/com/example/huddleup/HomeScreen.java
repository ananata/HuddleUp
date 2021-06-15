package com.example.huddleup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import OtherClasses.Event;
import OtherClasses.EventAdapter;
import OtherClasses.RecyclerItemClickListener;
import OtherClasses.SendMailTask;
import OtherClasses.VisualUtilities;

public class HomeScreen extends AppCompatActivity {

    VisualUtilities vu = new VisualUtilities();
    private RecyclerView eventsPlace;
    ImageView visitProfile, changeSettings, aboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Send email about login
        //String email = "twumasianknad@vcu.edu";
        //String subject = "New login to HuddleUp!";
        //String message = "You logged into your HuddleUp account on 4/24/2020. If this is an error please change password";
        //SendMailTask sm = new SendMailTask(this, email, subject, message);

        // Executing sendmail to send email
        //sm.execute();

        ///////////////////
        // weird work around for resetting SearchScreen.useOtherTeamsProfile
        SearchScreen.userOtherTeamsProfile = false;

        // Adds support for the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Mount Recycler View Widget to the Home screen
        eventsPlace = findViewById(R.id.RecyclerViewHomeScreen);

        // Add elements to "slide"
        List<Event> eventList = new ArrayList<>();
        eventList.add(
                new Event(
                        "My Teams",
                        "CONNECT",
                        R.mipmap.team
                )
        );

        eventList.add(
                new Event(
                        "My Messages",
                        "UPDATES",
                        R.mipmap.user_messages
                )
        );

        eventList.add(
                new Event(
                        "My Events",
                        "TIMELINE",
                        R.mipmap.events
                )
        );

        // Functionality is built into team screen
//        eventList.add(
//                new Event(
//                        "Join a Team",
//                        "EXPLORE",
//                        R.mipmap.join_team
//                )
//        );

        eventList.add(
                new Event(
                        "Create a Team",
                        "CREATE",
                        R.mipmap.create_team
                )
        );

        eventList.add(
                new Event(
                        "Plan an Event",
                        "PLANNING",
                        R.mipmap.create_event
                )
        );

        eventList.add(
                new Event(
                        "Search",
                        "DISCOVERY",
                        R.mipmap.search
                )
        );

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.HORIZONTAL, false);

        eventsPlace.setLayoutManager(linearLayoutManager);
        eventsPlace.setHasFixedSize(true);

        EventAdapter eventAdapter = new EventAdapter(this, eventList);
        eventsPlace.setAdapter(eventAdapter);

        // Add onClick listener to elements in RecycleView
        eventsPlace.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), eventsPlace, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // vu.popToast("The event at position " + position + " was pressed for a short time", (Activity)view.getContext());
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        // vu.popToast("The event at position " + position + " was pressed for a long time", (Activity)view.getContext());
                        try {
                            Intent intent = launchNextActivity(view, position);
                            //view.getContext().startActivity(intent);
                            startActivity(intent);
                        }
                        catch (NullPointerException e) {
                            // vu.popToast("Error: Unable to launch new intent", (Activity)view.getContext());
                            Log.d("Homescreen", "Unable to launch new intent");
                        }
                    }
                })
        );

        // Add onClick Listener to ImageView objects
        visitProfile = (ImageView) findViewById(R.id.ImageViewVisitMyProfile);
        visitProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                vu.popToast("Navigating to your profile", (Activity)view.getContext());
                // MyProfileScreen.isOtherUsersProfile = false;
                Context context = view.getContext();
                Intent intent = new Intent(context, MyProfileScreen.class);
                context.startActivity(intent);
            }
        });

        changeSettings = (ImageView) findViewById(R.id.ImageViewChangeAccountSettings);
        changeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vu.popToast("Navigating to settings page", (Activity)view.getContext());
                Context context = view.getContext();
                Intent intent = new Intent(context, SettingsActivity.class);
                context.startActivity(intent);
            }
        });

        // Snapping the scroll items into place
        final SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(eventsPlace);

        // Set a timer for default item
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1ms = 100ms
                RecyclerView.ViewHolder viewHolderDefault = eventsPlace.
                        findViewHolderForAdapterPosition(0);
                LinearLayout eventparentDefault = viewHolderDefault.itemView.
                        findViewById(R.id.LinearLayoutHomeScreenEventParent);
                eventparentDefault.animate().scaleY(1).scaleX(1).setDuration(350).
                        setInterpolator(new AccelerateInterpolator()).start();

                LinearLayout eventcategoryDefault = viewHolderDefault.itemView.
                        findViewById(R.id.eventbadge);
                eventcategoryDefault.animate().alpha(1).setDuration(300).start();

            }
        }, 100);

        // Add Scroll Animation
        eventsPlace.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    View view = snapHelper.findSnapView(linearLayoutManager);
                    int pos = linearLayoutManager.getPosition(view);

                    RecyclerView.ViewHolder viewHolder =
                            eventsPlace.findViewHolderForAdapterPosition(pos);

                    LinearLayout eventParent = viewHolder.itemView.findViewById(R.id.LinearLayoutHomeScreenEventParent);
                    eventParent.animate().scaleY(1).scaleX(1).setDuration(350).
                            setInterpolator(new AccelerateInterpolator()).start();

                    LinearLayout eventCategory = viewHolder.itemView.
                            findViewById(R.id.eventbadge);
                    eventCategory.animate().alpha(1).setDuration(300).start();

                }
                else {

                    View view = snapHelper.findSnapView(linearLayoutManager);
                    int pos = linearLayoutManager.getPosition(view);

                    RecyclerView.ViewHolder viewHolder =
                            eventsPlace.findViewHolderForAdapterPosition(pos);

                    LinearLayout eventParent = viewHolder.itemView.findViewById(R.id.LinearLayoutHomeScreenEventParent);
                    eventParent.animate().scaleY(0.5f).scaleX(0.5f).
                            setInterpolator(new AccelerateInterpolator()).setDuration(350).start();

                    LinearLayout eventCategory = viewHolder.itemView.
                            findViewById(R.id.eventbadge);
                    eventCategory.animate().alpha(0).setDuration(300).start();
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    /**
     * Intents need to be modified as more screens are implemented
     * Updates 03/30/2020 - The program can now run without crashing.
     * @param view View
     * @param position int
     */
    private Intent launchNextActivity(View view, int position) {
        Context context = view.getContext();
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(context, TeamSelectScreen.class);
                break;
            case 1:
                intent = new Intent(context, MessagesScreen.class);
                break;
            case 2:
                intent = new Intent(context, TimelineScreen.class);
                break;
//            case 3:
//                intent = new Intent(context, SearchScreen.class);
//                break;
            case 3:
                intent = new Intent(context, TeamCreateScreen.class);
                break;
            case 4:
                intent = new Intent(context, CreateTeamEventScreen.class);
                break;
            case 5:
                intent = new Intent(context, SearchScreen.class);
                break;
        }
        return intent;
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
                startActivity(new Intent(HomeScreen.this, CalenderScreen.class));
                return true;
            case R.id.navSearch:
                Toast.makeText(this, "Search selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(HomeScreen.this, SearchScreen.class));
                return true;
            case R.id.navUser:
                Toast.makeText(this, "User selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navMyProfile:
                Toast.makeText(this, "My Profile selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(HomeScreen.this, MyProfileScreen.class));
                return true;
            case R.id.navProfileIcon:
                Toast.makeText(this, "Profile Icon selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(HomeScreen.this, ProfileIconSelectScreen.class));
                return true;
            case R.id.navTeam:
                Toast.makeText(this, "Team selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.navTeamSelect:
                Toast.makeText(this, "Select Team selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(HomeScreen.this, TeamSelectScreen.class));
                return true;
            case R.id.navTeamCreate:
                Toast.makeText(this, "Create Team selected", Toast.LENGTH_SHORT);
                startActivity(new Intent(HomeScreen.this, TeamCreateScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}