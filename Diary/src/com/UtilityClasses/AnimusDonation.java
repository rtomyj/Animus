package com.UtilityClasses;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by CaptainSaveAHoe on 6/27/17.
 */

public class AnimusDonation {

    private AnimusDonation(){}
    public static PendingIntent requestGooglePlay(IInAppBillingService in_appBillingService, String donation, String packageName) throws JSONException, RemoteException {
        WeakReference<String> donationWeak =  new WeakReference<>(donation);
        PendingIntent pendingIntent = null;

        if (in_appBillingService != null) {
            ArrayList<String> skuList = new ArrayList<>();
            skuList.add(donationWeak.get());
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                Bundle skuDetails;
                skuDetails = in_appBillingService.getSkuDetails(3, packageName, "inapp", querySkus);

                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                    for (String thisResponse : responseList) {
                        JSONObject object = new JSONObject(thisResponse);
                        String sku = object.getString("productId");
                        //String price = objoect.getString("price");
                        if (sku.equals(donationWeak.get())) {
                            Bundle buyIntentBundle = in_appBillingService.getBuyIntent(3, packageName, sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                            pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            return pendingIntent;
                        }
                    }
                }



        }
        return  null;

    }




}
