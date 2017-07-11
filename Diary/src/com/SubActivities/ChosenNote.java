package com.SubActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.rtomyj.Diary.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ChosenNote extends AppCompatActivity{

	private EditText noteET;
	private String filename;
	private SharedPreferences sp;

	private boolean passcodeOn = false;
	private boolean passcodeCheck = true;

	private boolean saveBool = true;

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
				startActivityForResult(new Intent(this, Passcode.class), 1);
			}
			passcodeCheck = true;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();


		if (noteET == null){
		noteET = (EditText) findViewById(R.id.note);
		
		try {
			DataInputStream in = new DataInputStream(openFileInput(filename));
			WeakReference<DataInputStream> inWeak = new WeakReference<>(in);

			noteET.append(inWeak.get().readUTF() + " ");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		if ((sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf")
				.contains("DEFAULT") != true) {
			
			noteET.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/"
					+ (sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf")));

		}

			customizeUI();
		noteET.setTextSize(Float.parseFloat(sp.getString("TextSize", "14")));
		
		}
		
		
	}

	private void customizeUI(){

		if (sp.getString("Theme", "Default").contains("Onyx")) {
			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
			WeakReference<LinearLayout> parentWeak = new WeakReference<>(parent);
			noteET.setBackgroundColor(this.getResources().getColor(
					R.color.UIDarkGray));
			noteET.setTextColor(getResources().getColor(R.color.UIDarkText));
			parentWeak.get().setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			save();
		} catch (Exception e) {

		}

	}

	private void setTheme() {
		if (sp.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (sp.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		} else if (sp.getString("Theme", "Default").equals("Material")) {
			super.setTheme(R.style.Material);
		} else if (sp.getString("Theme", "Default").equals("Material 2")) {
			super.setTheme(R.style.Material2);
		} else if (sp.getString("Theme", "Default").equals("Material 3")) {
			super.setTheme(R.style.Material3);
		} else if (sp.getString("Theme", "Default").equals("Material 4")) {
			super.setTheme(R.style.Material4);
		}
	}

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme();

		setContentView(R.layout.note);


		filename = this.getIntent().getExtras().getString("NOTE");
		Toolbar actionbar = (Toolbar) findViewById( R.id.toolbar);
		WeakReference<Toolbar> actionbarWeak = new WeakReference<>(actionbar);
		setSupportActionBar(actionbarWeak.get());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);



	}

	private void save() throws FileNotFoundException {

		if (noteET.getText().toString().trim().equals("")) {
			File file = new File(this.getFilesDir(), filename);
			WeakReference<File> fileWeak = new WeakReference<>(file);
			fileWeak.get().delete();
			this.finish();
		} else {
			if (saveBool == true) {

				sp.edit().putString("NOTESAVED", filename).apply();
				
				DataOutputStream dos = new DataOutputStream(openFileOutput(
						filename, Context.MODE_PRIVATE));
				WeakReference<DataOutputStream> dosWeak = new WeakReference<>(dos);
				try {
					dosWeak.get().writeUTF(noteET.getText().toString().trim());

					//dos.flush();
					dosWeak.get().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void delete(MenuItem v) {
		AlertDialog.Builder adAlert = new AlertDialog.Builder(this);
		WeakReference<AlertDialog.Builder> adAlertWeak = new WeakReference<AlertDialog.Builder>(adAlert);

		adAlertWeak.get().setCancelable(true);
		adAlertWeak.get().setMessage("Current note will be deleted permanently.");
		adAlertWeak.get().setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {


				deleteEverything();

			}
		});
		adAlertWeak.get().setNegativeButton("Cancel", null);
		adAlertWeak.get().setTitle("Delete NewNote?");
		adAlertWeak.get().create();
		adAlertWeak.get().show();


	}
	public void deleteEverything(){
		new File(this.getFilesDir(), filename).delete();
		saveBool = false;


		sp.edit().putString("DELETENOTE", filename).apply();

		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chosen_note, menu);

		return true;
	}

	public void save(MenuItem m) {
		saveBool = true;
		this.finish();

	}
}
