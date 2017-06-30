package com.AnimusAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;

public class TagsAdapter extends ArrayAdapter<String> {
	private Context context;
	private ArrayList<String> tagsArr = new ArrayList();
	private ArrayList<Integer> tagAmountArr = new ArrayList();
	private ArrayList<String> fileNames = new ArrayList<>();
	
	private float textSize;
	
	private Typeface typeface;

	private String fontStyle;
	private SharedPreferences sp;


	// parsing file
	private File entryFile;
	private DataInputStream br;

	public TagsAdapter(Context c, ArrayList<String> temp,
			ArrayList<Integer> temp2, ArrayList<String> fileNames) {
		super(c, R.layout.tags_of_entries, temp);
		this.context = c;
		//this.fileNames = fileNames;

		// this makes two seperate arraylists... waste o ram.
		this.fileNames.addAll(fileNames);
		tagsArr.addAll(temp);
		tagAmountArr.addAll(temp2);


		sp = PreferenceManager.getDefaultSharedPreferences(c);


		fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

		if (fontStyle.contains("DEFAULT") != true)
			typeface = Typeface.createFromAsset(c.getAssets(), "fonts/"
				+ fontStyle);

	}

	
	 
	  private class ViewHolder {
			private TextView tagTV;
			private TextView amountTV;
			private TextView summaryTV;
		  	private CardView parentLL;

			private ViewHolder(View rowView) {


				summaryTV = (TextView) rowView
						.findViewById(R.id.example_of_entry_with_tag);
				tagTV = (TextView) rowView.findViewById(R.id.tag);
				amountTV = (TextView) rowView.findViewById(R.id.number_of_tags);
				parentLL = (CardView) rowView.findViewById(R.id.tag_list);


			
				 textSize = Float.parseFloat(sp.getString("TextSize", "14"));
				if (sp.getString("Theme", "Default").contains("Onyx")) {

					if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
						LinearLayout infoLL = (LinearLayout) rowView
								.findViewById(R.id.info);

						if (Build.VERSION.SDK_INT > 15)
							infoLL.setBackground(context.getResources().getDrawable(
									R.drawable.onyx_selector));
						else
							infoLL.setBackgroundDrawable(context.getResources().getDrawable(
									R.drawable.onyx_selector));
					}

					tagTV.setTextColor(context.getResources().getColor(R.color.UIDarkPurple));
					if (Build.VERSION.SDK_INT > 15)
						parentLL.setBackground(context.getResources().getDrawable(
								R.drawable.onyx_selector));
					else
						parentLL.setBackgroundDrawable(context.getResources().getDrawable(
								R.drawable.onyx_selector));

					amountTV.setTextColor(context.getResources().getColor(R.color.UIDarkText));
					summaryTV.setTextColor(context.getResources().getColor(R.color.UIDarkText));

				}

				summaryTV.setTextSize(textSize);
				tagTV.setTextSize(textSize + (float) 2);
				amountTV.setTextSize(textSize + (float) 2);




				if (fontStyle.contains("DEFAULT") != true) {

					tagTV.setTypeface(typeface);
					amountTV.setTypeface(typeface);
					summaryTV.setTypeface(typeface);
				}

			    
			  
			}
		}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		    ViewHolder holder = null;
			if (rowView == null) {

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.tags_of_entries, parent, false);
				holder = new ViewHolder(rowView);
				rowView.setTag(holder);
			}
			else{
				holder = (ViewHolder) rowView.getTag();
			}

		setInfo(holder, position);


		
		return rowView;
	}

	private void setInfo(ViewHolder holder, int position) {







			/*
				 * this underlines text SpannableString contentUnderline = new
				 * SpannableString(tags.get(position).replaceAll("_", " "));
				 *
				 * contentUnderline.setSpan(new UnderlineSpan(), 0,
				 * contentUnderline.length(), 0); tag.setText(contentUnderline);
				 */
		holder.tagTV.setText(tagsArr.get(position).replaceAll("_", " "));
		// if (tags.get(position).length() > 14)
		// tag.setTextSize(20);
		// if (tags.get(position).length() > 22)
		// tag.setTextSize(18);

		holder.amountTV.setText(Integer.toString(tagAmountArr.get(position)));

		holder.summaryTV.setText(null);

		holder.summaryTV.setMaxLines(Integer.parseInt(sp.getString("LineNum", "3")));
		holder.summaryTV.setMinLines(Integer.parseInt(sp.getString("LineNum", "3")));

		try {

			entryFile = new File(context.getFilesDir(), fileNames.get(position));

			br = new DataInputStream(context.openFileInput(entryFile
					.getName()));
			br.readUTF();
			holder.summaryTV.setText(Html.fromHtml(br.readUTF().trim()
					.replaceAll("\n", "<br />")));
			br.close();
		} catch (Exception e) {
			Log.e("test", e.toString());
		}



	}

	public void sortNum(ArrayList<String> numSortedTags,
			ArrayList<Integer> numSortedTagNum, ArrayList<String> fileNames) {
		this.tagAmountArr.clear();
		this.tagsArr.clear();
		this.fileNames.clear();

		this.tagsArr.addAll(numSortedTags);
		this.tagAmountArr.addAll(numSortedTagNum);
		this.fileNames.addAll(fileNames);
		this.notifyDataSetChanged();

	}

	public void sortAlph(ArrayList<String> alphaSortedTags,
			ArrayList<Integer> alphSortedTagNum, ArrayList<String> fileNames) {
		this.tagAmountArr.clear();
		this.tagsArr.clear();
		this.fileNames.clear();

		this.tagsArr.addAll(alphaSortedTags);
		this.tagAmountArr.addAll(alphSortedTagNum);
		this.fileNames.addAll(fileNames);
		this.notifyDataSetChanged();
	}

}
