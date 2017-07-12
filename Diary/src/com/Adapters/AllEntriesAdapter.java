package com.Adapters;

import android.content.Context;

import com.UtilityClasses.AnimusFiles;
import com.UtilityClasses.AnimusXML;
import com.UtilityClasses.CustomAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by CaptainSaveAHoe on 7/12/17.
 */

public class AllEntriesAdapter extends  EntriesAdapter {

    public AllEntriesAdapter(Context context, CustomAttributes userUIPreferences){
        super(context, userUIPreferences);
        loadAllEntries();
    }

    public AllEntriesAdapter (Context context, ArrayList<String> sortedFilesArrList, ArrayList<String> tag1ArrList, ArrayList<String> tag2ArrList, ArrayList<String> tag3ArrList, ArrayList<Boolean> favArrList,
    CustomAttributes userUIPreferences) {
        super(context, sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, userUIPreferences);
    }


    private void loadAllEntries(){
        ArrayList<File> filesArrayList = new ArrayList<>();
        filesArrayList.addAll(AnimusFiles.getFilesWithExtension(context.getFilesDir(),".txt"));

        Collections.sort(filesArrayList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.valueOf(f2.lastModified()).compareTo(
                        f1.lastModified());
            }
        });

        int sortedFilesArrListSize = filesArrayList.size();
        sortedFilesArrList = new ArrayList<>(sortedFilesArrListSize);
        tag1ArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, ""));
        tag2ArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, ""));
        tag3ArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, ""));
        favArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, false));

        for (File list : filesArrayList)
            sortedFilesArrList.add(list.getName());

        filesArrayList.clear();
        AnimusXML.getEntriesAdapterInfo(sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, context.getFilesDir());
    }
}
