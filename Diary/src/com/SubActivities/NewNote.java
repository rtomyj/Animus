package com.SubActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.UtilityClasses.AnimusMiscMethods;
import com.rtomyj.Diary.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class NewNote extends AppCompatActivity {

	private EditText note;
	private String filename;
	private File f;

	private SharedPreferences sp;

	private boolean terminate = false;
	private boolean passcodeOn = false;
	private boolean passcodeCheck = true;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				passcodeCheck = false;
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
				passcodeCheck = true;
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		passcodeOn = sp.getBoolean("Password", false);
		if (passcodeOn == true) {
			if (passcodeCheck == true) {
				Intent i = new Intent(this, Passcode.class);
				startActivityForResult(i, 1);
			}
			passcodeCheck = true;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (terminate == true) {
			try {
				save();
			} catch (Exception e) {

			}
		}
	}

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sp.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (sp.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (sp.getString("Theme", "Default").equals("Material")) {
			super.setTheme(R.style.Material);
		}
			else if (sp.getString("Theme", "Default").equals("Material 2")) {
				super.setTheme(R.style.Material2);
			}
				else if (sp.getString("Theme", "Default").equals("Material 3")) {
					super.setTheme(R.style.Material3);
				}
					else if (sp.getString("Theme", "Default").equals("Material 4")) {
						super.setTheme(R.style.Material4);
					}
		setContentView(R.layout.note);

		Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
		WeakReference<Toolbar> actionbarWeak = new WeakReference<>(actionbar);
		setSupportActionBar(actionbarWeak.get());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		note = (EditText) findViewById(R.id.note);

		String fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

		if (fontStyle.contains("DEFAULT") != true) {
			Typeface tf = Typeface.createFromAsset(this.getAssets(),
					"fonts/" + fontStyle);
			WeakReference<Typeface> tfWeak = new WeakReference<>(tf);
			note.setTypeface(tfWeak.get());
		}

		if (sp.getString("Theme", "Default").contains("Onyx")) {
			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
			WeakReference<LinearLayout> parentWeak = new WeakReference<>(parent);
			note.setBackgroundColor(this.getResources().getColor(
					R.color.UIDarkGray));
			note.setTextColor(getResources().getColor(R.color.UIDarkText));
			parentWeak.get().setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));

		}

		float textSize = Float.parseFloat(sp.getString("TextSize", "14"));
		note.setTextSize(textSize);
	}

	private void save() throws FileNotFoundException {
		if (note.getText().toString().trim().equals("") == false) {
			
			filename = AnimusMiscMethods.randomizedName();

			f = new File(this.getFilesDir(), filename + ".note");
			sp.edit().putString("NEWNOTE", filename + ".note").apply();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			DataOutputStream dos  = new DataOutputStream(openFileOutput(filename
					+ ".note", Context.MODE_PRIVATE));
			WeakReference<DataOutputStream> dosWeak = new WeakReference<>(dos);
			try {
				dosWeak.get().writeUTF(note.getText().toString().trim());
				dosWeak.get().flush();
				dosWeak.get().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		terminate = true;
	}

	@Override
	public boolean onNavigateUp() {
		terminate = true;
		return super.onNavigateUp();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.note, menu);

		return true;
	}

	public void save(MenuItem m) {
		terminate = true;
		this.finish();

	}

}
