package com.AnimusSubActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import java.util.TimerTask;

public class Passcode extends Activity {
	EditText password;
	TextView hint;
	int attempts = 0;
	private boolean save = false;
	private SharedPreferences sp;
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		password = null;
		hint = null;
		sp = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = PreferenceManager
		.getDefaultSharedPreferences(this);
		// if (sp.getBoolean("Passcode", false) == false) {

		// Intent i = new Intent("com.diary.favs");
		// startActivity(i);
		// } else
		
		if (sp.getString("Theme", "Default").contains("Onyx")) {
			super.setTheme(R.style.Onyx);
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
		setContentView(R.layout.passcode);

		try {
			getActionBar().hide();
		}catch(NullPointerException noActionBar){

		}

		password = (EditText) findViewById(R.id.password);
		hint = (TextView) findViewById(R.id.hint);
		if (sp.getString("Theme", "Default").equals("Material")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialPink));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialPink));
		}
		else if (sp.getString("Theme", "Default").equals("Material 2")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialBlue));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialBlue));
		}
		else if (sp.getString("Theme", "Default").equals("Material 3")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialBrown));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialBrown));
		}
		else if (sp.getString("Theme", "Default").equals("Material 4")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialOrange));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialOrange));
		}
		else if (sp.getString("Theme", "Default").contains("Onyx")){
			password.setBackgroundColor(getResources().getColor(R.color.DarkWhite_ish));
			hint.setBackgroundColor(getResources().getColor(R.color.DarkWhite_ish));

			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
			LinearLayout numberPad = (LinearLayout) findViewById(R.id.numberPad);
			parent.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));

			numberPad.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));
		}
		
		try{
		save = getIntent().getExtras().getBoolean("Save");
		}catch(Exception e){
			
		}

	}

	public void number(View v) {

		if (password.getText().toString().length() == 4) {
			hint.setText(getResources().getString(R.string.enter_password));
			hint.setTextColor(getResources().getColor(
					R.color.Non_Important_Text));
			password.setTextColor(getResources().getColor(
					R.color.Non_Important_Text));
		}
		if (v.getTag().toString().equals("backspace")) {
			String temp = password.getText().toString();
			if (temp.length() > 0) {
				temp = temp.substring(0, temp.length() - 1);
				password.setText(null);
				password.append(temp);
			}
		} else {
			password.append(v.getTag().toString());
		}

		if (password.getText().toString().length() == 4) {

			if (password.getText().toString().equals(sp.getString("PasswordValue", "0000")) == true) {
				Intent returnIntent = new Intent();
				this.setResult(RESULT_OK, returnIntent);
				finish();
			} else {
				password.setText(null);
				hint.setText(getResources().getString(R.string.wrong));
				password.setTextColor(getResources().getColor(R.color.UIRed));
				hint.setTextColor(getResources().getColor(R.color.Peach));
			}
		}
	}

	private TimerTask reset() {
		// TODO Auto-generated method stub
		attempts = 0;
		return null;
	}

	@Override
	public void onBackPressed() {
		if (save ==true){
			finish();
		}
		else{
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		}
	}
}
