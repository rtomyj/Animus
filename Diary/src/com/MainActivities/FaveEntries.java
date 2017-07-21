package com.MainActivities;

import android.os.Bundle;
import android.view.MenuItem;

import com.BaseClasses.Entries;
import com.rtomyj.Diary.R;

public class FaveEntries extends Entries {

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

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
	protected void onRestart() {
		super.onRestart();

	}


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentActivityIdentifier = FAVES;
		super.onCreate(savedInstanceState);


	}


	@Override
	protected void onStart() {
		super.onStart();

	}


	@Override
	protected void onStop() {
		super.onStop();
	}


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

}
