package com.baxamoosa.helpwanted.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;

import java.util.Date;

import timber.log.Timber;

public class AddEditJobActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText name;
    private EditText phone;
    private EditText address;
    private EditText wageRate;
    private Date date;
    private SharedPreferences sharedPref;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Job Post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.edit_business_name);
        phone = (EditText) findViewById(R.id.edit_business_phone);
        address = (EditText) findViewById(R.id.edit_business_address);
        wageRate = (EditText) findViewById(R.id.edit_business_wage_rate);
        date = new Date();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        email = sharedPref.getString(String.valueOf(R.string.person_email), "someone@email.com");
        Timber.v("email: " + email);

        if (getIntent().hasExtra(String.valueOf(R.string.business_name))) {
            name.setText(getIntent().getStringExtra(String.valueOf(R.string.business_name)));
        }
        if (getIntent().hasExtra(String.valueOf(R.string.business_phone))) {
            phone.setText(getIntent().getStringExtra(String.valueOf(R.string.business_phone)));
        }
        if (getIntent().hasExtra(String.valueOf(R.string.business_address))) {
            address.setText(getIntent().getStringExtra(String.valueOf(R.string.business_address)));
        }
    }

    public void submitToDB(View view) {

        Timber.v("submitToDB(View view)");

        ContentValues[] jobPostArr = new ContentValues[1];

        jobPostArr[0] = new ContentValues();
        jobPostArr[0].put(JobPostContract.JobPostList.COLUMN_BUSINESSID, getIntent().getExtras().getShort(String.valueOf(R.string.business_id)));
        jobPostArr[0].put(JobPostContract.JobPostList.COLUMN_BUSINESSNAME, getIntent().getExtras().getShort(String.valueOf(R.string.business_name)));
        jobPostArr[0].put(JobPostContract.JobPostList.COLUMN_BUSINESSADDRESS, getIntent().getExtras().getShort(String.valueOf(R.string.business_address)));
        jobPostArr[0].put(JobPostContract.JobPostList.COLUMN_BUSINESSPHONE, getIntent().getExtras().getShort(String.valueOf(R.string.business_phone)));
        jobPostArr[0].put(JobPostContract.JobPostList.COLUMN_WAGERATE, Integer.valueOf(wageRate.getText().toString()));
        jobPostArr[0].put(JobPostContract.JobPostList.COLUMN_POSTDATE, Long.valueOf(getIntent().getExtras().getShort(String.valueOf(R.string.business_post_date))));
        jobPostArr[0].put(JobPostContract.JobPostList.COLUMN_OWNER, email);

        getContentResolver().bulkInsert(JobPostContract.JobPostList.CONTENT_URI, jobPostArr);
        // grab the business related details from the bundle

        /*if (!place.getId().isEmpty()) {
            mBundle.putString(String.valueOf(R.string.business_id), place.getId());
        }
        if (!place.getName().toString().isEmpty()) {
            mBundle.putString(String.valueOf(R.string.business_name), (String) place.getName());
        }
        if (!place.getPhoneNumber().toString().isEmpty()) {
            mBundle.putString(String.valueOf(R.string.business_phone), (String) place.getPhoneNumber());
        }
        if (!place.getAddress().toString().isEmpty()) {
            mBundle.putString(String.valueOf(R.string.business_address), (String) place.getAddress());
        }
        if (!place.getLatLng().toString().isEmpty()) {
            mBundle.putDouble(String.valueOf(R.string.business_latitude), place.getLatLng().latitude);
        }
        if (!place.getLatLng().toString().isEmpty()) {
            mBundle.putDouble(String.valueOf(R.string.business_longitude), place.getLatLng().longitude);
        }
        if (!place.getWebsiteUri().toString().isEmpty()) {
            mBundle.putString(String.valueOf(R.string.business_website), place.getWebsiteUri().toString());
        }*/

        // grab the user related details from the Shared Prefs
        
        /*editor = sharedPref.edit();
        editor.putString(String.valueOf(R.string.person_name), result.getSignInAccount().getDisplayName());
        editor.putString(String.valueOf(R.string.person_email), result.getSignInAccount().getEmail());
        editor.putString(String.valueOf(R.string.person_id), result.getSignInAccount().getId());
        editor.putString(String.valueOf(R.string.person_photo), String.valueOf(result.getSignInAccount().getPhotoUrl()));*/

        Timber.v("date: " + date.getTime());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (BuildConfig.DEBUG) {
            Timber.v("onCreateLoader(int id, Bundle args)");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (BuildConfig.DEBUG) {
            Timber.v("onLoadFinished(Loader<Cursor> loader, Cursor data)");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (BuildConfig.DEBUG) {
            Timber.v("onLoaderReset(Loader<Cursor> loader)");
        }
    }
}
