package com.MainActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;

import com.Adapters.PicturesAdapter;
import com.rtomyj.Diary.R;
import java.util.ArrayList;

public class PicEntries extends MainActivity<PicturesAdapter, GridLayoutManager> {

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// changes the color of UI elements according to the them the user has selected.
    public void customizeUI(){
		super.customizeUI();
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList("PIC_ARR_LIST", activityAdapter.getPicturesArrList());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null){
			ArrayList<String> picArrList = savedInstanceState.getStringArrayList("PIC_ARR_LIST");

			if(picArrList != null) {
				activityAdapter = new PicturesAdapter(this, picArrList, userUIPreferences);
				actionBar.setSubtitle("Total: " + activityAdapter.getTotalPicCount());		// sets the total of all pictures saved in app not just the number that shows up in adapter
			}

		}else{
			if (activityAdapter == null) {
				activityAdapter = new PicturesAdapter(this, userUIPreferences);
			}
		}

		adapterSize = activityAdapter.getItemCount();
		setupActionBar();
		setInfoToActionBar(PIC_ENTRIES);
        customizeUI();			//changes colors of views for theme
	}


	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		switch (item.getItemId()){
			case R.id.pictures:
				break;
			default:
				super.onNavigationItemSelected(item);
		}
		return true;
	}


}
