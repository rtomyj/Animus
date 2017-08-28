package com.EntryActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.UtilityClasses.Audio;
import com.UtilityClasses.MiscMethods;
import com.rtomyj.Diary.R;

public class NewEntry extends Entry {


    @Override
    protected void onCreate(Bundle previousInstance) {
        super.onCreate(previousInstance);           // first puts content view then calls the parent super method
        setContentView(R.layout.new_entry_layout);
        animateDateAndMarkdown();

    }



    @Override
    protected void onStart() {
        super.onStart();

        baseMeta();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ((entryMeta.numOfPhotos > 0 || (entryTextET.getText().toString().trim().length() > 0)  || audio!= null  || ! entryMeta.filename.equals("") || entryMeta.isSaved ) && ! entryMeta.entryDeleted) {
            if (onPauseNotTakingPic) {
                saveData();
            }
        }

    }



    public void emailButtonClicked(View mi) {
        EditText title = (EditText) findViewById(R.id.title_of_entry);
        onPauseNotTakingPic = false;

        MiscMethods.email(title.toString(), entryTextET.toString(), "", context);
    }




}
