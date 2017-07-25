package com.MainActivities;

import android.os.Bundle;
import android.view.MenuItem;

import com.rtomyj.Diary.R;

public class FaveEntries extends Entries {

	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		switch(item.getItemId()) {
			case R.id.faves:
				closeNavDrawer();
				break;
			default:
				super.onNavigationItemSelected(item);
		}
		return true;
	}


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentActivityIdentifier = FAVES;
		super.onCreate(savedInstanceState);


	}


}
