package com.AnimusMainActivities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.AnimusAdapters.EntriesAdapter;
import com.AnimusSubActivities.Passcode;
import com.UtilityClasses.AnimusLauncherMethods;
import com.UtilityClasses.AnimusMiscMethods;
import com.UtilityClasses.AnimusFiles;
import com.UtilityClasses.AnimusUI;
import com.UtilityClasses.AnimusXML;
import com.UtilityClasses.AnimusDonation;
import com.UtilityClasses.MainActivity;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rtomyj.Diary.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public class Domus extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainActivity{
    // Views for class
	private ViewSwitcher greetingContextVS;
	private AdView ad = null;

    // Objects for list
    private EntriesAdapter entriesAdapter;
	private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recycleViewLayoutManager;
	private ArrayList<String> faveChangedFileNameArrList = new ArrayList<>(10);
	private ArrayList<String> deletedFileNameArrList = new ArrayList<>(10);

	// Misc. Objects
    private Toolbar actionBar;
	private Snackbar undoDeleteSnackBar;

    // Service objects
	private IInAppBillingService in_appBillingService = null;
	private ServiceConnection connection;
    private boolean shouldLaunchPasswordActivity = false, passWordCheckedSuccessfull = false;
    private boolean isPasswordOn = false;

    // others...
    private int selectedFile;
    private short currentVisibleItem = -1;
    private boolean potentialNewEntry = false;

    // for side nav
    private  DrawerLayout sideNavDrawer ;
	private short totalEntries = 0;

	// UI Customization
	private int primaryColor = 0, secondaryColor = 0, tagsTextColor = 0, numLines, textColorForDarkThemes = 0, darkThemeBackgroundColor = 0;
	private float textSize = 0;
	private String fontStyle, theme;

	// shows the greeting view only called from onCreate()
	public void showWelcome(){
			LinearLayout frameLayout = (LinearLayout) findViewById(R.id.parent);
			WeakReference<LinearLayout> frameLayoutWeak = new WeakReference<>(frameLayout);
			if (greetingContextVS.getCurrentView().equals(frameLayoutWeak.get()) ){
				greetingContextVS.showNext();
			}

	}

	// Shows the RecyclerView
	public void showList(){
			TextView welcome = (TextView) findViewById(R.id.welcome_text);
			WeakReference<TextView> frameLayoutWeak = new WeakReference<>(welcome);
			if (greetingContextVS.getCurrentView().equals(frameLayoutWeak.get()) ){
				greetingContextVS.showNext();
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
					case R.id.entries:
						break;
					case R.id.donation:
						donation();       // shows donation popup
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


	@Override
	public void onBackPressed() {
		// if back arrow is pressed and the side navigation is open, it closes the side navigation
		if (sideNavDrawer.isDrawerOpen(GravityCompat.START)) {
			sideNavDrawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	// shows popup for new users once and only once
    private void showIntro(SharedPreferences sp){
        if (! sp.contains("INTRO") ) {
            sp.edit().putBoolean("INTRO", false).apply();
        }

        if ( ! sp.getBoolean("INTRO", false) ) {
            AlertDialog.Builder introDialog = new AlertDialog.Builder(this);
            View introView = View.inflate(this, R.layout.intro, null);

            introDialog.setView(introView);

            introDialog.setPositiveButton(getResources().getString(R.string.intro_dialog_dismiss_button), null);
            introDialog.create();
            introDialog.show();
            sp.edit().putBoolean("INTRO", true).apply();  // sets preference to true for intro meaning the user already has seen it.
        }
    }

    /*
    gets called whenever activity is launched from a fresh state and there is nothing to load from the bundle of previous go.
     */
    private void loadNewData(SharedPreferences sp) {
		// stores the primary/secondary app color as integers and the tags background color as a drawable. Less calls to the xml.
		int [] colors = AnimusUI.getThemeElements(this, theme);
		primaryColor = colors[0];
		secondaryColor = colors[1];
		textColorForDarkThemes = colors[2];
		darkThemeBackgroundColor = colors[3];
		tagsTextColor = colors[5];

		textSize = Float.parseFloat(sp.getString("TextSize", "14"));
		fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";
		numLines = Integer.parseInt(sp.getString("LineNum", "3"));

		shouldLaunchPasswordActivity = sp.getBoolean("Password", false);

		entriesAdapter = new EntriesAdapter(this, primaryColor, secondaryColor, textColorForDarkThemes, tagsTextColor, theme, fontStyle, numLines, textSize);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Context context = this;
		SoftReference<Context> contextSoft = new SoftReference<>(context);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contextSoft.get());

        isPasswordOn = sp.getBoolean("Password", false);

		// changes the color of views according to theme
		theme =  sp.getString("Theme", "Default");
		AnimusUI.setTheme(context, theme);
        setContentView(R.layout.domus);

        // checks to see if there is a preference named INTRO, if not then it creates a new one with default value of false.
        // when true, intro has been seen and will not show the AlertDialog
        showIntro(sp);

		// creates new Toolbar object and sets it as the activities action bar
    	actionBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(actionBar);
		sideNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		// creates toggle for action bar and anchors the side navigation drawer object to it.
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle((Activity) contextSoft.get(), sideNavDrawer, actionBar, 0, 0);
		WeakReference<ActionBarDrawerToggle> toggleWeak = new WeakReference<>(toggle);
		toggleWeak.get().syncState();
		toggleWeak.clear();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);  // uses the methods in this class to listen for onclick events for the side nav drawer

		// if bundle isn't empty  it means the activity was recreated, it restores all the previous sessions variables from the bundle
        if (bundle != null){
           // loads data from bundle
				boolean faves[] = bundle.getBooleanArray("FAVES");
				assert faves != null;
				ArrayList<Boolean> favArrayList = new ArrayList<>(Collections.nCopies(faves.length, false));
				WeakReference<ArrayList<Boolean>> favArrayListWeak= new WeakReference<>(favArrayList);

                for (int i = 0; i < faves.length; i ++)
					favArrayListWeak.get().set(i, faves[i]);

				/*
				 since all the arrays im getting are from a bundle i figure that using a weak reference here will saveEntryText some memory since entriesAdapter object has a very long life cycle
				 and the bundle arrays will then have a reference to them for a long time
				  */
				WeakReference<ArrayList<String>> sortedFilesArrWeak = new WeakReference<>(bundle.getStringArrayList("SORTEDFILES"));
				WeakReference<ArrayList<String>> tag1ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG1"));
				WeakReference<ArrayList<String>> tag2ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG2"));
				WeakReference<ArrayList<String>> tag3ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG3"));
				WeakReference<Short> currentVisibleWeak = new WeakReference<>(bundle.getShort("CURRENTVISIBLEITEM"));


			primaryColor = bundle.getInt("PRIMARY_COLOR");
			secondaryColor = bundle.getInt("SECONDARY_COLOR");
			textColorForDarkThemes = bundle.getInt("DARK_THEME_TEXT_COLOR");
			tagsTextColor = bundle.getInt("TAGS_TEXT_COLOR");
			theme = bundle.getString("THEME");
			fontStyle = bundle.getString("FONT_STYLE");
			numLines = bundle.getInt("NUM_LINES");
			textSize = bundle.getFloat("TEXT_SIZE");
			shouldLaunchPasswordActivity = bundle.getBoolean("PASSWORD_CHECK");


				// new files adapter object
				entriesAdapter = new EntriesAdapter(contextSoft.get(),  sortedFilesArrWeak.get(), tag1ArrWeak.get(), tag2ArrWeak.get() , tag3ArrWeak.get() ,favArrayListWeak.get(),
						primaryColor, secondaryColor, textColorForDarkThemes, tagsTextColor, theme, fontStyle, numLines, textSize);
	            currentVisibleItem =  currentVisibleWeak.get();
        }
        else {  // if there is nothing to copy from bundle, call method
            loadNewData(sp);
        }

		recycleViewLayoutManager = new LinearLayoutManager(contextSoft.get());




    }
	// Sets the UI elements to specified colors depending on the theme used.
	public void customizeUI(){
		if (theme.contains("Onyx")) {
			TextView welcomeTV = (TextView) findViewById(R.id.welcome_text);

			recyclerView.setBackgroundColor(darkThemeBackgroundColor);
			welcomeTV.setTextColor(textColorForDarkThemes);
		}

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
					in_appBillingService.consumePurchase(3, getPackageName(), purchaseToken);

					
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			}
	}



	@Override
	protected void onStart() {
		super.onStart();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (recyclerView == null)// re-inits recyclerView  if it's null or first time onStart() is called.
			recyclerView = (RecyclerView) findViewById(R.id.list);


		customizeUI();  // changes UI elements


        // checks to see if there is an internet connection and puts an ad if there is.
        WeakReference<Context> contextWeak = new WeakReference<Context>(this);

		LinearLayout home = (LinearLayout) findViewById(R.id.parent);

        if (sp.getBoolean("ADS", true)  && ad == null){
            if (AnimusMiscMethods.isNetworkAvailable(contextWeak.get())) { // checks network access, if the phone is connected then an AD can be fetched.
                        ad = new AdView(contextWeak.get());
                        ad.setAdSize(AdSize.SMART_BANNER);
                        ad.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
                        home.addView(ad); // places AD in the bottom part of the Domus Activity.

                        AdRequest request = new AdRequest.Builder().setGender(AdRequest.GENDER_FEMALE).build(); // targets the core demographic of the app with ADS.
                        WeakReference<AdRequest> requestWeak = new WeakReference<>(request);

                        // Start loading the ad in the background.
                        ad.loadAd(requestWeak.get());
				        requestWeak.clear();
                    }

        }
        else {
            if (!AnimusMiscMethods.isNetworkAvailable(contextWeak.get())){ // if not internet connection on phone then it removes the ADview from the domus activity for more user space.
                home.removeView(ad);
            }
        }

		if (undoDeleteSnackBar == null) {
					undoDeleteSnackBar = Snackbar.make(home, getText(R.string.entry_deleted_snackbar_dialog), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.undo_text), new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					deletedFileNameArrList.remove(deletedFileNameArrList.size() - 1);  // removes last inserted element from array list
					entriesAdapter.restorePreviousDeletion(); // calls the restore method from entries adapter to get the info of the delete file.

					totalEntries ++;
					actionBar.setSubtitle("Total Entries: " +Integer.toString(totalEntries));

					if (entriesAdapter.getItemCount() == 0){
						showWelcome();
					}
					else
						showList();
				}
			});
			((TextView) (undoDeleteSnackBar.getView().findViewById(android.support.design.R.id.snackbar_text))).setTextColor(ContextCompat.getColor(contextWeak.get(), R.color.Peach));
		}

		// Creates xml files for tags, faves, etc...
		AnimusXML.checkForAppFiles(getFilesDir(), getAssets(), getBaseContext().getContentResolver());
        // Checks for inapp purchase history
		in_appCheck(sp);
		
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		WeakReference<SwipeRefreshLayout> mSwipeRefreshLayoutWeak = new WeakReference<>(mSwipeRefreshLayout);

		if (! sp.getBoolean("DROPBOXBACKUP", false)) { // there will be a swipeable layout for refreshing data, only if dropbox backup is true in settings.
			mSwipeRefreshLayoutWeak.get().setEnabled(false);
		}
		else{
			mSwipeRefreshLayoutWeak.get().setColorSchemeColors(
					ContextCompat.getColor(contextWeak.get(), R.color.UIMaterialTeal),
                    ContextCompat.getColor(contextWeak.get(), R.color.UIBlue),
                    ContextCompat.getColor(contextWeak.get(),R.color.UIPurple),
                    ContextCompat.getColor(contextWeak.get(),R.color.UIPink));
		}

        if (greetingContextVS == null)  // re-inits view switcher if it's null or first time onStart() is called.
		greetingContextVS = (ViewSwitcher) findViewById(R.id.change_greeting);

			if (recyclerView.getAdapter() == null) { // if there is no adapter binded to recyclerView then entriesAdapter is binded to it.
                Log.e("adapter added", "stuff");
            	recyclerView.setHasFixedSize(true);  // children will not impact the redrawing of recyclerView; good for performance.
                recyclerView.setLayoutManager(recycleViewLayoutManager);
                recyclerView.setAdapter(entriesAdapter);
		}

		if (entriesAdapter.getItemCount() == 0){
			showWelcome();  // if not entries are in the app then it shows the generic screen.
		}
		else
			showList();  // else it shows the user their entries.

				totalEntries =  (short)(entriesAdapter.getItemCount());
                actionBar.setSubtitle("Total Entries: " +  Integer.toString(totalEntries));

	}

	/*
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, 1111);
			} catch (IntentSender.SendIntentException e) {
				// Unable to resolve, message user appropriately
			}
		} else {
			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
		}
		
	}
	*/

	// launches the NewEntry activity where the user enters new data.
	public void newEntry(View view) {
		AnimusLauncherMethods.newEntry(this);
        potentialNewEntry = true;

    }

	public void feedback() {
		AnimusMiscMethods.email("", "", "mailto:corporationawesome@gmail.com", this);
	}

	// starts activity NewEntry
	public void newNote(View v) {
		Intent newEntry = new Intent("com.diary.new_note");
		startActivity(newEntry);
	}



	// There is a hamburger style icon on every view in the List Widget. This handles on click calls for such instances.
	public void dropDownMenuForSelectedFile(View threeVerticleDotMenu) {
		Context context = this;
		WeakReference<Context> contextWeak = new WeakReference<>(context);

		PopupMenu popup = new PopupMenu(contextWeak.get(), threeVerticleDotMenu);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.long_click_entries, popup.getMenu());
		popup.show();

		selectedFile  = threeVerticleDotMenu.getId();
	}


	// User can favorite an entry right from the List widget.
	/*
		whenever there is a change to the view faveView a new index is added to the array faveChangedFileNameArrList. This index is the corresponding index of the file in the adapter.
		If however, the index is already in the array, then it gets removed. The purpose is that all the changes will be handled at the same time. If there are no changes then that saves on time.
		 */
	public void favoriteSelectedFile(View faveView) {
		String filename = entriesAdapter.getFilename(faveView.getId());
		int index = faveChangedFileNameArrList.indexOf(filename), faveViewIndex = faveView.getId();
		if (index != -1)
			faveChangedFileNameArrList.remove(index);
		else
			faveChangedFileNameArrList.add(filename);

		for (String ind: faveChangedFileNameArrList){
				Log.e("Favorited", ind);
		}


		if (((TextView) faveView).getText().toString().equals("☆")) {
			((TextView) faveView).setText(Html.fromHtml("★"));
			entriesAdapter.setFaveStatus(faveViewIndex, true);

		} else {
			((TextView) faveView).setText(Html.fromHtml("☆"));
			entriesAdapter.setFaveStatus(faveViewIndex, false);
		}

	}

	public void delete(MenuItem m) {
		// dismisses SnackBar if it is shown to prevent a situation where user can't undo a deletion
		if(undoDeleteSnackBar.isShown())
			undoDeleteSnackBar.dismiss();


		Context context = this;

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		WeakReference<AlertDialog.Builder> builderWeak = new WeakReference<>(builder);

		builderWeak.get().setTitle(R.string.delete_entry_dialog);
		builderWeak.get().setMessage(R.string.delelte_dialog_message);
		builderWeak.get().setIcon(ContextCompat.getDrawable(this, R.drawable.white_discard));
		builderWeak.get().setNegativeButton(R.string.no, null);
		builderWeak.get().setPositiveButton(R.string.delete_confirmation,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						/*
						AnimusFiles.deleteSingleEntry(contextSoft.get(), entriesAdapter.getFilename(selectedFile));
						*/

						undoDeleteSnackBar.show();
						deletedFileNameArrList.add(entriesAdapter.getFilename(selectedFile));
						entriesAdapter.childRemoved(selectedFile);
						totalEntries --;
						actionBar.setSubtitle("Total Entries: " +Integer.toString(totalEntries));

						if (entriesAdapter.getItemCount() == 0){
							showWelcome();
						}
						else
							showList();
					}
				});
		builderWeak.get().create();
		builderWeak.get().show();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ad != null) {
			ad.pause();
		}
		if (!faveChangedFileNameArrList.isEmpty()) {
			/*
			this removes the intersection of deletedFileNameArrList and faveChangedFileNameArrList since it is redundant to set
			// fave for a file that iis going to be deleted.
			 */
			faveChangedFileNameArrList.removeAll(deletedFileNameArrList);
			AnimusXML.updateFavesToXML(this.getFilesDir(), faveChangedFileNameArrList); // commit the changes to favorite setting to the xml.
		}
		if(!deletedFileNameArrList.isEmpty()){
			for(String index: deletedFileNameArrList){
				Log.e("Deleted File", index);
			}
			new AnimusFiles.DeleteMultipleEntries(this, deletedFileNameArrList).execute("");
		}
	}


	// when the activity is being terminated it gets the chance to saveEntryText its state. Here it saves the files, the tags for the files, their status (whether they're favorites) etc.
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		boolean faves[] = new boolean[entriesAdapter.getItemCount()];

		for (int i = 0; i < entriesAdapter.getItemCount(); i ++)
			faves[i]= entriesAdapter.getFaveAtPosition(i);

		bundle.putShort("CURRENTVISIBLEITEM", currentVisibleItem);
        bundle.putStringArrayList("SORTEDFILES", entriesAdapter.getSortedFiles());
        bundle.putStringArrayList("TAG1", entriesAdapter.getFirstTags());
        bundle.putStringArrayList("TAG2", entriesAdapter.getSecondTags());
		bundle.putStringArrayList("TAG3", entriesAdapter.getThirdTags());
		bundle.putBooleanArray("FAVES", faves);

		bundle.putInt("PRIMARY_COLOR", primaryColor);
		bundle.putInt("SECONDARY_COLOR", secondaryColor);
		bundle.putInt("DARK_THEME_TEXT_COLOR", textColorForDarkThemes);
		bundle.putInt("TAGS_TEXT_COLOR", tagsTextColor);
		bundle.putString("THEME", theme);
		bundle.putString("FONT_STYLE", fontStyle);
		bundle.putInt("NUM_LINES", numLines);
		bundle.putFloat("TEXT_SIZE",textSize);

		bundle.putBoolean("PASSWORD_CHECK", shouldLaunchPasswordActivity);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ad != null) {
			ad.destroy();
			
		}
		if (in_appBillingService != null) {
	        unbindService(connection);
	    } 
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		// prevents password prompt from reappearing once the user enters the correct password and when user comes back from an activity
		shouldLaunchPasswordActivity = false;
		passWordCheckedSuccessfull = true;

		switch (requestCode){
			case NEW_ENTRY:
				String  newFileName = sp.getString("NEWFILENAME", null);

        	/*
        		 potentialNewEntry is a boolean  that only gets set to true when the user clicks on the NewEntry button and  connects to that activity.
         		This if statement will only run if there is a new entry and its purpose is to refresh the Adapter with the new entry.
          	*/
				if (potentialNewEntry  &&  newFileName != null ){
					// adds the new file to the adapter and refreshes it so the contents of the RecyclerView make chronological sense.
					entriesAdapter.newEntry(newFileName, sp.getString("NEWFILETAG1", ""), sp.getString("NEWFILETAG2", ""), sp.getString("NEWFILETAG3", ""), sp.getBoolean("NEWFILEFAVE", false));

					totalEntries ++;
					actionBar.setSubtitle("Total Entries: " +Integer.toString(totalEntries));
					actionBar.setSubtitle("Total Entries: " +Integer.toString(totalEntries));

					potentialNewEntry = false;

					recyclerView.scrollToPosition(0);

			/*
				I use a preference to store new file name and their tags on the NewEntry activity if, for some reason, they don't go back to this activity then the onCreate method will pull in new data from
				their directory (new file is in there) and also attempt to read these preferences later in the onStart method. Removing them preemptive wll remove redundancies.
		 	*/
					sp.edit().remove("NEWFILENAME").apply();
					sp.edit().remove("NEWFILETAG1").apply();
					sp.edit().remove("NEWFILETAG2").apply();
					sp.edit().remove("NEWFILETAG3").apply();
				}
				break;
			case SETTINGS:
				if (sp.getBoolean("THEMECHANGE", false) ) {  // whenever the  user changes the theme this changes the theme to this activity
					sp.edit().remove("THEMECHANGE").apply();
					this.recreate();
				}
				break;
			case PASSWORD_CHECK:
				/*
				if (resultCode == RESULT_OK) {
					shouldLaunchPasswordActivity = false;
					passWordCheckedSuccessfull = true;
				}
				break;
				*/
			case 1111:
				if (resultCode == RESULT_OK) {
					// mGoogleApiClient.connect();
				}
				break;
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (shouldLaunchPasswordActivity) {
			Intent i = new Intent(this, Passcode.class);
			startActivityForResult(i, PASSWORD_CHECK);
		}
		if (passWordCheckedSuccessfull) { 		// user has entered password successfully and the app will not need to launch password activity again.
			shouldLaunchPasswordActivity = true;
			passWordCheckedSuccessfull = false;
		}

	}

	public void selection(MenuItem z) {
		AnimusLauncherMethods.chosenFile(this, entriesAdapter.getFilename(selectedFile), selectedFile, entriesAdapter.getSortedFiles());
	}


	public void donation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.donation, null);

		if (theme.contains("Onyx")) {

			TextView title = (TextView) v.findViewById(R.id.title);
			TextView donation = (TextView) v.findViewById(R.id.donation_info);
			TextView inapp = (TextView) v.findViewById(R.id.inapp_info);

			title.setTextColor(textColorForDarkThemes);
			donation.setTextColor(textColorForDarkThemes);
			inapp.setTextColor(textColorForDarkThemes);
		}

		builder.setView(v);

		builder.setNeutralButton(getResources().getString(R.string.dismiss), null);
		builder.create();
		builder.show();

	}

	public void baseDonation(View v) {
		donate("base_donation");
	}

	public void removeAds(View v) {
		donate("ad_removal");
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

}
