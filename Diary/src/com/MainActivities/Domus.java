package com.MainActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;

import com.UtilityClasses.AnimusXML;
import com.rtomyj.Diary.R;

/**
 * Created by CaptainSaveAHoe on 7/10/17.
 */

public class Domus extends Entries {

    // shows popup for new users once and only once
    private void showIntro() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sp.contains("INTRO")) {
            sp.edit().putBoolean("INTRO", false).apply();
        }

        if (!sp.getBoolean("INTRO", false)) {
            AlertDialog.Builder introDialog = new AlertDialog.Builder(this);
            View introView = View.inflate(this, R.layout.intro, null);

            introDialog.setView(introView);

            introDialog.setPositiveButton(getResources().getString(R.string.intro_dialog_dismiss_button), null);
            introDialog.create();
            introDialog.show();
            sp.edit().putBoolean("INTRO", true).apply();  // sets preference to true for intro meaning the user already has seen it.
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            // checks to see if there is a preference named INTRO, if not then it creates a new one with default value of false.
            // when true, intro has been seen and will not show the AlertDialog
            showIntro();
        }
    }


    // Sets the UI elements to specified colors depending on the theme used.
    @Override
    void customizeUI() {
        super.customizeUI();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Creates xml files for tags, faves, etc...
        AnimusXML.checkForAppFiles(getFilesDir(), getAssets(), getBaseContext().getContentResolver());

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // when the activity is being terminated it gets the chance to saveEntryText its state. Here it saves the files, the tags for the files, their status (whether they're favorites) etc.
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);  // needed in order for password screen not be invoked every time user goes back from any other activity
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.domus:
                sideNavDrawer.closeDrawer(GravityCompat.START); // closes the side bar
                break;
            default:
                super.onNavigationItemSelected(item);
                break;
        }
        return true;
    }


    }
