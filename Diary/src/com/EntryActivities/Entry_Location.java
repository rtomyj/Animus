package com.EntryActivities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import java.util.List;

/**
 * Created by MacMini on 8/26/17.
 */

public class Entry_Location extends Entry_Base implements LocationListener {

    private Location location;
    // other components
    LocationManager locationManager;

    @Override
    public void onLocationChanged(Location location) {
        if ( entryMeta.locationName.equals("")) {
            this.location = location;
            new RetrieveLocation().execute("");
        }

    }

    private class RetrieveLocation extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            Geocoder geo = new Geocoder(Entry_Location.this);
            List<Address> address;
            try {
                address = geo.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);

                entryMeta.locationName = address.get(0).getAddressLine(0) + " " + address.get(0).getAddressLine(1).replaceAll(address.get(0).getPostalCode(), "").trim();
                if (entryMeta.locationName.length() > 20)
                    entryMeta.locationName = address.get(0).getAddressLine(1).replaceAll(address.get(0).getPostalCode(), "").replaceAll(address.get(0).getCountryCode(), "").trim();


                entryMeta.hasLocation = true;
                entryMeta.latitude = location.getLatitude();
                entryMeta.longitude = location.getLongitude();

                try {
                    locationManager.removeUpdates(Entry_Location.this);
                }
                catch (SecurityException locErr){
                    Log.e("Loc Err", locErr.toString());
                }
                return entryMeta.locationName;
            } catch (Exception e1) {
                if (locationManager != null) {
                    try {
                        locationManager.removeUpdates(Entry_Location.this);
                    }catch (SecurityException e){

                    }
                }
                e1.printStackTrace();
                return getResources().getString(R.string.network_err);
            }
        }

        protected void onPostExecute(String feed) {
            TextView currentLocationTV = (TextView) findViewById(R.id.location);
            currentLocationTV.setText(feed);
        }
    }


        public void getLocation(boolean isGPSGrantedByUser, TextView currentLocationTV){
            // if user has granted location services for app then location is pinged.
            if(isGPSGrantedByUser) {
                try {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 1000, this);
                } catch (SecurityException securityException) {
                    Log.e("Location Exception", securityException.toString());
                }
            }
            else{
                currentLocationTV.setVisibility(View.INVISIBLE);
            }

        }



        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
}
