package com.AnimusMainActivities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.AnimusSubActivities.Passcode;

/**
 * Created by CaptainSaveAHoe on 7/6/17.
 */

public class Activity_A extends AppCompatActivity {

    public final static int PASSWORD_CHECK = 0;
    boolean shouldLaunchPasswordActivity = false, passWordCheckedSuccessfull = false;

    // checks to see if the app should launch the password screen. It is only launched when the user has explicitly opted in for the feature in the settings screen and when the app is relaunched from multitasking
    @Override
    protected void onRestart() {
        super.onRestart();
        if (shouldLaunchPasswordActivity) {
            Intent i = new Intent(this, Passcode.class);
            startActivityForResult(i, PASSWORD_CHECK);
        }
        if (passWordCheckedSuccessfull) {        // user has entered password successfully and the app will not need to launch password activity again.
            shouldLaunchPasswordActivity = true;
            passWordCheckedSuccessfull = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // prevents password prompt from reappearing once the user enters the correct password and when user comes back from an activity
        shouldLaunchPasswordActivity = false;
        passWordCheckedSuccessfull = true;

        switch (requestCode) {
            case PASSWORD_CHECK:
                if (resultCode == RESULT_OK) {
                    shouldLaunchPasswordActivity = false;
                    passWordCheckedSuccessfull = true;
                }
                break;
        }

    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (bundle != null){        // loads data from bundle
            shouldLaunchPasswordActivity = bundle.getBoolean("PASSWORD_CHECK");

        }
        else {  // if there is nothing to copy from bundle, call method
            shouldLaunchPasswordActivity = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Password", false);
        }

    }

    // when the activity is being terminated it gets the chance to saveEntryText its state. Here it saves the files, the tags for the files, their status (whether they're favorites) etc.
    @Override
    protected void onSaveInstanceState(Bundle bundle) {

        bundle.putBoolean("PASSWORD_CHECK", shouldLaunchPasswordActivity);

    }
}
