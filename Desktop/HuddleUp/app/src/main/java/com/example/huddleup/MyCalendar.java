package com.example.huddleup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.huddleup.auth.and.database.Auth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyCalendar extends LinearLayout {
    private ImageButton previous, next;
    private TextView month;
    private GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private List<Date> dates = new ArrayList<>();
    private ArrayList<PlannedEvent> events = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    private MyGridAdapter myGridAdapter;
    private CalenderScreen calenderScreen; // for the popup access
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar);
    }
    */

    public MyCalendar(Context context)
    {
        super(context);
    }

    public MyCalendar(final Context context, @Nullable AttributeSet attrs)
    {
        super(context);
        this.context = context;
        IntializeLayout();
        // set up the events before setting up the calendar
            // events set up
        // auth.instance.user.username to get the user's name
        events = (ArrayList<PlannedEvent>) Auth.instance.user.events; // grabs events associated with the current user
        if (events.size() == 0) {
            Log.d("MyCalendar", "No events could be loaded");
        }

        // creates events for testing by adding to the list of events the calendar uses
            //events.add(new PlannedEvent("TestEvent1", "04/18/2020", "Test Area", "For testing"));
            //events.add(new PlannedEvent("TestEvent2", "04/19/2020", "Test Area", "For testing"));
        setUpCalendar();
        calenderScreen = new CalenderScreen();

        previous.setOnClickListener(new OnClickListener()
        {
                @Override
                public void onClick(View v)
                {
                    calendar.add(Calendar.MONTH, -1);
                    setUpCalendar();
                }
        });

        next.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendar.add(Calendar.MONTH, +1);
                setUpCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView EventName = view.findViewById(R.id.events_id); // what it says below the date number

                // gets the date that was clicked
                Date d = dates.get(position);

                //System.out.println(d); // prints the day that was clicked in the run for test purposes
                //System.out.println(convertDate(d.getTime())); // proves that I can get the time of the date clicked


                calenderScreen.showPopup(d, events); // shows a popup with the date and events for that date

                Toast.makeText(context, "Day clicked", Toast.LENGTH_SHORT).show(); // test notification
            }
        });
    }

    public MyCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, Context context1)
    {
        super(context, attrs, defStyleAttr);
        this.context = context1;
    }

    /*
    private void SaveEvent(String event, String time, String date, String month, String year)
    {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event, time, date, month, year, database);
        dbOpenHelper.close();
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();
    }
     */

    private void IntializeLayout() // sets up the layout
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_my_calendar, this);
        next = view.findViewById(R.id.next);
        previous = view.findViewById(R.id.previous);
        month = view.findViewById(R.id.monthText);
        gridView = view.findViewById(R.id.gridView);
    }

    private void setUpCalendar() // redoes or does the elements of the calendar
    {
        String currentDate = dateFormat.format(calendar.getTime());
        month.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        while (dates.size() < MAX_CALENDAR_DAYS)
        {
            dates.add(monthCalendar.getTime()); // adds each day to the calendar
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        myGridAdapter = new MyGridAdapter(context, dates, calendar, events);
        gridView.setAdapter(myGridAdapter);
    }

    public static String convertDate(long milli)
    {
        // Updated format so that is matches what is stored in database
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        String dateString = formatter.format(new Date(milli));

        return dateString;
    }

    private void changeEvents(ArrayList<PlannedEvent> e)
    {
        events = e;
        setUpCalendar();
    }
}
