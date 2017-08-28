package com.EntryActivities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.UtilityClasses.Audio;
import com.UtilityClasses.MiscMethods;
import com.UtilityClasses.Permissions;
import com.rtomyj.Diary.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by MacMini on 8/26/17.
 */

public class Entry extends Entry_Permissions {

    // Entry_Base info
    boolean  dataLoaded = false;

    void baseMeta(){
        // if onStart hasn't run (dataLoaded == false), UI hasn't been updated.
        // changes the UI of the views to match that of what the user wants.
        // adds time and date_selection_layout to entry
        if(! dataLoaded) {
            TextView monthTV = (TextView) findViewById(R.id.month_for_new_entry);
            TextView dayTV = (TextView) findViewById(R.id.day_for_new_entry);
            TextView timeTV = (TextView) findViewById(R.id.time_for_new_entry);
            TextView yearTV = (TextView) findViewById(R.id.year_for_new_entry);
            TextView currentLocationTV = (TextView) findViewById(R.id.location);
            TextView relationshipTV = (TextView) findViewById(R.id.relationship);
            TextView occupationTV = (TextView) findViewById(R.id.occupation);

            enteredTagsLL = (LinearLayout) findViewById(R.id.tags_in_entry);

            if (entryMeta == null) {      // if the app didn't load any data from bundle then entryMeta needs to be instantiated.
                entryMeta = new EntryMeta();
                entryMeta.setupEntryData(sp);       // gets date_selection_layout, time, entryMeta.hasStatuses, location, suggested tags, etc
                entryMeta.setupSuggestedTags(sp, this);       // sets up suggested tags array with contact names and previously used tags.
            }

            placeEntryMetaToViews(monthTV, dayTV, timeTV, yearTV, currentLocationTV, relationshipTV, occupationTV);      // places all info gotten from previous method to the UI
            changeMoodText(entryMeta.currMood, null);     // changes mood button to current mood.
            loadPicsFromLastInstance();         // puts pics from last instance to views.
            customizeUI(monthTV, dayTV, timeTV, yearTV, currentLocationTV, relationshipTV, occupationTV);       // changes the UI according to user preferences
            placeTagsToEntry();     // puts the tags user has added to the entry in the UI
        }

        if (entryMeta.hasAudio) {
            ImageSwitcher audioSwitcher = (ImageSwitcher) findViewById(R.id.audio);
            if (audio == null)
                audio = new Audio(context, entryMeta.filename, audioSwitcher, entryMeta.hasAudio);

        }


        dataLoaded = true;      // will prevent frequent onStart() calls from redoing operations that have already been done before.

    }


    // places all the right information to the UI such as time and date_selection_layout, location and entryMeta.hasStatuses (if appropriate) and mood from previous instance. Among others.
    private void placeEntryMetaToViews(TextView monthTV, TextView dayTV, TextView timeTV, TextView yearTV, TextView currentLocationTV, TextView relationshipTV, TextView occupationTV){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(entryMeta.dateMillis);

        monthTV.setText(String.format(Locale.getDefault(), "%1$tB", calendar));
        yearTV.setText(String.format(Locale.getDefault(), "%1$tY", calendar));
        timeTV.setText(MiscMethods.getLocalizedTime(calendar));
        dayTV.setText(String.format(Locale.getDefault(), "%1$td", calendar) + ',');


        // changes the value of the Status' if the user has the preference enabled with their values
        if (entryMeta.hasStatuses) {
            relationshipTV.setText(entryMeta.currRelationshipName);
            occupationTV.setText(entryMeta.currOccupationName);

        } else {        //hides views if preference is off.
            relationshipTV.setVisibility(View.INVISIBLE);
            occupationTV.setVisibility(View.INVISIBLE);
        }

        // if gps permission is given by user the preference pane and location was not recorded yet in then the app will ping for location
        if (entryMeta.hasGPSPermission && ! entryMeta.hasLocation) {
            if (locationManager == null) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation? Only needed when the user hasn't seen the dialog before
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Permissions.requestDialog(Permissions.MY_PERMISSIONS_REQUEST_LOCATION, context);
                    }else{
                        Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_LOCATION);
                    }

                }else
                    getLocation(true, currentLocationTV);      // changes the location text view to the value of the users current location.
            }
        }else if(entryMeta.hasGPSPermission && entryMeta.hasLocation){     // if  gps permission is set to true and the app already pinged for location and has that info, it sets the location name to the TextView
            currentLocationTV.setText(entryMeta.locationName);
        }
        else
            getLocation(false, currentLocationTV);     // hides location text view


        if (entryMeta.isFave){
            Button faveButton = (Button) findViewById(R.id.add_like_button);
            changeFave(faveButton);
        }


    }
}
