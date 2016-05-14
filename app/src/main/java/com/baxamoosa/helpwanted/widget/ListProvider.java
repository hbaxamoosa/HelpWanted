package com.baxamoosa.helpwanted.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.application.HelpWantedApplication;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.utility.Utility;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 5/14/16.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
    private Context context = null;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        Timber.v("ListProvider constructor");
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        Timber.v("populateListItem()");
            /*for (int i = 0; i < 10; i++) {
                ListItem listItem = new ListItem();
                listItem.heading = "Heading" + i;
                listItem.content = i + " This is the content of the app widget listview.Nice content though";
                listItemList.add(listItem);
            }*/

        // Get Job Posts from the ContentProvider
        Uri jobPostsUri = JobPostContract.JobPostList.CONTENT_URI;

        Cursor data = HelpWantedApplication.getAppContext().getContentResolver().query(jobPostsUri, Utility.JOBPOST_COLUMNS, null, null, JobPostContract.JobPostList.COLUMN_POSTDATE + " DESC");
        if (data == null) {
            if (BuildConfig.DEBUG) {
                Timber.d("date is null");
            }
            return;
        }
        if (!data.moveToFirst()) {
            if (BuildConfig.DEBUG) {
                Timber.d("!data.moveToFirst()");
            }
            data.close();
            return;
        }

        data.moveToFirst();

        for (int i = 0; i < data.getCount(); i++) {
            Timber.v("i: " + i);
            // extract data from cursor
            String name = data.getString(Utility.COL_BUSINESS_NAME);
            String address = data.getString(Utility.COL_BUSINESS_ADDRESS);
            String wagerate = data.getString(Utility.COL_WAGERATE);

            ListItem listItem = new ListItem();
            listItem.name = name;
            listItem.address = address;
            listItem.wagerate = wagerate;
            listItemList.add(listItem);
            data.moveToNext();
        }
        if (BuildConfig.DEBUG) {
            Timber.d("data.close()");
        }
        data.close();

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View we return RemoteViews
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.list_row);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.name, listItem.name);
        remoteView.setTextViewText(R.id.address, listItem.address);
        remoteView.setTextViewText(R.id.wagerate, "$" + listItem.wagerate);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}