package com.Settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.rtomyj.Diary.R;

public class NotifSettingsFrag extends PreferenceFragment{
	//TimePicker notifTime;
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		
		addPreferencesFromResource(R.xml.notifications_settings);
		NotifTimePicker test = new NotifTimePicker(getActivity(), null);
		PreferenceCategory t = (PreferenceCategory) findPreference("category");
		test.setOrder(0);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String temp = sp.getString("NotifTime", "7:00");
        
        //notifTime = (TimePicker) findViewById(R.id.notif_time);
        test.setSummary(temp);
		t.addPreference(test);
		
	}


}