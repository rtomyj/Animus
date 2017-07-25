package com.MainActivities.BaseClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.SubActivities.Passcode;

/*
         Created by CaptainSaveAHoe on 7/6/17.
 */

/*
    Extends Activity_Base. The sole purpose of this activity is to handle the event that the app needs the user to enter their password.  The only way this gets called is if the user exits the application
    either by multitasking or by pressing the home button. Whenever the application launches a new activity within its own package, the variable launchingWithinApp should be checked to true
    so that the activity doesn't mistake it for an action that would require a password upon going back to the activity.

<<<<<<< HEAD
=======
    Variable currentActivity should be overridden to whatever activity is the sub activity.
>>>>>>> a966f1388bbd459a8aac6ba356fb65af1d7e2e6e
 */
public class Activity_Password extends Activity_Base {
    private boolean shouldLaunchPasswordActivity, passWordCheckedSuccessful = false, launchingWithinApp = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldLaunchPasswordActivity && ! passWordCheckedSuccessful && ! launchingWithinApp) {
            Intent passwordCheckIntent = new Intent(this, Passcode.class);
            startActivityForResult(passwordCheckIntent, PASSWORD_CHECK);
        }
        else  {        // user has entered password successfully or if the user has come back to the activity from within the app, the app will NOT need to launch password activity again.
            passWordCheckedSuccessful = false;
            launchingWithinApp = false;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // prevents password prompt from reappearing once the user enters the correct password and when user comes back from an activity

        switch (requestCode) {
            case PASSWORD_CHECK:
                if (resultCode == RESULT_OK) {
                    passWordCheckedSuccessful = true;
                }
                break;
        }

    }


    // prevents user from changing orientation of a phone that has a really small screen. Also checks either bundle or preferences for password preference, creates a UI object and initializes it with user defined preferences and also sees if ads should be loaded.
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (bundle != null){
            shouldLaunchPasswordActivity = bundle.getBoolean("PASSWORD_CHECK");

        }
        else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            shouldLaunchPasswordActivity = sp.getBoolean("Password", false);

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("PASSWORD_CHECK", shouldLaunchPasswordActivity);

    }

}
