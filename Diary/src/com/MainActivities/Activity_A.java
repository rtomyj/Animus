package com.MainActivities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.SubActivities.Passcode;

/**
 * Created by CaptainSaveAHoe on 7/6/17.
 */
/*
    Base class of any activity that needs AppCompat functionality. Handles password related tasks. All sub classes need to call the super counterparts in order for the methods to be invoked.
 */
public class Activity_A extends AppCompatActivity {

    // primitives for class
    public final static int PASSWORD_CHECK = 0;
    boolean shouldLaunchPasswordActivity = false, passWordCheckedSuccessful = false;

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


    // checks bundle if orientation change was the cause of the launch. Else checks the user preferences to get password setting
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {		// blocks orientation change on really small screens
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (bundle != null){
            shouldLaunchPasswordActivity = bundle.getBoolean("PASSWORD_CHECK");

        }
        else {
            shouldLaunchPasswordActivity = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Password", false);
        }

    }

    // saves password setting to bundle
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("PASSWORD_CHECK", shouldLaunchPasswordActivity);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
