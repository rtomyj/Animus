package com.Adapters.MainActivites;

import android.content.Context;

import com.Adapters.Parents.EntriesBaseAdapter;
import com.MainActivities.Domus;
import com.UtilityClasses.Files;
import com.UtilityClasses.XML;
import com.UtilityClasses.CustomAttributes;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


/*
     Created by CaptainSaveAHoe on 7/12/17.
 */

/*
    Adapter for the Activity "Domus"

    It displays all .txt files in a list in the order last modified (descending). Its parent class handles most of the work.
 */

public class DomusAdapter extends EntriesBaseAdapter {

    public DomusAdapter(Context context, CustomAttributes userUIPreferences){
        super(context, userUIPreferences);
        loadAllEntries();
    }

    public DomusAdapter(Context context, ArrayList<String> sortedFilesArrList, ArrayList<String> tag1ArrList, ArrayList<String> tag2ArrList, ArrayList<String> tag3ArrList,
                        ArrayList<Boolean> favArrList, CustomAttributes userUIPreferences) {

        super(context, sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, userUIPreferences);
    }


    private void loadAllEntries(){
        final ArrayList<File> filesArrayList = new ArrayList<>(Files.getAllFilesWithExtension(context.getFilesDir(), ".txt"));

        initSize = filesArrayList.size();
        sortedFilesArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        tag1ArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        tag2ArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        tag3ArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        favArrList = new ArrayList<>(Collections.nCopies(initSize, false));

        new Thread(new Runnable() {
            @Override
            public void run() {
                int currentIndex = 0;
                for (File list : filesArrayList) {
                    sortedFilesArrList.set(currentIndex, list.getName());
                    currentIndex++;
                }
                filesArrayList.clear();

                XML.getEntriesAdapterInfo(sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, context.getFilesDir());
            }
        }).start();

        if (initSize < MAX_CACHE_SIZE && initSize != 0)
            setCacheSize(initSize);
        else if (initSize == 0)
            setCacheSize(1);
    }


}
