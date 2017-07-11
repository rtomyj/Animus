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

import com.rtomyj.Diary.R;

import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
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

	public TagsAdapter(Context context, ArrayList<String> tagsArr, ArrayList<Integer> tagAmountArr, ArrayList<String> fileNames) {
		this.context = context;

		this.fileNames.addAll(fileNames);
		this.tagsArr.addAll(tagsArr);
		this.tagAmountArr.addAll(tagAmountArr);

		sp = PreferenceManager.getDefaultSharedPreferences(this.context);

		fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

		if (fontStyle.contains("DEFAULT") != true)
			typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/" + fontStyle);

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

		textSize = Float.parseFloat(sp.getString("TextSize", "14"));
		if (sp.getString("Theme", "Default").contains("Onyx")) {
			holder.tagTV.setTextColor(context.getResources().getColor(R.color.UIDarkPurple));
			holder.parentLL.setBackground(context.getResources().getDrawable(R.drawable.onyx_selector));

			holder.amountTV.setTextColor(context.getResources().getColor(R.color.UIDarkText));
			holder.summaryTV.setTextColor(context.getResources().getColor(R.color.UIDarkText));

		}

		holder.summaryTV.setTextSize(textSize);
		holder.tagTV.setTextSize(textSize + (float) 2);
		holder.amountTV.setTextSize(textSize + (float) 2);


		if ( ! fontStyle.contains("DEFAULT") ) {

			holder.tagTV.setTypeface(typeface);
			holder.amountTV.setTypeface(typeface);
			holder.summaryTV.setTypeface(typeface);
		}

		setInfo(holder, position);
	}

	@Override
	public int getItemCount() {
		return tagsArr.size();
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
