package com.UtilityClasses;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by CaptainSaveAHoe on 6/8/17.
 */

public interface Entry {
    int USE_CAMERA_CONST = 100;
    int PICK_PHOTO_CONST = 101;

    void setupEntryData();
    void setupSuggestedTags();
    void placeInfoToViews(TextView monthTV, TextView dayTV, TextView timeTV, TextView yearTV, TextView currentLocationTV, TextView relationshipTV, TextView occupationTV);
    void placeTagsToEntry();
    void getLocation(boolean isGPSGrantedByUser, TextView currentLocationTV);

    void customizeUI(TextView monthTV, TextView dayTV, TextView timeTV, TextView yearTV, TextView currentLocationTV, TextView relationshipTV, TextView occupationTV);

    void makeImageViews(int imageNum, File loadPics);
    void loadPicsFromLastInstance();
    void setListenersToImageViews(ImageView imageView, final int imageNum, final LinearLayout holder);
    void choosePhoto(MenuItem m);

    void messWithAudio(View view);
    void playAudio(View view);
    void pauseAudio(View view);
    void replay(View view) ;
    void recordAudio(View v);

    void saveButtonClicked(View v);
    void saveData();

    void boldButtonClicked(View bold);
    void italicButtonClicked(View italic);
    void underlineButtonClicked(View underline);

    void changeMoodText(int mood, TextView moodTV);
    void moodButtonClicked(final View mood);

    void addTag(String newTag, boolean addToList);
    void EditTags(View v);
    void newTag(View v);
    void extractTagFromDialog(String newTag);

    void changeFave(View view);


    void emailButtonClicked(View mi);
    }
