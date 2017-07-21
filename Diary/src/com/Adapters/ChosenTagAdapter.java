package com.Adapters;

import android.content.Context;

import com.UtilityClasses.XML;
import com.UtilityClasses.CustomAttributes;

import java.util.ArrayList;
import java.util.Collections;

/*
     Created by MacMini on 7/19/17.
 */

public class ChosenTagAdapter extends EntriesAdapter {

    public ChosenTagAdapter(Context context, CustomAttributes userUIPreferences, String tagName, int initSize){
        super(context, userUIPreferences);
        loadEntriesWithChosenTag(tagName, initSize);
    }

    public ChosenTagAdapter(Context context, ArrayList<String> sortedFilesArrList, ArrayList<String> tag1ArrList, ArrayList<String> tag2ArrList, ArrayList<String> tag3ArrList, ArrayList<Boolean> favArrList,
                            CustomAttributes userUIPreferences) {
        super(context, sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, userUIPreferences);
    }

    private void loadEntriesWithChosenTag(String tagName, int initSize){
        sortedFilesArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        tag1ArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        tag2ArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        tag3ArrList = new ArrayList<>(Collections.nCopies(initSize, ""));
        favArrList = new ArrayList<>(Collections.nCopies(initSize, false));


        XML.getChosenTagEntries(sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, context.getFilesDir(), tagName);
    }
}
