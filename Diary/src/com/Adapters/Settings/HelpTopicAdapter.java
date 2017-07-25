package com.Adapters.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import java.util.ArrayList;


public class HelpTopicAdapter extends ArrayAdapter<String> {
	  private final Context context;
	  private final ArrayList<String> values = new ArrayList();
	  

	  public HelpTopicAdapter(Context context, ArrayList<String> temp) {
	    super(context, R.layout.help_topics, temp);
	    this.context = context;
	    values.addAll(temp);
	  }
	  @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.help_topics, parent, false);
		    TextView topic = rowView.findViewById(R.id.topic);
		    TextView info = rowView.findViewById((R.id.info));

		    topic.setText(values.get(position));
		    SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(context);

			if (sp.getString("Theme", "Default").equals("Onyx B")) {

				LinearLayout p = rowView.findViewById(R.id.parent);

				info.setTextColor(context.getResources().getColor(R.color.UIDarkText));
				p.setBackground(context.getResources().getDrawable(
						R.drawable.onyx_selector));
			}
			else if (sp.getString("Theme", "Default").equals("Onyx P")) {

				LinearLayout p = rowView.findViewById(R.id.parent);

				info.setTextColor(context.getResources().getColor(R.color.UIDarkText));
				p.setBackground(context.getResources().getDrawable(
						R.drawable.onyx_selector));
			}
		    if(position == 0){    
		    	info.setText(context.getResources().getString(R.string.privacy_explanation));
		    	
		    }
		    
		    if(position == 1){
		    	info.setText(context.getResources().getString(R.string.tags_explanation));
		    	
		    }
		    if(position == 2){
		    	info.setText(context.getResources().getString(R.string.help_usage));		    
		    	
		    }
		    if(position == 3){
		    	info.setText(context.getResources().getString(R.string.help_notes_and_entries));		    
		    	
		    }


		   
		    return rowView;
		  }
		} 
