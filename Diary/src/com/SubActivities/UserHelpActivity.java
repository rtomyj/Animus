package com.SubActivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.Adapters.Settings.HelpTopicAdapter;
import com.rtomyj.Diary.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class UserHelpActivity extends AppCompatActivity{

	ListView listview;
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sp.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (sp.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (sp.getString("Theme", "Default").equals("Material")){
			super.setTheme(R.style.Material);
		}
		else if (sp.getString("Theme", "Default").equals("Material 2")) {
			super.setTheme(R.style.Material2);
		}
		else if(sp.getString("Theme", "Default").equals("Material 3")){
			super.setTheme(R.style.Material3);
		}
		else if(sp.getString("Theme", "Default").equals("Material 4")){
			super.setTheme(R.style.Material4);
		}
		setContentView(R.layout.help);


		Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
		WeakReference<Toolbar> actionbarWeak = new WeakReference<>(actionbar);
		setSupportActionBar(actionbarWeak.get());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		

	}
	@Override
	protected void onStart() {
		super.onStart();
		
		listview = (ListView) findViewById(R.id.help_list);	    	

		
		 ArrayList<String> topics = new ArrayList();
		 topics.addAll(Arrays.asList(getResources().getStringArray(R.array.privacy_topics)));
		
	    final HelpTopicAdapter adapter = new HelpTopicAdapter(this, topics);
	    listview.setAdapter(adapter);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);


		if (sp.getString("Theme", "Default").contains("Onyx")) {
			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);

			parent.setBackgroundColor(getResources()
					.getColor(R.color.UIDarkOnyx));
		}
	}
	
}
