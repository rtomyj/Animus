package com.EntryActivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.UtilityClasses.Files;
import com.UtilityClasses.Permissions;
import com.UtilityClasses.Tags;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


 class EntryMeta{

    public int  currMood = 0, numOfPhotos = 0;

    public String locationName = "", currOccupationName = "", currRelationshipName = "", filename = "", changedFileName = "";

    public ArrayList<CharSequence> tagsArrList ;
     public ArrayList<String> tagSuggestionsArrList;
    public long dateMillis = 0;
     public double latitude, longitude;

    public boolean isFave = false, isSaved = false, hasStatuses = false, hasAudio = false, hasGPSPermission, hasLocation = false, entryDeleted = false;

    public EntryMeta(){

    }

     public void setupEntryData(SharedPreferences sp){       // if there is nothing being loaded from bundle then there needs to be data_activity_layout loaded from SharedPreference or created here.
         dateMillis = System.currentTimeMillis();

         hasStatuses = sp.getBoolean("Statuses", false);
         if (hasStatuses) {
             currRelationshipName = sp.getString("Relationship", null);
             currOccupationName = sp.getString("Occupation", null);
         }
         hasGPSPermission = sp.getBoolean("GPS", false);

         // initializes null ArrayLists
         tagsArrList = new ArrayList<>();
         tagSuggestionsArrList = new ArrayList<>();

     }

     public void setupSuggestedTags(SharedPreferences sp, Context context){
         //  gets suggested tags from previously used tags found in xml
         tagSuggestionsArrList.addAll(Tags.getUniqueTags(context.getFilesDir()));
         // gets contact names from the users contacts to suggest tags.
         if (sp.getBoolean("Contacts", false)) {
             if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                 // Should we show an explanation?
                 if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, Manifest.permission.READ_CONTACTS)) {     // only shows explanation if user hasn't seen it or doesn't clicked "never show again"
                     Permissions.requestDialog(Permissions.MY_PERMISSIONS_REQUEST_CONTACTS, context);
                 }else{
                     Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_CONTACTS);
                 }

             }else
                 tagSuggestionsArrList.addAll(Files.getContactSuggestions(context));

         }

     }


}
