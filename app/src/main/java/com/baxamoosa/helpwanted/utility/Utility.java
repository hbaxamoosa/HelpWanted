package com.baxamoosa.helpwanted.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.Loader;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.application.HelpWantedApplication;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by hasnainbaxamoosa on 4/14/16.
 */
public class Utility {

    public static final int ALL_JOBPOSTS = 0;
    public static final int ACTIVE_JOBPOSTS = 1;
    public static final int EXPIRED_JOBPOSTS = 2;
    public static final int FAVORITE_JOBPOSTS = 3;

    // These indices are tied to JOBPOST_COLUMNS.  If JOBPOST_COLUMNS changes, these must change.
    public static final int COL_ID = 0;
    public static final int COL_BUSINESS_ID = 1;
    public static final int COL_BUSINESS_NAME = 2;
    public static final int COL_BUSINESS_ADDRESS = 3;
    public static final int COL_BUSINESS_PHONE = 4;
    public static final int COL_BUSINESS_WEBSITE = 5;
    public static final int COL_BUSINESS_LATITUDE = 6;
    public static final int COL_BUSINESS_LONGITUDE = 7;
    public static final int COL_WAGERATE = 8;
    public static final int COL_POSTDATE = 9;
    public static final int COL_OWNER = 10;
    public static final String[] JOBPOST_COLUMNS = {
            JobPostContract.JobPostList.COLUMN_ID,
            JobPostContract.JobPostList.COLUMN_BUSINESSID,
            JobPostContract.JobPostList.COLUMN_BUSINESSNAME,
            JobPostContract.JobPostList.COLUMN_BUSINESSADDRESS,
            JobPostContract.JobPostList.COLUMN_BUSINESSPHONE,
            JobPostContract.JobPostList.COLUMN_BUSINESSWEBSITE,
            JobPostContract.JobPostList.COLUMN_BUSINESSLATITUDE,
            JobPostContract.JobPostList.COLUMN_BUSINESSLONGITUDE,
            JobPostContract.JobPostList.COLUMN_WAGERATE,
            JobPostContract.JobPostList.COLUMN_POSTDATE,
            JobPostContract.JobPostList.COLUMN_OWNER
    };

    public static final int LENGTH_OF_VALIDITY = 7; // using a small window for testing purposes. for Production this should be 30
    public static Firebase mRef = new Firebase(HelpWantedApplication.getAppContext().getResources().getString(R.string.firebase_connection_string));

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    public static boolean isNetworkAvailable(Context c) {

        /*if (BuildConfig.DEBUG) {
            Timber.v("isNetworkAvailable(Context c)");
        }*/

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static JobPost[] populateJobPostArray(Loader<Cursor> loader, Cursor data) {
        Cursor mCursor = data;
        mCursor.moveToFirst();
        DatabaseUtils.dumpCursor(data);

        // Timber.v("mCursor.getCount(): " + mCursor.getCount());

        // Create JobPost objects array
        JobPost[] jobPosts = new JobPost[mCursor.getCount()];
        for (int i = 0; i < mCursor.getCount(); i++) {
            jobPosts[i] = new JobPost();
            jobPosts[i]._id = mCursor.getString(Utility.COL_ID);
            // Timber.v("jobPosts[i].id: " + jobPosts[i]._id);
            jobPosts[i].businessId = mCursor.getString(Utility.COL_BUSINESS_ID);
            jobPosts[i].businessName = mCursor.getString(Utility.COL_BUSINESS_NAME);
            jobPosts[i].businessAddress = mCursor.getString(Utility.COL_BUSINESS_ADDRESS);
            jobPosts[i].businessPhone = mCursor.getString(Utility.COL_BUSINESS_PHONE);
            jobPosts[i].businessWebsite = mCursor.getString(Utility.COL_BUSINESS_WEBSITE);
            jobPosts[i].businessLatitude = mCursor.getDouble(Utility.COL_BUSINESS_LATITUDE);
            jobPosts[i].businessLongitude = mCursor.getDouble(Utility.COL_BUSINESS_LONGITUDE);
            jobPosts[i].wageRate = mCursor.getInt(Utility.COL_WAGERATE);
            jobPosts[i].date = mCursor.getLong(Utility.COL_POSTDATE);
            jobPosts[i].user = mCursor.getString(Utility.COL_OWNER);

            mCursor.moveToNext();
        }
        return jobPosts;
    }

    public static boolean isValid(long postDate) {
        boolean bool;
        Calendar calendar = Calendar.getInstance();
        GregorianCalendar validDate = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - LENGTH_OF_VALIDITY));
        Long validTime = validDate.getTimeInMillis();
        /*Timber.v("validTime: " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(validTime)));
        Timber.v("date: " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(postDate)));*/
        /*Timber.v("result is valid");*//*Timber.v("result is invalid");*/
        bool = validTime < postDate;
        return bool;
    }
}
