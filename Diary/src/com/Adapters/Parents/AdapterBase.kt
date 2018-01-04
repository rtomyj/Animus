package com.Adapters.Parents

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.UtilityClasses.CustomAttributes
import java.util.Locale

/*
    Created by CaptainSaveAHoe on 7/22/17.
 */

/*
    A base class for any adapter for a RecyclerView.
 */
abstract class AdapterBase<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T> {
    protected lateinit var userUIPreferences: CustomAttributes
    protected var context: Context
    protected lateinit var locale: Locale

    protected constructor(context: Context, userUIPreferences: CustomAttributes) {
        this.context = context
        this.userUIPreferences = userUIPreferences
        locale = Locale.getDefault()
    }

    protected constructor(context: Context) {
        this.context = context
    }
}
