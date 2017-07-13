package com.BaseClasses;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.UtilityClasses.AnimusMiscMethods;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rtomyj.Diary.R;

import java.lang.ref.WeakReference;

/**
 * Created by CaptainSaveAHoe on 7/13/17.
 */

public class Activity_Ads extends Activity_Password {
    private AdView ad = null;
    private boolean loadAds = true;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (bundle != null){
            loadAds = bundle.getBoolean("LOAD_ADS");
        }else
            loadAds = sp.getBoolean("ADS", true);
    }

    private void loadAds(){
        LinearLayout home = (LinearLayout) findViewById(R.id.parent);

        if (loadAds) {
            if (AnimusMiscMethods.isNetworkAvailable(this)) { // checks network access, if the phone is connected then an AD can be fetched.
                if (ad == null){
                    ad = new AdView(this);
                    ad.setAdSize(AdSize.SMART_BANNER);
                    ad.setAdUnitId("ca-app-pub-9636313395157467/8316827940");
                    AdRequest request = new AdRequest.Builder().setGender(AdRequest.GENDER_FEMALE).build(); // targets the core demographic of the app with ADS.

                    // Start loading the ad in the background.
                    ad.loadAd(request);
                    home.addView(ad); // places AD in the bottom part of the Entries Activity.
                }
            }

        } else {
            if (!AnimusMiscMethods.isNetworkAvailable(this)) { // if not internet connection on phone then it removes the ADview from the main_activity_base activity for more user space.
                home.removeView(ad);
            }
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
        if (ad != null) {
            ad.destroy();

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
        if (ad != null) {
            ad.pause();
        }

    }
}
