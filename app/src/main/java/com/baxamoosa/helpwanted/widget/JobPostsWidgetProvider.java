package com.baxamoosa.helpwanted.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 5/14/16.
 */

/**
 * Provider for a widget showing job posts.
 * Delegates widget updating to {@link JobPostsWidgetIntentService} to ensure that
 * data retrieval is done on a background thread
 */
public class JobPostsWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        /*int[] appWidgetIds holds ids of multiple instance of your widget
		 * meaning you are placing more than one widgets on your homescreen*/
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        Timber.v("updateWidgetListView(Context context, int appWidgetId)");

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_jobposts);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, JobPostsWidgetIntentService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget

        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        // remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget, svcIntent);
        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        // Create an Intent to launch MainActivity
        Timber.v("onclick for widget");
        Intent launchIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.listViewWidget, pendingIntent);

        return remoteViews;
    }
}