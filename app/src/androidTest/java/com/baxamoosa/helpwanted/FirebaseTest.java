package com.baxamoosa.helpwanted;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.data.JobPostsDbHelper;
import com.baxamoosa.helpwanted.data.JobPostsProvider;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 5/13/16.
 */
public class FirebaseTest extends AndroidTestCase {

    public static final String LOG_TAG = FirebaseTest.class.getSimpleName();
    static private final int BULK_INSERT_RECORDS_TO_INSERT = 5;

    public void populateFirebase() {
        Firebase mJobPost = Utility.mRef.child("jobpost");

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            String _id = "ChIJkwAWec4yW4YRBWSKMPYdIe0" + i;
            String businessID = "ChIJkwAWec4yW4YRBWSKMPYdIe0" + i + i;
            Calendar calendar = Calendar.getInstance();
            GregorianCalendar postDate = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - i));
            Long postTime = postDate.getTimeInMillis();
            Timber.v("postDate: " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(postTime)));

            JobPost a = new JobPost(_id,
                    businessID,
                    "Starbucks" + i,
                    "13450 Research Blvd, City, CA 12345, United States",
                    "+1 512-401-6253",
                    "http://www.starbucks.com/" + i,
                    -97.79085309999999,
                    30.4472483,
                    i,
                    postTime,
                    "someone@gmail.com");
            Utility.mRef.push().setValue(a);
            Timber.v("successfully posted " + i + " to Firebase");
            Timber.v("postDate: " + postDate);
        }
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                JobPostContract.JobPostList.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                JobPostContract.FavoriteList.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                JobPostContract.JobPostList.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                JobPostContract.FavoriteList.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        JobPostsDbHelper dbHelper = new JobPostsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(JobPostContract.JobPostList.TABLE_NAME, null, null);
        db.delete(JobPostContract.FavoriteList.TABLE_NAME, null, null);
        db.close();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(), JobPostsProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: JobPostsProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + JobPostContract.CONTENT_AUTHORITY,
                    providerInfo.authority, JobPostContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: JobPostsProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        deleteAllRecordsFromDB();

        testProviderRegistry();

        populateFirebase();
    }
}
