package com.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.Adapters.FontAdapter;
import com.rtomyj.Diary.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FontSelectionActivity extends AppCompatActivity {

	SharedPreferences sp ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		else if(sp.getString("Theme", "Default").equals("Material 3")){
			super.setTheme(R.style.Material3);
		}else if(sp.getString("Theme", "Default").equals("Material 4")){
			super.setTheme(R.style.Material4);
		}
		setContentView(R.layout.fonts);


		Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
		WeakReference<Toolbar> actionbarWeak = new WeakReference<>(actionbar);
		setSupportActionBar(actionbarWeak.get());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	}



	@Override
	protected void onStart() {
		
		ListView listview = (ListView) findViewById(R.id.fonts);

		final ArrayList<String> list = new ArrayList<String>();
		list.add("DEFAULT");
		list.add("Ubuntu-Light");
		list.add("bendable");
		list.add("Mercato-Light");
		list.add("ShareTechMono-Regular");
		list.add("Plain&SimplyFat");
		list.add("MarckScript-Regular");
		list.add("TypeWritersSubstitute");
		list.add("Rothenburg Decorative");
		list.add("data-latin");
		list.add("day_roman");
		list.add("Monoglyceride");
		list.add("always forever");
		list.add("Marker Felt");
		list.add("girlnextdoor");
		
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						 "font style changed", Toast.LENGTH_LONG).show();

				sp.edit().putString("FONTSTYLE", list.get(position)).commit();
			}
		});
		

		ListView lv = (ListView) findViewById(R.id.fonts);
		FontAdapter fa = new FontAdapter(this, list);
		lv.setAdapter(fa);


		if (sp.getString("Theme", "Default").contains("Onyx")) {

			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);

			parent.setBackgroundColor(getResources()
					.getColor(R.color.UIDarkOnyx));


		}


		super.onStart();
	}
	
	
}
