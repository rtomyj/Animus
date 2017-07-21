package com.MainActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BaseClasses.Activity_Base;
import com.UtilityClasses.UI;
import com.rtomyj.Diary.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity_Base {
	private EditText passwordET;
	private TextView hintTV;

//	private int attempts = 0;

	private String password;
	private boolean checkPasscode = false;




	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

		checkPasscode = sp.getBoolean("Password", false);
		password = sp.getString("PasswordValue", "0000");


		String themeName = sp.getString("Theme", "Default");

		UI.setTheme(this, themeName);		// sets theme
		int [] colors = UI.getThemeElements(this, themeName);		// gets colors according to theme
		int primaryColor = colors[0], darkThemeTextColor = colors[2], darkThemeBackgroundColor = colors[3], darkThemeForegroundColor = colors[4];

		if (checkPasscode ) { // if the user has password enabled the splash screen will be the password input screen instead of regular splash
			setContentView(R.layout.passcode);

			passwordET = (EditText) findViewById(R.id.password);
			hintTV = (TextView) findViewById(R.id.hint);

			passwordET.setBackgroundColor(primaryColor);
			hintTV.setBackgroundColor(primaryColor);


			if (themeName.contains("Onyx")){		 // checks the theme name and sees if it is an "Onyx" theme, changes colors accordingly
					LinearLayout parentLL = (LinearLayout) findViewById(R.id.parent);
					LinearLayout numberPadLL = (LinearLayout) findViewById(R.id.numberPad);

					parentLL.setBackgroundColor(darkThemeBackgroundColor);
					numberPadLL.setBackgroundColor(darkThemeForegroundColor);
			}

		} else { 		// if no passcode then regular splash layout is used.
			setContentView(R.layout.splash);

			LinearLayout parentLL = (LinearLayout) findViewById(R.id.parent);
			WeakReference<LinearLayout> parentLLWeak = new WeakReference<>(parentLL);

			parentLLWeak.get().setBackgroundColor(primaryColor);

			TextView tv = (TextView) findViewById(R.id.quote);

			if (themeName.contains("Onyx"))
					tv.setTextColor(darkThemeTextColor);


			final Intent domusIntent = new Intent(this, Domus.class);
			domusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


			// runs a delayed task. Entries will launch automatically in 2.5 seconds
			final Timer time = new Timer();
			time.schedule(new TimerTask() {
				public void run() {
					time.cancel();
					startActivity(domusIntent);
				}}, 2500, 1);
			}


		}


	public void number(View v) {
		if (passwordET.getText().toString().length() == 4) {
			hintTV.setText(getResources().getString(R.string.enter_password));
		}
		if (v.getTag().toString().equals("backspace")) {
			StringBuilder tempPassword = new StringBuilder();
			tempPassword.append( passwordET.getText().toString());
			if (tempPassword.length() > 0) {
				tempPassword.deleteCharAt(tempPassword.length() - 1);
				//passwordET.setText(null);
				passwordET.setText(tempPassword);
			}
		} else {
			passwordET.append(v.getTag().toString());
		}

		if (passwordET.getText().toString().length() == 4) {

			if (passwordET.getText().toString()
					.equals(password)) {
				Intent i = new Intent(this, Domus.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			} else {
				hintTV.setText(getResources().getString(R.string.wrong) + "\n" + getResources().getString(R.string.enter_password));
				passwordET.setText(null);
			//	attempts++;
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!checkPasscode) { // if use is not using a passcode, show the regular splash

			TextView quoteTV = (TextView) findViewById(R.id.quote);
			TextView ver_numTV = (TextView) findViewById(R.id.ver_num);

			quoteTV.setTextSize(userUIPreferences.textSize);
			ver_numTV.setTextSize(userUIPreferences.textSize);
		}
	}


}