package com.AnimusMainActivities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AnimusAdapters.TagsAdapter;
import com.UtilityClasses.AnimusDonation;
import com.UtilityClasses.AnimusLauncherMethods;
import com.UtilityClasses.AnimusMiscMethods;
import com.UtilityClasses.AnimusUI;
import com.UtilityClasses.CustomAttributes;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rtomyj.Diary.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Tags extends MainActivity {
	// used to get info
	private SharedPreferences sp;

	// inapp and passcode
	private IInAppBillingService in_appBillingService = null;
	private ServiceConnection connection;
	private boolean passcodeOn = false;
	private boolean passcodeCheck = true;

	//lists
	private ArrayList<Integer> alphSortedTagNumArrList = new ArrayList<>();
	private ArrayList<String> alphaSortedTagsArrList = new ArrayList();
	private ArrayList<String> alphaSortedFilenamesArrList = new ArrayList();

	private ArrayList<Integer> numSortedTagNumArrList = new ArrayList<>();
	private ArrayList<String> numSortedTagsArrList = new ArrayList();
	private ArrayList<String> numSortedFilenamesArrList = new ArrayList();

	// views
	private TagsAdapter tagsAdapter;

	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager recycleViewLayoutManager;
	private AdView adView = null;

	// other
	private boolean isAlphaSort = true;
	private CustomAttributes userUIPreferences;
	private Toolbar actionBar;
	private DrawerLayout sideNavDrawer;


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}


	public void in_appCheck(SharedPreferences sp){
		if (connection == null){
			connection = new ServiceConnection() {
				@Override
				public void onServiceDisconnected(ComponentName name) {
					in_appBillingService = null;
				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					in_appBillingService = IInAppBillingService.Stub.asInterface(service);
				}
			};

			if (Build.VERSION.SDK_INT < 21) {
				Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
				bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
			}
			else {

				Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
				serviceIntent.setPackage("com.android.vending");
				bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
			}

			try {
				Bundle ownedItems = in_appBillingService.getPurchases(3, getPackageName(), "inapp", null);
				// Get the recyclerView of purchased items
				ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				WeakReference<ArrayList<String>> purchaseDataListWeak = new WeakReference<>(purchaseDataList);

				for (String purchaseData : purchaseDataListWeak.get()) {
					JSONObject o = new JSONObject(purchaseData);
					WeakReference<JSONObject> jSonObjectWeak = new WeakReference<>(o);

					String purchaseToken = jSonObjectWeak.get().optString("token", jSonObjectWeak.get().optString("purchaseToken"));
					// Consume purchaseToken, handling any errors
					if (purchaseToken.equals("ad_removal"))
						sp.edit().putBoolean("ADS", false).apply();
					else
						in_appBillingService.consumePurchase(3, getPackageName(), purchaseToken);


				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}


	public void removeAds(View v) {
		donate("ad_removal");
	}


	@Override
	protected void onStart() {
		super.onStart();

		if (recyclerView == null)// re-inits recyclerView  if it's null or first time onStart() is called.
			recyclerView = (RecyclerView) findViewById(R.id.list);

		if (recyclerView.getAdapter() == null) { // if there is no tagsAdapter binded to recyclerView then entriesAdapter is binded to it.
			Log.e("tagsAdapter added", "stuff");
			recyclerView.setHasFixedSize(true);  // children will not impact the redrawing of recyclerView; good for performance.
			recyclerView.setLayoutManager(recycleViewLayoutManager);
			recyclerView.setAdapter(tagsAdapter);
		}



		if (sp == null)
		sp = PreferenceManager.getDefaultSharedPreferences(this);

		// checks to see if there is an internet connection and puts an ad if there is.
		if (sp.getBoolean("ADS", true)  && adView == null){
			if (AnimusMiscMethods.isNetworkAvailable(this)) {


				adView = new AdView(this);
				adView.setAdSize(AdSize.SMART_BANNER);

				adView.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
				LinearLayout home = (LinearLayout) findViewById(R.id.parent);
				home.addView(adView);

				AdRequest request = new AdRequest.Builder().setGender(
						AdRequest.GENDER_FEMALE).build();

				// Start loading the ad in the background.
				adView.loadAd(request);
			}


		}
		else {
			if (!AnimusMiscMethods.isNetworkAvailable(this)){

				LinearLayout home = (LinearLayout) findViewById(R.id.parent);
				home.removeView(adView);
			}
		}


		if (recyclerView == null)
			recyclerView = (RecyclerView) findViewById(R.id.list);

		if (tagsAdapter == null)
			new LoadTags(this).execute("null");
		else
			tagsAdapter.notifyDataSetChanged();


		/* on click for items in tags



				Intent i = new Intent(c, ChosenTag.class);
				if (isAlphaSort == true)
					i.putExtra("TAG", alphaSortedTagsArrList.get(position));
				else
					i.putExtra("TAG", numSortedTagsArrList.get(position));
				startActivityForResult(i, 2);
		 */


		in_appCheck(sp);

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		sp = PreferenceManager.getDefaultSharedPreferences(this);
		userUIPreferences = new CustomAttributes(this, sp);

		AnimusUI.setTheme(this, userUIPreferences.theme);

		setContentView(R.layout.main_activity_base);



		if (savedInstanceState != null) {
			isAlphaSort = savedInstanceState.getBoolean("IS_ALPHA_SORT");
		}


		// creates new Toolbar object and sets it as the activities action bar
		actionBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(actionBar);
		sideNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		// creates toggle for action bar and anchors the side navigation drawer object to it.
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, sideNavDrawer, actionBar, 0, 0);
		WeakReference<ActionBarDrawerToggle> toggleWeak = new WeakReference<>(toggle);
		toggleWeak.get().syncState();
		toggleWeak.clear();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setItemIconTintList(null);
		navigationView.setNavigationItemSelectedListener(this);  // uses the methods in this class to listen for onclick events for the side nav drawer

		recycleViewLayoutManager = new LinearLayoutManager(this);
	}


	@Override
	public void showWelcome() {

	}

	@Override
	public void showList() {

	}

	@Override
	public void feedback() {
		AnimusMiscMethods.email("", "", "mailto:corporationawesome@gmail.com", this);
	}

	/*
         * public boolean onPrepareOptionsMenu(Menu dropDownMenuForSelectedFile) { if (isAlphaSort == true)
         * dropDownMenuForSelectedFile.removeItem(R.id.alph_sort); else dropDownMenuForSelectedFile.removeItem(R.id.value_sort);
         * return super.onPrepareOptionsMenu(dropDownMenuForSelectedFile); }
         */
	public void newEntry(View view) {
		AnimusLauncherMethods.newEntry(this);
	}


	public synchronized void sort(MenuItem m) {
		if (isAlphaSort == true) {
			isAlphaSort = false;
			m.setTitle(getResources().getString(R.string.alph_sort));
			m.setIcon(getResources().getDrawable(R.drawable.white_sort_alph));
			tagsAdapter.sortNum(numSortedTagsArrList, numSortedTagNumArrList, numSortedFilenamesArrList);
			Log.e("11111111", Integer.toString(alphaSortedFilenamesArrList.size()));
		} else {
			isAlphaSort = true;

			m.setTitle(getResources().getString(R.string.value_sort));
			m.setIcon(getResources().getDrawable(R.drawable.white_sort));
			tagsAdapter.sortAlph(alphaSortedTagsArrList, alphSortedTagNumArrList, alphaSortedFilenamesArrList);
			Log.e("eee", Integer.toString(alphaSortedFilenamesArrList.size()));
		}

	}


	/*
	public class CustomComparator implements Comparator<String> {
		private final Pattern pattern = Pattern.compile("(\\d+)\\s+(.*)");

		@Override
		public int compare(String s1, String s2) {
			Matcher m1 = pattern.matcher(s1);
			if (!m1.matches()) {
				throw new IllegalArgumentException("s1 doesn't match: " + s1);
			}
			Matcher m2 = pattern.matcher(s2);
			if (!m2.matches()) {
				throw new IllegalArgumentException("s2 doesn't match: " + s2);
			}
			int i1 = Integer.parseInt(m1.group(1));
			int i2 = Integer.parseInt(m2.group(1));
			if (i1 < i2) {
				return 1;
			} else if (i1 > i2) {
				return -1;
			}
			return m1.group(2).compareTo(m2.group(2));
		}
	}
	*/

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("IS_ALPHA_SORT", isAlphaSort);
	}



	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	public void donation(MenuItem view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.donation, null);
		TextView title = (TextView) v.findViewById(R.id.title);
		TextView donation = (TextView) v.findViewById(R.id.donation_info);
		TextView inapp = (TextView) v.findViewById(R.id.inapp_info);


		title.setTextColor(getResources().getColor(R.color.UIDarkText));
		donation.setTextColor(getResources().getColor(R.color.UIDarkText));
		inapp.setTextColor(getResources().getColor(R.color.UIDarkText));


		builder.setView(v);
		builder.setNeutralButton(getResources().getString(R.string.dismiss), null);
		builder.create();
		builder.show();
		
	}

	
	public void baseDonation(View v){
		donate ("base_donation");
	}


	public void donate(String donationType){
		try {
			PendingIntent pendingIntent = AnimusDonation.requestGooglePlay(in_appBillingService, donationType, getPackageName());
			if (pendingIntent == null)
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.donation_err), Toast.LENGTH_LONG).show();
			else
				startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
		}catch (JSONException | RemoteException | IntentSender.SendIntentException jsonException){
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.donation_err), Toast.LENGTH_LONG).show();
			Log.e("Err with in_app", jsonException.toString());
		}

	}

	private class LoadTags extends AsyncTask<String, Integer, String> {
		private Context context;
		private ArrayList<String> fileNames = new ArrayList<>();

		private ArrayList<String> unsortedTagsArrList = new ArrayList<>();
		private ArrayList<Integer> unsortedTagNumArrList = new ArrayList();

		public LoadTags(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... params) {


			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			File xml = new File(getFilesDir(), "Files.xml");

			if (numSortedTagsArrList.size() == 0) {
				alphaSortedTagsArrList.clear();
				alphSortedTagNumArrList.clear();
				numSortedTagsArrList.clear();
				numSortedTagNumArrList.clear();

				try {
					Document doc;
					Element element;
					Node node;
					NodeList nodeList;
					DocumentBuilder builder = factory.newDocumentBuilder();
					doc = builder.parse(xml);

					nodeList = doc.getElementsByTagName("Files");
					node = nodeList.item(0);
					element = (Element) node;

					nodeList = element.getChildNodes();

				for (int z = 0; z < nodeList.getLength(); z++) {
					node = nodeList.item(z);

					if (node.getNodeName() != "#text") {

						element = (Element) node;

						// if current node isnt for the file in question, it searches through all its tags and adds those that are unique to suggestionsArrList

							String tagsInCurrentNode = ((Element) node).getAttribute("tags");

							for (int j = 0; j < Integer.parseInt(tagsInCurrentNode); j ++)
								if (!unsortedTagsArrList.contains(((Element) node).getAttribute("tag" + Integer.toString(j + 1)))) {
									unsortedTagsArrList.add(((Element) node).getAttribute("tag" + Integer.toString(j + 1)).replaceAll("_", " "));
									unsortedTagNumArrList.add(1);

									fileNames.add(((Element) node).getAttribute("name"));
									Log.e("tag", ((Element) node).getAttribute("tag" + Integer.toString(j + 1)));

								}
						else{
									for (int x = 0; x < unsortedTagsArrList.size(); x++){
										if (unsortedTagsArrList.get(x).equals(((Element) node).getAttribute("tag" + Integer.toString(j + 1)))){

											unsortedTagNumArrList.set(x, unsortedTagNumArrList.get(x) + 1);
										}
									}
								}

					}
				}
			} catch (Exception ex) {

			}

				boolean addedAlph, addedNumerically;


				for (int i = 0; i < unsortedTagsArrList.size(); i ++){

					addedAlph = false;
					addedNumerically = false;
					if (i == 0) {
						alphaSortedTagsArrList.add(unsortedTagsArrList.get(i));
						alphSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
						alphaSortedFilenamesArrList.add(fileNames.get(i));

						numSortedTagsArrList.add(unsortedTagsArrList.get(i));
						numSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
						numSortedFilenamesArrList.add(fileNames.get(i));
					}

					else {


						sorting: for (int j = 0; j < alphaSortedTagsArrList.size(); j++) {

							if (alphaSortedTagsArrList.get(j).compareTo(unsortedTagsArrList.get(i)) > 0) {
								alphaSortedTagsArrList.add(j, unsortedTagsArrList.get(i));
								alphSortedTagNumArrList.add(j, unsortedTagNumArrList.get(i));
								alphaSortedFilenamesArrList.add(j, fileNames.get(i));
								addedAlph = true;
								break sorting;
							}
							if (!addedAlph && j == (alphaSortedTagsArrList.size() -1)){

								alphaSortedTagsArrList.add(unsortedTagsArrList.get(i));
								alphSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
								alphaSortedFilenamesArrList.add(fileNames.get(i));
								break sorting;
							}




						}

						sortingNumerically: for (int j = 0; j < numSortedTagNumArrList.size(); j++) {

							if (numSortedTagNumArrList.get(j) <= unsortedTagNumArrList.get(i)) {
								numSortedTagsArrList.add(j, unsortedTagsArrList.get(i));
								numSortedTagNumArrList.add(j, unsortedTagNumArrList.get(i));

								numSortedFilenamesArrList.add(j, fileNames.get(i));
								addedNumerically = true;
								break sortingNumerically;
							}
							if (!addedNumerically && j == (numSortedTagNumArrList.size() -1)){

								numSortedTagsArrList.add(unsortedTagsArrList.get(i));
								numSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
								numSortedFilenamesArrList.add(fileNames.get(i));
								break sortingNumerically;
							}

						}
					}

				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			/*
			if (isAlphaSort == true)
				tagsAdapter = new TagsAdapter(context, alphaSortedTagsArrList, alphSortedTagNumArrList);
			else
				tagsAdapter = new TagsAdapter(context, numSortedTagsArrList, numSortedTagNumArrList);

				*/

			tagsAdapter = new TagsAdapter(context, alphaSortedTagsArrList, alphSortedTagNumArrList, alphaSortedFilenamesArrList);
			//recyclerView.setAdapter(tagsAdapter);

		}

	}



	// All clicks from the dropDownMenuForSelectedFile side bar are handled here. Uses a handler to delay the click so the sidebar has time to close and the transitions are smooth.
	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		android.os.Handler delayedAction = new android.os.Handler();
		final Context context = this;
		final SoftReference<Context> contextSoft = new SoftReference<>(context);

		delayedAction.postDelayed(new Runnable() {
			@Override
			public void run() {
				switch (item.getItemId()){
					case R.id.domus:
						break;
					case R.id.donation:
						//donation();       // shows donation popup
						break;
					case R.id.feedback:
						feedback();      // launches feedback operations
						break;
					default:
						AnimusLauncherMethods.getNavigationIntent(item.getItemId(), contextSoft.get()); 		// getNavigationIntent gets the correct intent for the method to use.
						break;
				}
			}
		}, 150);
		sideNavDrawer.closeDrawer(GravityCompat.START); // closes the side bar
		return true;
	}
}
