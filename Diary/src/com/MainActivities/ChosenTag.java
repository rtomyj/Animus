package com.MainActivities;

import android.content.Intent;
import android.os.Bundle;
import com.BaseClasses.Entries;
import com.rtomyj.Diary.R;

public class ChosenTag extends Entries {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentActivityIdentifier = CHOSEN_TAG	;

		Intent activityIntent = this.getIntent();
		chosenTagName = activityIntent.getStringExtra("TAG_NAME");
		adapterInitSize = activityIntent.getByteExtra("TAG_NUM", (byte) 5);

		super.onCreate(savedInstanceState);
		//super.setActionBarTitle(getResources().getString(R.string.CURRENT_TAG) + chosenTagName);

	}

}
