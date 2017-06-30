package com.AnimusSubActivities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.AnimusMainActivities.Domus;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rtomyj.Diary.R;

import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Notes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

	private IInAppBillingService mservice = null;
	private ServiceConnection connection;
	private RecyclerView.LayoutManager mLayoutManager;

	private RecyclerView recyclerView;

	private AdView ad = null;

	private int lastSelection = 0;


	private NotesAdapter adapter;
	private Context context;

	private boolean passcodeOn = false;
	private boolean passcodeCheck = true;

	SharedPreferences sp;

	@Override
	protected void onDestroy() {
		if (ad != null) {
			ad.destroy();
		}
		super.onDestroy();
	}


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.entries) {
			Intent domus = new Intent(this, Domus.class);
			startActivityForResult(domus, 2);

        } else if (id == R.id.notes) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		passcodeOn = sp.getBoolean("Password", false);
		if (passcodeOn == true) {
			if (passcodeCheck == true) {
				Intent i = new Intent(this, Passcode.class);
				startActivityForResult(i, 1);
			}
			passcodeCheck = true;
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putInt("LASTSELECTION", lastSelection);
		b.putStringArrayList("NOTESARRAY", adapter.getNotesArr());
	}

	private  void setTheme(){
		sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sp.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (sp.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (sp.getString("Theme", "Default").equals("Material")) {
			super.setTheme(R.style.Material);
		}
		else if (sp.getString("Theme", "Default").equals("Material 2")) {
			super.setTheme(R.style.Material2);
		}
		else if (sp.getString("Theme", "Default").equals("Material 3")) {
			super.setTheme(R.style.Material3);
		}
		else if (sp.getString("Theme", "Default").equals("Material 4")) {
			super.setTheme(R.style.Material4);
		}
	}

	private void customizeUI(){
		if (sp.getString("Theme", "Default").contains("Onyx")) {
			LinearLayout notes = (LinearLayout) findViewById(R.id.notes);
			notes.setBackgroundColor(this.getResources().getColor(
					R.color.UIDarkOnyx));

			TextView welcome_text = (TextView) findViewById(R.id.welcome_text);

			welcome_text.setTextColor(getResources().getColor(R.color.UIDarkText));
		}
	}

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		setTheme();
		context = this;

		setContentView(R.layout.notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


		navigationView.setItemIconTintList(null);
		customizeUI();
		mLayoutManager = new LinearLayoutManager(context);

		if (b!= null && b.getStringArrayList("NOTESARRAY")!= null){

				adapter = new NotesAdapter(this, b.getStringArrayList("NOTESARRAY"));
				lastSelection = b.getInt("LASTSELECTION");


			}

		else{
			adapter =  new NotesAdapter(this);
		}


		// sometimes stuff gets added twice. For example, when the user uses the widget to luanch the newnote activity then goes back, the note is saved causing there to be a preference that confirms this. However, you don't need this preference when the activity first created.
		sp.edit().putString("NEWNOTE", null).apply();
		sp.edit().putString("NOTESAVED", null).apply();
		sp.edit().putString("DELETENOTE", null).apply();


	}

	private void showWelcome(){

		ViewSwitcher changeViews = (ViewSwitcher) findViewById(R.id.change_greeting);
		TextView greeting = (TextView) findViewById(R.id.welcome_text);

		WeakReference<ViewSwitcher> changeViewsWeak = new WeakReference<>(changeViews);
		WeakReference<TextView> greetingWeak = new WeakReference<>(greeting);

		if (changeViewsWeak.get().getCurrentView().equals(greetingWeak.get()) != true)
			changeViewsWeak.get().showPrevious();

	}

	private void showListView(){
		ViewSwitcher  changeViews = (ViewSwitcher) findViewById(R.id.change_greeting);
		TextView greeting = (TextView) findViewById(R.id.welcome_text);

		WeakReference<ViewSwitcher> changeViewsWeak = new WeakReference<>(changeViews);
		WeakReference<TextView> greetingWeak = new WeakReference<>(greeting);

		if (changeViewsWeak.get().getCurrentView().equals(greetingWeak.get()) == true)
			changeViewsWeak.get().showNext();
	}

	@Override
	protected void onStart() {
		super.onStart();



		if (context == null)
			context = this;
		if (sp == null)
			sp =  PreferenceManager
					.getDefaultSharedPreferences(context);

		if (sp.getBoolean("ADS", true) == true && ad == null) {
			ad = new AdView(this);
			ad.setAdSize(AdSize.SMART_BANNER);
			ad.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
			LinearLayout home = (LinearLayout) findViewById(R.id.note);
			home.addView(ad);

			AdRequest request = new AdRequest.Builder().build();

			// Start loading the ad in the background.
			ad.loadAd(request);
		}



		//in_appCheck();

		if (recyclerView == null){
			recyclerView = (RecyclerView) findViewById(R.id.notes_lists);
			recyclerView.setHasFixedSize(true);
			recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setAdapter(adapter);
		}


		 if (sp.getString("NEWNOTE", null)  != null){
			 String newNote = sp.getString("NEWNOTE", null);
			adapter.addNewNote(newNote);

			sp.edit().putString("NEWNOTE", null).apply();


			 recyclerView.scrollToPosition(0);
		}
		else if (sp.getString("NOTESAVED", null) != null){
			String editedNote =sp.getString("NOTESAVED", null);
			adapter.removeNote(editedNote);
			adapter.insertNote(0, editedNote);

			sp.edit().putString("NOTESAVED", null).apply();

		}



		 else if (sp.getString("DELETENOTE", null)!= null){
			 adapter.removeNote(PreferenceManager
					 .getDefaultSharedPreferences(this).getString("DELETENOTE", "null"));
			sp.edit().putString("DELETENOTE", null).apply();




		 }

		if(adapter.getItemCount() != 0){
			showListView();
		}else
		{
			showWelcome();
		}






	}

	private void inappCheck(){

		if (connection == null){
			connection = new ServiceConnection() {

				@Override
				public void onServiceDisconnected(ComponentName name) {
					mservice = null;

				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					mservice = IInAppBillingService.Stub.asInterface(service);
				}
			};



			if (Build.VERSION.SDK_INT < 21) {

				Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
				bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
			}
			else
			{

				Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
				serviceIntent.setPackage("com.android.vending");
				bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
			}

			Bundle ownedItems ;

			try {
				ownedItems = mservice.getPurchases(3, getPackageName(), "inapp",
						null);
				// Get the list of purchased items
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
						mservice.consumePurchase(3, getPackageName(), purchaseToken);




				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			ownedItems = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}



	@Override
	protected void onPause() {
		if (ad != null) {
			ad.pause();
		}
		super.onPause();
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

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
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
		builder.setNeutralButton(
				getResources().getString(
						R.string.dismiss), null);
		builder.create();
		builder.show();
		
	}

	public void settingsClicked(MenuItem view) {
		Intent i = new Intent(this, MainSettingsFrag.class);
		startActivityForResult(i, 2);
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
	public void removeAds(View v) {
		donate("ad_removal");
	}
	public void donate(String donation){
		if (mservice != null) {
			ArrayList<String> skuList = new ArrayList<String>();
			skuList.add(donation);
			Bundle querySkus = new Bundle();
			querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
			Bundle skuDetails;
			try {
				skuDetails = mservice.getSkuDetails(3, getPackageName(),
						"inapp", querySkus);

				int response = skuDetails.getInt("RESPONSE_CODE");
				if (response == 0) {

					ArrayList<String> responseList = skuDetails
							.getStringArrayList("DETAILS_LIST");

					for (String thisResponse : responseList) {
						JSONObject object = new JSONObject(thisResponse);
						String sku = object.getString("productId");
						//String price = object.getString("price");
						if (sku.equals(donation)) {
							//System.out.println("price " + price);
							Bundle buyIntentBundle = mservice.getBuyIntent(3,
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
	
	public void newNoteFromButton(View v){
		Intent i = new Intent(this, NewNote.class);
		startActivityForResult(i, 2);
	}

}