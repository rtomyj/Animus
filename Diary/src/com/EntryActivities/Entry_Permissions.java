package com.EntryActivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import com.UtilityClasses.Audio;
import com.UtilityClasses.Files;
import com.UtilityClasses.Permissions;
import com.UtilityClasses.Pictures;
import com.rtomyj.Diary.R;

import java.io.IOException;

/**
 * Created by MacMini on 8/25/17.
 */

public class Entry_Permissions extends Entry_Pictures{



    @Override
    protected void onCreate(Bundle previousInstance) {
        super.onCreate(previousInstance);           // first puts content view then calls the parent super method

    }


    /*
    this method is called whenever a user is requested to accept or deny a permission and they dismiss the dialog
    It checks what they chose, and does an action according to it.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Permissions.MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                    takePic();
                }
                break;

            case Permissions.MY_PERMISSIONS_REQUEST_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                    entryMeta.tagSuggestionsArrList.addAll(Files.getContactSuggestions(this));
                }
                break;

            case Permissions.MY_PERMISSIONS_REQUEST_LOCATION:
                TextView currentLocationTV = (TextView) findViewById(R.id.location);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                    getLocation(true, currentLocationTV);

                } else {
                    getLocation(false, currentLocationTV);
                }
                break;
            case Permissions.MY_PERMISSIONS_REQUEST_MIC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                    saveData();
                    ImageSwitcher audioSwitcher = (ImageSwitcher) findViewById(R.id.audio);
                    if (audio == null)
                        audio = new Audio(context, entryMeta.filename, audioSwitcher);

                }
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case USE_CAMERA_CONST:
                launchingWithinApp = true;
                switch (resultCode){
                    case RESULT_OK:
                        try {
                            ++ entryMeta.numOfPhotos;
                            saveData();
                            makeImageViews(entryMeta.numOfPhotos, Pictures.saveImage(picFromCameraURI, context, entryMeta.filename, entryMeta.numOfPhotos));

                        } catch (IOException e) {
                            Log.e("Taking pic Err", e.toString());
                        }
                        break;

                }
                break;
            case PICK_PHOTO_CONST:
                launchingWithinApp = true;
                switch (resultCode){
                    case RESULT_OK:
                        try {
                            Uri selectedImage = data.getData();
                            ++ entryMeta.numOfPhotos;
                            saveData();
                            makeImageViews(entryMeta.numOfPhotos, Pictures.saveImage(selectedImage, context, entryMeta.filename, entryMeta.numOfPhotos));
                        } catch (IOException e) {
                            Log.e("Pic file not found", e.toString());
                        }
                        break;

                }
                break;
        }
    }




}
