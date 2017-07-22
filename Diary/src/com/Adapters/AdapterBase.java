package com.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.UtilityClasses.CustomAttributes;

import java.util.Locale;

/*
    Created by CaptainSaveAHoe on 7/22/17.
 */


 abstract class AdapterBase<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

     CustomAttributes userUIPreferences;
     Context context;
     Locale locale;


     AdapterBase(Context context, CustomAttributes userUIPreferences){
        this.context = context;
        this.userUIPreferences = userUIPreferences;
         locale = Locale.getDefault();
    }
}
