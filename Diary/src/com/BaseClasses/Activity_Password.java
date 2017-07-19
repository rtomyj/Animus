package com.BaseClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.SubActivities.Passcode;

/**
 * Created by CaptainSaveAHoe on 7/6/17.
 */
/*
    Base class of any activity that needs AppCompat functionality. Handles password related tasks. All sub classes need to call the super counterparts in order for the methods to be invoked.
    All sub classes should also override the loadAds attribute since not all sub classes should show them.

    Variable currentActivity should be overridden to whatever activity is the sub activity.
 */
public class Activity_Password extends Activity_Base {
    // primitives for class
    private boolean shouldLaunchPasswordActivity = false, passWordCheckedSuccessful = false;

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

        if (bundle != null){
            shouldLaunchPasswordActivity = bundle.getBoolean("PASSWORD_CHECK");

        }
        else {
            shouldLaunchPasswordActivity = sp.getBoolean("Password", false);

        }

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
