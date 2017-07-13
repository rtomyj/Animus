package com.BaseClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
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
import android.widget.ViewSwitcher;

import com.MainActivities.Domus;
import com.UtilityClasses.AnimusLauncherMethods;
import com.UtilityClasses.AnimusMiscMethods;
import com.rtomyj.Diary.R;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Created by CaptainSaveAHoe on 7/5/17.
 */


/*
    Super class for all main activity ... activities. A class/screen that incorporates this will have all its RecyclerView, Adapter, Toolbar, AdView, etc setup.

    Extended class can call all important views/variables. Those that are used just in this class are set to private.
 */

 public class MainActivity <T extends RecyclerView.Adapter, S extends RecyclerView.LayoutManager> extends Activity_Donations
        implements NavigationView.OnNavigationItemSelectedListener {

    // Misc views
    private ViewSwitcher greetingContextVS;
    private Toolbar actionBar;
    private DrawerLayout sideNavDrawer;


    // Objects to make the listView work
    private RecyclerView recyclerView;
    private S recycleViewLayoutManager;
    protected T activityAdapter;


    // Misc primitives
    protected int adapterSize = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main_activity_base);

        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setEnabled(false);
/*
        //  allows data to get refreshed from whatever backup option user chooses
        //              !!!!! Backup coming soon !!!!!

        if (!sp.getBoolean("DROPBOXBACKUP", false)) { // there will be a swipeable layout for refreshing data, only if dropbox backup is true in settings.
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mSwipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(this, R.color.UIMaterialTeal),
                    ContextCompat.getColor(this, R.color.UIBlue),
                    ContextCompat.getColor(this, R.color.UIPurple),
                    ContextCompat.getColor(this, R.color.UIPink));
        }

        */

    }

    protected void setup(){
        setupActionBar();
        setAdaptersForCurrentActivity();
        customizeUI();
        changeGreetingText();
    }

    // creates new Toolbar object and sets it as the activities action bar. Called in subclass after the layout has been set for the activity, otherwise a nullptr will occur
    private void setupActionBar(){
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        sideNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // creates toggle for action bar and anchors the side navigation drawer object to it.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, sideNavDrawer, actionBar, 0, 0);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);  // uses the methods in this class to listen for onclick events for the side nav drawer
        navigationView.setCheckedItem(R.id.domus);
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
                        //donation();       // shows donation popup
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


    // when user wants to submit feedback via email this method handles it.
    public void feedback() {
        AnimusMiscMethods.email("", "", "mailto:corporationawesome@gmail.com", this);
    }


    // shows the greeting view only called from onCreate()
    protected void showWelcome() {
        LinearLayout frameLayout = (LinearLayout) findViewById(R.id.parent);
        if (greetingContextVS.getCurrentView().equals(frameLayout)) {
            greetingContextVS.showNext();
        }

    }

    // Shows the RecyclerView
    protected void showList() {
        TextView welcome = (TextView) findViewById(R.id.welcome_text);
        if (greetingContextVS.getCurrentView().equals(welcome)) {
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


    @Override
    protected void onStart() {
        super.onStart();
        if (greetingContextVS == null)  // re-inits view switcher if it's null or first time onStart() is called.
            greetingContextVS = (ViewSwitcher) findViewById(R.id.change_greeting);

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



     private void customizeUI(){
        if (userUIPreferences.theme.contains("Onyx")) {
            TextView welcomeTV = (TextView) findViewById(R.id.welcome_text);

            recyclerView.setBackgroundColor(userUIPreferences.darkThemeBackgroundColor);
            welcomeTV.setTextColor(userUIPreferences.textColorForDarkThemes);
        }
    }

    // launches the NewEntry activity where the user enters new data.
    public void newEntry(View view) {
        AnimusLauncherMethods.newEntry(this);
    }

    @SuppressWarnings("unchecked")
    protected void setAdaptersForCurrentActivity(){
        adapterSize = activityAdapter.getItemCount();
        switch (currentActivity) {
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

    protected void setActionBarSubTitle(String title){
        actionBar.setSubtitle(title);
    }
    protected  void scrollToX(int x){
        recycleViewLayoutManager.scrollToPosition(0);
    }
    private void changeGreetingText(){
        TextView welcome = (TextView) findViewById(R.id.welcome_text);
        switch (currentActivity){
            case DOMUS:
                welcome.setText(getResources().getString(R.string.entries_welcome));
                break;
            case PIC_ENTRIES:
                welcome.setText(getResources().getString(R.string.pictures_welcome));
                break;
            case TAGS:
                welcome.setText(getResources().getString(R.string.tags_welcome));
                break;
            case FAVES:
                welcome.setText(getResources().getString(R.string.faves_welcome));
                break;
        }
    }

    protected void closeNavDrawer(){
        sideNavDrawer.closeDrawer(GravityCompat.START); // closes the side bar
    }


}
