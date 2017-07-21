package com.Settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.BaseClasses.Activity_Base;

public class MainSettingsActivity extends Activity_Base{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Toolbar actionBar = new Toolbar(this);
		this.setSupportActionBar(actionBar);


		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettingsFragment()).commit();
		
	}




}
