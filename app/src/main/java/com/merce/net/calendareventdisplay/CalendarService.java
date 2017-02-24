package com.merce.net.calendareventdisplay;

/**
 * Created by vsha91 on 12/20/2016.
 */

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.merce.net.calendareventdisplay.CalendarConstants.EVENT_URL_CONSTANT;

@Named("calendarService")
@Singleton
public class CalendarService {


    public ArrayList<CalendarEvent> readCalendarEvent(Context context) {
        ArrayList<CalendarEvent> calendarEventList = new ArrayList<CalendarEvent>();
        String[] projection = new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.EVENT_LOCATION};
        Cursor cursor = context.getContentResolver().query(EVENT_URL_CONSTANT, projection, getselection(), null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];
        for (int i = 0; i < CNames.length; i++) {
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setTitle(cursor.getString(1));
            Log.i("Title is ", cursor.getString(1));
            calendarEvent.setLocation(cursor.getString(5));
            Log.i("Location is ", cursor.getString(5));
            if (null != (cursor.getString(3))) {
                calendarEvent.setBegin(getDate(Long.parseLong(cursor.getString(3))));
                Log.i("start time is ", cursor.getString(3));
            }
            if (null != (cursor.getString(4))) {
                calendarEvent.setEnd(getDate(Long.parseLong(cursor.getString(4))));
                Log.i("end time is ", cursor.getString(4));
            }
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();
            calendarEventList.add(calendarEvent);
        }
        return calendarEventList;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getselection() {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.DATE, 1);
        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ))";
        return selection;
    }

    public void populateEventList(CalendarEventActivity calendarEventActivity) {
        int permissionCheck = ContextCompat.checkSelfPermission(calendarEventActivity,
                Manifest.permission.READ_CALENDAR);
        // Construct the data source
        CalendarService calendarService = new CalendarService();
        // Create the adapter to convert the array to views
        Context context = calendarEventActivity;

        if (permissionCheck >= 0) {
            List<CalendarEvent> list = calendarService.readCalendarEvent(context);
            ArrayList<CalendarEvent> calenderList = new ArrayList<CalendarEvent>();
            calenderList.addAll(list);
            CustomEventAdapter adapter = new CustomEventAdapter(calendarEventActivity, calenderList);
            // Attach the adapter to a ListView
            ListView listView = (ListView) calendarEventActivity.findViewById(R.id.lvEvent);
            listView.setAdapter(adapter);
        }
    }

}
