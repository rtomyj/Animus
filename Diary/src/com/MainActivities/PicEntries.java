package com.MainActivities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;

import com.Adapters.MainAdapters.PicturesAdapter;
import com.MainActivities.BaseClasses.MainActivity;
import com.rtomyj.Diary.R;
import java.util.ArrayList;

public class PicEntries extends MainActivity<PicturesAdapter, GridLayoutManager> {

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList("PIC_ARR_LIST", activityAdapter.getPicturesArrList());

		int size = activityAdapter.getItemCount(), index = 0;
		long[] lastModifiedArr = new long[size];
		for (Long lastModified : activityAdapter.getLastModifiedArrList()){
			lastModifiedArr[index] = lastModified;
			index++;
		}
		outState.putLongArray("MODIFIED_LONG_FOR_PICS", lastModifiedArr);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentActivityIdentifier = PIC_ENTRIES;
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null){
			ArrayList<String> picArrList = savedInstanceState.getStringArrayList("PIC_ARR_LIST");

			int size = picArrList.size(),  index = 0;
			ArrayList<Long> lastModifiedArrList = new ArrayList<>(size);
			long[] lastModifiedArr = savedInstanceState.getLongArray("MODIFIED_LONG_FOR_PICS");
			for (long lastModified: lastModifiedArr){
				lastModifiedArrList.add(lastModified);
				index ++;
			}

			activityAdapter = new PicturesAdapter(this, picArrList, lastModifiedArrList, userUIPreferences);

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
