package com.EntryActivities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MainActivities.BaseClasses.Activity_Password;
import com.UtilityClasses.Files;
import com.UtilityClasses.MiscMethods;
import com.UtilityClasses.XML;
import com.rtomyj.Diary.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/*
  Created by CaptainSaveAHoe on 6/8/17.
 */

public class Entry_Base extends Activity_Password {

    // Activity Vars
    SharedPreferences sp;
    Context context;

    EntryMeta entryMeta;

    // views for activity
    EditText titleET, entryTextET;
    LinearLayout enteredTagsLL;

    boolean onPauseNotTakingPic = false;


    @Override
    protected void onCreate(Bundle previousInstance) {
        super.onCreate(previousInstance);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        context = this;

            if (previousInstance != null) {     // gets info about_activity_layout last instance from bundle
                // file variables
                entryMeta = new EntryMeta();
                entryMeta.filename = previousInstance.getString("FILENAME");
                entryMeta.isSaved = previousInstance.getBoolean("IS_SAVED");
                entryMeta.dateMillis = previousInstance.getLong("FILE_CREATED_MILLIS");

                // info variables
                entryMeta.hasStatuses = previousInstance.getBoolean("STATUSES");
                if(entryMeta.hasStatuses ) {
                    entryMeta.currOccupationName = previousInstance.getString("JOB");
                    entryMeta.currRelationshipName = previousInstance.getString("PARTNER");
                }
                entryMeta.currMood = previousInstance.getInt("MOOD");
                entryMeta.isFave = previousInstance.getBoolean("IS_FAVE");
                entryMeta.hasAudio = previousInstance.getBoolean("HAS_AUDIO");

                // location variables
                entryMeta.hasGPSPermission = previousInstance.getBoolean("GPS_PERMISSION");
                entryMeta.hasLocation = previousInstance.getBoolean("STORED_LOCATION");
                if (entryMeta.hasLocation) {
                    entryMeta.latitude = previousInstance.getDouble("LATITUDE");
                    entryMeta.longitude = previousInstance.getDouble("LONGITUDE");
                    entryMeta.locationName = previousInstance.getString("LOCATION");
                }


                // tags arrays
                // checks the bundle to see if the arrays are empty, if arrays aren't empty then all of the elements are added to this instance arrays with the same name. Otherwise this instance Arrays are instantiated.
                if (previousInstance.getCharSequenceArrayList("ARR_TAGS") != null )
                    entryMeta.tagsArrList = new ArrayList<>(previousInstance.getCharSequenceArrayList("ARR_TAGS"));
                else
                    entryMeta.tagsArrList = new ArrayList<>();

                if (previousInstance.getStringArrayList("SUGGESTED_TAGS") != null)
                    entryMeta.tagSuggestionsArrList = new ArrayList<>(previousInstance.getStringArrayList("SUGGESTED_TAGS"));
                else
                    entryMeta.tagSuggestionsArrList = new ArrayList<>();

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);       // hides keyboard


        onPauseNotTakingPic = false;
        if (sp == null || context == null ){
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            context = this;
        }


        // inits entryText EditText as well as puts a watcher to count the words and characters.
        if (entryTextET == null) {
            titleET = (EditText) findViewById(R.id.title_of_entry);
            entryTextET = (EditText) findViewById(R.id.entry_text);
            entryTextET.addTextChangedListener(new TextWatcher() {
                TextView textCount = (TextView) findViewById(R.id.textCount);

                @Override
                public synchronized void afterTextChanged(Editable s) {
                    StringBuilder characterCount = new StringBuilder("");
                    characterCount.append("C: ");
                    characterCount.append(Integer.toString(s.length()));
                    characterCount.append( '\t' );

                    StringBuilder wordCount = new StringBuilder("");
                    wordCount.append(characterCount);
                    wordCount.append("W: " );
                    wordCount.append(String.valueOf(MiscMethods.countWords(entryTextET.getText().toString())));

                    textCount.setText(wordCount);
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

        }

    }


    public void deleteClicked(View v) {
        // otherwise data_activity_layout has to be erased.
        AlertDialog.Builder t = new AlertDialog.Builder(this);
        t.setMessage(R.string.delete_entry_dialog_message_);
        t.setIcon(ContextCompat.getDrawable(this, R.drawable.white_discard));
        t.setPositiveButton(R.string.DELETE,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        entryMeta.entryDeleted = true;
                        if (entryMeta.isSaved) {
                            Files.deleteEntry(context.getFilesDir(), entryMeta.filename);
                            Files.removeNewEntryFromPreference(sp);
                        }
                        finish();
                    }

                });
        t.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        t.setCancelable(false);
        t.show();
    }
    public void animateDateAndMarkdown(){

        // animates the actionbar_layout on the bottom as well as the date_selection_layout layout.
        LinearLayout infoLL = (LinearLayout) findViewById(R.id.info_layout);
        LinearLayout  markup = (LinearLayout) findViewById(R.id.markup);

        Animation slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.info_down_from_top);
        Animation slideUpAnim = AnimationUtils.loadAnimation(context, R.anim.toolbar_up_from_bottom);

        infoLL.startAnimation(slideDownAnim);
        markup.startAnimation(slideUpAnim);
    }


    public void changeFave(View view){
        if (((Button) view).getText().equals("☆")){
            ((Button) view).setText("★");
            entryMeta.isFave = true;
        }
        else {
            ((Button) view).setText("☆");
            entryMeta.isFave = false;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        // file variables
        savedInstance.putString("FILENAME", entryMeta.filename);
        savedInstance.putLong("FILE_CREATED_MILLIS", entryMeta.dateMillis);
        savedInstance.putBoolean("IS_SAVED", entryMeta.isSaved);

        // info variables
        savedInstance.putString("PARTNER", entryMeta.currRelationshipName);
        savedInstance.putString("JOB", entryMeta.currOccupationName);
        savedInstance.putInt("MOOD", entryMeta.currMood);
        savedInstance.putBoolean("STATUSES", entryMeta.hasStatuses);
        savedInstance.putBoolean("IS_FAVE", entryMeta.isFave);

        //location variables
        savedInstance.putString("LOCATION", entryMeta.locationName);
        savedInstance.putBoolean("STORED_LOCATION", entryMeta.hasLocation);
        savedInstance.putDouble("LATITUDE",entryMeta. latitude);
        savedInstance.putDouble("LONGITUDE", entryMeta.longitude);
        savedInstance.putBoolean("GPS_PERMISSION", entryMeta.hasGPSPermission);

        // tags
        savedInstance.putStringArrayList("SUGGESTED_TAGS", entryMeta.tagSuggestionsArrList);
        savedInstance.putCharSequenceArrayList("ARR_TAGS", entryMeta.tagsArrList);

    }


    public void boldButtonClicked(View bold) {
        MiscMethods.bold(entryTextET);
    }
    public void italicButtonClicked(View italic) {
        MiscMethods.italic(entryTextET);
    }
    public void underlineButtonClicked(View underline) {
        MiscMethods.underline(entryTextET);
    }



    public void moodButtonClicked(final View mood) {
        AlertDialog.Builder selectMood = new AlertDialog.Builder(context);
        CharSequence itemsT[] = getResources().getStringArray(R.array.moods_arr);

        selectMood.setTitle("Select Mood");
        selectMood.setNeutralButton(R.string.DISMISS, null);
        selectMood.setSingleChoiceItems(itemsT, entryMeta.currMood,
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        entryMeta.currMood = item;
                        changeMoodText(entryMeta.currMood, (TextView) mood);
                        dialog.dismiss();
                    }
                });
        selectMood.show();

    }

    public void changeMoodText(int mood, TextView moodTV){
        if (moodTV == null)
            moodTV = (TextView) findViewById(R.id.mood);

        moodTV.setText(MiscMethods.getMood(mood, moodTV));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onPauseNotTakingPic = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onPauseNotTakingPic = true;
    }

    public void saveButtonClicked(View v) {
        onPauseNotTakingPic = true;
        this.finish();
    }


    synchronized public void saveData(){
        String currentFileName = titleET.getText().toString().trim().replaceAll("[^\\p{L} \\s 0-9]", "").replaceAll(" ", "_");
        String oldFileName = null;

        if (! entryMeta.filename.equals(currentFileName) ){         // when the title EditText and filename variable aren't equal all of the files have to be renamed.
            oldFileName = entryMeta.filename;
            entryMeta.filename = Files.renameFiles(context.getFilesDir(), entryMeta.filename, currentFileName);
            if ( ! entryMeta.isSaved)
                XML.recordNewEntryToXML(context.getResources(), context.getFilesDir(), entryMeta.filename, entryMeta.numOfPhotos, entryMeta.currRelationshipName, entryMeta.currOccupationName, entryMeta.tagsArrList, entryMeta.locationName, entryMeta.latitude,entryMeta.longitude, entryMeta.isFave, entryMeta.hasLocation, entryMeta.currMood);
        }
        else if (entryMeta.filename.equals(currentFileName) && ! entryMeta.isSaved){
            try {
                entryMeta.filename = Files.createNewEntryFile(currentFileName, context);
                XML.recordNewEntryToXML(context.getResources(), context.getFilesDir(), entryMeta.filename, entryMeta.numOfPhotos, entryMeta.currRelationshipName, entryMeta.currOccupationName, entryMeta.tagsArrList, entryMeta.locationName, entryMeta.latitude,entryMeta.longitude, entryMeta.isFave, entryMeta.hasLocation, entryMeta.currMood);

                Log.e("new file created", "name is " + entryMeta.filename);
            }catch (IOException io){
                Log.e("Err creating entry file", io.toString());
            }
        }

        try {
            if (entryMeta.isSaved)
                XML.updateEntryInXML(context.getResources(), context.getFilesDir(), entryMeta.filename, oldFileName, entryMeta.numOfPhotos, entryMeta.currRelationshipName, entryMeta.currOccupationName, entryMeta.tagsArrList, entryMeta.locationName, entryMeta.latitude,entryMeta.longitude, entryMeta.isFave, entryMeta.hasLocation, entryMeta.currMood);
            entryMeta.isSaved = true;
            Files.saveEntryText(entryMeta.filename, context, entryTextET);
            Files.saveNewEntryInfoToPreferences(sp, entryMeta.filename, entryMeta.tagsArrList, entryMeta.isFave);
        } catch (IOException io) {
            Log.e("Err saving txt", io.toString());
        }

    }



    public void customizeUI(TextView monthTV, TextView dayTV, TextView timeTV, TextView yearTV, TextView currentLocationTV, TextView relationshipTV, TextView occupationTV){
        EditText title = (EditText) findViewById(R.id.title_of_entry);
        EditText text = (EditText) findViewById(R.id.entry_text);

        LinearLayout  markup = (LinearLayout) findViewById(R.id.markup);
        LinearLayout content = (LinearLayout) findViewById(R.id.content);
        Button faveTV = (Button) findViewById(R.id.add_like_button);


        // weak references to UI elements
        WeakReference<EditText> titleWeak = new WeakReference<>(title);
        WeakReference<EditText> textWeak = new WeakReference<>(text);

        WeakReference<LinearLayout> markupWeak = new WeakReference<>(markup);
        WeakReference<LinearLayout> contentWeak = new WeakReference<>(content);


        if (userUIPreferences.theme.contains("Onyx")) {
            CoordinatorLayout parent = (CoordinatorLayout) findViewById(R.id.New_Entry_Layout);
            Button mood = (Button) findViewById(R.id.mood);

            markupWeak.get().setBackgroundColor(userUIPreferences.secondaryColor);

            mood.setTextColor(userUIPreferences.textColorForDarkThemes);
            monthTV.setTextColor(userUIPreferences.textColorForDarkThemes);
            timeTV.setTextColor(userUIPreferences.textColorForDarkThemes);
            titleWeak.get().setTextColor(userUIPreferences.textColorForDarkThemes);
            textWeak.get().setTextColor(userUIPreferences.textColorForDarkThemes);

            textWeak.get().setHighlightColor(ContextCompat.getColor(context, R.color.UIDarkBlue_Green));
            contentWeak.get().setBackgroundColor(ContextCompat.getColor(context, R.color.UIDarkGray));
            parent.setBackgroundColor(ContextCompat.getColor(context, R.color.UIDarkForeground));
        }


        if (  !  userUIPreferences.fontStyle.contains("DEFAULT") ) {


            titleWeak.get().setTypeface(userUIPreferences.userSelectedFontTF, Typeface.BOLD);
            textWeak.get().setTypeface(userUIPreferences.userSelectedFontTF);

            // if false, location textview will be removed...
            if(entryMeta.hasGPSPermission)
                currentLocationTV.setTypeface(userUIPreferences.userSelectedFontTF);

            timeTV.setTypeface(userUIPreferences.userSelectedFontTF);
            monthTV.setTypeface(userUIPreferences.userSelectedFontTF);
            if (entryMeta.hasStatuses) {
                relationshipTV.setTypeface(userUIPreferences.userSelectedFontTF);
                occupationTV.setTypeface(userUIPreferences.userSelectedFontTF);
            }

            if (entryMeta.hasGPSPermission)
                currentLocationTV.setTextSize(userUIPreferences.textSize);
        }

        markupWeak.get().setBackgroundColor(userUIPreferences.secondaryColor);
        titleWeak.get().setTextColor(userUIPreferences.secondaryColor);
        dayTV.setTextColor(userUIPreferences.secondaryColor);
        faveTV.setTextColor(userUIPreferences.primaryColor);

        titleWeak.get().setTextSize(userUIPreferences.mediumTextSize);
        if(entryMeta.hasStatuses) {
            relationshipTV.setTextSize(userUIPreferences.textSize);
            occupationTV.setTextSize(userUIPreferences.textSize);
        }
        if (entryMeta.hasGPSPermission)
            currentLocationTV.setTextSize(userUIPreferences.textSize);

        monthTV.setTextSize(userUIPreferences.textSize);
        timeTV.setTextSize(userUIPreferences.textSize);
        dayTV.setTextSize(userUIPreferences.largeTextSize);
        yearTV.setTextSize(userUIPreferences.textSize);
        textWeak.get().setTextSize(userUIPreferences.textSize);
    }

}
