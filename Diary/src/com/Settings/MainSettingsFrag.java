package com.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rtomyj.Diary.R;

public class MainSettingsFrag extends AppCompatActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  final SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
		Toolbar actionBar = new Toolbar(this);

		this.setSupportActionBar(actionBar);


		if (sp.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (sp.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (sp.getString("Theme", "Default").equals("Material")){
			super.setTheme(R.style.Material);
		}
		else if (sp.getString("Theme", "Default").equals("Material 2")){
			super.setTheme(R.style.Material2);
		}
		else if (sp.getString("Theme", "Default").equals("Material 3")){
			super.setTheme(R.style.Material3);
		}
		else if (sp.getString("Theme", "Default").equals("Material 4")){
			super.setTheme(R.style.Material4);
		}

		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainSettingsActivity()).commit();

		
	}




}
