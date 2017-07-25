package com.Adapters.MainAdapters;

import android.content.Context;
import android.util.Log;

import com.Adapters.Parents.EntriesBaseAdapter;
import com.UtilityClasses.XML;
import com.UtilityClasses.CustomAttributes;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

/*
     Created by CaptainSaveAHoe on 7/12/17.
 */


/*
        Adapter the populates a list with entries that are the users favorites.

        The parent class 'EntriesBaseAdapter.java' does most of the work.
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
        //short faveNum = XML.getFaveNum(context.getFilesDir());

        sortedFilesArrList = new ArrayList<>();
        tag1ArrList = new ArrayList<>();
        tag2ArrList = new ArrayList<>();
        tag3ArrList = new ArrayList<>();
        favArrList = new ArrayList<>();
        try {
            XML.getFaveEntries(sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, context.getFilesDir());
        }catch (ParserConfigurationException | SAXException | IOException exception){
            Log.e("Err parsing faves ", exception.toString());
        }

    }
}
