package com.MainActivities.BaseClasses;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.UtilityClasses.MiscMethods;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rtomyj.Diary.R;


/*
         Created by CaptainSaveAHoe on 7/13/17.
 */

/*
        Handles Ad related tasks. Checks for available network and if it finds a connection then it tries to get an Ad from the remote host and places the ad to the parent view.
        Parent View should have a weight of 1 so that anything added to it resize the parent just enough to see the added view and the parent itself.
 */
public class Activity_Ads extends Activity_Password {
    private AdView adView = null;
    private boolean loadAds = true;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null){
            loadAds = bundle.getBoolean("LOAD_ADS");
        }else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            loadAds = sp.getBoolean("ADS", true);
        }
    }

    private void loadAds(){
        LinearLayout home = (LinearLayout) findViewById(R.id.parent);
        boolean isNetworkAvailable = MiscMethods.isNetworkAvailable(this);

        if (loadAds &&  isNetworkAvailable && adView == null) {
                    adView = new AdView(this);
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
                    AdRequest request = new AdRequest.Builder().setGender(AdRequest.GENDER_FEMALE).build();     // targets the core demographic of the app with ADS.

                    // Start loading the adView in the background.
                    adView.loadAd(request);
                    home.addView(adView); // places AD in the bottom part of the Entries Activity.
        } else if (loadAds && ! isNetworkAvailable && adView != null) {
            home.removeView(adView);
            adView.destroy();
            adView = null;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("LOAD_ADS", loadAds);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadAds();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null)
            adView.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();

    }

}
