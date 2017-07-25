package com.Adapters.MainActivites;

import android.content.Context;
import android.util.Log;

import com.Adapters.Parents.EntriesBaseAdapter;
import com.UtilityClasses.XML;
import com.UtilityClasses.CustomAttributes;

import java.util.ArrayList;
import java.util.Collections;

/*
     Created by CaptainSaveAHoe on 7/12/17.
 */

public class FaveAdapter extends EntriesBaseAdapter {


    public FaveAdapter(Context context, CustomAttributes userUIPreferences){
        super(context, userUIPreferences);
        loadFaveEntries();
    }

    public FaveAdapter(Context context, ArrayList<String> sortedFilesArrList, ArrayList<String> tag1ArrList, ArrayList<String> tag2ArrList, ArrayList<String> tag3ArrList, ArrayList<Boolean> favArrList,
                       CustomAttributes userUIPreferences) {
        super(context, sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, userUIPreferences);
    }


    private void loadFaveEntries(){

            sortedFilesArrList = new ArrayList<>();
            tag1ArrList = new ArrayList<>();
            tag2ArrList = new ArrayList<>();
            tag3ArrList = new ArrayList<>();
            favArrList = new ArrayList<>();
            XML.getFaveEntries(sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, context.getFilesDir());

    }
}
