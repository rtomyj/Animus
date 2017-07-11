package com.SubActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import java.util.ArrayList;

public class SideNavAdapter extends ArrayAdapter<String> {

	private Context c;

	private ArrayList<String> navigon = new ArrayList<>();
	private CharSequence activity;

	private Typeface tf ;
	
	private SharedPreferences sp;
	private TextView allScreens = null;
	private TextView amount = null;

	private String textFilesNum;
	private String noteFilesNum;
	private String picFilesNum;
    private String uniqueTagNum;
    private String amountOfFaves;

	public SideNavAdapter(Context c, ArrayList<String> list,
			CharSequence activity , String textFilesNum, String noteFilesNum, String picFilesNum, String uniqueTagNum, String amountOfFaves) {
		super(c, R.layout.side_nav, list);
		this.navigon.addAll(list);
		this.c = c;
		this.activity = activity;
		

		sp = PreferenceManager.getDefaultSharedPreferences(c); 
		
		if (sp.getString("FONTSTYLE", "DEFAULT").equals("DEFAULT") != true) {
			if (tf == null)
			tf = Typeface.createFromAsset(c.getAssets(), "fonts/"
					+ sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf");
		}

		this.textFilesNum = textFilesNum;
		this.noteFilesNum = noteFilesNum;
		this.picFilesNum = picFilesNum;
        this.uniqueTagNum = uniqueTagNum;
        this.amountOfFaves = amountOfFaves;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		
		LayoutInflater inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.side_nav, parent, false);

		allScreens = (TextView) rowView.findViewById(R.id.all_screens);
		amount = (TextView) rowView.findViewById(R.id.amount);
		

			
			
			allScreens.setTypeface(tf);
			amount.setTypeface(tf);




			allScreens.setTextColor(c.getResources().getColor(R.color.Peach));
			amount.setTextColor(c.getResources().getColor(R.color.Peach));

		if (sp.getString("Theme", "Default").equals("Onyx")) {
			LinearLayout l = (LinearLayout) rowView.findViewById(R.id.side_nav);

			l.setBackgroundColor(c.getResources().getColor(
					R.color.Transparent_UIDarkGray));

			allScreens.setTextColor(c.getResources().getColor(R.color.UIDarkText));
			amount.setTextColor(c.getResources().getColor(R.color.UIDarkText));
		}

		

		// tf = Typeface.createFromAsset(c.getAssets(),
		// "fonts/Roboto-Regular.ttf");
		// allScreens.setTypeface(tf);

		allScreens.setText(navigon.get(position));

		if (position == 0) {


			amount.setText(textFilesNum);

			if (activity.equals(c.getResources().getString(R.string.entries))) {
				allScreens.setTypeface(tf, Typeface.BOLD);
				amount.setTypeface(tf, Typeface.BOLD);

			}
		}
		else if (position == 1) {

			amount.setText(noteFilesNum);

			if (activity.equals(navigon.get(position))) {
				allScreens.setTypeface(tf, Typeface.BOLD);
				amount.setTypeface(tf, Typeface.BOLD);

			}
		}
		else if (position == 2) {

			amount.setText(picFilesNum);

			if (activity.equals(navigon.get(position))) {
				allScreens.setTypeface(tf, Typeface.BOLD);
				amount.setTypeface(tf, Typeface.BOLD);
			}
		}
		else if (position == 3) {



			amount.setText(uniqueTagNum);
			if (activity.equals(navigon.get(position))) {
				allScreens.setTypeface(tf, Typeface.BOLD);
				amount.setTypeface(tf, Typeface.BOLD);
			}
		}
		
		
		
		
		
		else if (position == 4) {

			
			amount.setText(amountOfFaves);
			if (activity.equals(navigon.get(position))) {
				allScreens.setTypeface(tf, Typeface.BOLD);
				amount.setTypeface(tf, Typeface.BOLD);
			}
			
			
			

		}
		else if (position == 5) {
		}
		else if (position == 6) {
			amount.setText("");
		}
		
		
		
		return rowView;
	}

    public void changeFaveAmount(int newAmount){
        this.amountOfFaves = Integer.toString(newAmount);
    }

}
