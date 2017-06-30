package com.UtilityClasses;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Debug extends AppCompatActivity {
	private TextView filesOutput;
	private TextView xmlOutput;
	private ArrayList<String> a = new ArrayList<>();
	private String temp;

	private BufferedReader inputBR;

	
	void setTheme(SharedPreferences sp){
		if (sp.getString("Theme", "Default").equals("Onyx")) {
			super.setTheme(R.style.Onyx);
		}
		else if (sp.getString("Theme", "Default").equals("Material")){
			super.setTheme(R.style.Material);
		}
		else if (sp.getString("Theme", "Default").equals("Material 2")){
			super.setTheme(R.style.Material2);
		}
		else if(sp.getString("Theme", "Default").equals("Material 3")){
			super.setTheme(R.style.Material3);
		}else if(sp.getString("Theme", "Default").equals("Material 4")){
			super.setTheme(R.style.Material4);
		}
	}
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sp);
		
		setContentView(R.layout.debug);

		filesOutput = (TextView) findViewById(R.id.scanFiles);
		filesOutput.setText(null);
		xmlOutput = (TextView) findViewById(R.id.xml);

		xmlOutput.setText("Files.xml\n\n");


		a.addAll(Arrays.asList(this.fileList()));
		
		
		for (int i = 0; i < a.size(); i++) {
			filesOutput.setText(filesOutput.getText().toString() + a.get(i) + "\n");
		}
		try {

			inputBR = new BufferedReader(new FileReader(new File(
					getFilesDir(), "Files.xml")));
			temp = "temp";
			while (temp != null) {

				temp = inputBR.readLine();
				if (temp != null)
				xmlOutput.setText(xmlOutput.getText().toString() + temp + "\n");
			}
			inputBR.close();



		} catch (Exception e) {

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
}
