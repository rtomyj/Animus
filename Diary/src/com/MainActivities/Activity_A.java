package com.MainActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.SubActivities.Passcode;
import com.UtilityClasses.AnimusUI;
import com.UtilityClasses.CustomAttributes;

/**
 * Created by CaptainSaveAHoe on 7/6/17.
 */
/*
    Base class of any activity that needs AppCompat functionality. Handles password related tasks. All sub classes need to call the super counterparts in order for the methods to be invoked.
    All sub classes should also override the loadAds attribute since not all sub classes should show them.

    Variable currentActivity should be overidden to whatever activity is the sub activity.
 */
public class Activity_A extends AppCompatActivity {
    // constants used when launching activity for result to handle the result
    public final static byte NEW_ENTRY = 1;
    public final static byte CHOSEN_FILE = 2;
    public final static byte SETTINGS = 3;

    public final static byte PASSWORD_CHECK = 0;

    protected final static byte DOMUS = -1;
    protected final static byte PIC_ENTRIES = -2;
    protected final static byte TAGS = -3;
    protected final static byte FAVES = - 4;

    // primitives for class
    boolean shouldLaunchPasswordActivity = false, passWordCheckedSuccessful = false, loadAds = false;
    protected byte currentActivity = DOMUS;

    // user UI preferences
    protected CustomAttributes userUIPreferences;

    // checks to see if the app should launch the password screen. It is only launched when the user has explicitly opted in for the feature in the settings screen and when the app is relaunched from multitasking
    @Override
    protected void onRestart() {
        super.onRestart();

        if (shouldLaunchPasswordActivity) {
            Intent i = new Intent(this, Passcode.class);
            startActivityForResult(i, PASSWORD_CHECK);
        }
        if (passWordCheckedSuccessful) {        // user has entered password successfully and the app will not need to launch password activity again.
            shouldLaunchPasswordActivity = true;
            passWordCheckedSuccessful = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // prevents password prompt from reappearing once the user enters the correct password and when user comes back from an activity
        shouldLaunchPasswordActivity = false;
        passWordCheckedSuccessful = true;

        switch (requestCode) {
            case PASSWORD_CHECK:
                if (resultCode == RESULT_OK) {
                    shouldLaunchPasswordActivity = false;
                    passWordCheckedSuccessful = true;
                }
                break;
        }

    }


    // prevents user from changing orientation of a phone that has a really small screen. Also checks either bundle or preferences for password preference, creates a UI object and inits it with user defined preferences and also sees if ads should be loaded.
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {		// blocks orientation change on really small screens
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (bundle != null){
            shouldLaunchPasswordActivity = bundle.getBoolean("PASSWORD_CHECK");

            // loads data from bundle
            userUIPreferences = new CustomAttributes(this, bundle.getInt("PRIMARY_COLOR"), bundle.getInt("SECONDARY_COLOR"), bundle.getInt("TAGS_TEXT_COLOR"), bundle.getInt("DARK_THEME_TEXT_COLOR"),
                    bundle.getInt("DARK_THEME_BACKGROUND_COLOR"), bundle.getInt("NUM_LINES"), bundle.getFloat("TEXT_SIZE"), bundle.getString("FONT_STYLE"), bundle.getString("THEME"));

            loadAds = bundle.getBoolean("LOAD_ADS");

        }
        else {
            shouldLaunchPasswordActivity = sp.getBoolean("Password", false);

            userUIPreferences = new CustomAttributes(this, sp);
            if (currentActivity < PASSWORD_CHECK)
                loadAds = sp.getBoolean("ADS", true);
        }

        // changes the color of views according to theme
        AnimusUI.setTheme(this, userUIPreferences.theme);

    }

    // saves password setting to bundle
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("PASSWORD_CHECK", shouldLaunchPasswordActivity);

    }

    // Allows back functionality from all sub classes.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
