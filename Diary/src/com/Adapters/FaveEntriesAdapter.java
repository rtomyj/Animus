package com.Adapters;

import android.content.Context;
import android.util.Log;

import com.UtilityClasses.AnimusXML;
import com.UtilityClasses.CustomAttributes;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by CaptainSaveAHoe on 7/12/17.
 */

public class FaveEntriesAdapter extends EntriesAdapter {


    public FaveEntriesAdapter(Context context, CustomAttributes userUIPreferences){
        super(context, userUIPreferences);
        loadFaveEntries();
    }

    public FaveEntriesAdapter (Context context, ArrayList<String> sortedFilesArrList, ArrayList<String> tag1ArrList, ArrayList<String> tag2ArrList, ArrayList<String> tag3ArrList, ArrayList<Boolean> favArrList,
                              CustomAttributes userUIPreferences) {
        super(context, sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, userUIPreferences);
    }


    private void loadFaveEntries(){

        try {
            short faveNum = AnimusXML.getFaveNum(context.getFilesDir());

            sortedFilesArrList = new ArrayList<>(Collections.nCopies(faveNum, ""));
            tag1ArrList = new ArrayList<>(Collections.nCopies(faveNum, ""));
            tag2ArrList = new ArrayList<>(Collections.nCopies(faveNum, ""));
            tag3ArrList = new ArrayList<>(Collections.nCopies(faveNum, ""));
            favArrList = new ArrayList<>(Collections.nCopies(faveNum, true));
            AnimusXML.getFaveEntries(sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, context.getFilesDir());
        }catch (IndexOutOfBoundsException exception){
            Log.e("Error parsing xml", exception.toString());
        }
    }
}
