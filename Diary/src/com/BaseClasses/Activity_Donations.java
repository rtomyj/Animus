package com.BaseClasses;

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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.UtilityClasses.Donation;
import com.android.vending.billing.IInAppBillingService;
import com.rtomyj.Diary.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/*
     Created by CaptainSaveAHoe on 7/13/17.
 */


/*
        Handles all in app purchase related things.
 */




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


public class Activity_Donations extends Activity_Ads {
    // Service objects
    private IInAppBillingService in_appBillingService = null;
    private ServiceConnection connection;


    @Override
    protected void onStart() {
        super.onStart();
        in_appCheck();
    }


    /*
    shows an AlertDialog that has 2 buttons, one for donations and the other to remove ads as well as TextViews that explain what the buttons are for. If the theme is dark then the background for the TV's
    have to be changed.
*/
     void donation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.donation, null);

        if (userUIPreferences.theme.contains("Onyx")) {
            TextView titleTV =  v.findViewById(R.id.title);
            TextView donationTV = v.findViewById(R.id.donation_info);
            TextView inapp_infoTV =  v.findViewById(R.id.inapp_info);

            titleTV.setTextColor(userUIPreferences.textColorForDarkThemes);
            donationTV.setTextColor(userUIPreferences.textColorForDarkThemes);
            inapp_infoTV.setTextColor(userUIPreferences.textColorForDarkThemes);
        }

        builder.setView(v);
        builder.setNeutralButton(getResources().getString(R.string.dismiss), null);
        builder.create();
        builder.show();

    }

    // invoked when user clicks the donation button in the donation AlertDialog
    public void baseDonation(View v) {
        donate("base_donation");       // name of the in_app purchase at DevConsole website.
    }

    // invoked when user clicks the remove ads button in the donation AlertDialog
    public void removeAds(View v) {
        donate("ad_removal");       // name of the in_app purchase at DevConsole website.
    }

    // called by either the removeAds or the baseDonation method. Handles the in_app accordingly
    public void donate(String donationType) {
        try {
            PendingIntent pendingIntent = Donation.requestGooglePlay(in_appBillingService, donationType, getPackageName());
            if (pendingIntent == null)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.donation_err), Toast.LENGTH_LONG).show();
            else
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
        } catch (JSONException | RemoteException | IntentSender.SendIntentException jsonException) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.donation_err), Toast.LENGTH_LONG).show();
            Log.e("Err with in_app", jsonException.toString());
        }

    }

    private void in_appCheck() {
        if (connection == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
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
                    JSONObject jsonObject = new JSONObject(purchaseData);

                    String purchaseToken = jsonObject.optString("token", jsonObject.optString("purchaseToken"));
                    // Consume purchaseToken, handling any errors
                    if (purchaseToken.equals("ad_removal"))
                        sp.edit().putBoolean("ADS", false).apply();
                    else
                        in_appBillingService.consumePurchase(3, getPackageName(), purchaseToken);

                }
            } catch (RemoteException | JSONException | NullPointerException e) {
                Log.e("In app connection err", e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (in_appBillingService != null) {
            unbindService(connection);
        }
    }
}
