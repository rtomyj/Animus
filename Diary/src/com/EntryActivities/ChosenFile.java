package com.EntryActivities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.SubActivities.Passcode;
import com.UtilityClasses.PhotoViewer;
import com.UtilityClasses.MiscMethods;
import com.rtomyj.Diary.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@SuppressLint("CutPasteId")
public class ChosenFile extends AppCompatActivity implements SensorListener {

	// activity related
	private SharedPreferences sp;
	private Context context;
	private int currentFileFromDomus;
	private float textSize = 14;
	private boolean edited = false;
	private boolean shouldISave = false;
	private boolean showing = false;
	private File chosenFile;


	// sensors
	private SensorManager sensorMgr;
	private long lastUpdate = -1;
	private float last_x, last_y, last_z;
	private ShareActionProvider mShareActionProvider;


	private boolean passcodeOn = false;
	private boolean passcodeCheck = true;

	// entry info
	private String filename;
	private String name;
	private String relationship = "";
	private String job = "";
	private String locationName = "";
	private Double latitude, longitude;
	private String newFilename = "";
	private String fav = "false";
	private int currMood = 0;
	private java.util.Calendar lastModified = Calendar.getInstance();
	private int amountOfCurrentTags = 0;


	// lists
	private ArrayList<String> allTagsArrList;
	private ArrayList<String> tagsLoadedOnStartArrList;
	private ArrayList<String> deletedTagsArrList;
	private ArrayList<String> sortedFilesArrList = new ArrayList<>();
	private ArrayList<String> suggestionsArrList ;


	// Views
	private TextView favTV;
	private TextView partnerTV;
	private TextView occupationTV;
	private View mView;
	private EditText entryTextET;
	private TextView entryTextTV;



	// pictures
	private Boolean picLoaded = false;
	private Uri outputFileUri;
	private int imageCount = 1;
	private LinearLayout.LayoutParams paramsForTagsOval;



	// audio
	private AlertDialog.Builder audioPlayer;
	private MediaPlayer mPlayer;
	private File audioFile;


	// other
	private String string_constant;
	private AlertDialog.Builder deletePic;









	public class OnSwipeTouchListener implements OnTouchListener {

		private final GestureDetector gestureDetector;

		public OnSwipeTouchListener(Context context) {
			gestureDetector = new GestureDetector(context,
					new GestureListener());
		}

		public void onSwipeLeft() {
		}

		public void onSwipeRight() {
		}

		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}

		private final class GestureListener extends SimpleOnGestureListener {

			private static final int SWIPE_DISTANCE_THRESHOLD = 280;
			private static final int SWIPE_VELOCITY_THRESHOLD = 600;

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				float distanceX = e2.getX() - e1.getX();
				float distanceY = e2.getY() - e1.getY();
				if (Math.abs(distanceX) > Math.abs(distanceY)
						&& Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
						&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
					if (distanceX > 0)
						onSwipeRight();
					else
						onSwipeLeft();
					return true;
				}
				return false;
			}
		}
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
		shouldISave = true;
	}

	@Override
	public boolean onNavigateUp() {
		shouldISave = true;
		return super.onNavigateUp();
	}

	@Override
	protected void onStart() {

		super.onStart();


		if (favTV == null || partnerTV == null || occupationTV == null) {
			favTV = (TextView) findViewById(R.id.add_like_button);
			partnerTV = (TextView) findViewById(R.id.relationship);
			occupationTV = (TextView) findViewById(R.id.occupation);

		}

		if (allTagsArrList == null && tagsLoadedOnStartArrList == null && deletedTagsArrList == null ) {


			allTagsArrList = new ArrayList<>();
			tagsLoadedOnStartArrList = new ArrayList<>();
			deletedTagsArrList = new ArrayList<>();
			suggestionsArrList = new ArrayList<>();


			LinearLayout tags = (LinearLayout) findViewById(R.id.entryTags);
			new LoadData(tags).execute("null");

		}


		if (context == null)
			context = this;


		if (suggestionsArrList == null){
			suggestionsArrList = new ArrayList<>();
			if (sp.getBoolean("Contacts", false) == true) {
				ContentResolver cr = getContentResolver();
				Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
						null, null, null);
				if (cur.getCount() > 0) {
					while (cur.moveToNext()) {
						if (cur.getString(
								cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
								.equals("1")) {
							suggestionsArrList
									.add(cur.getString(cur
											.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
						}
					}
				}
			}






		}




		if (newFilename.equals("") != true)
			this.setTitle(newFilename.replaceAll("_", " "));



		if (fav.equals("true")) {
			favTV.setText(Html.fromHtml("★"));
		}

		deletePic = new AlertDialog.Builder(this);
		deletePic.setTitle(R.string.delete_pic_dialog_title);
		deletePic.setMessage(R.string.delete_pic_confirmation);






	}

	private void setViewsToTheme(){

		textSize = Float.parseFloat(sp.getString("TextSize", "14"));

		TextView t2 = (TextView) findViewById(R.id.date);
		TextView t4 = (TextView) findViewById(R.id.location);
		TextView jobTV = (TextView) findViewById(R.id.occupation);
		TextView partnerTV = (TextView) findViewById(R.id.relationship);




		String fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

		if (fontStyle.contains("DEFAULT") != true) {
			Typeface tf = Typeface.createFromAsset(this.getAssets(),
					"fonts_adapter_layout/" + fontStyle);
			t2.setTypeface(tf);
			entryTextTV.setTypeface(tf);
			entryTextET.setTypeface(tf);
			t4.setTypeface(tf);
			jobTV.setTypeface(tf);
			partnerTV.setTypeface(tf);

		}

		entryTextTV.setTextSize(textSize);
		entryTextET.setTextSize(textSize);


		t4.setTextSize(textSize);
		t2.setTextSize(textSize);
		partnerTV.setTextSize(textSize);
		jobTV.setTextSize(textSize);
		//b.setTextSize(textSize);


		LinearLayout infoLL = (LinearLayout) findViewById(R.id.info);

		LinearLayout markupLL = (LinearLayout) findViewById(R.id.markup);
/*
		if (sp.getString("Theme", "Default").contains("Onyx")) {
			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);



			Button mood = (Button) findViewById(R.id.mood);
			mood.setTextColor(getResources().getColor(R.color.UILightForeground));
			LinearLayout changeTextVS = (LinearLayout) findViewById(R.id.content);

			// make sure you use setBackgroundDrawable for sdk 15 and below

			if (sp.getString("Theme", "Default").equals("Onyx P"))
				markupLL.setBackgroundColor(getResources()
						.getColor(R.color.UIDarkPink));

			else
				markupLL.setBackgroundColor(getResources()
						.getColor(R.color.UIDarkBlue));


			ViewSwitcher vs2 = (ViewSwitcher) findViewById(R.id.ChangeText);

			parent.setBackgroundColor(getResources().getColor(R.color.UIDarkForeground));
			changeTextVS.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));
			entryTextTV.setTextColor(getResources().getColor(R.color.UIDarkNormalText));
			entryTextTV.setHighlightColor(getResources().getColor(R.color.UIDarkBlue_Green));

			// FIX THIS SO ITS NOT JUST A TRY CATCH
			try{
				vs2.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));

			}catch(Exception e){

			}

			infoLL.setBackgroundColor(getResources()
					.getColor(R.color.UIDarkBackground));



			t2.setTextColor(getResources().getColor(R.color.UIDarkNormalText));
			entryTextET.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));
			entryTextET.setTextColor(getResources().getColor(R.color.UIDarkNormalText));
			entryTextET.setHighlightColor(getResources()
					.getColor(R.color.UIDarkBlue_Green));


		}
		else if (sp.getString("Theme", "Default").equals("Material")) {

			markupLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialLightGreen));

			infoLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialPink));


		}
		else if (sp.getString("Theme", "Default").equals("Material 2")) {

			markupLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialDeepOrange));

			infoLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialBlue));


		}
		else if (sp.getString("Theme", "Default").equals("Material 3")) {

			markupLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialYellow));

			infoLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialPurple));


		}
		else if (sp.getString("Theme", "Default").equals("Material 4")) {
			markupLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialGreen));
			infoLL.setBackgroundColor(getResources().getColor(R.color.UIMaterialOrange));

		}
		*/

	}
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);


		sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		setUITheme();




		setContentView(R.layout.chosen_entry_activity_layout);

		LinearLayout info = (LinearLayout) findViewById(R.id.info);
		LinearLayout markup = (LinearLayout) findViewById(R.id.markup);
		Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.info_down_from_top);
		Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.toolbar_up_from_bottom);



		info.startAnimation(slideDown);
		markup.startAnimation(slideUp);

		entryTextET = (EditText) findViewById(R.id.editText);
		entryTextTV = (TextView) findViewById(R.id.text);

		entryTextET.addTextChangedListener(new TextWatcher() {

			TextView wordCount = (TextView) findViewById(R.id.wordCount);
			TextView charCount = (TextView) findViewById(R.id.charCount);

			@Override
			public synchronized void afterTextChanged(Editable s) {


				wordCount.setText("W: " + String.valueOf(ChosenFile.countWords(entryTextET.getText().toString())));
				charCount.setText("C:  "
						+ Integer.toString(entryTextET.getText()
						.length()));

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
			                              int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start,
			                          int before, int count) {
				// TODO Auto-generated method stub

			}
		});

		// changes views color to match theme
		setViewsToTheme();

		paramsForTagsOval = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		paramsForTagsOval.setMargins(0, 0, 30, 0);




		// hides the boldButtonClicked, italics etc buttons
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			ViewSwitcher menuVS = (ViewSwitcher) findViewById(R.id.ChangeButtons);
			menuVS.setVisibility(View.INVISIBLE);
		}


		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean accelSupported = sensorMgr.registerListener(this,
				SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_GAME);

		if (!accelSupported) {
			// on accelerometer on this device
			sensorMgr.unregisterListener(this,
					SensorManager.SENSOR_ACCELEROMETER);
		}
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}


		TextView d = (TextView) findViewById(R.id.date);

		LinearLayout tags = (LinearLayout) findViewById(R.id.entryTags);



		if (bundle != null) {
			allTagsArrList = new ArrayList<>();
			tagsLoadedOnStartArrList = new ArrayList<>();
			deletedTagsArrList = new ArrayList<>();
			suggestionsArrList = new ArrayList<>();

			if (bundle.getStringArrayList("ALLTAGS").size() != 0)
				allTagsArrList.addAll(bundle.getStringArrayList("ALLTAGS"));
			if (bundle.getStringArrayList("OLDTAGS").size() != 0)
				tagsLoadedOnStartArrList.addAll(bundle.getStringArrayList("OLDTAGS"));
			if (bundle.getStringArrayList("DELETEDTAGS").size() != 0)
				deletedTagsArrList.addAll(bundle.getStringArrayList("DELETEDTAGS"));
			if (bundle.getStringArrayList("SUGGESTIONS").size() != 0) {
				suggestionsArrList = new ArrayList<>();
				suggestionsArrList.addAll(bundle.getStringArrayList("SUGGESTIONS"));

			}


			newFilename = bundle.getString("NEWNAME");
			currMood = bundle.getInt("MOOD");


			lastModified.setTimeInMillis(bundle.getLong("ENTRYTIME"));
			changeMoodText(currMood, null);


			fav = bundle.getString("FAV");
			job = bundle.getString("JOB");
			edited = bundle.getBoolean("EDITED");
			relationship = bundle.getString("PARTNER");
			locationName = bundle.getString("LOCATION");

			TextView tv;

			for (int j = 0; j < allTagsArrList.size(); j++) {
				tv = new TextView(this);
				tv.setId(amountOfCurrentTags);

				tv.setSingleLine(true);
				tv.setLayoutParams(paramsForTagsOval);
				tv.setTextSize(textSize);
				tv.setTextColor(getResources().getColor(R.color.UILightForeground));
				tv.setText(allTagsArrList.get(j).replaceAll("_", " "));

				if (Build.VERSION.SDK_INT > 15)
					tv.setBackground(getResources().getDrawable(
							R.drawable.drop_shadow_purple));
				else
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.drop_shadow_purple));
				tv.setPadding(24, 12, 24, 12);

				if (textSize > 12)
					tv.setTextSize(textSize - 4);
				else

					tv.setTextSize(textSize - 2);


				tags.addView(tv);
			}



			string_constant = bundle.getString("STRINGCONSTANT");
			filename = bundle.getString("FILENAME");
			chosenFile = new File(getFilesDir(), filename);
			name = bundle.getString("NAME");



		}
		else  {

			Intent i = getIntent();
			i.getExtras().get("FILENAME");
			sortedFilesArrList.addAll(i.getExtras().getStringArrayList("FILESARRAY"));
			currentFileFromDomus = i.getExtras().getInt("POSITION");
			filename = i.getExtras().getString("FILENAME");


			chosenFile = new File(getFilesDir(), filename);
			name = filename.replace(".txt", "").trim();

			lastModified.setTimeInMillis(chosenFile.lastModified());


			try {
				DataInputStream in = new DataInputStream(openFileInput(filename));
				try {
					if (filename.replaceAll(".txt", "").substring(0, 4)
							.equals("Temp") == true) {
						this.setTitle(getResources().getString(R.string.untitled));
					} else
						this.setTitle(filename.replaceAll(".txt", "").replaceAll(
								"_", " "));

				} catch (Exception e) {
					this.setTitle(filename.replaceAll(".txt", ""));
				}
				in = new DataInputStream(openFileInput(filename));



				in.readUTF();
				// text.setText(Html.fromHtml(text.getText().toString() +
				// in.readUTF()));
				string_constant = in.readUTF();

				in.close();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}



		entryTextTV.setText(Html.fromHtml(string_constant.replace("\n", "<br />")));
		entryTextET.setText(string_constant.replace("\n", "<br/>"));



/*
		if (Locale.getDefault().getCountry().equals("US"))
			d.setText(Html.fromHtml("<b>" + weekday[lastModified.get(Calendar.DAY_OF_WEEK)]
					+ " " + month[lastModified.get(Calendar.MONTH)] + " "
					+ lastModified.get(Calendar.DAY_OF_MONTH) + ", " + lastModified.get(Calendar.YEAR)
					+ "\t\t" + "</b>"
					+ MiscMethods.setHour(lastModified.get(Calendar.HOUR)) + ":"
					+ MiscMethods.setMinute(lastModified.get(Calendar.MINUTE))
					+ MiscMethods.ampm(lastModified.get(Calendar.HOUR))));
		else
			d.setText(Html.fromHtml("<b>" + weekday[lastModified.get(Calendar.DAY_OF_WEEK)]
					+ " " + lastModified.get(Calendar.DAY_OF_MONTH) + " "
					+ month[lastModified.get(Calendar.MONTH)] + ". " + lastModified.get(Calendar.YEAR)
					+ "\t\t" + "</b>" + lastModified.get(Calendar.HOUR) + ":"
					+ MiscMethods.setMinute(lastModified.get(Calendar.MINUTE))));


*/







	}







	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);
		File loadPics;

		int loadCount = 1;

		loadPics = new File(getFilesDir(), name + "(" + loadCount + ").png");
		if (picLoaded == false) {
			while (loadPics.exists() == true) {

				new LoadPic( loadPics)
						.execute("null");
				loadCount++;

				loadPics = new File(getFilesDir(), name + "(" + loadCount

				+ ").png");

			}

		}

		picLoaded = true;

	}

	@Override
	protected void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putStringArrayList("OLDTAGS", tagsLoadedOnStartArrList);
		b.putString("FAV", fav);
		b.putStringArrayList("ALLTAGS", allTagsArrList);
		b.putStringArrayList("DELETEDTAGS", deletedTagsArrList);
		b.putBoolean("EDITED", edited);
		b.putStringArrayList("SUGGESTIONS", suggestionsArrList);

		b.putString("PARTNER", relationship);
		b.putString("JOB", job);
		b.putString("LOCATION", locationName);
		b.putString("NEWNAME", newFilename);
		b.putInt("MOOD", currMood);

		b.putLong("ENTRYTIME", lastModified.getTimeInMillis());

		b.putString("STRINGCONSTANT", string_constant);
		b.putString("FILENAME", filename);
		b.putString("NAME", name);
	}

	private void setUITheme(){
		if (sp.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}else if(sp.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (sp.getString("Theme", "Default").equals("Material")){
			super.setTheme(R.style.Material);
		}
		else if(sp.getString("Theme", "Default").equals("Material 2")){
			super.setTheme(R.style.Material2);
		}else if(sp.getString("Theme", "Default").equals("Material 3")){
			super.setTheme(R.style.Material3);
		}
		else if(sp.getString("Theme", "Default").equals("Material 4")){
			super.setTheme(R.style.Material4);
		}
	}


	private void getLocale(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		new RetrieveLocation().execute("null");
	}
	



	private class RetrieveLocation extends AsyncTask<String, Void, String> {
		

		

		protected String doInBackground(String... urls) {
			if (hasConnection(ChosenFile.this) == true) {
				Geocoder geo = new Geocoder(ChosenFile.this);
				List<Address> address = null;
				try {
					address = geo.getFromLocation(latitude, longitude, 3);

					locationName = address.get(0).getAddressLine(0) + ", "
							+ address.get(0).getAddressLine(1);
					if (locationName.length() > 25)
						locationName = address.get(0).getAddressLine(1)
								.replaceAll(address.get(0).getPostalCode(), "")
								.replaceAll(address.get(0).getCountryCode(), "")
								.trim();
					return locationName;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					locationName = "";
					return locationName;
				}
			} else {
				return locationName;
			}
		}

		protected void onPostExecute(String feed) {

			TextView v = (TextView) findViewById(R.id.location);
		v.setText(feed);


		}
	}


	public void newTag(View v) {
		AlertDialog.Builder newTag = new AlertDialog.Builder(this);
		final AutoCompleteTextView tag = new AutoCompleteTextView(this);
		tag.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_dropdown_item_1line, suggestionsArrList);
		tag.setAdapter(adapter);
		tag.setThreshold(3);

		tag.requestFocus();

		newTag.setTitle(getResources().getString(R.string.new_tag));
		newTag.setView(tag);

		final LinearLayout tags = (LinearLayout) findViewById(R.id.entryTags);
		newTag.setNeutralButton(getResources().getString(R.string.add),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String temp = tag.getText().toString().trim();
						temp = temp.toString().replaceAll("[^a-zA-Z \\s]", "");

						if (temp.equals("") == false) {
							if (allTagsArrList.contains(temp) == false) {
								TextView tv = new TextView(context);
								tv.setId(amountOfCurrentTags);

								if (Build.VERSION.SDK_INT > 15)
									tv.setBackground(getResources()
											.getDrawable(
													R.drawable.drop_shadow_purple));
								else {
									tv.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.drop_shadow_purple));
								}
								tv.setLayoutParams(paramsForTagsOval);
								tv.setSingleLine(true);

								tv.setTextSize(textSize);
								tv.setTextColor(getResources().getColor(
										R.color.UILightForeground));
								tv.setText(temp);
								tags.addView(tv);

								if (textSize > 12)
								tv.setTextSize(textSize - 4);
								else

									tv.setTextSize(textSize - 2);

								allTagsArrList.add(temp.replaceAll("_", " "));
								if (deletedTagsArrList.contains(temp) == true)
									deletedTagsArrList.remove(temp);
								tv.setPadding(24, 12, 24, 12);

								shouldISave = true;
							}
						}
						if (allTagsArrList.size() != 0)
							tags.setClickable(true);

					}
				});
		newTag.create();
		final AlertDialog alert = newTag.create();
		alert.show();

		tag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String temp = tag.getText().toString().trim();
					temp = temp.toString().replaceAll("[^a-zA-Z \\s]", "");

					if (temp.trim().equals("") != true) {
						if (allTagsArrList.contains(temp) == false) {
							TextView tv = new TextView(context);
							tv.setId(amountOfCurrentTags);
							tv.setLayoutParams(paramsForTagsOval);
							tv.setSingleLine(true);
							if (Build.VERSION.SDK_INT > 15)
								tv.setBackground(getResources().getDrawable(
										R.drawable.drop_shadow_purple));
							else
								tv.setBackgroundDrawable(getResources()
										.getDrawable(
												R.drawable.drop_shadow_purple));
							tv.setTextSize(textSize);
							tv.setTextColor(getResources().getColor(
									R.color.UILightForeground));
							tv.setPadding(24, 12, 24, 12);

							if (textSize > 12)
								tv.setTextSize(textSize - 4);
							else

								tv.setTextSize(textSize - 2);


							tv.setText(temp);
							tags.addView(tv);
							allTagsArrList.add(temp.replaceAll("_", " "));

							if (deletedTagsArrList.contains(temp) == true)
								deletedTagsArrList.remove(temp);
							shouldISave = true;
						}
						return true;
					} else
						return true;
				}
				return false;
			}
		});
	}

	public void addAudio(MenuItem m) throws IOException {
		createNewAudio(false);
	}

	public void playAudio(View view) {
		mPlayer.start();

		ImageSwitcher switchPlayPause = (ImageSwitcher) mView
				.findViewById(R.id.playPause);
		switchPlayPause.showNext();
	}

	public void pauseAudio(View view) {
		mPlayer.pause();

		ImageSwitcher switchPlayPause = (ImageSwitcher) mView
				.findViewById(R.id.playPause);
		switchPlayPause.showPrevious();
	}

	public void replay(View v) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		ImageSwitcher switchPlayPause = (ImageSwitcher) mView
				.findViewById(R.id.playPause);
		mPlayer.reset();
		mPlayer.setDataSource(audioFile.getPath());
		mPlayer.prepare();

		if (switchPlayPause.getCurrentView().equals(
				mView.findViewById(R.id.pause)) == true) {
			switchPlayPause.showNext();
		}
	}

	public void listenAudio(MenuItem v) throws FileNotFoundException {
		mPlayer = new MediaPlayer();

		makeAudioDialog();
		try {
			mPlayer.setDataSource(audioFile.getPath());
			audioPlayer.show();

			mPlayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void swapViews(MenuItem m) throws IOException {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		ViewSwitcher menuVS = (ViewSwitcher) findViewById(R.id.ChangeButtons);

		final ViewSwitcher vs = (ViewSwitcher) findViewById(R.id.ChangeText);

		try {
			menuVS.showNext();
		} catch (Exception e) {

		}
		vs.showNext();
		if (vs.getCurrentView().equals(entryTextTV)) {


			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)

				menuVS.setVisibility(View.INVISIBLE);
			try {
				DataOutputStream dos = new DataOutputStream(openFileOutput(
						filename, Context.MODE_PRIVATE));

				m.setTitle(getResources().getString(R.string.EDIT));
				m.setIcon(R.drawable.white_edit);
				string_constant = entryTextET.getText().toString().trim();
				entryTextTV.setText(Html.fromHtml(string_constant.replace("\n",
						"<br />")));
				entryTextET.setText(null);
				
				
				dos.writeUTF(name + "\n");
				dos.writeUTF(string_constant);

				dos.flush();
				dos.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			chosenFile.setLastModified(lastModified.getTimeInMillis());

			File changeDate;
			int t = 1;
			changeDate = new File(getFilesDir(),
					filename.replaceAll(".txt", "") + "(" + t + ").png");
			while (changeDate.exists() == true) {
				changeDate.setLastModified(lastModified.getTimeInMillis());
				t++;
				changeDate = new File(getFilesDir(), filename.replaceAll(
						".txt", "") + "(" + t + ").png");
			}
			changeDate = new File(getFilesDir(),
					filename.replaceAll(".txt", "") + ".mpeg4");
			if (changeDate.exists() == true) {
				changeDate.setLastModified(lastModified.getTimeInMillis());
			}

			entryTextET.setClickable(false);
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			// nameOfFile.setTextColor(getResources().getColor(
			// R.color.Legit_Purple));
		} else {

			getResources().getConfiguration();
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
menuVS.setVisibility(View.VISIBLE);
			m.setTitle(getResources().getString(R.string.DONE));
			m.setIcon(R.drawable.white_accept);
			entryTextET.setText(null);
			entryTextET.append(string_constant + " ");
			entryTextET.setClickable(true);
			entryTextET.setFocusableInTouchMode(true);
			//entryText.requestFocus();
			final InputMethodManager inputMethodManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.showSoftInput(entryTextET,
					InputMethodManager.SHOW_IMPLICIT);
			// nameOfFile.setTextColor(getResources().getColor(R.color.Black));
		}
	}


	public void tagsClicked(View v) {
		AlertDialog.Builder tagsList = new AlertDialog.Builder(this);
		final LinearLayout tags = (LinearLayout) findViewById(R.id.entryTags);

		CharSequence[] itemsT = new CharSequence[allTagsArrList.size()];

		for (int i = 0; i < allTagsArrList.size(); i++) {
			itemsT[i] = allTagsArrList.get(i).replaceAll("_", " ");
		}
		tagsList.setTitle(R.string.edit_tags_dialog_title);
		tagsList.setNeutralButton(R.string.CANCEL, null);
		tagsList.setSingleChoiceItems(itemsT, -1,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {
						deletedTagsArrList.add(allTagsArrList.get(item));
						allTagsArrList.remove(item);

						tags.removeViewAt(item);
						if (allTagsArrList.size() == 0)
							tags.setClickable(false);

						dialog.dismiss();
					}
				});
		tagsList.show();
	}

	private void makeAudioDialog() {
		audioPlayer = new AlertDialog.Builder(this);
		mView = View.inflate(this, R.layout.audio_dialog_layout, null);
		audioPlayer.setView(mView);

		audioPlayer.setNeutralButton(R.string.DISMISS, null);
		audioPlayer.setNegativeButton(R.string.DELETE,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File f = new File(getFilesDir(), name + ".mpeg4");
						f.delete();
						mPlayer.reset();

						invalidateOptionsMenu();
					}
				});

		/**
		 * if (android.os.Build.VERSION.SDK_INT <= 18) { audioPlayer
		 * .setOnDismissListener(new DialogInterface.OnDismissListener() {
		 * 
		 * @Override public void onDismiss(DialogInterface dialog) {
		 *           mPlayer.pause(); mPlayer.reset();
		 * 
		 *           try {
		 * 
		 *           mPlayer.setDataSource(audioFile.getPath());
		 *           mPlayer.prepare();
		 * 
		 *           } catch (IllegalStateException e) { e.printStackTrace(); }
		 *           catch (IOException e) { e.printStackTrace(); } } }); }
		 **/

		audioPlayer.setCancelable(false);
		audioPlayer.setPositiveButton(R.string.RE_RECORD,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							createNewAudio(true);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
		audioPlayer.create();
	}

	public void addPic(View m) {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		
		PopupMenu popup = new PopupMenu(this, m);

		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.pic_button_popup, popup.getMenu());
		popup.show();
	}

	public void useCamera(MenuItem m) throws IOException {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		outputFileUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), System.currentTimeMillis()
				+ ".png"));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, 7884);
	}

	public void choosePhoto(MenuItem m) {
		Intent choosePic = new Intent(Intent.ACTION_PICK);
		choosePic.setType("image/*");
		startActivityForResult(choosePic, 100);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				passcodeCheck = false;
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
				passcodeCheck = true;
			}
		}

		if (requestCode == 100) {
			passcodeCheck = false;
			if (resultCode == RESULT_OK) {

				try {

					Uri selectedImage = data.getData();
					Bitmap bitmap;

					InputStream imageStream = null;

					imageStream = getContentResolver().openInputStream(
							selectedImage);

					bitmap = BitmapFactory.decodeStream(imageStream);
					// saveEntryText();
					final File picFile = new File(getFilesDir(), name + "("
							+ imageCount + ")" + ".png");
					picFile.createNewFile();
					FileOutputStream imageOutput = new FileOutputStream(picFile);
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
					imageOutput.write(bytes.toByteArray());
					imageOutput.close();

					final ImageView p;

					p = new ImageView(this);
					picListener(p);
					p.setId(imageCount);
					p.setAdjustViewBounds(true);

					imageCount++;

					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inSampleSize = 2;
					opt.inPreferQualityOverSpeed = false;
					Bitmap resized = BitmapFactory.decodeFile(
							picFile.getAbsolutePath(), opt);
					p.setImageBitmap(resized);

					final LinearLayout layoutHoldingPics = (LinearLayout) findViewById(R.id.layoutWithPWithinEntry);
					layoutHoldingPics.addView(p);

				}  catch (IOException e) {
					Log.e("Errr", e.toString());
				}
			}
		}
		switch (requestCode) {
		case 7884:
			passcodeCheck = false;
			if (resultCode == RESULT_OK) {
				try {
					Uri selectedImage = outputFileUri;
					getContentResolver().notifyChange(selectedImage, null);
					ContentResolver cr = getContentResolver();
					Bitmap bitmap;
					// saveEntryText();
					final File picFile = new File(getFilesDir(), name + "("
							+ imageCount + ")" + ".png");
					picFile.createNewFile();

					imageCount++;

					final ImageView p;
					p = new ImageView(this);

					picListener(p);
					p.setId(imageCount);
					p.setAdjustViewBounds(true);

					FileOutputStream imageOutput = new FileOutputStream(picFile);
					bitmap = Bitmap
							.createBitmap(android.provider.MediaStore.Images.Media
									.getBitmap(cr, selectedImage));
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
					imageOutput.write(bytes.toByteArray());
					imageOutput.close();

					if (imageCount > 1) {
					

						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inSampleSize = 2;
						opt.inPreferQualityOverSpeed = false;
						Bitmap resized = BitmapFactory.decodeFile(
								picFile.getAbsolutePath(), opt);
						p.setImageBitmap(resized);

						final LinearLayout layoutHoldingPics = (LinearLayout) findViewById(R.id.layoutWithPWithinEntry);
						layoutHoldingPics.addView(p);
						// bitmap.recycle();
						// resized.recycle();
					}
				}  catch (IOException e) {

					Log.e("Errr", e.toString());
				}
			}
		}
	}

	public void createNewAudio(Boolean re_record) throws IOException {
		final MediaRecorder audio = new MediaRecorder();
		audio.setAudioSource(MediaRecorder.AudioSource.MIC);
		audio.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		audio.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		File aFile;
		if (re_record == true) {
			aFile = new File(getFilesDir(), "temp_dir" + ".mpeg4");
			aFile.createNewFile();
			audio.setOutputFile(getFilesDir() + "temp_dir" + ".mpeg4");
		} else {
			aFile = new File(getFilesDir(), name + ".mpeg4");
			aFile.createNewFile();
			audio.setOutputFile(aFile.getPath());
		}

		try {
			audio.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final ProgressDialog progress = new ProgressDialog(ChosenFile.this);
		progress.setTitle(R.string.RECORDING);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setButton(getResources().getString(R.string.STOP),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						progress.dismiss();
						audio.stop();
						audio.release();
					}
				});
		progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface p1) {
				audio.stop();
				audio.release();
			}
		});
		audio.start();
		progress.show();

		invalidateOptionsMenu();
	}

	public void delete(MenuItem m) {
		deleteThis(new View(this));
	}

	public void deleteThis(View v) {
		showing = true;
		AlertDialog.Builder t = new AlertDialog.Builder(this);
		t.setMessage(R.string.delete_entry_dialog_message_);
		t.setPositiveButton(R.string.DELETE,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						deleteEverything();
				}
		});
				
		t.setNegativeButton(R.string.NO, null);
		/**
		 * if (Build.VERSION.SDK_INT >16) { t.setOnDismissListener(new
		 * DialogInterface.OnDismissListener() {
		 * 
		 * @Override public void onDismiss(DialogInterface dialog) { showing =
		 *           false; } }); }
		 **/
		t.setCancelable(false);
		t.show();
	}

	
	private void deleteEverything() {
		DocumentBuilderFactory factory;
		TransformerFactory tFactory;
		Transformer transformer = null;
		DocumentBuilder builder = null;


		Document doc = null;
		DOMSource source = null;
		StreamResult result = null;



		File f = new File(getFilesDir(), filename);
		f.delete();
		filename = filename.replace(".txt", "");
		f = new File(getFilesDir(), filename + ".mpeg4");
		f.delete();

		int t = 1;
		f = new File(getFilesDir(), filename + "(" + t
				+ ").png");
		while (f.exists() == true) {
			f.delete();
			t++;
			f = new File(getFilesDir(), filename + "(" + t
					+ ").png");
		}


		factory = DocumentBuilderFactory.newInstance();
		File filesXML = new File(getFilesDir(), "Files.xml");
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(filesXML);

			tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer();
			source = new DOMSource(doc);
			result = new StreamResult(filesXML);
		}catch(Exception x){

		}





		Element element;
		Node node;
		NodeList nodeList;

		Node parent = doc.getElementsByTagName("Files").item(0);
		nodeList = parent.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);

			if (node.getNodeName() != "#text") {

				element = (Element) node;
				if (element.getAttribute("name").equals(
						filename.replaceAll(" ", "_") + ".txt")) {
					parent.removeChild(node);
					node = nodeList.item(i - 1);
					if (node.getNodeName() == "#text")
					parent.removeChild(node);

					doc.normalize();
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}


		finish();

	}

	@Override
	protected void onPause() {
		super.onPause();


		final LinearLayout tags = (LinearLayout) findViewById(R.id.entryTags);
		final ViewSwitcher vs = (ViewSwitcher) findViewById(R.id.ChangeText);

		if (vs.getCurrentView().equals(entryTextET)) {
			string_constant = entryTextET.getText().toString().trim();
			entryTextTV.setText(Html.fromHtml(string_constant.replace("\n", "<br />")));
		}
		if (sensorMgr != null) {
			sensorMgr.unregisterListener(this,
					SensorManager.SENSOR_ACCELEROMETER);
			sensorMgr = null;
		}
		if (shouldISave == true) {
			try {
				DataOutputStream dos = new DataOutputStream(openFileOutput(
						filename, Context.MODE_PRIVATE));
				dos.writeUTF(name + "\n");
				try{
				dos.writeUTF(string_constant.trim());
				}
				catch(NullPointerException e){
					deleteEverything();
				}

				dos.flush();
				dos.close();

				chosenFile.setLastModified(lastModified.getTimeInMillis());
				File changeDate;
				int t = 1;
				changeDate = new File(getFilesDir(), filename.replaceAll(
						".txt", "") + "(" + t + ").png");
				while (changeDate.exists() == true) {
					changeDate.setLastModified(lastModified.getTimeInMillis());
					t++;
					changeDate = new File(getFilesDir(), filename.replaceAll(
							".txt", "") + "(" + t + ").png");
				}
				changeDate = new File(getFilesDir(), filename.replaceAll(
						".txt", "") + ".mpeg4");
				if (changeDate.exists() == true) {
					changeDate.setLastModified(lastModified.getTimeInMillis());
				}

				tags.setClickable(false);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (newFilename.equals("") != true) {
				chosenFile = new File(getFilesDir(), filename);
				if (chosenFile.exists())
					chosenFile.renameTo(new File(getFilesDir(), newFilename + ".txt"));
				chosenFile = new File(getFilesDir(), filename.replaceAll(".txt", "")
						+ ".mpeg4");
				if (chosenFile.exists())
					chosenFile.renameTo(new File(getFilesDir(), newFilename + ".mpeg4"));

				int picNum = 1;
				chosenFile = new File(getFilesDir(), filename.replaceAll(".txt", "")
						+ "(" + picNum + ").png");

				while (chosenFile.exists()) {
					chosenFile.renameTo(new File(getFilesDir(), newFilename + "("
							+ picNum + ").png"));
					picNum++;
					chosenFile = new File(getFilesDir(), filename.replaceAll(".txt", "")
							+ "(" + picNum + ").png");
				}

			}
			tags();

			Intent returnIntent = new Intent();
			this.setResult(RESULT_OK, returnIntent);
			finish();
		}
	}

	public void tags() {



		TransformerFactory tFactory;

		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;
		Document doc = null;
		DOMSource source = null;
		StreamResult result = null;

		Transformer transformer = null;


		factory = DocumentBuilderFactory.newInstance();
		File filesXML = new File(getFilesDir(), "Files.xml");




			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(filesXML);

				tFactory = TransformerFactory.newInstance();

				transformer = tFactory.newTransformer();
				source = new DOMSource(doc);
				result = new StreamResult(filesXML);




			builder = factory.newDocumentBuilder();
			doc = builder.parse(filesXML);


		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
			catch(TransformerException d){

			}






		Element element;
		Node node;
		NodeList nodeList = null;
		try {
			node = doc.getElementsByTagName("Files").item(0);
			nodeList = node.getChildNodes();
		} catch (Exception e) {

		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);

			if (node.getNodeName() != "#text") {

				element = (Element) node;
				if (element.getAttribute("name").equals(
						filename.replaceAll(" ", "_"))) {

					if (newFilename.equals("") != true) {
						element.setAttribute("name", newFilename + ".txt");
					}

					for (int y = 0; y < allTagsArrList.size(); y++)
						element.setAttribute("tag" + (y + 1), allTagsArrList.get(y)
								.replaceAll(" ", "_"));

					if (allTagsArrList.size() == 0) {
						for (int j = 0; j < tagsLoadedOnStartArrList.size(); j++)
							element.removeAttribute("tag" + (j + 1));
					}
					if (allTagsArrList.size() < tagsLoadedOnStartArrList.size()) {
						for (int j = allTagsArrList.size(); j < tagsLoadedOnStartArrList.size(); j++)
							element.removeAttribute("tag" + (j + 1));
					}

					element.setAttribute("tags",
							Integer.toString((allTagsArrList.size())));
					element.setAttribute("pics",
							Integer.toString(imageCount - 1));
					if (fav.equals("true"))
						element.setAttribute("favoriteSelectedFile", "true");
					else
						element.setAttribute("favoriteSelectedFile", "false");


					CharSequence moods[] = getResources().getStringArray(R.array.moods_arr);
					element.setAttribute("mood", moods[currMood].toString());

					doc.normalize();
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}






	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms.
			if ((curTime - lastUpdate) > 100) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				float speed = Math.abs(values[SensorManager.DATA_X]
						+ values[SensorManager.DATA_Y]
						+ values[SensorManager.DATA_Z] - last_x - last_y
						- last_z)
						/ diffTime * 10000;

				if (speed > 2700 && showing == false) {
					deleteThis(new View(this));
				}
				last_x = values[SensorManager.DATA_X];
				last_y = values[SensorManager.DATA_Y];
				last_z = values[SensorManager.DATA_Z];
			}
		}
	}

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void email(View mi) {

		Intent mail = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		mail.setType("text/plain");
		mail.putExtra(Intent.EXTRA_SUBJECT, filename.replaceAll(".txt", ""));
		mail.putExtra(Intent.EXTRA_TEXT, string_constant);
		mail.setData(Uri.parse("mailto:")); // or just "mailto:" for blank
		mail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such
														// that when user
														// returns to your app,
														// your app is
														// displayed, instead of
														// the emailButtonClicked app.
		try {

			startActivity(mail);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.feedback_error),
					Toast.LENGTH_LONG).show();
		}
	}

	public void shareFromMenuItem(MenuItem m) {
		/*
		 * View v = findViewById(R.id.shareMenuItem); PopupMenu popup = new
		 * PopupMenu(this, v); MenuInflater inflater = popup.getMenuInflater();
		 * inflater.inflate(R.dropDownMenuForSelectedFile.share_menu, popup.getMenu()); popup.show();
		 */

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.share_alert, null);
		// Get the layout inflater

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(v);
		builder.setTitle(getResources().getString(R.string.share_dialog_title));
		builder.setNeutralButton(
				getResources().getString(R.string.share_dialog_neutral_button),
				null);
		builder.create();
		builder.show();
	}

	public void fav(View v) {
		shouldISave = true;


		if (favTV == null) {
			favTV = (TextView) findViewById(R.id.add_like_button);

		}

		if (favTV.getText().toString().equals("☆")) {
			favTV.setText(Html.fromHtml("★"));
			fav = "true";
		} else {
			favTV.setText(Html.fromHtml("☆"));
			fav = "false";
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void bold(View v) {

		entryTextET.getText().insert(entryTextET.getSelectionStart(), "<b>");
		entryTextET.getText().insert(entryTextET.getSelectionEnd(), "</b>");
		entryTextET.setSelection(entryTextET.getSelectionStart() - 4);

	}

	public void italic(View v) {
		entryTextET.getText().insert(entryTextET.getSelectionStart(), "<i>");
		entryTextET.getText().insert(entryTextET.getSelectionEnd(), "</i>");
		entryTextET.setSelection(entryTextET.getSelectionStart() - 4);

	}

	public void underline(View v) {

		entryTextET.getText().insert(entryTextET.getSelectionStart(), "<u>");
		entryTextET.getText().insert(entryTextET.getSelectionEnd(), "</u>");
		entryTextET.setSelection(entryTextET.getSelectionStart() - 4);

	}


	public static boolean hasConnection(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnected();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		passcodeOn = sp.getBoolean("Password", false);
		if (passcodeOn == true) {
			if (passcodeCheck == true) {
				Intent i = new Intent(this, Passcode.class);
				startActivityForResult(i, 1);
			}
			passcodeCheck = true;
		}

	}

	/*
	public void changeTime(View v) {
		AlertDialog.Builder editTime = new AlertDialog.Builder(this);
		mView = View.inflate(this, R.layout.date_selection_layout, null);
		editTime.setView(mView);


		final TimePicker tp = (TimePicker) mView.findViewById(R.id.time_pick);
		final DatePicker dp = (DatePicker) mView.findViewById(R.id.date_pick);
		final TextView d = (TextView) findViewById(R.id.date_selection_layout);

		tp.setCurrentHour(lastModified.get(Calendar.HOUR));
		tp.setCurrentMinute(lastModified.get(Calendar.MINUTE));
		dp.updateDate(lastModified.get(Calendar.YEAR), lastModified.get(Calendar.MONTH),
				lastModified.get(Calendar.DAY_OF_MONTH));

		editTime.setNeutralButton(R.string.DISMISS, null);

		editTime.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String[] month;
						if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

							//month = getResources().getStringArray(R.array.non_abrev_months_arr);
						} else
							month = getResources().getStringArray(
									R.array.months_abrev);

						String[] weekday = getResources().getStringArray(
								R.array.abrev_weekdays);


						lastModified.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute(), 0);

						lastModified.setTimeInMillis(lastModified.getTimeInMillis());

						if (Locale.getDefault().getCountry().equals("US"))
							d.setText(Html.fromHtml("<b>"
									+ weekday[lastModified.get(Calendar.DAY_OF_WEEK)]
									+ ", "
									+ month[lastModified.get(Calendar.MONTH)]
									+ " "
									+ lastModified.get(Calendar.DAY_OF_MONTH)
									+ ", "
									+ lastModified.get(Calendar.YEAR)
									+ "</b>"
									+ "<br />"
									+ MiscMethods.setHour(lastModified.get(Calendar.HOUR))
									+ ":"
									+ MiscMethods
											.setMinute(lastModified.get(Calendar.MINUTE))
									+ MiscMethods.ampm(lastModified.get(Calendar.HOUR))));
						else
							d.setText(Html.fromHtml("<b>"
									+ weekday[lastModified.get(Calendar.DAY_OF_WEEK)]
									+ ", "
									+ lastModified.get(Calendar.DAY_OF_MONTH)
									+ " "
									+ month[lastModified.get(Calendar.MONTH)]
									+ ". "
									+ lastModified.get(Calendar.YEAR)
									+ "</b>"
									+ "<br />"
									+ lastModified.get(Calendar.HOUR)
									+ ":"
									+ MiscMethods
											.setMinute(lastModified.get(Calendar.MINUTE))));
					}
				});

		editTime.create();
		edit

	public void changeTimeMenu(MenuItem v) {
		changeTime(new View(this));
	}
*/
	public void renameFiles(MenuItem v) {
		final AlertDialog.Builder newName = new AlertDialog.Builder(this);
		final EditText renamed = new EditText(this);
		renamed.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_CAP_WORDS);

		renamed.requestFocus();
		newName.setTitle(getResources().getString(R.string.rename_file_dialog));
		newName.setView(renamed);
		if (filename.substring(0, 4).equals("Temp") != true) {
			renamed.setText(filename.replaceAll(".txt", "")
					.replaceAll("_", " "));

			renamed.selectAll();
		}

		newName.setPositiveButton(getResources().getString(R.string.OK),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (renamed
								.getText()
								.toString()
								.trim()
								.equals(filename.replaceAll(".txt", "")
										.replaceAll("_", " ")) != true
								&& renamed.getText().toString().trim()
										.equals("") != true) {
							String ts = renamed.getText().toString().trim()
									.replaceAll("[^\\p{L} \\s 0-9]", "")
									.replaceAll(" ", "_");
							File ft = new File(context.getFilesDir(), ts + ".txt");
							if (ft.exists() == true) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.file_exists_err),
										Toast.LENGTH_LONG).show();
							} else {
								rename(ts.replaceAll("_", " "));
								newFilename = ts.replaceAll(" ", "_");

							}
						}

					}
				});
		newName.setNegativeButton(getResources().getString(R.string.DISMISS),
				null);
		newName.create();
		final AlertDialog alert = newName.create();
		alert.show();
		final String title = this.getTitle().toString();

		renamed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (renamed.getText().toString().trim()
							.replaceAll("[^\\p{L} \\s 0-9]", "").equals(title) != true
							&& renamed.getText().toString().trim().equals("") != true) {

						File ft = new File(context.getFilesDir(), renamed.getText()
								.toString().trim()
								.replaceAll("[^\\p{L} \\s 0-9]", "")
								.replaceAll(" ", "_")
								+ ".txt");
						if (ft.exists() == true) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.file_exists_err)
											+ filename, Toast.LENGTH_LONG)
									.show();
						} else {
							rename(renamed.getText().toString().trim());
							newFilename = renamed.getText().toString().trim()
									.replaceAll("[^\\p{L} \\s 0-9]", "")
									.replaceAll(" ", "_");
							alert.dismiss();
						}
					}
					return true;
				}
				return false;
			}
		});
	}

	protected void rename(String string) {
		this.setTitle(string);

	}


	public void picListener(ImageView v) {

		final LinearLayout layoutHoldingPics = (LinearLayout) findViewById(R.id.layoutWithPWithinEntry);
		final ImageView p = v;
		p.setPadding(0, 0, 5, 0);

		p.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {

				launchPhotoViewer(v);
			}
		});
		p.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(final View v) {
				deletePic.setPositiveButton(R.string.YES,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								layoutHoldingPics.removeView(v);
								File picDeletion = new File(getFilesDir(), name
										+ "(" + v.getId() + ").png");
								File renamePic;
								int temp = v.getId();
								picDeletion.delete();
								if (temp < imageCount) {
									for (int i = temp; i < imageCount; i++) {
										renamePic = new File(getFilesDir(),
												name + "(" + (i + 1) + ").png");
										renamePic.renameTo(new File(
												getFilesDir(), name + "(" + (i)
												+ ").png"));
									}
								}
								imageCount--;

							}
						});
				deletePic.setNegativeButton(R.string.NO, null);
				deletePic.show();

				return false;
			}
		});
	}

	private class LoadData extends AsyncTask<String, Integer, String> {
		LinearLayout tags;
		ArrayList<String> temp = new ArrayList<>();
		String latitude = "";
		String longitude = "";


		TransformerFactory tFactory;

		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;
		Document doc = null;







		public LoadData(LinearLayout tags) {
			this.tags = tags;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (latitude.equals("") == false && longitude.equals("") == false)
				getLocale(Double.parseDouble(latitude),
						Double.parseDouble(longitude));


			if (fav.equals("true"))
				favTV.setText(Html.fromHtml("★"));



			if (relationship.equals("") && job.equals("")){

				partnerTV.setText(null);
				occupationTV.setText(null);

			}else {
				partnerTV.setText(relationship);
				occupationTV.setText(job);
			}
			for (int y = 0; y < allTagsArrList.size(); y++) {
				TextView tv = new TextView(context);
				tv.setId(amountOfCurrentTags);
				tv.setLayoutParams(paramsForTagsOval);
				tv.setSingleLine(true);
				tv.setTextSize(textSize);
				tv.setTextColor(getResources().getColor(R.color.UILightForeground));

				if (Build.VERSION.SDK_INT > 15)
					tv.setBackground(getResources().getDrawable(
							R.drawable.drop_shadow_purple));
				else
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.drop_shadow_purple));
				tv.setPadding(24, 12, 24, 12);

				if (textSize > 12)
					tv.setTextSize(textSize - 4);
				else

					tv.setTextSize(textSize - 2);

				tv.setText(allTagsArrList.get(y).replaceAll("_", " "));
				tags.addView(tv);



			}

			changeMoodText(currMood, null);
		}

		@Override
		protected String doInBackground(String... params) {



			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			File filesXML = new File(getFilesDir(), "Files.xml");
			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(filesXML);

				tFactory = TransformerFactory.newInstance();

			} catch (SAXException e) {
			} catch (ParserConfigurationException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {

				Element element;
				Node node;
				NodeList nodeList;

				nodeList = doc.getElementsByTagName("Files");
				node = nodeList.item(0);
				nodeList = node.getChildNodes();


					for (int z = 0; z < nodeList.getLength(); z++) {
						node = nodeList.item(z);

						if (node.getNodeName() != "#text") {

							element = (Element) node;



							if (element.getAttribute("name").equals(
									filename.replaceAll(" ", "_"))) {

								relationship = element.getAttribute("partner");
								job = element.getAttribute("occupation");
								fav = element.getAttribute("favoriteSelectedFile");

								String moods[] =  getResources().getStringArray(R.array.moods_arr);



								String mood = element.getAttribute("mood");
								for (int i = 0; i < moods.length; i++){
									if (mood.equals(moods[i])){
										currMood = i;
									}
								}

								amountOfCurrentTags = Integer.parseInt(element
										.getAttribute("tags"));


								for (int y = 0; y < amountOfCurrentTags; y++) {
									tagsLoadedOnStartArrList.add(element.getAttribute("tag"
											+ (y + 1)));
									allTagsArrList.add(element.getAttribute("tag"
											+ (y + 1)));


								}


								try {
									if (element.getAttribute("latitude")
											.equals("") == true) {

										locationName = "NULL";
									} else {
										locationName = element
												.getAttribute("locationName");
										latitude = element.getAttribute(
												"latitude").trim();
										longitude = element.getAttribute(
												"longitude").trim();

									}
								} catch (Exception e4) {

								}

							}

							// if current node isnt for the file in question, it searches through all its tags and adds those that are unique to suggestionsArrList
							else{
								String tagsInCurrentNode = ((Element) node).getAttribute("tags");

								for (int j = 0; j < Integer.parseInt(tagsInCurrentNode); j ++)
									if (!suggestionsArrList.contains(((Element) node).getAttribute("tag" + Integer.toString(j + 1)))) {
										suggestionsArrList.add(((Element) node).getAttribute("tag" + Integer.toString(j + 1)).replaceAll("_", " "));
									}
							}
						}
					}
			} catch (Exception e) {

			}


			return null;
		}

	}

	private class LoadPic extends AsyncTask<String, Integer, Bitmap> {

		File loadPics;

		public LoadPic(File loadPics) {
			this.loadPics = loadPics;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			
			if (loadPics.length() > 2300000 * 1.3){
			 BitmapFactory.decodeFile(loadPics.getAbsolutePath(), opt);
			
			opt.inPreferQualityOverSpeed = false;
			int size = 0;
			int height = opt.outHeight, width = opt.outWidth;
			
			
			while (height > 1800 && width > 1500){
				height = height /2;
				width = width / 2;
				
				size +=2;
			}

			opt.inSampleSize = size;
			}
			opt.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFile(loadPics.getAbsolutePath(),
					opt);
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bm) {

			ImageView loadedP;
			loadedP = new ImageView(context);
			loadedP.setAdjustViewBounds(true);
			loadedP.setPadding(0, 0, 5, 0);
			final LinearLayout layoutHoldingPics = (LinearLayout) findViewById(R.id.layoutWithPWithinEntry);

			loadedP.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {

					launchPhotoViewer(v);
				}
			});

			loadedP.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(final View v) {
					deletePic.setPositiveButton(R.string.YES,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									layoutHoldingPics.removeView(v);
									File picDeletion = new File(getFilesDir(),
											name + "(" + v.getId() + ").png");
									File renamePic;
									int temp = v.getId();
									picDeletion.delete();
									if (temp < imageCount) {
										for (int i = temp; i < imageCount; i++) {
											renamePic = new File(getFilesDir(),
													name + "(" + (i + 1)
															+ ").png");
											renamePic.renameTo(new File(
													getFilesDir(), name + "("
													+ (i) + ").png"));
										}
									}
									imageCount--;

								}
							});
					deletePic.setNegativeButton(R.string.NO, null);
					deletePic.show();

					return false;
				}
			});

			loadedP.setId(imageCount);
			imageCount++;

			loadedP.setImageBitmap(bm);
			layoutHoldingPics.addView(loadedP);

		}

	}


	
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}
	public static synchronized int countWords(String s){

		int wordCount = 0;

		boolean word = false;
		int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++) {
			// if the char is a letter, word = true.
			if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
				word = true;
				// if char isn't a letter and there have been letters before,
				// counter goes up.
			} else if (!Character.isLetter(s.charAt(i)) && word) {
				wordCount++;
				word = false;
				// last word of String; if it doesn't end with a non letter, it
				// wouldn't count without this.
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				wordCount++;
			}
		}
		return wordCount;
	}


	protected void launchPhotoViewer(View v) {
		Intent i = new Intent(this, PhotoViewer.class);
		i.putExtra("PHOTO", filename.replace(".txt", "") + "(" + Integer.toString(v.getId()) + ").png");
		//i.putExtra("FILENAME", sortedFiles.get(position));
		startActivityForResult(i, 2);


	}

	public void changeMood(View v)
	{

		shouldISave = true;
		AlertDialog.Builder selectMood = new AlertDialog.Builder(this);
		CharSequence itemsT[] = getResources().getStringArray(R.array.moods_arr);


		final TextView mood = (TextView) findViewById(R.id.mood);

		selectMood.setTitle("Select Mood");
		selectMood.setNeutralButton(R.string.DISMISS, null);
		selectMood.setSingleChoiceItems(itemsT, currMood,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {

						currMood = item;

						changeMoodText(currMood, mood);
						dialog.dismiss();
					}
				});
		selectMood.show();


	}


	private void changeMoodText(int mood, TextView moodTV){
		if (moodTV == null)
			moodTV = (TextView) findViewById(R.id.mood);

		moodTV.setText(MiscMethods.getMood(mood, moodTV));
	}
}