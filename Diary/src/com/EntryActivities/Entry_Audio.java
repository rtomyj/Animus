package com.EntryActivities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageSwitcher;

import com.UtilityClasses.Audio;
import com.UtilityClasses.Permissions;
import com.rtomyj.Diary.R;

import java.io.IOException;

/**
 * Created by MacMini on 8/25/17.
 */

public class Entry_Audio extends Entry_Tagging {
    // recording ui and components
    Audio audio;




    public void recordAudio()  {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Permissions.requestDialog(Permissions.MY_PERMISSIONS_REQUEST_MIC, context);
            }else{
                Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_MIC);
            }
        } else {
            ImageSwitcher audioSwitcher = (ImageSwitcher) findViewById(R.id.audio);
            if (audio == null) {
                Log.e("audio recording", "fiilename is " + entryMeta.filename);
                audio = new Audio(context, entryMeta.filename, audioSwitcher);
            }
        }
    }

    public void messWithAudio(View v){
        if (audio != null)
            audio.messWithAudio();

    }

    public void playAudio(View view) {
        if (audio != null)
            audio.playAudio();
    }

    public void pauseAudio(View view) {
        if (audio != null)
            audio.pauseAudio();
    }

    public void replay(View v) {
        if (audio != null) {
            try {
                audio.replay();
            }catch(IOException io){
                Log.e("Audio file not found", io.toString());

            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        // file variables
        if (audio != null)
            savedInstance.putBoolean("HAS_AUDIO", audio.isRecordingSaved());
        else
            savedInstance.putBoolean("HAS_AUDIO", false);

    }

    public void recordAudio(View v)  {
        saveData();
        recordAudio();
    }
}
