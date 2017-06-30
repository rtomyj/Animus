package com.AnimusSubActivities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.rtomyj.Diary.R;

/**
 * Created by rtomyj on 11/26/15.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.test);


         int mUIFlag =
                 View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                         ;

    //getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
        this.getActionBar().hide();
    }

    public void next(View v){

    }

    public void previous(View v){

    }
}
