package com.AnimusAdapters;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.UtilityClasses.AnimusFiles;
import com.UtilityClasses.AnimusPictures;
import com.rtomyj.Diary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.ViewHolder> {
	// for info
	private Calendar calendar= Calendar.getInstance();
	private Context context;
	private int totalPicCount = 0;

	// holds data for adapter
	private ArrayList<String> picturesArrList;

	// user preferences;
	private String fontStyle = "", theme= "";
	private Typeface userSelectedFontTF;
	private float textSize;
	private int primaryColor = 0, secondaryColor = 0, darkTextColor = 0, darkThemeBackgroundColor = 0, darkThemeForegroundColor = 0;

	// Cache
	private AnimusPictures.LRUBitmapCache cache;


	public PicturesAdapter(Context context, ArrayList<String> picturesArrList) {
		this.context = context;
		this.picturesArrList = (picturesArrList);
		setAdapterBaseData();
	}
	public PicturesAdapter(Context context){
		this.context = context;
		setAdapterBaseData();
		getPicFiles();
	}
	public PicturesAdapter(Context context, ArrayList<String> picturesArrList, int primaryColor, int secondaryColor, int darkTextColor, int darkThemeForegroundColor, int darkThemeBackgroundColor,
										float textsize, String fontStyle, String theme){
		this.context = context;
		this.picturesArrList = (picturesArrList);
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.darkTextColor = darkTextColor;
		this.darkThemeForegroundColor = darkThemeForegroundColor;
		this.darkThemeBackgroundColor = darkThemeBackgroundColor;
		this.textSize = textsize;
		this.fontStyle = fontStyle;
		this.theme = theme;

		makeTypeFace();
		setupCache();
	}

	public PicturesAdapter(Context context, int primaryColor, int secondaryColor, int darkTextColor, int darkThemeForegroundColor, int darkThemeBackgroundColor,
						   float textsize, String fontStyle, String theme){
		this.context = context;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.darkTextColor = darkTextColor;
		this.darkThemeForegroundColor = darkThemeForegroundColor;
		this.darkThemeBackgroundColor = darkThemeBackgroundColor;
		this.textSize = textsize;
		this.fontStyle = fontStyle;
		this.theme = theme;

		getPicFiles();
		setupCache();
		makeTypeFace();
	}
	private void makeTypeFace(){
		if (!fontStyle.contains("DEFAULT")) {  // if font style is anything but the value default, it creates a typeface with the specified name.
		userSelectedFontTF = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontStyle);
	}

	}


	private void getUserPreferences(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

		makeTypeFace();

		textSize = Float.parseFloat(sp.getString("TextSize", "14"));
	}
	private  void setupCache(){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int memory = am.getMemoryClass() * 1024 * 1024 / 14;
		// dividing resources up
		cache = new AnimusPictures.LRUBitmapCache(memory);

	}


	private void setAdapterBaseData(){
		setupCache();
		getUserPreferences();
	}

	private synchronized void getPicFiles(){
		final ArrayList<File> files = AnimusFiles.getFilesWithExtension(context.getFilesDir(), ".png");
		totalPicCount = files.size();

		picturesArrList = new ArrayList<>(totalPicCount);
		int index = 0;

		while (index < 10 && ! files.isEmpty()){
			addPicFileToList(files.get(0));
			files.remove(0);
			index ++;
		}
		if (! files.isEmpty()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (File file : files) {
						addPicFileToList(file);
					}

				}
			}).start();
		}

	}
	private void addPicFileToList(File file){
		StringBuilder filename = new StringBuilder(file.getName());
		filename.delete(filename.indexOf("("), filename.length() );
		if ( !picturesArrList.contains(filename.toString()))
			picturesArrList.add(filename.toString());
		Log.e("hh", filename.toString());
	}

	
	
	 static class ViewHolder extends  RecyclerView.ViewHolder{
		 private TextView titleTV;
		 private TextView monthTV;
		 private TextView dayTV;
		 private TextView yearTV;
		 private ImageView imageView;
		 private  LinearLayout parentTV;
			
		 	ViewHolder(View rowView) {
				super(rowView);
				titleTV = (TextView) rowView.findViewById(R.id.title);
				monthTV = (TextView) rowView.findViewById(R.id.month);
				dayTV = (TextView) rowView.findViewById(R.id.day);
				yearTV = (TextView) rowView.findViewById(R.id.year);
				imageView = (ImageView) rowView.findViewById(R.id.image_in_adapter);
				parentTV = (LinearLayout) rowView.findViewById(R.id.parent);
			}

		}


	@Override
	public PicturesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_adapter, parent, false);
		return new ViewHolder(parentView);
		/*
		View rowView = convertView;
	    ViewHolder holder;
		if (rowView == null ) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.pic_adapter, parentTV, false);
			holder = new ViewHolder(rowView, position);
			rowView.setTag(holder);
		
		}
		else{
			holder = (ViewHolder) rowView.getTag();

			if (holder.position != position){
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.pic_adapter, parentTV, false);
				holder = new ViewHolder(rowView, position);
				rowView.setTag(holder);

				Log.e(picturesArrList.get(position) + " in the if statement", String.valueOf(position));
			}
		
		}

		
		return rowView;
		*/
	}

	@Override
	public synchronized void onBindViewHolder(PicturesAdapter.ViewHolder holder, int position) {

		File f = new File(context.getFilesDir(), picturesArrList.get(position) + ".txt");
		if (  f.exists()) {
			if (!fontStyle.contains("DEFAULT")) {
				holder.titleTV.setTypeface(userSelectedFontTF);
				holder.monthTV.setTypeface(userSelectedFontTF);
				holder.yearTV.setTypeface(userSelectedFontTF);
				holder.dayTV.setTypeface(userSelectedFontTF);
			}

			if (theme.equals("Onyx")) {
				holder.titleTV.setTextColor(darkTextColor);
				holder.monthTV.setTextColor(darkTextColor);
				holder.yearTV.setTextColor(darkTextColor);
				holder.parentTV.setBackground(ContextCompat.getDrawable(context, R.drawable.onyx_selector));
			}


			holder.dayTV.setTextColor(secondaryColor);
			holder.titleTV.setTextColor(secondaryColor);

			holder.titleTV.setTextSize(textSize + (float) 2);
			holder.monthTV.setTextSize(textSize);
			holder.dayTV.setTextSize(textSize + (float) 5);
			holder.yearTV.setTextSize(textSize);

			if (!picturesArrList.get(position).substring(0, 4).equals("Temp"))
				holder.titleTV.setText(picturesArrList.get(position).replace("_", " "));
			else
				holder.titleTV.setText(null);


			calendar.setTimeInMillis(f.lastModified());
			//Log.e(String.valueOf(position), picturesArrList.get(position));

			String months[] = context.getResources().getStringArray(R.array.months);
			holder.monthTV.setText(months[calendar.get(Calendar.MONTH)]);
			holder.dayTV.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + ",");
			holder.yearTV.setText(Integer.toString(calendar.get(Calendar.YEAR)));


			setBitmap(position, holder.imageView);
		}
	}

	private void setBitmap(int position, ImageView pictureView) {
		File f =new File(context.getFilesDir(), picturesArrList.get(position) + "(1).png");
		int num = 2;
		while ( ! f.exists()){
			f = new File(context.getFilesDir(), picturesArrList.get(position) + "(" + Integer.toString(num) + ").png");
			num++;
		}

		Bitmap cachedImage = cache.getBitmapFromMemCache(f.getAbsolutePath());
		if (cachedImage != null) {
			pictureView.setImageBitmap(cachedImage);
		} else {
			new LoadPics(pictureView, f.length()).execute(f.getAbsolutePath());
		}

	}

	// Return the size of your data set (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return picturesArrList.size();}

	private class LoadPics extends AsyncTask<String, Integer, Bitmap> {
		private ImageView imageView;
		private long size;

		LoadPics(ImageView p, long size) {
			imageView = p;
			this.size = size;
		}
		protected synchronized Bitmap doInBackground(String... file) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			//Log.e(file[0], String.valueOf(size);
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file[0], opt);
			opt.inPreferQualityOverSpeed = false;
			int size = 2;
			int height = opt.outHeight / size, width = opt.outWidth / size;
			while (height > 680 && width > 420 && size < 6) {
				height = height / 2;
				width = width / 2;
				size += 2;
			}
			opt.inSampleSize = size;
			opt.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFile((file[0]), opt);
			cache.addBitmapToMemoryCache(file[0], bm);
			return bm;
		}
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
		}

		public ArrayList<String> getPicturesArrList(){
			return picturesArrList;
		}

		public int getTotalPicCount(){
			return totalPicCount;
		}

}
