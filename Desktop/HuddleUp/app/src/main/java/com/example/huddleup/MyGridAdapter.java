package com.example.huddleup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyGridAdapter extends ArrayAdapter
{
    private List<Date> dates;
    private Calendar currentDate;
    private ArrayList<PlannedEvent> events;
    private LayoutInflater inflater;
    private TextView eventsID;

    public MyGridAdapter(@NonNull Context c, List<Date> d, Calendar cd, ArrayList<PlannedEvent> e)
    {
        super(c, R.layout.single_cell_layout);
        this.dates = d;
        this.currentDate = cd;
        this.events = e;
        inflater = LayoutInflater.from(c);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Date monthDate = dates.get(position); // the date for the part of the calendar box
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        View view = convertView;

        dateCalendar.setTime(monthDate);
        if (view == null)
        {
            view = inflater.inflate(R.layout.single_cell_layout, parent, false);
        }
        TextView Day_Number = (TextView)view.findViewById(R.id.calendar_day);
        eventsID = (TextView)view.findViewById(R.id.events_id);

        if (MyCalendar.convertDate(Calendar.getInstance().getTimeInMillis()).contentEquals(MyCalendar.convertDate(monthDate.getTime()))) // if the current day of the current month of the current year, then alter the background color
        {
            view.setBackground(getContext().getResources().getDrawable(R.drawable.round_and_lightblue)); // sets background to a rounded light blue box
        }
        else if (displayMonth == currentMonth && displayYear == currentYear) // if the current month of the current year, then alter the background
        {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.clear)); // uses a color added in the colors.xml file
            //view.setBackground(getContext().getResources().getDrawable(R.drawable.round_and_lightblue)); // sets background to a rounded light blue box
        }
        else // sets background of not current selected month days
        {
            //view.setBackgroundColor(getContext().getResources().getColor(R.color.grey));

            view.setBackgroundColor(getContext().getResources().getColor(R.color.clear)); // has the background be clear
            TextView viewy = (TextView)view.findViewById(R.id.calendar_day); // gets the text view for the text
            viewy.setTextColor(getContext().getResources().getColor(R.color.clear)); // has the text be clear
            eventsID.setTextColor(getContext().getResources().getColor(R.color.clear)); // clear text
        }


        Day_Number.setText(String.valueOf(DayNo)); // sets what number is displayed for the calendar days

        // sets up the count of events for each day
        int eventCount = 0;
        for (int i = 0; i < events.size(); i++)
        {
            if (events.get(i).getStringDate().contentEquals(MyCalendar.convertDate(monthDate.getTime())))
            {
                eventCount++;
            }
        }
        eventsID.setText("Events: " + eventCount);

        return view;
    }

    @Override
    public int getCount()
    {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item)
    {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position)
    {
        return dates.get(position);
    }
}
