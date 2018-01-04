package com.Adapters.MainAdapters;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Adapters.Parents.AdapterBase;
import com.UtilityClasses.CustomAttributes;
import com.UtilityClasses.Files;
import com.UtilityClasses.LauncherMethods;
import com.UtilityClasses.Pictures;
import com.rtomyj.Diary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


/*
		Adapter that displays an Entry_Base that has at least one picture. Displays one picture that corresponds to its entry and the date_selection_layout of the entry as well as its name.
 */

public class PicturesAdapter extends AdapterBase<PicturesAdapter.ViewHolder > {
	// for info
	private Calendar calendar= Calendar.getInstance();

	// holds data_activity_layout for adapter
	private ArrayList<String> picturesArrList;
	private ArrayList<Long> lastModifiedArrList;

	// Cache
	private Pictures.LRUBitmapCache cache;

	public PicturesAdapter(Context context, ArrayList<String> picturesArrList, ArrayList<Long> lastModifiedArrList, CustomAttributes userUIPreferences){
		super(context, userUIPreferences);
		this.picturesArrList = new ArrayList<>(picturesArrList);
		this.lastModifiedArrList = new ArrayList<>(lastModifiedArrList);

		setupCache();
	}

	public PicturesAdapter(Context context, CustomAttributes userUIPreferences){
		super(context, userUIPreferences);

		getPicFiles();
		setupCache();
	}

	// makes a cache instance that uses a portion of the Devices ram
	private void setupCache(){
		ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
		int memory = am.getMemoryClass() * 1024 * 1024 / 20;		// dividing resources up
		cache = new Pictures.LRUBitmapCache(memory);

	}


	/*
	 gets all .png files from app storage.
	 Only stores their filename with no etension or pic number (eg tempFile(2).png becomes tempFile). Only one entry is stored, only entries with at least on pic entry is stored, and later only one pic per entry is displayed.
	  */
	private void getPicFiles(){
		final ArrayList<File> files = Files.getFilesWithExtension(getContext().getFilesDir(), ".png");
		int estimatedInitPicCount = files.size() / 2;

		picturesArrList = new ArrayList<>(estimatedInitPicCount);
		lastModifiedArrList = new ArrayList<>(estimatedInitPicCount);
		int index = 0;

		// starts off adapter with at most 10 filenames
		while (index < 10 && ! files.isEmpty()){
			addPicFileToList(files.get(0));
			files.remove(0);
			index ++;
		}

		// after the 10 or so filenames are added the rest get added in a separate thread.
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

	/*
		adds the filename of entries that have .png file types to the adapters array. Takes out any extension and numbering scheme.
		Eg, entryName(2).png becomes entryName

		The only way a name gets added to the adapters array is if the name is not a duplicate within adapters array and if the equivalent .txt file exists.
		Eg, entryName(2) will be added as entryName only if entryName.txt exists.
	 */

	private void addPicFileToList(File file){
		StringBuilder filename = new StringBuilder(file.getName());
		filename.delete(filename.indexOf("("), filename.length() );
		File f = new File(getContext().getFilesDir(), filename.toString() + ".txt");

		if ( !picturesArrList.contains(filename.toString()) && f.exists()) {
			picturesArrList.add(filename.toString());
			//Log.e("pic file added", filename.toString());
			lastModifiedArrList.add(f.lastModified());

		}

	}

	
	
	 static class ViewHolder extends  RecyclerView.ViewHolder{
		 private TextView titleTV;
		 private TextView monthTV;
		 private TextView dayTV;
		 private TextView yearTV;
		 private ImageView imageView;
		 private  LinearLayout parent;
			
		 	ViewHolder(View rowView) {
				super(rowView);
				titleTV = rowView.findViewById(R.id.title);
				monthTV =  rowView.findViewById(R.id.month);
				dayTV =  rowView.findViewById(R.id.day);
				yearTV =  rowView.findViewById(R.id.year);
				imageView = rowView.findViewById(R.id.image_in_adapter);
				parent =  rowView.findViewById(R.id.parent);
			}

		}


	@Override
	public PicturesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_adapter_layout, parent, false);
		return new ViewHolder(parentView);
	}

	@Override
	public synchronized void onBindViewHolder(PicturesAdapter.ViewHolder holder, int position) {
		setOnClick(holder.parent, position);
		customizeUI(holder);
		setData(holder.titleTV, holder.monthTV, holder.dayTV, holder.yearTV, position);
		setBitmap(position, holder.imageView);

	}
	private void setData(TextView titleTV, TextView monthTV, TextView dayTV, TextView yearTV, int position){
		if (!picturesArrList.get(position).substring(0, 4).equals("Temp"))
			titleTV.setText(picturesArrList.get(position).replace("_", " "));
		else
			titleTV.setText(null);


		calendar.setTimeInMillis(lastModifiedArrList.get(position));
		//Log.e(String.valueOf(position), picturesArrList.get(position));

		monthTV.setText(String.format(getLocale(), "%ta", calendar));
		dayTV.setText(String.format(getLocale(),"%tm", calendar));
		yearTV.setText(String.format(getLocale(),"%ty", calendar));
	}

	private void setOnClick(LinearLayout parent, int position){
		parent.setClickable(true);
		parent.setId(position);
		parent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View parent) {
				int position = parent.getId();
				LauncherMethods.chosenFile(getContext(), picturesArrList.get(position)  + ".txt", position, picturesArrList);

			}
		});
	}

	private void customizeUI(ViewHolder holder){
		if (!getUserUIPreferences().fontStyle.contains("DEFAULT")) {
			holder.titleTV.setTypeface(getUserUIPreferences().userSelectedFontTF);
			holder.monthTV.setTypeface(getUserUIPreferences().userSelectedFontTF);
			holder.yearTV.setTypeface(getUserUIPreferences().userSelectedFontTF);
			holder.dayTV.setTypeface(getUserUIPreferences().userSelectedFontTF);
		}

		if (getUserUIPreferences().theme.contains("Onyx")) {
			holder.titleTV.setTextColor(getUserUIPreferences().textColorForDarkThemes);
			holder.monthTV.setTextColor(getUserUIPreferences().textColorForDarkThemes);
			holder.yearTV.setTextColor(getUserUIPreferences().textColorForDarkThemes);
			holder.parent.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.onyx_selector));
		}


		holder.dayTV.setTextColor(getUserUIPreferences().secondaryColor);
		holder.titleTV.setTextColor(getUserUIPreferences().secondaryColor);

		holder.titleTV.setTextSize(getUserUIPreferences().mediumTextSize);
		holder.monthTV.setTextSize(getUserUIPreferences().textSize);
		holder.dayTV.setTextSize(getUserUIPreferences().largeTextSize);
		holder.yearTV.setTextSize(getUserUIPreferences().textSize);
	}

	private void setBitmap(int position, ImageView pictureView) {
		File f =new File(getContext().getFilesDir(), picturesArrList.get(position) + "(1).png");
		int num = 2;
		while ( ! f.exists()){
			f = new File(getContext().getFilesDir(), picturesArrList.get(position) + "(" + Integer.toString(num) + ").png");
			num++;
		}

		String filename = f.getAbsolutePath();
		Bitmap cachedImage = cache.getBitmapFromMemCache(filename);
		if (cachedImage != null) {
			pictureView.setImageBitmap(cachedImage);
		} else {
			new LoadPics(pictureView, f.length(), filename).execute("");
		}

	}

	// Return the size of your data_activity_layout set (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return picturesArrList.size();}

	private class LoadPics extends AsyncTask<String, Integer, Bitmap> {
		private ImageView imageView;
		private long size;
		String filename;

		LoadPics(ImageView p, long size, String filename) {
			imageView = p;
			this.size = size;
			this.filename = filename;
		}
		protected synchronized Bitmap doInBackground(String... file) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			//Log.e(file[0], String.valueOf(size);
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filename, opt);
			opt.inPreferQualityOverSpeed = false;
			int size = 2;
			int height = opt.outHeight / size, width = opt.outWidth / size;
			while (height > 680 && width > 420 && size < 6) {
				height /= size;
				width /= size;
				size += 2;
			}
			opt.inSampleSize = size;
			opt.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFile((filename), opt);
			cache.addBitmapToMemoryCache(filename, bm);
			return bm;
		}
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
		}

		public ArrayList<String> getPicturesArrList(){
			return picturesArrList;
		}
		public ArrayList<Long> getLastModifiedArrList(){
			return lastModifiedArrList;
		}

}
