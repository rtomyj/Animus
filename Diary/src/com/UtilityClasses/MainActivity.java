package com.UtilityClasses;

import android.content.SharedPreferences;
import android.view.View;

/**
 * Created by CaptainSaveAHoe on 6/21/17.
 */

public interface MainActivity {
    int NEW_ENTRY = 1;
    int CHOSEN_FILE = 2;
    int SETTINGS = 10;
    int PASSWORD_CHECK = 0;

    void showWelcome();
    void showList();
    void feedback();
    void newEntry(View view);
    void customizeUI();

    void baseDonation(View v);
    void removeAds(View v);
    void donate(String donationType);
    void in_appCheck(SharedPreferences sp);
}
