package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.MainActivities.ChosenTag;
import com.UtilityClasses.XML;
import com.UtilityClasses.CustomAttributes;
import com.rtomyj.Diary.R;

import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
	private Context context;
	private ArrayList<String> tagsArrList;
	private ArrayList<Byte> tagAmountArrList;
	private ArrayList<String> fileNamesArrList;

	private CustomAttributes userUIPreferences;

	public TagsAdapter(Context context, ArrayList<String> tagsArrList, ArrayList<Byte> tagAmountArrList, ArrayList<String> fileNamesArrList) {
		this.context = context;
		this.tagsArrList = new ArrayList<>(tagsArrList);
		this.tagAmountArrList = new ArrayList<>(tagAmountArrList);
		this.fileNamesArrList = new ArrayList<>(fileNamesArrList);

	}
	public TagsAdapter(Context context, CustomAttributes userUIPreferences){
		tagsArrList = new ArrayList<>();
		tagAmountArrList = new ArrayList<>();
		fileNamesArrList = new ArrayList<>();

		this.context = context;
		this.userUIPreferences = userUIPreferences;

		XML.getTagsFromXML(tagsArrList, tagAmountArrList, fileNamesArrList,context.getFilesDir());
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
			summaryTV = parent.findViewById(R.id.example_of_entry_with_tag);
			tagTV = parent.findViewById(R.id.tag);
			amountTV =  parent.findViewById(R.id.number_of_tags);
			parentLL = parent.findViewById(R.id.tag_list);
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
		holder.parentLL.setId(position);
		holder.parentLL.setClickable(true);
		holder.parentLL.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int position = view.getId();
				Intent selectedTag = new Intent(context, ChosenTag.class);
				selectedTag.putExtra("TAG_NAME", tagsArrList.get(position));
				selectedTag.putExtra("TAG_NUM", (byte) tagAmountArrList.get(position));

				context.startActivity(selectedTag);
			}
		});


		if (userUIPreferences.theme.contains("Onyx")) {
			holder.parentLL.setBackground(userUIPreferences.darkThemeSelectorShader);

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
		return tagsArrList.size();
	}


	private void setInfo(ViewHolder holder, int position) {
		File entryFile;
		DataInputStream br;

		holder.tagTV.setText(tagsArrList.get(position).replaceAll("_", " "));
		holder.amountTV.setText(Byte.toString(tagAmountArrList.get(position)));
		holder.summaryTV.setText("");

		try {

			entryFile = new File(context.getFilesDir(), fileNamesArrList.get(position));

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
		this.tagAmountArrList.clear();
		this.tagsArrList.clear();
		this.fileNamesArrList.clear();

		this.tagsArrList.addAll(numSortedTags);
		this.tagAmountArrList.addAll(numSortedTagNum);
		this.fileNamesArrList.addAll(fileNames);
		this.notifyDataSetChanged();

	}

	public void sortAlph(ArrayList<String> alphaSortedTags,
			ArrayList<Byte> alphSortedTagNum, ArrayList<String> fileNames) {
		this.tagAmountArrList.clear();
		this.tagsArrList.clear();
		this.fileNamesArrList.clear();

		this.tagsArrList.addAll(alphaSortedTags);
		this.tagAmountArrList.addAll(alphSortedTagNum);
		this.fileNamesArrList.addAll(fileNames);
		this.notifyDataSetChanged();
	}

}
