package com.UtilityClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.MainActivities.Domus;
import com.MainActivities.FaveEntries;
import com.BaseClasses.MainActivity;
import com.EntryActivities.NewEntry;
import com.MainActivities.PicEntries;
import com.EntryActivities.ChosenFile;
import com.Settings.MainSettingsFrag;
import com.SubActivities.Notes;
import com.MainActivities.Tags;
import com.rtomyj.Diary.R;

import java.util.ArrayList;

/**
 * Created by CaptainSaveAHoe on 6/7/17.
 */

public class AnimusLauncherMethods {

    public static Intent launchPhotoViewer(View v, Context context, String filename) {      // launches a new activity to see the selected picture in a bigger space
        Intent i = new Intent(context, PhotoViewer.class);
        i.putExtra("PHOTO", filename + "(" + Integer.toString(v.getId())+ ").png");
        return i;
    }

    public static void launchActivity(int menuItem, Context parent){
        Intent intent = null;
        switch (menuItem){
            case R.id.domus:
                intent  = new Intent(parent, Domus.class);
                break;
            case R.id.notes:
                intent  = new Intent(parent, Notes.class);
                break;
            case R.id.pictures:
                intent = new Intent(parent, PicEntries.class);
                break;
            case R.id.tags:
                intent = new Intent(parent, Tags.class);
                break;
            case R.id.faves:
                intent = new Intent(parent, FaveEntries.class);
                break;
            case R.id.settings:
                intent= new Intent(parent, MainSettingsFrag.class);
                break;
            case R.id.debug:
                intent= new Intent(parent, Debug.class);
                break;

        }
        switch (menuItem){      // if an activity needs a result when calling new activity then it will return the intent to the caller otherwise it launches the activity here and returns a null intent.
            case R.id.settings:
                ((Activity)parent).startActivityForResult(intent, MainActivity.SETTINGS);
                break;
            default:
                parent.startActivity(intent);
                break;
        }

    }

    public static void chosenFile(Context context, String filename, int position, ArrayList<String> sortedFiles){
        Intent chosenFile = new Intent(context, ChosenFile.class);
        chosenFile.putExtra("FILENAME", filename);

        chosenFile.putExtra("FILESARRAY", sortedFiles);
        chosenFile.putExtra("POSITION",position);
        ((Activity) context).startActivityForResult(chosenFile, MainActivity.CHOSEN_FILE);
    }
    public static void newEntry(Context context){
        Intent intent = new Intent(context, NewEntry.class);
        (( Activity)  context).startActivityForResult(intent, MainActivity.NEW_ENTRY);
        ((Activity) context).overridePendingTransition(R.anim.scale, R.anim.scale);
    }


}
