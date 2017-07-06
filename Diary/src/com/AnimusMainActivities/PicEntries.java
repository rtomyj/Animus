package com.AnimusMainActivities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.MenuItem;

import com.AnimusAdapters.PicturesAdapter;
import com.rtomyj.Diary.R;
import java.util.ArrayList;

public class PicEntries extends MainActivity<PicturesAdapter> {

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
		if (activityAdapter == null) {
			activityAdapter = new PicturesAdapter(this, userUIPreferences);
		}

		actionBar.setSubtitle("Total: " + activityAdapter.getTotalPicCount());

		int adapterSize = activityAdapter.getItemCount();

		if (adapterSize > 0) {
			showList();
			if (recyclerView.getAdapter() == null) { // if there is no adapter binded to recyclerView then entriesAdapter is binded to it.

				Log.e("adapter added", "stuff");
				recyclerView.setHasFixedSize(true);  // children will not impact the redrawing of recyclerView; good for performance.
				recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
				recyclerView.setAdapter(activityAdapter);
			}

		/*
			picturesRV.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
				                        int position, long id) {
					Intent i = new Intent(context, ChosenFile.class);
					i.putExtra("FILENAME", (pictureFilesArrayList.get(position) + ".txt"));
					i.putExtra("FILESARRAY", pictureFilesArrayList);
					i.putExtra("POSITION", position);
					pass_codeCheck = false;
					startActivityForResult(i, 2);

				}
			});
		*/
		}
		else if (adapterSize == 0){
			showWelcome();
		}
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
		// creates new Toolbar object and sets it as the activities action bar

		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {		// blocks orientation change on really small screens
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}


		if (savedInstanceState != null){
			ArrayList<String> picArrList = savedInstanceState.getStringArrayList("PIC_ARR_LIST");

			if(picArrList != null) {
				activityAdapter = new PicturesAdapter(this, picArrList, userUIPreferences);
				actionBar.setSubtitle("Total: " + activityAdapter.getTotalPicCount());		// sets the total of all pictures saved in app not just the number that shows up in adapter
			}

		}

        //sets theme according to preference before setting the view
		setContentView(R.layout.main_activity_base);
		setupActionBar();

        //changes colors of views for theme
        customizeUI();
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
