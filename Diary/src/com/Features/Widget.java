package com.Features;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.EntryActivities.NewEntry;
import com.SubActivities.SplashScreen;
import com.rtomyj.Diary.R;

public class Widget extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        int appWidgetId;
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            appWidgetId = appWidgetIds[i];
            Log.e("Widget Id", Integer.toString(appWidgetId));

            // Create an Intent to launch ExampleActivity
            Intent launchApp = new Intent(context, SplashScreen.class);
            launchApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchApp, 0);
            // Get the layout for the Amp Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.logo, pendingIntent);

            //

            Intent newEntry = new Intent(context, NewEntry.class);
            newEntry.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent newEntryPending = PendingIntent.getActivity(context, 0, newEntry, 0);

            views.setOnClickPendingIntent(R.id.new_entry, newEntryPending);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
