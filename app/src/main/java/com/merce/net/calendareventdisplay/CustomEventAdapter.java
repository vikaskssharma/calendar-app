package com.merce.net.calendareventdisplay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

/*
Cu
 */
public class CustomEventAdapter extends ArrayAdapter<CalendarEvent> {
    Logger log = Logger.getLogger("CustomUsersAdapter");


    public CustomEventAdapter(Context context, ArrayList<CalendarEvent> users) {
        super(context, 0, users);
    }

    /*
    this method is used to create list  view layout
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final CalendarEvent eventDetails = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            TextView dates = (TextView) convertView.findViewById(R.id.dates);
            dates.setText("TODAY APPOINTMENT");
            dates.setBackgroundColor(Color.rgb(255, 235, 205));
            dates.setTypeface(Typeface.DEFAULT_BOLD);
            // Lookup view for data population
            TextView eventTitle = (TextView) convertView.findViewById(R.id.eventTitle);
            eventTitle.setTextColor(Color.rgb(139, 69, 19));
            TextView eventBegin = (TextView) convertView.findViewById(R.id.eventBegin);
            Button eventLocation = (Button) convertView.findViewById(R.id.eventLocation);
            eventLocation.setBackgroundColor(Color.rgb(229, 255, 204));
            TextView eventEnd = (TextView) convertView.findViewById(R.id.eventEnd);
            eventTitle.setText("Title:   " + eventDetails.getTitle());
            eventBegin.setTypeface(Typeface.DEFAULT_BOLD);
            eventBegin.setText("Start Time:  " + eventDetails.getBegin());
            eventEnd.setText("End Time:  " + eventDetails.getEnd());
            eventEnd.setTypeface(Typeface.DEFAULT_BOLD);
            eventLocation.setText(eventDetails.getLocation());
            eventLocation.setTag(eventDetails);
            final View finalConvertView = convertView;
            eventLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String loc = eventDetails.getLocation();
                    Log.i("loc : ", loc);
                    OpenApi(eventDetails, finalConvertView);
                }
            });
        }
        return convertView;
    }
   /*
   this method is used to open api geocode and google map.
    */

    private void OpenApi(CalendarEvent eventDetails, View view) {
        final TextView mTxtDisplay = (TextView) view.findViewById(R.id.dates);
        String regex_loc = URLEncoder.encode(eventDetails.getLocation());
        StringBuilder geocode_url = new StringBuilder(CalendarConstants.GEOCODE_URL).append(regex_loc).
                append(CalendarConstants.APP_ID_KEY).append(CalendarConstants.APP_ID_VALUE)
                .append(CalendarConstants.APP_CODE_KEY).append(CalendarConstants.APP_CODE_VALUE)
                .append(CalendarConstants.GECODE_GEN);
        Log.i("geo code url is::  ",geocode_url.toString());
        /*
         creating json object to retrieve latitude and longitude from geocode api
         */
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, geocode_url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                Log.i("response is", jsonResponse.toString());
                try {
                    String longitude = getDisplayPosition(jsonResponse).getString("Longitude");
                    String latitude = getDisplayPosition(jsonResponse).getString("Latitude");
                    StringBuilder google_map_url = new StringBuilder(CalendarConstants.GOOGLE_MAP_URL)
                            .append(latitude).append(CalendarConstants.COMMA).append(longitude);
                    Log.i("google map url is:::  ",google_map_url.toString());
                    Intent browserIntent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse(google_map_url.toString()));
                    getContext().startActivity(browserIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                mTxtDisplay.setText("Error: " + error.getMessage());

                // hide the progress dialog

            }
        }

        );
         /*
            Adding request to request queue
          */
        AppController.getInstance().

                addToRequestQueue(jsonObjReq, CalendarConstants.TAG_JSON_OBJ);


    }

    /*
    this method used to traverse json response of geocode.
     */
    private JSONObject getDisplayPosition(JSONObject jsonResponse) throws JSONException {

        JSONObject displayPosition = jsonResponse.getJSONObject("Response").getJSONArray("View").getJSONObject(0)
                .getJSONArray("Result").getJSONObject(0).getJSONObject("Location").getJSONObject("DisplayPosition");
        return displayPosition;
    }
}
