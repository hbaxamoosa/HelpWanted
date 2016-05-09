package com.baxamoosa.helpwanted.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.Firebase;

import java.util.Date;

import timber.log.Timber;

public class AddEditJobActivity extends AppCompatActivity {

    private EditText name;
    private EditText phone;
    private EditText email;
    private EditText address;
    private EditText wageRate;
    private Date date;
    private SharedPreferences sharedPref;

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
        email = (EditText) findViewById(R.id.edit_business_email);
        address = (EditText) findViewById(R.id.edit_business_address);
        wageRate = (EditText) findViewById(R.id.edit_business_wage_rate);
        date = new Date();

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (getIntent().hasExtra(getString(R.string.addJob))) {
            Timber.v("getIntent().hasExtra(getString(R.string.addJob))");
            addJob();
        } else if (getIntent().hasExtra(getString(R.string.editJob))) {
            Timber.v("getIntent().hasExtra(getString(R.string.addJob))");
            editJob();
        } else {
            Timber.v("this should NEVER happen");
        }
    }

    private void addJob() {
        if (getIntent().hasExtra(getString(R.string.business_name))) {
            name.setText(getIntent().getStringExtra(getString(R.string.business_name)));
        }
        if (getIntent().hasExtra(getString(R.string.business_address))) {
            address.setText(getIntent().getStringExtra(getString(R.string.business_address)));
        }
        if (getIntent().hasExtra(getString(R.string.business_phone))) {
            phone.setText(getIntent().getStringExtra(getString(R.string.business_phone)));
        }
        if (getIntent().hasExtra(getString(R.string.business_email))) {
            email.setText(sharedPref.getString(getString(R.string.person_email), "someone@email.com"));
        }
    }

    private void editJob() {
        ContentResolver mResolver = getContentResolver();
        String selection = JobPostContract.FavoriteList.COLUMN_ID + "=?";
        String[] selectionArgs = {getIntent().getExtras().getString(getString(R.string._id))};

        Timber.v("selection: " + selection);
        Timber.v("selectionArgs: " + selectionArgs[0].toString());
        Cursor mCursor = mResolver.query(JobPostContract.FavoriteList.CONTENT_URI, Utility.JOBPOST_COLUMNS, selection, selectionArgs, null);
        Timber.v("mCursor.getCount" + mCursor.getCount());
        mCursor.moveToFirst();

        // set text values into view
        name.setText(mCursor.getString(Utility.COL_BUSINESS_NAME));
        address.setText(mCursor.getString(Utility.COL_BUSINESS_ADDRESS));
        phone.setText(mCursor.getString(Utility.COL_BUSINESS_PHONE));
        email.setText(mCursor.getString(Utility.COL_OWNER));
    }

    public void submitToDB(View view) {
        Timber.v("submitToDB(View view)");

        Timber.v("creating Firebase reference");
        Firebase rootRef = new Firebase(getString(R.string.firebase_connection_string));
        Firebase mJobPost = rootRef.child("jobpost");

        JobPost a = new JobPost(getIntent().getExtras().getString(getString(R.string.business_id)),
                getIntent().getExtras().getString(getString(R.string.business_id)),
                name.getText().toString(),
                address.getText().toString(),
                phone.getText().toString(),
                getIntent().getExtras().getString(getString(R.string.business_website)),
                getIntent().getExtras().getDouble(getString(R.string.business_latitude)),
                getIntent().getExtras().getDouble(getString(R.string.business_longitude)),
                Integer.parseInt(wageRate.getText().toString()),
                date.getTime(),
                sharedPref.getString(getString(R.string.person_email), "someone@email.com"));
        Timber.v("attempting to post to Firebase");
        rootRef.push().setValue(a);
        Timber.v("successfully posted to Firebase");

        // job post submitted via content provider, so go back to MyJobsActivity
        startActivity(new Intent(this, MyJobsActivity.class));
    }

    public void selectNewPlace(View view) {

    }

    /*private void placePhotosTask(String placeID) {

        if (BuildConfig.DEBUG) {
            Timber.v("placePhotosTask(String placeID)");
        }
        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(mPhoto.getWidth(), mPhoto.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                mPhoto.setImageResource(R.drawable.empty_photo);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (BuildConfig.DEBUG) {
                    Timber.v("onPostExecute(AttributedPhoto attributedPhoto)");
                }
                if (attributedPhoto != null) {
                    if (BuildConfig.DEBUG) {
                        Timber.v("(attributedPhoto != null)");
                    }
                    // Photo has been loaded, display it.
                    mPhoto.setImageBitmap(attributedPhoto.bitmap);

                    // Display the attribution as HTML content if set.
                    if (attributedPhoto.attribution == null) {
                        mAttribution.setVisibility(View.GONE);
                    } else {
                        mAttribution.setVisibility(View.VISIBLE);
                        mAttribution.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                    }

                }
            }
        }.execute(placeID);
    }

    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        *//**
     * Loads the first photo for a place id from the Geo Data API.
     * The place id must be the first (and only) parameter.
     *//*
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        *//**
     * Holder for an image and its attribution.
     *//*
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }*/
}
