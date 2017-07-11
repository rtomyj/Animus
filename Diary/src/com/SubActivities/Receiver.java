package com.SubActivities;

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.MainActivities.Entries;

public class Receiver extends IntentService{

public Receiver(){
    super("myService");
}


@Override
protected void onHandleIntent(Intent intent) {
    // TODO Auto-generated method stub
    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notify = new Notification(R.drawable.btn_star_big_on,"Its Time to Eat",1000);

    Context context = Receiver.this;
    CharSequence title = "Its Time to Eat";
    CharSequence details = "Click Here to Search for Restaurants";
    Intent myIntent = new Intent(context, Entries.class);
    PendingIntent pending = PendingIntent.getActivity(context, 0, myIntent, 0);
    //notify.setLatestEventInfo(context, title, details, pending);
    nm.notify(0,notify);


}
}
