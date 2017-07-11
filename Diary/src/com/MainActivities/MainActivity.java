package com.MainActivities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.UtilityClasses.AnimusDonation;
import com.UtilityClasses.AnimusLauncherMethods;
import com.UtilityClasses.AnimusMiscMethods;
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

/**
 * Created by CaptainSaveAHoe on 7/5/17.
 */


/*
    Super class for all main activity ... activities. A class/screen that incorporates this will have all its RecyclerView, Adapter, Toolbar, AdView, etc setup.

    Extended class can call all important views/variables. Those that are used just in this class are set to private.
 */

 public class MainActivity <T extends RecyclerView.Adapter, S extends RecyclerView.LayoutManager> extends Activity_A implements NavigationView.OnNavigationItemSelectedListener {

    // Misc views
    protected ViewSwitcher greetingContextVS;
    private AdView ad = null;
    protected Toolbar actionBar;
    protected DrawerLayout sideNavDrawer;

    // Objects to make the listView work
    protected RecyclerView recyclerView;
    protected T activityAdapter;
    protected S recycleViewLayoutManager;

    // Service objects
    private IInAppBillingService in_appBillingService = null;
    private ServiceConnection connection;

    // Misc primitives
    protected boolean potentialNewEntry = false, loadAds = false;

    // others...
    protected int adapterSize = 0;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main_activity_base);

    }

    // creates new Toolbar object and sets it as the activities action bar. Called in subclass after the layout has been set for the activity, otherwise a nullptr will occur
    void setupActionBar(){
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        sideNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // creates toggle for action bar and anchors the side navigation drawer object to it.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, sideNavDrawer, actionBar, 0, 0);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);  // uses the methods in this class to listen for onclick events for the side nav drawer

    }



    // All clicks from the dropDownMenuForSelectedFile side bar are handled here. Uses a handler to delay the click so the sidebar has time to close and the transitions are smooth.
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        android.os.Handler delayedAction = new android.os.Handler();

        Context context = this;
        final SoftReference<Context> contextSoft = new SoftReference<>(context);

        delayedAction.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case R.id.donation:
                        donation();       // shows donation popup
                        break;
                    case R.id.feedback:
                        feedback();      // launches feedback operations
                        break;
                    default:
                        AnimusLauncherMethods.launchActivity(item.getItemId(), contextSoft.get());        // launchActivity gets the correct intent for the method to use.
                        break;
                }
            }
        }, 150);
        sideNavDrawer.closeDrawer(GravityCompat.START); // closes the side bar
        return true;
    }


    /*
        shows an AlertDialog that has 2 buttons, one for donations and the other to remove ads as well as TextViews that explain what the buttons are for. If the theme is dark then the background for the TV's
        have to be changed.
    */
    void donation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.donation, null);

        if (userUIPreferences.theme.contains("Onyx")) {
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView donation = (TextView) v.findViewById(R.id.donation_info);
            TextView inapp = (TextView) v.findViewById(R.id.inapp_info);

            title.setTextColor(userUIPreferences.textColorForDarkThemes);
            donation.setTextColor(userUIPreferences.textColorForDarkThemes);
            inapp.setTextColor(userUIPreferences.textColorForDarkThemes);
        }

        builder.setView(v);

        builder.setNeutralButton(getResources().getString(R.string.dismiss), null);
        builder.create();
        builder.show();

    }


    // invoked when user clicks the donation button in the donation AlertDialog
    public void baseDonation(View v) {
        donate("base_donation");
    }

    // invoked when user clicks the remove ads button in the donation AlertDialog
    public void removeAds(View v) {
        donate("ad_removal");
    }

    // called by either the removeAds or the baseDonation method. Handles the in_app accordingly
    public void donate(String donationType) {
        try {
            PendingIntent pendingIntent = AnimusDonation.requestGooglePlay(in_appBillingService, donationType, getPackageName());
            if (pendingIntent == null)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.donation_err), Toast.LENGTH_LONG).show();
            else
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
        } catch (JSONException | RemoteException | IntentSender.SendIntentException jsonException) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.donation_err), Toast.LENGTH_LONG).show();
            Log.e("Err with in_app", jsonException.toString());
        }

    }


    // when user wants to submit feedback via email this method handles it.
    public void feedback() {
        AnimusMiscMethods.email("", "", "mailto:corporationawesome@gmail.com", this);
    }


    // shows the greeting view only called from onCreate()
    void showWelcome() {
        LinearLayout frameLayout = (LinearLayout) findViewById(R.id.parent);
        WeakReference<LinearLayout> frameLayoutWeak = new WeakReference<>(frameLayout);
        if (greetingContextVS.getCurrentView().equals(frameLayoutWeak.get())) {
            greetingContextVS.showNext();
        }

    }

    // Shows the RecyclerView
    void showList() {
        TextView welcome = (TextView) findViewById(R.id.welcome_text);
        WeakReference<TextView> frameLayoutWeak = new WeakReference<>(welcome);
        if (greetingContextVS.getCurrentView().equals(frameLayoutWeak.get())) {
            greetingContextVS.showNext();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETTINGS:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                if (sp.getBoolean("THEMECHANGE", false)) {  // whenever the  user changes the theme this changes the theme to this activity
                    sp.edit().remove("THEMECHANGE").apply();
                    this.recreate();
                }
                break;
        }

    }


    @Override
    public void onBackPressed() {
        // if back arrow is pressed and the side navigation is open, it closes the side navigation
        if (sideNavDrawer.isDrawerOpen(GravityCompat.START)) {
            sideNavDrawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }


    void in_appCheck(SharedPreferences sp) {
        if (connection == null) {
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
            } else {

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

    private void loadAds(){
        LinearLayout home = (LinearLayout) findViewById(R.id.parent);
        if (loadAds && ad == null) {
            if (AnimusMiscMethods.isNetworkAvailable(this)) { // checks network access, if the phone is connected then an AD can be fetched.
                ad = new AdView(this);
                ad.setAdSize(AdSize.SMART_BANNER);
                ad.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
                home.addView(ad); // places AD in the bottom part of the Entries Activity.

                AdRequest request = new AdRequest.Builder().setGender(AdRequest.GENDER_FEMALE).build(); // targets the core demographic of the app with ADS.
                WeakReference<AdRequest> requestWeak = new WeakReference<>(request);

                // Start loading the ad in the background.
                ad.loadAd(requestWeak.get());
                requestWeak.clear();
            }

        } else {
            if (!AnimusMiscMethods.isNetworkAvailable(this)) { // if not internet connection on phone then it removes the ADview from the main_activity_base activity for more user space.
                home.removeView(ad);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        loadAds();

        //  allows data to get refreshed from whatever backup option user chooses
        //              !!!!! Backup coming soon !!!!!
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        if (!sp.getBoolean("DROPBOXBACKUP", false)) { // there will be a swipeable layout for refreshing data, only if dropbox backup is true in settings.
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mSwipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(this, R.color.UIMaterialTeal),
                    ContextCompat.getColor(this, R.color.UIBlue),
                    ContextCompat.getColor(this, R.color.UIPurple),
                    ContextCompat.getColor(this, R.color.UIPink));
        }


        if (greetingContextVS == null)  // re-inits view switcher if it's null or first time onStart() is called.
            greetingContextVS = (ViewSwitcher) findViewById(R.id.change_greeting);

        // Checks for inapp purchase history
        in_appCheck(sp);


        if (recyclerView == null)       // re-inits recyclerView  if it's null or first time onStart() is called.
            recyclerView = (RecyclerView) findViewById(R.id.list);

        if (adapterSize == 0) {
            showWelcome();  // if not entries are in the app then it shows the generic screen.
        } else
            showList();  // else it shows the user their entries.

        if (recyclerView.getAdapter() == null) { // if there is no adapter bond to recyclerView then entriesAdapter is binded to it.
            Log.e("adapter added", "stuff");
            recyclerView.setHasFixedSize(true);  // children will not impact the redrawing of recyclerView; good for performance.
            recyclerView.setLayoutManager(recycleViewLayoutManager);
            recyclerView.setAdapter(activityAdapter);
        }

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
    protected void onPause() {
        super.onPause();
        if (ad != null) {
            ad.pause();
        }

    }


    // when the activity is being terminated it gets the chance to saveEntryText its state. Here it saves the files, the tags for the files, their status (whether they're favorites) etc.
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("PRIMARY_COLOR",  userUIPreferences.primaryColor);
        bundle.putInt("SECONDARY_COLOR", userUIPreferences.secondaryColor);
        bundle.putInt("DARK_THEME_TEXT_COLOR", userUIPreferences.textColorForDarkThemes);
        bundle.putInt("DARK_THEME_BACKGROUND_COLOR", userUIPreferences.darkThemeBackgroundColor);
        bundle.putInt("TAGS_TEXT_COLOR", userUIPreferences.tagsTextColor);
        bundle.putString("THEME", userUIPreferences.theme);
        bundle.putString("FONT_STYLE", userUIPreferences.fontStyle);
        bundle.putInt("NUM_LINES", userUIPreferences.numLines);
        bundle.putFloat("TEXT_SIZE", userUIPreferences.textSize);

        bundle.putBoolean("LOAD_ADS", loadAds);

    }


     void customizeUI(){
        if (userUIPreferences.theme.contains("Onyx")) {
            TextView welcomeTV = (TextView) findViewById(R.id.welcome_text);

            recyclerView.setBackgroundColor(userUIPreferences.darkThemeBackgroundColor);
            welcomeTV.setTextColor(userUIPreferences.textColorForDarkThemes);
        }
    }
    // launches the NewEntry activity where the user enters new data.
    public void newEntry(View view) {
        AnimusLauncherMethods.newEntry(this);
        potentialNewEntry = true;

    }

    @SuppressWarnings("unchecked")
    void setInfoToActionBar(Byte activity){
        switch (activity) {
            case DOMUS:
                recycleViewLayoutManager =  (S) new LinearLayoutManager(this);
                actionBar.setSubtitle("Total: " + adapterSize);
                break;
            case PIC_ENTRIES:
                recycleViewLayoutManager =  (S) new GridLayoutManager(this, 2);
                actionBar.setSubtitle("Total: " + adapterSize);
                break;
            case TAGS:
                recycleViewLayoutManager = (S) new LinearLayoutManager(this);
                actionBar.setSubtitle("Total: " + adapterSize);
                break;
            default:
                recycleViewLayoutManager = (S) new LinearLayoutManager(this);
                actionBar.setSubtitle("Total: " + adapterSize);
                break;
        }
    }

}
