package com.AnimusMainActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.AnimusAdapters.PicturesAdapter;
import com.AnimusSubActivities.FontSelectionActivity;
import com.AnimusSubActivities.Passcode;
import com.UtilityClasses.AnimusLauncherMethods;
import com.UtilityClasses.AnimusMiscMethods;
import com.UtilityClasses.AnimusUI;
import com.UtilityClasses.MainActivity;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rtomyj.Diary.R;

import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class PicEntries extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , MainActivity{
	// used to get info
	private Context context;

	// in-app and password
	private IInAppBillingService in_appBillingService = null;
	private ServiceConnection in_appServiceConnection;

	// pass_code
	private boolean pass_codeOn = false;
	private boolean pass_codeCheck = true;
	private static final int PASS_CHECK = 1234;

	// views.
	private RecyclerView picturesRV;
	private AdView adView = null;
	private DrawerLayout sideNavDrawer;

	// Preferences
	private boolean loadAds = false;
	private String theme = "", fontStyle = "";
	private int primaryColor = 0, secondaryColor = 0, darkTextColor = 0, darkThemeBackgroundColor = 0, darkThemeForegroundColor = 0;
	private float textSize;

	// other
	private PicturesAdapter picAdapter;
	private static final int NEW_ENTRY = 9876;
	private Toolbar actionBar;


	@Override
	protected void onPause() {
		if (adView != null) {
            adView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
            adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
			case PASS_CHECK:
				if(requestCode == RESULT_OK)
					pass_codeCheck = false;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (pass_codeOn) {
			if (pass_codeCheck) {
				Intent i = new Intent(this, Passcode.class);
				startActivityForResult(i, PASS_CHECK);
			}
		}

	}


	private void inappPreLolli(){
		if (in_appServiceConnection == null){
			in_appServiceConnection = new ServiceConnection() {

				@Override
				public void onServiceDisconnected(ComponentName name) {
					in_appBillingService = null;

				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					in_appBillingService = IInAppBillingService.Stub.asInterface(service);
				}
			};

			bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), in_appServiceConnection, FontSelectionActivity.BIND_AUTO_CREATE);
			Bundle ownedItems;


			try {
				ownedItems = in_appBillingService.getPurchases(3, getPackageName(), "inapp",
						null);
				// Get the list of purchased items
				ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				for (String purchaseData : purchaseDataList) {
					JSONObject o = new JSONObject(purchaseData);
					String purchaseToken = o.optString("token",
							o.optString("purchaseToken"));
					// Consume purchaseToken, handling any errors
					if (purchaseToken == "ad_removal") {
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
						sp.edit().putBoolean("ADS", false).apply();
					}
					else
						in_appBillingService.consumePurchase(3, getPackageName(), purchaseToken);




				}
			} catch (Exception e) {

				e.printStackTrace();
			}


			ownedItems = null;
		}
		//mGoogleApiClient.connect();
	}

	// Lollipop methods
	private void inappLolli(){

		if (in_appServiceConnection == null){
			in_appServiceConnection = new ServiceConnection() {

				@Override
				public void onServiceDisconnected(ComponentName name) {
					in_appBillingService = null;

				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					in_appBillingService = IInAppBillingService.Stub.asInterface(service);
				}
			};

			Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
			serviceIntent.setPackage("com.android.vending");
			bindService(serviceIntent, in_appServiceConnection, Context.BIND_AUTO_CREATE);

			Bundle ownedItems;


			try {
				ownedItems = in_appBillingService.getPurchases(3, getPackageName(), "inapp", null);
				// Get the list of purchased items
				ArrayList<String> purchaseDataList = ownedItems
						.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				for (String purchaseData : purchaseDataList) {
					JSONObject o = new JSONObject(purchaseData);
					String purchaseToken = o.optString("token",
							o.optString("purchaseToken"));
					// Consume purchaseToken, handling any errors
					if (purchaseToken == "ad_removal") {
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
						sp.edit().putBoolean("ADS", false).commit();
					}
					else
						in_appBillingService.consumePurchase(3, getPackageName(), purchaseToken);




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
		if (picAdapter == null){
			picAdapter = new PicturesAdapter(this, primaryColor, secondaryColor, darkTextColor, darkThemeBackgroundColor, darkThemeForegroundColor, textSize,
					fontStyle, theme);

			actionBar.setSubtitle("Total: " + picAdapter.getTotalPicCount());

		}
		int adapterSize = picAdapter.getItemCount();

		if (picturesRV == null && adapterSize > 0) {
			showList();
			picturesRV = (RecyclerView) findViewById(R.id.list);

			if (picturesRV.getAdapter() == null) { // if there is no adapter binded to recyclerView then entriesAdapter is binded to it.

				Log.e("adapter added", "stuff");
				picturesRV.setHasFixedSize(true);  // children will not impact the redrawing of recyclerView; good for performance.
				picturesRV.setLayoutManager(new GridLayoutManager(this, 2));
				picturesRV.setAdapter(picAdapter);
			}

		/*
			picturesRV.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
				                        int position, long id) {
					Intent i = new Intent(context, ChosenFile.class);
					i.putExtra("FILENAME", (pictureFilesArrayList.get(position) + ".txt"));
					i.putExtra("FILESARRAY", pictureFilesArrayList);
					i.putExtra("POSITION", position);
					pass_codeCheck = false;
					startActivityForResult(i, 2);

				}
			});
		*/
		}
		else if (adapterSize == 0){
			showWelcome();
		}

		if (loadAds&& adView == null) {
			if (AnimusMiscMethods.isNetworkAvailable(this)) {

				adView = new AdView(this);
				adView.setAdSize(AdSize.SMART_BANNER);
				adView.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
				LinearLayout home = (LinearLayout) findViewById(R.id.parent);
				home.addView(adView);

				AdRequest request = new AdRequest.Builder().setGender(AdRequest.GENDER_FEMALE).build();

				// Start loading the ad in the background.
				adView.loadAd(request);


			} else {
					LinearLayout home = (LinearLayout) findViewById(R.id.parent);
					home.removeView(adView);
			}

			//sets up intents for iAP
			if (Build.VERSION.SDK_INT < 21)
				inappPreLolli();
			else {
				inappLolli();
			}


		}
	}

	// changes the color of UI elements according to the them the user has selected.
    public void customizeUI(){

    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putStringArrayList("PIC_ARR_LIST", picAdapter.getPicturesArrList());

		// user preferences
		outState.putInt("PRIMARY_COLOR", primaryColor);
		outState.putInt("SECONDARY_COLOR",  secondaryColor);
		outState.putInt("DARK_THEME_TEXT", darkTextColor);
		outState.putInt("DARK_THEME_BACKGROUND", darkThemeBackgroundColor);
		outState.putInt("DARK_THEME_FOREGROUND", darkThemeForegroundColor);
		outState.putBoolean("ADS", loadAds);
		outState.putString("THEME", theme);
		outState.putString("FONT_STYLE", fontStyle);
		outState.putFloat("TEXT_SIZE", textSize);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		context = this;
		// creates new Toolbar object and sets it as the activities action bar

		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {		// blocks orientation change on really small screens
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}


		if (savedInstanceState != null){
			ArrayList<String> picArrList = savedInstanceState.getStringArrayList("PIC_ARR_LIST");

			// user preferences
			primaryColor = savedInstanceState.getInt("PRIMARY_COLOR");
			secondaryColor = savedInstanceState.getInt("SECONDARY_COLOR");
			darkTextColor = savedInstanceState.getInt("DARK_THEME_TEXT");
			darkThemeBackgroundColor = savedInstanceState.getInt("DARK_THEME_BACKGROUND");
			darkThemeForegroundColor = savedInstanceState.getInt("DARK_THEME_FOREGROUND");
			loadAds = savedInstanceState.getBoolean("ADS");
			theme = savedInstanceState.getString("THEME");
			fontStyle = savedInstanceState.getString("FONT_STYLE");
			textSize = savedInstanceState.getFloat("TEXT_SIZE");



			if(picArrList != null) {
				picAdapter = new PicturesAdapter(context, picArrList, primaryColor, secondaryColor, darkTextColor, darkThemeBackgroundColor, darkThemeForegroundColor,
						textSize, fontStyle, theme);
				actionBar.setSubtitle("Total: " + picAdapter.getTotalPicCount());		// sets the total of all pictures saved in app not just the number that shows up in adapter
			}

		}else{
			// gets preferences
			loadAds = sp.getBoolean("ADS", true);
			theme = sp.getString("Theme", "Default");
			pass_codeOn = sp.getBoolean("Password", false);
			fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";
			textSize = Float.parseFloat(sp.getString("TextSize", "14"));

			int [] colors = AnimusUI.getThemeElements(context, theme);
			primaryColor = colors[0];
			secondaryColor = colors[1];
			darkTextColor = colors[2];
			darkThemeBackgroundColor = colors[3];
			darkThemeForegroundColor = colors[4];
		}

        //sets theme according to preference before setting the view
		AnimusUI.setTheme(context, theme);
		setContentView(R.layout.main_activity_base);

        //changes colors of views for theme
        customizeUI();

		actionBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(actionBar);
		sideNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		// creates toggle for action bar and anchors the side navigation drawer object to it.
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle((Activity) context, sideNavDrawer, actionBar, 0, 0);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setItemIconTintList(null);
		navigationView.setNavigationItemSelectedListener(this);  // uses the methods in this class to listen for onclick events for the side nav drawer

	}

	public void newEntry(View view) {
		Intent newEntry = new Intent(this, NewEntry.class);
		startActivityForResult(newEntry, 2);

		overridePendingTransition(R.anim.scale, R.anim.scale);
	}

	@Override
	public void showWelcome() {
		ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.change_greeting);
		TextView welcomeTextTV = (TextView) findViewById(R.id.welcome_text);

		if ( ! switcher.getCurrentView().equals(welcomeTextTV)){
			switcher.showNext();
		}

	}

	@Override
	public void showList() {
		ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.change_greeting);
		TextView welcomeTextTV = (TextView) findViewById(R.id.welcome_text);

		if ( switcher.getCurrentView().equals(welcomeTextTV)){
			switcher.showPrevious();
		}

	}

	public void feedback() {
		AnimusMiscMethods.email("", "", "mailto:corporationawesome@gmail.com", context);
	}

	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		android.os.Handler delayedAction = new android.os.Handler();
		Context context = this;
		final SoftReference<Context> contextSoft = new SoftReference<>(context);

		delayedAction.postDelayed(new Runnable() {
			@Override
			public void run() {

				switch (item.getItemId()) {
					case R.id.pictures:
						break;
					case R.id.donation:
						donation();       // shows donation popup
						break;
					case R.id.feedback:
						feedback();      // launches feedback operations
						break;
					default:
						AnimusLauncherMethods.getNavigationIntent(item.getItemId(), contextSoft.get());        // getNavigationIntent gets the correct intent for the method to use.
						break;
				}
			}
		}, 150);
		sideNavDrawer.closeDrawer(GravityCompat.START); // closes the side bar
		return true;
	}


	public void donation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.donation, null);

		switch (theme){
			case "Onyx B":
			case "Onyx P":
				TextView titleTV = (TextView) v.findViewById(R.id.title);
				TextView donationTV = (TextView) v.findViewById(R.id.donation_info);
				TextView in_appTV = (TextView) v.findViewById(R.id.inapp_info);

				titleTV.setTextColor(darkTextColor);
				donationTV.setTextColor(darkTextColor);
				in_appTV.setTextColor(darkTextColor);
				break;

		}

        builder.setView(v);
		builder.setNeutralButton(getResources().getString(R.string.dismiss), null);
		builder.create();
		builder.show();
	}

	public void baseDonation(View v){
		donate ("base_donation");
	}

	public void donate(String donation){
		if (in_appBillingService != null) {
			ArrayList<String> skuList = new ArrayList<String>();
			skuList.add(donation);
			Bundle querySkus = new Bundle();
			querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
			Bundle skuDetails;
			try {
				skuDetails = in_appBillingService.getSkuDetails(3, getPackageName(),
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
							Bundle buyIntentBundle = in_appBillingService.getBuyIntent(3,
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

	@Override
	public void in_appCheck(SharedPreferences sp) {

	}


}
