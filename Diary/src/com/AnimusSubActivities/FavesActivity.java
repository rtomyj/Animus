package com.AnimusSubActivities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.AnimusAdapters.EntriesAdapter;
import com.AnimusMainActivities.NewEntry;
import com.AnimusMainActivities.PicEntries;
import com.AnimusMainActivities.Tags;
import com.UtilityClasses.AnimusMiscMethods;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rtomyj.Diary.R;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class FavesActivity extends AppCompatActivity {

	private IInAppBillingService inAppBillingService = null;
	private ServiceConnection connectionToIAP;
	
	private AdView ad = null;
	private int fileToDelete;
	private boolean isDrawerOpen = false;
	private ListView drawerLV;
	private ActionBarDrawerToggle mDrawerToggle;
	private SideNavAdapter drawerAdapter;

	private boolean isPasscodeOn = false;
	private boolean hasPasscodeChecked = true;

    private boolean loadedFromBundle = false;



    private EntriesAdapter filesAdapter;

	private ArrayList<String> firstTagArrList = new ArrayList<>();
	private ArrayList<String> secondTagArrList = new ArrayList<>();
	private ArrayList<String> thirdTagArrList = new ArrayList<>();
	private ArrayList<String> sortedFilesArrList = new ArrayList<>();
	private ArrayList<Boolean> favesArrList = new ArrayList<>();

    private int currentVisibleItem = -1;


    private TextView month_and_year;

    private String[] monthsArr ;
    private Context context;
    private SharedPreferences sp;


    private LoadDate Date;


	// for sidenav
	private String faveNum;
	private String uniqueTagsNum;
	private String textFilesNum;
	private String noteFilesNum;
	private String picFilesNum;
	
	@Override
	protected void onDestroy() {
		if (ad != null) {
			ad.destroy();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		if (ad != null) {
			ad.pause();
		}
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				hasPasscodeChecked = false;
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
				hasPasscodeChecked = true;
			}
		}
		if (requestCode == 2) {

			if (resultCode == RESULT_OK) {
				hasPasscodeChecked = false;
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
				hasPasscodeChecked = false;
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		isPasscodeOn = PreferenceManager
				.getDefaultSharedPreferences(this).getBoolean("Password", false);
		if (isPasscodeOn == true) {
			if (hasPasscodeChecked == true) {
				Intent i = new Intent(this, Passcode.class);
				startActivityForResult(i, 1);
			}
			hasPasscodeChecked = true;
		}

	}

	private void setTheme(SharedPreferences sharedPreferences){

		if (sharedPreferences.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material")) {
			super.setTheme(R.style.Material);

		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material 2")) {
			super.setTheme(R.style.Material2);

		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material 3")) {
			super.setTheme(R.style.Material3);

		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material 4")) {
			super.setTheme(R.style.Material4);

		}
	}
	
	private void editUI(SharedPreferences sharedPreferences){
		float textSize = Float.parseFloat(sharedPreferences.getString("TextSize", "16"));
		month_and_year.setTextSize(textSize);

		String fontStyle = sharedPreferences.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";


		if (fontStyle.contains("DEFAULT") != true) {

			Typeface tf = Typeface.createFromAsset(this.getAssets(),
					"fonts/" + fontStyle);
			month_and_year.setTypeface(tf);
		}
		
		
		if (sharedPreferences.getString("Theme", "Default").equals("Onyx P")) {
			DrawerLayout l = (DrawerLayout) findViewById(R.id.drawer_layout);


			TextView welcome = (TextView) findViewById(R.id.welcome_text);

			l.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));
            drawerLV.setBackgroundColor(getResources().getColor(R.color.UIDarkPink));

			month_and_year.setBackgroundColor(getResources().getColor(R.color.DarkWhite_ish));

			welcome.setTextColor(getResources().getColor(R.color.UIDarkText));



		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Onyx B")) {
			DrawerLayout l = (DrawerLayout) findViewById(R.id.drawer_layout);


			TextView welcome = (TextView) findViewById(R.id.welcome_text);

			l.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));
			drawerLV.setBackgroundColor(getResources().getColor(R.color.UIDarkBlue));

			month_and_year.setBackgroundColor(getResources().getColor(R.color.DarkWhite_ish));

			welcome.setTextColor(getResources().getColor(R.color.UIDarkText));



		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material")) {
			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialPink));
			drawerLV.setBackgroundColor(getResources().getColor(R.color.UIMaterialLightGreen));

		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material 2")) {
            drawerLV.setBackgroundColor(getResources().getColor(R.color.UIMaterialDeepOrange));
			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialBlue));

		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material 3")) {

			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialPurple));
            drawerLV.setBackgroundColor(getResources().getColor(R.color.UIMaterialDeepYellow));

		}
		else if (sharedPreferences.getString("Theme", "Default").equals("Material 4")) {

            drawerLV.setBackgroundColor(getResources().getColor(R.color.UIMaterialGreen));
			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialOrange));
		}
		else{

			drawerLV.setBackgroundColor(getResources().getColor(R.color.UIBlue));
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = PreferenceManager
				.getDefaultSharedPreferences(this);





		setTheme(sp);
		this.setContentView(R.layout.favs);


        if (savedInstanceState != null){
            //Boolean temp[] = favesArrList.toArray(new Boolean[favesArrList.size()]);

            currentVisibleItem = savedInstanceState.getInt("LISTPOSITION");

            monthsArr = savedInstanceState.getStringArray("MONTHS");
            if(savedInstanceState.getStringArrayList("FIRSTTAGSARRLIST") != null)
            firstTagArrList.addAll(savedInstanceState.getStringArrayList("FIRSTTAGSARRLIST"));
            if(savedInstanceState.getStringArrayList("SECONDTAGSARRLIST") != null)
            secondTagArrList.addAll(savedInstanceState.getStringArrayList("SECONDTAGSARRLIST"));
            if(savedInstanceState.getStringArrayList("THIRDTAGSARRLIST") != null)
            thirdTagArrList.addAll(savedInstanceState.getStringArrayList("THIRDTAGSARRLIST"));


            if(savedInstanceState.getStringArrayList("SORTEDFILES") != null)
            sortedFilesArrList.addAll(savedInstanceState.getStringArrayList("SORTEDFILES"));

            loadedFromBundle = true;

            for (int i = 0; i < sortedFilesArrList.size(); i++)
                favesArrList.add(true);



	        faveNum = savedInstanceState.getString("FAVENUM");
	        uniqueTagsNum = savedInstanceState.getString("UNIQUETAGNUM");
	        textFilesNum = savedInstanceState.getString("TEXTFILESNUM");
	        noteFilesNum = savedInstanceState.getString("NOTEFILESNUM");
	        picFilesNum = savedInstanceState.getString("PICFILESNUM");


        }
        else {

	        loadedFromBundle = false;

	        textFilesNum = AnimusMiscMethods.getNumberOfTextFiles(this);
	        noteFilesNum = AnimusMiscMethods.getNumberOfNoteFiles(this);
	        picFilesNum = AnimusMiscMethods.getNumberOfPicFiles(this);


	        faveNum = this.getIntent().getStringExtra("FAVENUM");
//	        uniqueTagsNum = this.getIntent().getExtras().getString("UNIQUETAGNUM");
        }

			
			
		

		

	}

	public void addFromIntent(){
		if (getIntent().getExtras().getStringArrayList("TAG1") != null) {
			firstTagArrList.addAll(getIntent().getExtras().getStringArrayList("TAG1"));
			secondTagArrList.addAll(getIntent().getExtras().getStringArrayList("TAG2"));
			thirdTagArrList.addAll(getIntent().getExtras().getStringArrayList("TAG3"));
			sortedFilesArrList.addAll(getIntent().getExtras().getStringArrayList(
                    "FILE"));
		} else {
			

			File filesXML = new File(getFilesDir(), "Files.xml");

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			try {

				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(filesXML);

				NodeList nodeList = doc.getElementsByTagName("Files");
				Node node = nodeList.item(0);
				nodeList = node.getChildNodes();
				Element element;

				for (int z = 1; z < nodeList.getLength(); z++) {
					node = nodeList.item(z);
					z++;
					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("favoriteSelectedFile").equals("true") == true) {

							//z = 100000;
							firstTagArrList.add(element.getAttribute("tag1"));

							secondTagArrList.add(element.getAttribute("tag2"));
							thirdTagArrList.add(element.getAttribute("tag3"));
							sortedFilesArrList.add(element.getAttribute("name"));

							Collections.reverse(firstTagArrList);
							Collections.reverse(secondTagArrList);
							Collections.reverse(thirdTagArrList);
							Collections.reverse(sortedFilesArrList);
						}
					}
				}

			} catch (Exception e) {

			}

		}

		

		for (int i = 0; i < sortedFilesArrList.size(); i++)
			favesArrList.add(true);
	}

	private void inappPreLolli(SharedPreferences sharedPreferences){
		if (connectionToIAP == null){
			connectionToIAP = new ServiceConnection() {

				@Override
				public void onServiceDisconnected(ComponentName name) {
					inAppBillingService = null;

				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					inAppBillingService = IInAppBillingService.Stub.asInterface(service);
				}
			};

			bindService(new Intent(
							"com.android.vending.billing.InAppBillingService.BIND"),
					connectionToIAP, FontSelectionActivity.BIND_AUTO_CREATE);

			Bundle ownedItems;


			try {
				ownedItems = inAppBillingService.getPurchases(3, getPackageName(), "inapp",
						null);
				// Get the list of purchased items
				ArrayList<String> purchaseDataList = ownedItems
						.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				for (String purchaseData : purchaseDataList) {
					JSONObject o = new JSONObject(purchaseData);
					String purchaseToken = o.optString("token",
							o.optString("purchaseToken"));
					// Consume purchaseToken, handling any errors
					if (purchaseToken == "ad_removal")
                        sharedPreferences.edit().putBoolean("ADS", false).commit();
					else
						inAppBillingService.consumePurchase(3, getPackageName(), purchaseToken);




				}
			} catch (Exception e) {

				e.printStackTrace();
			}


			ownedItems = null;
		}
		//mGoogleApiClient.connect();
	}

	// Lollipop methods
	private void inappLolli(SharedPreferences sharedPreferences){

		if (connectionToIAP == null){
			connectionToIAP = new ServiceConnection() {

				@Override
				public void onServiceDisconnected(ComponentName name) {
					inAppBillingService = null;

				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					inAppBillingService = IInAppBillingService.Stub.asInterface(service);
				}
			};

			Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
			serviceIntent.setPackage("com.android.vending");
			bindService(serviceIntent, connectionToIAP, Context.BIND_AUTO_CREATE);

			Bundle ownedItems;


			try {
				ownedItems = inAppBillingService.getPurchases(3, getPackageName(), "inapp",
                        null);
				// Get the list of purchased items
				ArrayList<String> purchaseDataList = ownedItems
						.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				for (String purchaseData : purchaseDataList) {
					JSONObject o = new JSONObject(purchaseData);
					String purchaseToken = o.optString("token",
							o.optString("purchaseToken"));
					// Consume purchaseToken, handling any errors
					if (purchaseToken == "ad_removal")
                        sharedPreferences.edit().putBoolean("ADS", false).commit();
					else
						inAppBillingService.consumePurchase(3, getPackageName(), purchaseToken);




				}
			} catch (Exception e) {

				e.printStackTrace();
			}


			ownedItems = null;
		}
	}


	public void removeAds(View v) {
		donate("ad_removal");
	}


	@Override
	protected void onStart() {
		super.onStart();




		// checks to see if there is an internet connection and puts an ad if there is.
		if (sp.getBoolean("ADS", true) == true && ad == null){
			if (AnimusMiscMethods.isNetworkAvailable(this)) {


				ad = new AdView(this);
				ad.setAdSize(AdSize.SMART_BANNER);

				ad.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
				LinearLayout home = (LinearLayout) findViewById(R.id.parent);
				home.addView(ad);

				AdRequest request = new AdRequest.Builder().setGender(
						AdRequest.GENDER_FEMALE).build();

				// Start loading the ad in the background.
				ad.loadAd(request);
			}


		}
		else {
			if (!AnimusMiscMethods.isNetworkAvailable(this)){

				LinearLayout home = (LinearLayout) findViewById(R.id.parent);
				home.removeView(ad);
			}
		}
        if (month_and_year == null) {
            month_and_year = (TextView) findViewById(R.id.month_and_year);
            context = this;
            monthsArr = context.getResources().getStringArray(R.array.months);


			if (loadedFromBundle)
			Date = (LoadDate) new LoadDate(context,month_and_year , monthsArr, currentVisibleItem).execute("null");
        }

		if (Build.VERSION.SDK_INT < 21)
			inappPreLolli(sp);
		else
		{
			inappLolli(sp);
		}




			if (drawerLV == null) {


				final String[] screens = getResources().getStringArray(
						R.array.navigation_titles);
				ArrayList<String> screenList = new ArrayList<String>();
				screenList.addAll(Arrays.asList(screens));
				DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

                drawerLV = (ListView) findViewById(R.id.left_drawer);
                editUI(PreferenceManager
                        .getDefaultSharedPreferences(this));

                if (loadedFromBundle == false)
                addFromIntent();

				// mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				// R.layout.drawer_item, screens));


				// gets data for sidenav

				String textFilesNum = AnimusMiscMethods.getNumberOfTextFiles(this);
				String noteFilesNum = AnimusMiscMethods.getNumberOfNoteFiles(this);String picFileNum = AnimusMiscMethods.getNumberOfPicFiles(this);

				drawerAdapter = new SideNavAdapter(this, screenList, this.getTitle(), textFilesNum, noteFilesNum, picFileNum, uniqueTagsNum, faveNum);
				drawerLV.setAdapter(drawerAdapter);




				drawerLV.setOnItemClickListener(new DrawerItemClickListener());
				mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
						mDrawerLayout, /* DrawerLayout object */
						R.string.open, /* "open drawer" description for accessibility */
						R.string.close /* lose drawer" description for accessibility */
				) {
					@Override
					public void onDrawerClosed(View view) {

						isDrawerOpen = false;
						invalidateOptionsMenu(); // creates call to
						// onPrepareOptionsMenu()
					}

					@Override
					public void onDrawerOpened(View drawerView) {

						isDrawerOpen = true;

						try {

						}catch (NullPointerException noActionBar){

						}
						invalidateOptionsMenu(); // creates call to
						// onPrepareOptionsMenu()
					}
				};
				mDrawerLayout.setDrawerListener(mDrawerToggle);

				try {

				}catch (NullPointerException noActionBar){

				}


		}
		else{
			drawerLV.invalidateViews();
		}

		if (filesAdapter == null) {
			ListView listview = (ListView) findViewById(R.id.list);
			final Context c = this;
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					Intent i = new Intent(c, ChosenFile.class);
					i.putExtra("FILENAME", sortedFilesArrList.get(position));
					i.putExtra("FILESARRAY", sortedFilesArrList);
					i.putExtra("POSITION", position);
					startActivityForResult(i, 2);

				}
			});

			final String[] weekdays = getResources().getStringArray(
					R.array.abrev_weekdays);
			listview.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub

				}

                @Override
                public void onScroll(AbsListView view,
                                     int firstVisibleItem, int visibleItemCount,
                                     int totalItemCount) {

                    if (sortedFilesArrList.size() > 0) {
                        date(firstVisibleItem);
                    }


                }




            });
			/*
			filesAdapter = new EntriesAdapter(this, sortedFilesArrList, firstTagArrList, secondTagArrList, thirdTagArrList, favesArrList,
					weekdays);
			listview.setAdapter(filesAdapter);
			if (sortedFilesArrList.size() == 0) {
				ViewSwitcher vs = (ViewSwitcher) findViewById(R.id.change_greeting);
				TextView tv = (TextView) findViewById(R.id.welcome_text);
				if (vs.getCurrentView().equals(tv) == false)
					vs.showNext();
			}
*/
		}
		else{

		}



	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Data the toggle state after onRestoreInstanceState has occurred.
		// this makes the icon with three horizontal bars appear next to the app
		// icon

		try {

			mDrawerToggle.syncState();
		}catch (NullPointerException noActionBar){

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls

		try {

			mDrawerToggle.onConfigurationChanged(newConfig);
		}catch (NullPointerException noActionBar){

		}
	}

	private void selectItem(int position) {
		if (position == 0) {
			
			NavUtils.navigateUpFromSameTask(this);
		}
		if (position == 1) {
			Intent newEntry = new Intent(this, Notes.class);
			startActivityForResult(newEntry, 2);
		}
		if (position == 2) {
			Intent i = new Intent(this, PicEntries.class);
			i.putExtra("UNIQUETAGNUM", uniqueTagsNum);
			i.putExtra("FAVENUM", faveNum);
			startActivityForResult(i, 2);
		}
		if (position == 3) {
			Intent i = new Intent(this, Tags.class);
			i.putExtra("UNIQUETAGNUM", uniqueTagsNum);
			i.putExtra("FAVENUM", faveNum);
			startActivityForResult(i, 2);
		}

		// update selected item and title, then close the drawer
		drawerLV.setItemChecked(position, true);

		DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.closeDrawer(drawerLV);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	public void selection(MenuItem z) {
		Intent i = new Intent(this, ChosenFile.class);
		i.putExtra("FILENAME", sortedFilesArrList.get(fileToDelete));

		i.putExtra("FILESARRAY", sortedFilesArrList);
		i.putExtra("POSITION", sortedFilesArrList.indexOf(sortedFilesArrList));
		startActivityForResult(i, 2);
	}

	public void fav(View v) {
		if (((TextView) v).getText().toString().equals("☆")) {
			((TextView) v).setText(Html.fromHtml("★"));

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			File filesXML = new File(getFilesDir(), "Files.xml");

			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(filesXML);

				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(filesXML);

				Node node = doc.getElementsByTagName("Files").item(0);
				NodeList nodeList = node.getChildNodes();

				Element element;
				String filename = sortedFilesArrList.get(v.getId()).replaceAll(" ",
						"_");

				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);

					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("name").equals(filename)) {
							element.setAttribute("favoriteSelectedFile", "true");
							favesArrList.set(v.getId(), true);
							filesAdapter.newFavSet(favesArrList);

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
			} catch (Exception x) {

			}

		} else {
			((TextView) v).setText(Html.fromHtml("☆"));

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			File filesXML = new File(getFilesDir(), "Files.xml");

			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(filesXML);

				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(filesXML);

				Node node = doc.getElementsByTagName("Files").item(0);
				NodeList nodeList = node.getChildNodes();

				Element element;
				String filename = sortedFilesArrList.get(v.getId()).replaceAll(" ",
						"_");

				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);

					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("name").equals(filename)) {
							element.setAttribute("favoriteSelectedFile", "false");
							favesArrList.set(v.getId(), false);
							filesAdapter.newFavSet(favesArrList);
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
			} catch (Exception x) {

			}

		}
		drawerAdapter.notifyDataSetChanged();
	}

	public void menu(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.long_click_entries, popup.getMenu());

		popup.show();
		String temp = "";
		for (int i = 0; i < sortedFilesArrList.size(); i++) {
			temp = temp + " " + sortedFilesArrList.get(i);
		}
		// Toast.makeText(getApplicationContext(), temp,
		// Toast.LENGTH_LONG).show();

		fileToDelete = v.getId();
		// Toast.makeText(getApplicationContext(),
		// sortedFiles.get(fileToDelete),
		// Toast.LENGTH_LONG).show();
	}

	public void delete(MenuItem m) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delelte_dialog_message);
		builder.setIcon(getResources().getDrawable(R.drawable.white_discard));
		builder.setNegativeButton(R.string.no, null);
		builder.setPositiveButton(R.string.delete_confirmation,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						new DeleteEntry().execute("null");
					}
				});
		builder.create();
		builder.show();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main_ui_menu, menu);

		return true;
	}
	
	public void newEntry(View view) {
		Intent i = new Intent(this, NewEntry.class);
		startActivityForResult(i, 2);


		overridePendingTransition(R.anim.scale, R.anim.scale);
	}

	public void newNote(MenuItem view) {
		Intent i = new Intent(this, NewNote.class);
		startActivityForResult(i, 2);
	}

	public void settingsClicked(MenuItem view) {
		Intent i = new Intent(this, MainSettingsFrag.class);
		startActivityForResult(i, 2);
	}

	public void feedback(MenuItem view) {

		Intent mail = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		mail.setType("text/plain");
		mail.putExtra(Intent.EXTRA_SUBJECT, "");
		mail.putExtra(Intent.EXTRA_TEXT, "");
		mail.setData(Uri.parse("mailto:corporationawesome@gmail.com")); // or
																		// just
																		// "mailto:"
																		// for
																		// blank
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
	
	public void donation(MenuItem view) {
		/*
		
		*/
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.donation, null);
		TextView title = (TextView) v.findViewById(R.id.title);
		TextView donation = (TextView) v.findViewById(R.id.donation_info);
		TextView inapp = (TextView) v.findViewById(R.id.inapp_info);


		title.setTextColor(getResources().getColor(R.color.UIDarkText));
		donation.setTextColor(getResources().getColor(R.color.UIDarkText));
		inapp.setTextColor(getResources().getColor(R.color.UIDarkText));


		builder.setView(v);
		builder.setNeutralButton(
				getResources().getString(
						R.string.dismiss), null);
		builder.create();
		builder.show();
		
	}


	
	public void baseDonation(View v){
		donate ("base_donation");
	}public void secondTier(View v){
		donate ("second_tier");
	}public void thirdTier(View v){
		donate ("third_tier");
	}
	public void godTier(View v){
		donate("god_tier");
	}
	public void donate(String donation){
		if (inAppBillingService != null) {
			ArrayList<String> skuList = new ArrayList<String>();
			skuList.add(donation);
			Bundle querySkus = new Bundle();
			querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
			Bundle skuDetails;
			try {
				skuDetails = inAppBillingService.getSkuDetails(3, getPackageName(),
						"inapp", querySkus);

				int response = skuDetails.getInt("RESPONSE_CODE");
				if (response == 0) {

					ArrayList<String> responseList = skuDetails
							.getStringArrayList("DETAILS_LIST");

					for (String thisResponse : responseList) {
						JSONObject object = new JSONObject(thisResponse);
						String sku = object.getString("productId");
						String price = object.getString("price");
						if (sku.equals(donation)) {
							System.out.println("price " + price);
							Bundle buyIntentBundle = inAppBillingService.getBuyIntent(3,
									getPackageName(), sku, "inapp",
									"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
							PendingIntent pendingIntent = buyIntentBundle
									.getParcelable("BUY_INTENT");
							startIntentSenderForResult(
									pendingIntent.getIntentSender(), 1001,
									new Intent(), Integer.valueOf(0),
									Integer.valueOf(0), Integer.valueOf(0));
						}
					}
				}
			} catch (Exception e) {

			}
		} else {

			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.donation_err),
					Toast.LENGTH_LONG).show();
		}
	}

	private class DeleteEntry extends AsyncTask<String, Integer, String> {

		Element element;
		Node node;
		NodeList nodeList;
		File filesXML = new File(getFilesDir(), "Files.xml");

		DOMSource source;
		StreamResult result;
		Document doc;
		Node parent;

		DocumentBuilder builder;
		Transformer transformer;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			File f = new File(getFilesDir(), sortedFilesArrList.get(fileToDelete));
			ArrayList<String> tags = new ArrayList<String>();

			f.delete();
			String filename;
			filename = sortedFilesArrList.get(fileToDelete).replace(".txt", "");
			f = new File(getFilesDir(), filename + ".mpeg4");
			f.delete();

			int t = 1;
			f = new File(getFilesDir(), filename + "(" + t + ").png");
			while (f.exists() == true) {
				f.delete();
				t++;
				f = new File(getFilesDir(), filename + "(" + t + ").png");
			}

			DocumentBuilder builder;
			try {
				builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();

				doc = builder.parse(filesXML);

				TransformerFactory tFactory = TransformerFactory.newInstance();
				transformer = tFactory.newTransformer();
				source = new DOMSource(doc);
				result = new StreamResult(filesXML);

				parent = doc.getElementsByTagName("Files").item(0);
				nodeList = parent.getChildNodes();

				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);

					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("name").equals(
								filename.replaceAll(" ", "_") + ".txt")) {
							for (int y = 0; y < Integer.parseInt(element
									.getAttribute("tags")); y++) {
								tags.add(element.getAttribute("tag" + (y + 1)));

							}
							parent.removeChild(node);
							node = nodeList.item(i - 1);
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

				doc = builder.parse(new File(getFilesDir(), "Tags.xml"));
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (TransformerConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			source = new DOMSource(doc);
			result = new StreamResult(new File(getFilesDir(), "Tags.xml"));

			int loop = 0;
			int tempInt = 0;
			parent = null;
			for (int i = 0; i < tags.size(); i++) {
				NodeList tagList = doc.getElementsByTagName("Tags");
				Node tagNode = tagList.item(0);
				element = (Element) tagNode;

				String temp = tags.get(i);
				temp.toString().replaceAll("\\s", "");

				NodeList searchList = element.getElementsByTagName(temp
						.toString().trim().replaceAll(" ", "_"));

				Node searchNode = searchList.item(0);
				if (searchNode != null) {
					element = (Element) searchNode;
					tempInt = Integer.parseInt(element.getAttribute("amount")
							.trim());
					tempInt--;

					element.setAttribute("amount", Integer.toString(tempInt));

					doc.normalize();

					parent = doc.getElementsByTagName(
							tags.get(i).trim().replaceAll(" ", "_")).item(0);
					nodeList = parent.getChildNodes();

					for (int j = 0; j < nodeList.getLength(); j++) {
						node = nodeList.item(j);

						if (node.getNodeName() != "#text") {

							element = (Element) node;
							if (element.getAttribute("name").equals(
									filename.replaceAll(" ", "_") + ".txt")) {
								parent.removeChild(node);
								node = nodeList.item(j - 1);
								parent.removeChild(node);
								doc.normalize();
								loop++;
								if (loop == tags.size()) {
									j = 1000000;
									i = 1000000;
								}

								try {
									transformer.transform(source, result);
								} catch (TransformerException e) {
									// TODO Auto-generated catch
									// block
									e.printStackTrace();
								}
							}
						}
					}

				}
				doc.normalizeDocument();
				if (tempInt <= 0) {
					element = (Element) doc.getElementsByTagName("Tags")
							.item(0);
					element.removeChild(parent.getPreviousSibling());
					element.removeChild(parent);
					element.setAttribute("amount", Integer.toString(Integer
							.parseInt(element.getAttribute("amount")) - 1));
					for (int a = 0; a < tagList.getLength(); a++) {
						node = tagList.item(a);
						// this is always "Tags"
						// Toast.makeText(getApplicationContext(),
						// node.getNodeName(), Toast.LENGTH_LONG)
						// .show();
						if (node.getNodeName() == "#text") {

							Node anotherNode = tagList.item(a + 1);
							if (anotherNode.getNodeName() == "#text") {
								Element _ele = (Element) anotherNode;
								element.removeChild(_ele);

								doc.normalizeDocument();

								// Toast.makeText(getApplicationContext(),
								// "Jello",
								// Toast.LENGTH_LONG).show();
							}
						}

					}

					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			sortedFilesArrList.remove(fileToDelete);
			firstTagArrList.remove(fileToDelete);
			secondTagArrList.remove(fileToDelete);
			thirdTagArrList.remove(fileToDelete);
			favesArrList.remove(fileToDelete);
			return null;
		}
	}


    private class LoadDate extends AsyncTask<String, Integer, String> {
        private TextView month_and_year;
        private Context c;
        private String[] m;
        private int firstVisibleItem;

        private File f;
        private java.util.Calendar calendar;

        public LoadDate(Context c, TextView month_and_year, String[] m,
                        int firstVisibleItem) {
            this.month_and_year = month_and_year;
            this.c = c;
            this.m = m;
            this.firstVisibleItem = firstVisibleItem;
        }

        @Override
        protected String doInBackground(String... params) {
            if (isCancelled() == false) {
                f = new File(context.getFilesDir(), sortedFilesArrList.get(firstVisibleItem));
                calendar = java.util.Calendar.getInstance();
                calendar.setTimeInMillis(f.lastModified());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if ( ! (m[calendar.get(java.util.Calendar.MONTH)] + " " + Integer.toString(calendar.get(java.util.Calendar.YEAR))).equals(month_and_year.getText())){
                month_and_year.setText(m[calendar.get(java.util.Calendar.MONTH)] + " " + Integer.toString(calendar.get(java.util.Calendar.YEAR)));
            }
        }

    }

    private synchronized void date(int firstVisibleItem) {
        if (currentVisibleItem != firstVisibleItem) {

            if (Date != null) {
                if (Date.getStatus() == AsyncTask.Status.RUNNING || Date.getStatus() == AsyncTask.Status.PENDING) {
                    Date.cancel(true);
                    Date = null;
                }
            }
            Date = (LoadDate) new LoadDate(context,month_and_year , monthsArr, firstVisibleItem).execute("null");
        }

        currentVisibleItem = firstVisibleItem;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


	    outState.putInt("LISTPOSITION", currentVisibleItem);

	    outState.putStringArray("MONTHS", monthsArr);
        outState.putStringArrayList("FIRSTTAGSARRLIST", firstTagArrList);
	    outState.putStringArrayList("SECONDTAGSARRLIST", secondTagArrList);
        outState.putStringArrayList("THIRDTAGSARRLIST", thirdTagArrList);
	    outState.putStringArrayList("SORTEDFILES", sortedFilesArrList);


	    outState.putString("FAVENUM", faveNum);
	    outState.putString("UNIQUETAGNUM", uniqueTagsNum);
	    outState.putString("TEXTFILESNUM", textFilesNum);
	    outState.putString("NOTEFILESNUM", noteFilesNum);
	    outState.putString("PICFILESNUM", picFilesNum);


    }

}
