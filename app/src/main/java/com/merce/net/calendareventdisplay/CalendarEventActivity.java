package com.merce.net.calendareventdisplay;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import java.util.logging.Logger;

import javax.inject.Inject;
    /*
        Calendar Main event activity
     */

public class CalendarEventActivity extends Activity {

    CalendarService calendarService=new CalendarService();
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);
        calendarService.populateEventList(this);

    }


}
