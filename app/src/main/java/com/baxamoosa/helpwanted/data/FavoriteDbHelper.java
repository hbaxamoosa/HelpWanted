package com.baxamoosa.helpwanted.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hasnainbaxamoosa on 4/20/16.
 */

/**
 * Manages a local database for movie favorites.
 */

public class FavoriteDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favorite.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold favorite movies.
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteList.TABLE_NAME + " (" +
                JobPostContract.JobPostList.COLUMN_ID + " TEXT," +
                JobPostContract.JobPostList.COLUMN_BUSINESSID + " TEXT, " +
                JobPostContract.JobPostList.COLUMN_BUSINESSNAME + " TEXT, " +
                JobPostContract.JobPostList.COLUMN_BUSINESSADDRESS + " TEXT, " +
                JobPostContract.JobPostList.COLUMN_BUSINESSPHONE + " TEXT, " +
                JobPostContract.JobPostList.COLUMN_BUSINESSWEBSITE + " TEXT, " +
                JobPostContract.JobPostList.COLUMN_BUSINESSLATITUDE + " DOUBLE, " +
                JobPostContract.JobPostList.COLUMN_BUSINESSLONGITUDE + " DOUBLE, " +
                JobPostContract.JobPostList.COLUMN_WAGERATE + " INT, " +
                JobPostContract.JobPostList.COLUMN_POSTDATE + " LONG, " +
                JobPostContract.JobPostList.COLUMN_OWNER + " TEXT" +
                ");";

        // Timber.v(TAG + " SQL_CREATE_FAVORITES_TABLE is " + SQL_CREATE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteList.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}