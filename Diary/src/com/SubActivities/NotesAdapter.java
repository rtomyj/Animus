package com.SubActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rtomyj.Diary.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
	  private Context context;
	private SharedPreferences sp;
	  private ArrayList<String> notesArr = new ArrayList();

	  private File f;
	  private String temp;

	private String fontStyle;
	private Typeface tf;

	public static class ViewHolder extends RecyclerView.ViewHolder{
		private TextView summary;
		private TextView lastModified;
		private  LinearLayout note ;
		private View parent;


		public ViewHolder(View rowView) {
			super(rowView);
			parent = rowView;
			summary = (TextView) rowView.findViewById(R.id.summary_of_note);
			lastModified = (TextView) rowView.findViewById((R.id.last_modified));
			note = (LinearLayout) rowView.findViewById(R.id.sub_card_view);
		}

	}


	@Override
	public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
													  int viewType) {
		// create a new view
		View layoutInflated = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.files_of_notes, parent, false);

		WeakReference<View> layoutInflatedWeak = new WeakReference<>(layoutInflated);

		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewHolder = new ViewHolder(layoutInflatedWeak.get());
		return viewHolder;
	}


	  public NotesAdapter(Context context, ArrayList<String> temp) {
		  if (this.context == null)
	    this.context = context;
		  notesArr.addAll(temp);

		  sp = PreferenceManager
				  .getDefaultSharedPreferences(context);




		  if (fontStyle == null) {
			   fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

			  if (fontStyle.contains("DEFAULT") != true) {
				  tf = Typeface.createFromAsset(context.getAssets(),
						  "fonts/" + fontStyle);
			  }
		  }
	  }
	public NotesAdapter(Context context){
		if (this.context == null)
			this.context = context;

		sp = PreferenceManager
				.getDefaultSharedPreferences(context);


		if (fontStyle == null) {
			fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

			if (fontStyle.contains("DEFAULT") != true) {
				tf = Typeface.createFromAsset(context.getAssets(),
						"fonts/" + fontStyle);
			}
		}

		FilenameFilter entriesFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".note");
			}
		};


		File[] notes = context.getFilesDir().listFiles(entriesFilter);
		ArrayList<File> files = new ArrayList<>();
		WeakReference<ArrayList<File>> filesWeak = new WeakReference<>(files);
		WeakReference<File[]> notesWeak = new WeakReference<>(notes);

		filesWeak.get().addAll(Arrays.asList(notesWeak.get()));

		Collections.sort(filesWeak.get(), new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return Long.valueOf(f2.lastModified()).compareTo(
						f1.lastModified());
			}

		});

		for (int i =0; i < filesWeak.get().size() ; i++) {
			notesArr.add(filesWeak.get().get(i).getName());
		}
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {

holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
	@Override
	public boolean onLongClick(View v) {

		Toast.makeText(context, "yooooo", Toast.LENGTH_SHORT).show();
		return false;
	}
});

		holder.parent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent( context
						, ChosenNote.class);
				i.putExtra("NOTE", notesArr.get(position));
				( (Activity)context).startActivityForResult(i, 2);
			}
		});



		if (fontStyle.contains("DEFAULT") != true){

			holder.summary.setTypeface(tf);
			holder.lastModified.setTypeface(tf);
		}

		holder.summary.setMaxLines(Integer.parseInt(sp.getString("LineNum", "3")));
		holder.summary.setMinLines(Integer.parseInt(sp.getString("LineNum", "3")));
		if (sp.getString("Theme", "Default").contains("Onyx")) {

			if (Build.VERSION.SDK_INT > 15)
				holder.note.setBackground(context.getResources().getDrawable(
						R.drawable.onyx_selector));
			else
				holder.note.setBackgroundDrawable(context.getResources().getDrawable(
						R.drawable.onyx_selector));
			holder.summary.setTextColor(context.getResources().getColor(R.color.UIDarkText));
		}

		float textSize = Float.parseFloat(sp.getString("TextSize", "14"));
		holder.summary.setTextSize(textSize);
		holder.lastModified.setTextSize(textSize);


		f = new File(context.getFilesDir(), notesArr.get(position));
		String filename = f.getName();
		WeakReference<String> filenameWeak = new WeakReference<>(filename);

		Time t = new Time();
		t.set(f.lastModified());
		holder.lastModified.setText(Integer.toString(t.month + 1) + "/" + Integer.toString(t.monthDay) + "/" + Integer.toString(t.year));

		holder.summary.setText("");
		try {
			DataInputStream br  = new DataInputStream(context.openFileInput(filenameWeak.get()));
			WeakReference<DataInputStream> brWeak = new WeakReference<>(br);

			temp = brWeak.get().readUTF().trim();
			if (temp.equals("") == true){
				holder.summary.setText("");
			}
			else
				holder.summary.setText(temp);

			brWeak.get().close();
		} catch (IOException e) {
			e.printStackTrace();
			holder.lastModified.setText("null");
		}

	}

	@Override
	public int getItemCount() {
		return notesArr.size();
	}
		
		public void removeNote(String name) {
			int index = notesArr.indexOf(name);
			notesArr.remove(index);

			notifyItemRemoved(index);
		}
	public void insertNote(int index, String name){
		notesArr.add(index, name);
		notifyItemInserted(index);
	}
	public ArrayList<String>  getNotesArr(){
		return notesArr;
	}

	public void addNewNote(String note){
		notesArr.add(0, note);
		notifyItemInserted(0);
	}
		} 