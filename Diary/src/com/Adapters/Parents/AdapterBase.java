package com.Adapters.Parents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import com.UtilityClasses.CustomAttributes;
import java.util.Locale;

/*
    Created by CaptainSaveAHoe on 7/22/17.
 */

/*
    A base class for any adapter for a RecyclerView.
 */
  public abstract class AdapterBase<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
      protected CustomAttributes userUIPreferences;
      protected Context context;
      protected Locale locale;

     protected AdapterBase(Context context, CustomAttributes userUIPreferences){
        this.context = context;
        this.userUIPreferences = userUIPreferences;
         locale = Locale.getDefault();
    }
    protected AdapterBase(Context context){
        this.context = context;
    }
}
