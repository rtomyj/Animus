package com.Settings;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

public class CustomPreference extends SwitchPreference {
    public CustomPreference(Context context) {
        super(context);
    }

    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }    
}
