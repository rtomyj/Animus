package com.BaseClasses;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
<<<<<<< HEAD

import com.UtilityClasses.UI;
=======
import com.UtilityClasses.AnimusUI;
>>>>>>> a966f1388bbd459a8aac6ba356fb65af1d7e2e6e
import com.UtilityClasses.CustomAttributes;

/*
         Created by CaptainSaveAHoe on 7/13/17.
 */

/*
    This class should be the base class for most of the activities in the app one way or another. The purpose of this Activity is to hold the current activity identifier. It is a byte value that identifies the
    child of this activity. This identifier will allow for specific calls depending on the sub class this is being a parent to.

    CustomAttributes holds the values such as primaryColor, secondaryColor, fontSize, etc.... Anything that the user has predefined in their preferences.

    Finally this class blocks smaller devices from switching orientation and sets the theme for the whole activity (calling setTheme or UI.setTheme will not be necessary in sub classes).
 */
public class Activity_Base extends AppCompatActivity implements ActivityTypes{
    protected byte currentActivityIdentifier;

    // user UI preferences
    protected CustomAttributes userUIPreferences;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL)   	// blocks orientation change on really small screens
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        if (bundle != null){
            userUIPreferences = new CustomAttributes(
                    this, bundle.getInt("PRIMARY_COLOR"), bundle.getInt("SECONDARY_COLOR"), bundle.getInt("TAGS_TEXT_COLOR"), bundle.getInt("DARK_THEME_TEXT_COLOR"),
                    bundle.getInt("DARK_THEME_BACKGROUND_COLOR"), bundle.getInt("NUM_LINES"), bundle.getFloat("TEXT_SIZE"),
                    bundle.getString("FONT_STYLE"), bundle.getString("THEME"));

            currentActivityIdentifier = bundle.getByte("CURRENT_ACTIVITY");
        }
        else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            userUIPreferences = new CustomAttributes(this, sp);
        }

        // changes the color of views according to theme
        UI.setTheme(this, userUIPreferences.theme);

    }


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


        bundle.putByte("CURRENT_ACTIVITY", currentActivityIdentifier);
    }
}
