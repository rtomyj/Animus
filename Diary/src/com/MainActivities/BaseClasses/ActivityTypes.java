package com.MainActivities.BaseClasses;

/**
 * Created by MacMini on 7/18/17.
 */

public interface ActivityTypes {
    // constants used when launching activity for result to handle the result
    byte NEW_ENTRY = 1;
    byte CHOSEN_FILE = 2;
    byte SETTINGS = 3;
    byte GENERIC_CLASS = 64;        // A class that extends this class but doesn't need too many bells and whistles.

    byte PASSWORD_CHECK = 0;

    byte DOMUS = -1;
    byte PIC_ENTRIES = -2;
   byte TAGS = -3;
   byte FAVES = - 4;
    byte CHOSEN_TAG = -5;
}
