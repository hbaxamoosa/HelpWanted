package com.baxamoosa.helpwanted.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.ui.JobPostingListActivity;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 5/5/16.
 */
public class HelpWantedSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync job posts, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    // public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_INTERVAL = 60;  // TODO: 5/8/16 increase this short interval after testing is complete 
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final int NOTIFICATION_ID = 999;

    private ValueEventListener jobPostsListener;

    public HelpWantedSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Timber.v("HelpWantedSyncAdapter(Context context, boolean autoInitialize)");
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        Timber.v("getSyncAccount(Context context)");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        HelpWantedSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Timber.v("onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)");

        // since the user is signing out, we need to flush the jobpost table
        getContext().getContentResolver().delete(JobPostContract.JobPostList.CONTENT_URI, null, null);

        jobPostsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Timber.v("There are " + snapshot.getChildrenCount() + " job posts");
                int i = 0;
                for (DataSnapshot jobPostSnapshot : snapshot.getChildren()) {
                    JobPost jobPost = jobPostSnapshot.getValue(JobPost.class); // Firebase is returning JobPost objects from the Cloud

                    // mJobPost = new JobPost[(int) snapshot.getChildrenCount()];
                    if (i < snapshot.getChildrenCount()) {

                        ContentValues jobPostArr = new ContentValues(); // ContentValues[] for local storage
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_ID, jobPost.get_id());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_BUSINESSID, jobPost.getBusinessId());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_BUSINESSNAME, jobPost.getbusinessName());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_BUSINESSADDRESS, jobPost.getbusinessAddress());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_BUSINESSPHONE, jobPost.getbusinessPhone());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_BUSINESSLATITUDE, jobPost.getbusinessLatitude());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_BUSINESSLONGITUDE, jobPost.getbusinessLongitude());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_WAGERATE, jobPost.getWageRate());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_POSTDATE, jobPost.getDate());
                        jobPostArr.put(JobPostContract.JobPostList.COLUMN_OWNER, jobPost.getUser());
                        getContext().getContentResolver().insert(JobPostContract.JobPostList.CONTENT_URI, jobPostArr);

                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Timber.v("The read failed: " + firebaseError.getMessage());
            }
        };

        Utility.mRef.addValueEventListener(jobPostsListener);

        ContentResolver mResolver = getContext().getContentResolver();
        Cursor mCursor = mResolver.query(JobPostContract.JobPostList.CONTENT_URI, Utility.JOBPOST_COLUMNS, null, null, null);
        // create notification
        if (mCursor.getCount() > 0) {
            Timber.v("there are " + mCursor.getCount() + " new job posts for notification");
            notifyJobPosts();
        } else {
            Timber.v("no job posts for notification");
        }
    }

    private void notifyJobPosts() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications) {
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            ContentResolver mResolver = context.getContentResolver();
            Cursor mCursor = mResolver.query(JobPostContract.JobPostList.CONTENT_URI, Utility.JOBPOST_COLUMNS, null, null, null);


            int iconId = R.drawable.ic_launcher_big;
            Resources resources = context.getResources();

            // On Honeycomb and higher devices, we can retrieve the size of the large icon
            // Prior to that, we use a fixed size
            @SuppressLint("InlinedApi")
            int largeIconWidth = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                    ? resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
                    : resources.getDimensionPixelSize(R.dimen.notification_large_icon_default);
            @SuppressLint("InlinedApi")
            int largeIconHeight = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                    ? resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height)
                    : resources.getDimensionPixelSize(R.dimen.notification_large_icon_default);

            String title = context.getString(R.string.app_name);

            // Define the text of the forecast.
            String contentText = String.format("There are " + mCursor.getCount() + " new job posts.");

            // NotificationCompatBuilder is a very convenient way to build backward-compatible
            // notifications.  Just throw in some data.
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getContext())
                            .setColor(resources.getColor(R.color.colorPrimary))
                            .setSmallIcon(iconId)
                            .setContentTitle(title)
                            .setContentText(contentText);

            // Make something interesting happen when the user clicks on the notification.
            // In this case, opening the app is sufficient.
            Intent resultIntent = new Intent(context, JobPostingListActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

            //refreshing last sync
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastNotificationKey, System.currentTimeMillis());
            editor.commit();

            mCursor.close();
        }
    }
}