package com.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.UtilityClasses.AnimusXML;
import com.UtilityClasses.CustomAttributes;
import com.rtomyj.Diary.R;

import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
	private Context context;
	private ArrayList<String> tagsArr;
	private ArrayList<Byte> tagAmountArr;
	private ArrayList<String> fileNames;

	private CustomAttributes userUIPreferences;

	public TagsAdapter(Context context, ArrayList<String> tagsArr, ArrayList<Byte> tagAmountArr, ArrayList<String> fileNames) {
		this.context = context;
		this.tagsArr = new ArrayList<>(tagsArr);
		this.tagAmountArr = new ArrayList<>(tagAmountArr);
		this.fileNames = new ArrayList<>(fileNames);

	}
	public TagsAdapter(Context context, CustomAttributes userUIPreferences){
		tagsArr = new ArrayList<>();
		tagAmountArr = new ArrayList<>();
		fileNames = new ArrayList<>();

		this.context = context;
		this.userUIPreferences = userUIPreferences;

		AnimusXML.getTagsFromXML(tagsArr, tagAmountArr,fileNames ,context.getFilesDir());
	}


	/*
 holds a limited amount of UI objects. When the recycler view needs to get new objects, old ones are recycled from here.
 Otherwise the views are used again by the LayoutManager
  */
	static class ViewHolder extends RecyclerView.ViewHolder{
		private TextView tagTV;
		private TextView amountTV;
		private TextView summaryTV;
		private CardView parentLL;

		ViewHolder(View parent) {
			super(parent);
			summaryTV = (TextView) parent.findViewById(R.id.example_of_entry_with_tag);
			tagTV = (TextView) parent.findViewById(R.id.tag);
			amountTV = (TextView) parent.findViewById(R.id.number_of_tags);
			parentLL = (CardView) parent.findViewById(R.id.tag_list);
		}

	}

	// Create new views (invoked by the layout manager). Should do any parent specific customizations after the LayoutInflater method is invoked.
	@Override
	public TagsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_of_entries, parent, false);
		return  new TagsAdapter.ViewHolder(parentView);
	}


	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public synchronized void onBindViewHolder(TagsAdapter.ViewHolder holder, final int position) {
		if (userUIPreferences.theme.contains("Onyx")) {
			holder.parentLL.setBackground(context.getResources().getDrawable(R.drawable.onyx_selector));

			holder.amountTV.setTextColor(userUIPreferences.textColorForDarkThemes);
			holder.summaryTV.setTextColor(userUIPreferences.textColorForDarkThemes);

		}
		holder.tagTV.setTextColor(userUIPreferences.secondaryColor);

		holder.summaryTV.setTextSize(userUIPreferences.textSize);
		holder.tagTV.setTextSize(userUIPreferences.textSize + (float) 2);
		holder.amountTV.setTextSize(userUIPreferences.textSize + (float) 2);


		if ( userUIPreferences.userSelectedFontTF != null ) {

			holder.tagTV.setTypeface(userUIPreferences.userSelectedFontTF);
			holder.amountTV.setTypeface(userUIPreferences.userSelectedFontTF);
			holder.summaryTV.setTypeface(userUIPreferences.userSelectedFontTF);
		}
		holder.summaryTV.setMaxLines(userUIPreferences.numLines);
		holder.summaryTV.setMinLines(userUIPreferences.numLines);

		setInfo(holder, position);
	}

	@Override
	public int getItemCount() {
		return tagsArr.size();
	}


	private void setInfo(ViewHolder holder, int position) {
		File entryFile;
		DataInputStream br;

		holder.tagTV.setText(tagsArr.get(position).replaceAll("_", " "));
		holder.amountTV.setText(Byte.toString(tagAmountArr.get(position)));
		holder.summaryTV.setText("");

		try {

			entryFile = new File(context.getFilesDir(), fileNames.get(position));

			br = new DataInputStream(context.openFileInput(entryFile.getName()));
			br.readUTF();
			holder.summaryTV.setText(Html.fromHtml(br.readUTF().trim().replaceAll("\n", "<br />")));
			br.close();
		} catch (Exception e) {
			Log.e("test", e.toString());
		}



	}

	public void sortNum(ArrayList<String> numSortedTags,
			ArrayList<Byte> numSortedTagNum, ArrayList<String> fileNames) {
		this.tagAmountArr.clear();
		this.tagsArr.clear();
		this.fileNames.clear();

		this.tagsArr.addAll(numSortedTags);
		this.tagAmountArr.addAll(numSortedTagNum);
		this.fileNames.addAll(fileNames);
		this.notifyDataSetChanged();

	}

	public void sortAlph(ArrayList<String> alphaSortedTags,
			ArrayList<Byte> alphSortedTagNum, ArrayList<String> fileNames) {
		this.tagAmountArr.clear();
		this.tagsArr.clear();
		this.fileNames.clear();

		this.tagsArr.addAll(alphaSortedTags);
		this.tagAmountArr.addAll(alphSortedTagNum);
		this.fileNames.addAll(fileNames);
		this.notifyDataSetChanged();
	}

}
