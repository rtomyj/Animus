package com.Settings;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.ProgressBar;

import com.SubActivities.Passcode;
import com.Features.PasswordSet;
import com.rtomyj.Diary.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class MainSettingsFragment extends PreferenceFragment {
	private static final byte REMOVE_PASSWORD = 0, ADD_PASSWORD = 1;
	private SwitchPreference password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_settings);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());


        final EditTextPreference relationship = (EditTextPreference) findPreference("Relationship");
        final EditTextPreference  job = (EditTextPreference) findPreference("Occupation");
		final ListPreference resetTags = (ListPreference) findPreference("Reset");
		final ListPreference themePref = (ListPreference) findPreference("Theme");


		final Activity parent = this.getActivity();

		themePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				android.os.Handler delayedAction = new android.os.Handler();
				WeakReference<Handler> delayedActionWeak = new WeakReference<>(delayedAction);
				delayedActionWeak.get().postDelayed(new Runnable() {
					@Override
					public void run() {
						sp.edit().putBoolean("THEME_CHANGED", true).apply();
						parent.recreate();
					}
				}, 500);
				return true;
			}
		});


        password = (SwitchPreference) findPreference("Password");
        final Context c = this.getActivity();
        
        
        relationship.setSummary(sp.getString("Relationship", c.getResources().getString(R.string.single)));
        job.setSummary(sp.getString("Occupation", c.getResources().getString(R.string.unemployed)));

		resetTags.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (newValue.toString().equals("Reset Tags")){

					File xml = new File(c.getFilesDir(), "Tags.xml");
					xml.delete();


						BufferedReader reader;
						BufferedWriter writer;
						try {
							xml.createNewFile();
							InputStreamReader in = new InputStreamReader(c.getAssets().open(
									"Vars.xml"));

							reader = new BufferedReader(in);
							writer = new BufferedWriter(new FileWriter(xml));

							String line = reader.readLine();
							while (line != null) {
								writer.write(line + "\n");
								line = reader.readLine();
							}
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				else if (newValue.toString().equals("Reset All Files")){


					AlertDialog.Builder builder = new AlertDialog.Builder(c);
					builder.setTitle("Delete all data?");
					builder.setMessage("This action cannot be undone.");

					builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AlertDialog builder = new AlertDialog.Builder(c).create();
							ProgressBar deleteProgress = new ProgressBar(c, null, android.R.attr.progressBarStyleHorizontal);
                            deleteProgress.setPadding(30,0,30, 0);

							deleteProgress.setProgress(0);
							builder.setTitle("Deleting...");
							builder.setMessage("Progress");
							builder.setView(deleteProgress);
                            builder.setCancelable(false);
							builder.show();


							File xml;
							ArrayList<String> files = new ArrayList();
							files.addAll(Arrays.asList(c.fileList()));

							for (int i = 0; i < files.size(); i++){
								if (files.get(i) != "gaClientID") {
									xml = new File(c.getFilesDir(), files.get(i));
									xml.delete();
								}

							}
							deleteProgress.setProgress(97);

							// new Files.xml
							xml = new File(c.getFilesDir(), "Files.xml");
								// Toast.makeText(getApplicationContext(), "File created",
								// Toast.LENGTH_LONG).show();

								BufferedReader reader;
								BufferedWriter writer;


								try {
									xml.createNewFile();
									InputStreamReader in = new InputStreamReader(c.getAssets().open(
											"Files.xml"));

									reader = new BufferedReader(in);
									writer = new BufferedWriter(new FileWriter(xml));

									String line = reader.readLine();
									while (line != null) {
										writer.write(line + "\n");
										line = reader.readLine();
									}
									writer.close();
								} catch (IOException e) {
									e.printStackTrace();
								}


							deleteProgress.setProgress(98);
							// new Tags.xml
							xml = new File(c.getFilesDir(), "Tags.xml");

							try {
								xml.createNewFile();
								InputStreamReader in = new InputStreamReader(c.getAssets().open(
										"Vars.xml"));

								reader = new BufferedReader(in);
								writer = new BufferedWriter(new FileWriter(xml));

								String line = reader.readLine();
								while (line != null) {
									writer.write(line + "\n");
									line = reader.readLine();
								}
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}



							deleteProgress.setProgress(99);
							builder.cancel();


						}


					});
					builder.setNegativeButton("Cancel", null);
					builder.create().show();




				}

				return false;
			}
		});

		final OnPreferenceClickListener _moveCursorToEndClickListener =
        	    new OnPreferenceClickListener()
        	    {
        	        @Override
        	        public boolean onPreferenceClick(Preference preference)
        	        {
        	            EditTextPreference editPref = (EditTextPreference)preference;
        	            editPref.getEditText().setSelection( editPref.getText().length());
        	            return true;
        	        }
        	    };
        	    
        job.setOnPreferenceClickListener(_moveCursorToEndClickListener);
        relationship.setOnPreferenceClickListener(_moveCursorToEndClickListener);
        
        relationship.setOnPreferenceChangeListener(new OnPreferenceChangeListener() 
        {   
        	
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) 
            {
            	relationship.setSummary(newValue.toString());
				return true;
            }

        });
    
	job.setOnPreferenceChangeListener(new OnPreferenceChangeListener() 
    {   
    	
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) 
        {
        	job.setSummary(newValue.toString());
			return true;
        }

    });
	
	
	password.setOnPreferenceChangeListener(new OnPreferenceChangeListener() 
    {   
    	
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) 
        {
        	if (! sp.getBoolean("Password", false)){
        		Intent i = new Intent(c, PasswordSet.class);
    			startActivityForResult(i, REMOVE_PASSWORD);
        	}
        	else{
        		Intent i = new Intent(c, Passcode.class);
        		i.putExtra("Save", true);
    			startActivityForResult(i, 2);
        	}
        		
			return true;
        }

    });
}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REMOVE_PASSWORD:
				if (resultCode == Activity.RESULT_CANCELED) {
					password.setChecked(false);
				}
				break;
			case ADD_PASSWORD:
				if (resultCode == Activity.RESULT_CANCELED) {
					password.setChecked(true);
				}
				break;
		}
	}
	
	
}