package com.BaseClasses;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.UtilityClasses.AnimusUI;
import com.UtilityClasses.CustomAttributes;

/**
 * Created by CaptainSaveAHoe on 7/13/17.
 */

public class Activity_Base extends AppCompatActivity{
    // constants used when launching activity for result to handle the result
    public final static byte NEW_ENTRY = 1;
    public final static byte CHOSEN_FILE = 2;
    public final static byte SETTINGS = 3;
    public final static byte GENERIC_CLASS = 64;        // A class that extends this class but doesn't need too many bells and whistles.

    public final static byte PASSWORD_CHECK = 0;

    protected final static byte DOMUS = -1;
    protected final static byte PIC_ENTRIES = -2;
    protected final static byte TAGS = -3;
    protected final static byte FAVES = - 4;


    protected byte currentActivity;

    // user UI preferences
    protected CustomAttributes userUIPreferences;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {		// blocks orientation change on really small screens
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (bundle != null){
            // loads data from bundle
            userUIPreferences = new CustomAttributes(this, bundle.getInt("PRIMARY_COLOR"), bundle.getInt("SECONDARY_COLOR"), bundle.getInt("TAGS_TEXT_COLOR"), bundle.getInt("DARK_THEME_TEXT_COLOR"),
                    bundle.getInt("DARK_THEME_BACKGROUND_COLOR"), bundle.getInt("NUM_LINES"), bundle.getFloat("TEXT_SIZE"), bundle.getString("FONT_STYLE"), bundle.getString("THEME"));

        }
        else {
            userUIPreferences = new CustomAttributes(this, sp);
        }

        // changes the color of views according to theme
        AnimusUI.setTheme(this, userUIPreferences.theme);

    }

    // when the activity is being terminated it gets the chance to saveEntryText its state. Here it saves the files, the tags for the files, their status (whether they're favorites) etc.
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("PRIMARY_COLOR",  userUIPreferences.primaryColor);
        bundle.putInt("SECONDARY_COLOR", userUIPreferences.secondaryColor);
        bundle.putInt("DARK_THEME_TEXT_COLOR", userUIPreferences.textColorForDarkThemes);
        bundle.putInt("DARK_THEME_BACKGROUND_COLOR", userUIPreferences.darkThemeBackgroundColor);
        bundle.putInt("TAGS_TEXT_COLOR", userUIPreferences.tagsTextColor);
        bundle.putString("THEME", userUIPreferences.theme);
        bundle.putString("FONT_STYLE", userUIPreferences.fontStyle);
        bundle.putInt("NUM_LINES", userUIPreferences.numLines);
        bundle.putFloat("TEXT_SIZE", userUIPreferences.textSize);
    }
}
