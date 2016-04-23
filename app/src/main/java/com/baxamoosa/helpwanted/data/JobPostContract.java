package com.baxamoosa.helpwanted.data;

/**
 * Created by hasnainbaxamoosa on 4/20/16.
 */


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the job post database.
 */

public class JobPostContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.baxamoosa.helpwanted";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_FAVORITES = "jobpost";

    /*
        Inner class that defines the contents of the location table
     */
    public static final class JobPostList implements BaseColumns {

        public static final String TABLE_NAME = "jobposts";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        // Columns for jobposts table
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "name";
        public static final String COLUMN_THUMBNAIL = "address";
        public static final String COLUMN_SYNOPSIS = "phone";
        public static final String COLUMN_RATING = "website";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_FAVORITE = "favorite";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static Uri buildJobPostsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
