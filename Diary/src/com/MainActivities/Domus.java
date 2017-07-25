package com.MainActivities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import com.UtilityClasses.Files;
import com.rtomyj.Diary.R;

/*
     Created by CaptainSaveAHoe on 7/10/17.
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
        currentActivityIdentifier = DOMUS;

        super.onCreate(bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Creates xml files for tags, faves, etc...
        showIntro();
        Files.checkForAppFiles(getFilesDir(), getAssets(), getBaseContext().getContentResolver());

    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.domus:
                super.closeNavDrawer();
                break;
            default:
                super.onNavigationItemSelected(item);
                break;
        }
        return true;
    }


    }
