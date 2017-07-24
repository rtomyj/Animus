package com.Adapters;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.UtilityClasses.CustomAttributes;
import com.UtilityClasses.Files;
import com.rtomyj.Diary.R;

import java.io.IOException;


/*
    Created by MacMini on 7/20/17.
 */

 abstract class AdapterSummaryCache<T extends RecyclerView.ViewHolder> extends AdapterBase<T> {

     private int summaryLength;
     static final int MAX_CACHE_SIZE = 15;
     private LruCache<String , String> summaryCache;


     AdapterSummaryCache(Context context, CustomAttributes userUIPreferences){
         super(context, userUIPreferences);
         if (userUIPreferences.numLines < 4) {
         summaryLength = 250;
     } else {
         summaryLength = 400;
     }

         summaryCache = new LruCache<>(MAX_CACHE_SIZE);
     }



    /*
        sets info to the summary TextView. There is a cache to save the summary text once it is read from the corresponding file potentially saving memory or operations or both.
*/
     void setSummary(TextView summaryTV, String identifier){
        String summaryString =  summaryCache.get( identifier);

        if (summaryString == null) {
            try {
                summaryString = Files.getTextFromFile(context, identifier, summaryLength);
            } catch (IOException e) {
                Log.e("Err loading text", e.toString());
                summaryString = "";
            }
            summaryCache.put(identifier, summaryString);

        }else{
            Log.e("Loaded from cache", identifier);
        }

        if (summaryString.isEmpty()) { // if the entry was empty, places a text informing the user and changes the gravity to center.
            summaryTV.setGravity(Gravity.CENTER );
            summaryTV.setText(context.getResources().getString(R.string.text));

        } else { // else sets the first couple of bytes of the entry to the summary view and change the font style if applicable.
            summaryTV.setText(Html.fromHtml(summaryString.replaceAll("\n", "</br>")));
        }
    }

    void setCacheSize(int newSize){
        summaryCache.resize(newSize);
    }

}
