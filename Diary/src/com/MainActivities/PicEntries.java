package com.MainActivities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;

import com.Adapters.PicturesAdapter;
import com.BaseClasses.MainActivity;
import com.rtomyj.Diary.R;
import java.util.ArrayList;

public class PicEntries extends MainActivity<PicturesAdapter, GridLayoutManager> {

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList("PIC_ARR_LIST", activityAdapter.getPicturesArrList());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentActivityIdentifier = PIC_ENTRIES;
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null){
			ArrayList<String> picArrList = savedInstanceState.getStringArrayList("PIC_ARR_LIST");

			if(picArrList != null) {
				activityAdapter = new PicturesAdapter(this, picArrList, userUIPreferences);
				setActionBarSubTitle("Total: " + activityAdapter.getTotalPicCount());		// sets the total of all pictures saved in app not just the number that shows up in adapter
			}

		}

		if (activityAdapter == null) {
			activityAdapter = new PicturesAdapter(this, userUIPreferences);
		}


		setupViews();
	}


	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		switch (item.getItemId()){
			case R.id.pictures:
				closeNavDrawer();
				break;
			default:
				super.onNavigationItemSelected(item);
		}
		return true;
	}


}
