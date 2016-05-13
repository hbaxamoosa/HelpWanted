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
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
            /*Timber.v("getIntent().hasExtra(getString(R.string.addJob))");*/
            addJob();
        } else if (getIntent().hasExtra(getString(R.string.editJob))) {
            /*Timber.v("getIntent().hasExtra(getString(R.string.editJob))");*/
            editJob();
        } else {
            /*Timber.v("this should NEVER happen");*/
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
        email.setText(sharedPref.getString(getString(R.string.person_email), "someone@email.com"));
    }

    private void editJob() {
        ContentResolver mResolver = getContentResolver();
        String selection = JobPostContract.FavoriteList.COLUMN_ID + "=?";
        String[] selectionArgs = {getIntent().getExtras().getString(getString(R.string._id))};

        /*Timber.v("selection: " + selection);
        Timber.v("selectionArgs: " + selectionArgs[0]);*/

        Cursor mCursor = mResolver.query(JobPostContract.JobPostList.CONTENT_URI, Utility.JOBPOST_COLUMNS, selection, selectionArgs, null);
        /*Timber.v("mCursor.getCount: " + mCursor.getCount());*/
        mCursor.moveToFirst();

        // set text values into view
        name.setText(mCursor.getString(Utility.COL_BUSINESS_NAME));
        address.setText(mCursor.getString(Utility.COL_BUSINESS_ADDRESS));
        phone.setText(mCursor.getString(Utility.COL_BUSINESS_PHONE));
        email.setText(mCursor.getString(Utility.COL_OWNER));
        wageRate.setText(mCursor.getString(Utility.COL_WAGERATE));
    }

    public void submitToDB(View view) {
        /*Timber.v("submitToDB(View view)");*/

        if (getIntent().hasExtra(getString(R.string.addJob))) { // adding a new job post
            Timber.v("getIntent().hasExtra(getString(R.string.addJob))");
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
            rootRef.push().setValue(a);

            startActivity(new Intent(this, MainActivity.class));
        } else if (getIntent().hasExtra(getString(R.string.editJob))) { // editing an existing job post
            Timber.v("getIntent().hasExtra(getString(R.string.editJob))");
            // TODO: 5/9/16 delete the existing post and create a new job post Timber.v("deleteJobPost()");

            // delete job post from Firebase (cloud). See http://www.sitepoint.com/creating-a-cloud-backend-for-your-android-app-using-firebase/
            Utility.mRef
                    .orderByChild("_id")
                    .equalTo(getIntent().getExtras().getString(getString(R.string._id)))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                /*Timber.v("dataSnapshot.hasChildren()");*/
                                DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                firstChild.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            /*Timber.v("onCancelled(FirebaseError firebaseError)");*/
                        }
                    });

            String selection = JobPostContract.FavoriteList.COLUMN_BUSINESSID + "=?";
            String[] selectionArgs = {getIntent().getExtras().getString(getString(R.string.business_id))};

            /*Timber.v("selection: " + selection);
            Timber.v("selectionArgs: " + selectionArgs[0].toString());*/

            ContentResolver resolverJobPosts = getContentResolver();
            // delete job post from jobpost table (local)
            resolverJobPosts.delete(JobPostContract.JobPostList.CONTENT_URI, selection, selectionArgs);

            ContentResolver resolverFavorites = getContentResolver();
            // delete job post from favorites table (local)
            resolverFavorites.delete(JobPostContract.FavoriteList.CONTENT_URI, selection, selectionArgs);
            /*Timber.v("Job post for business ID " + getIntent().getExtras().getString(getString(R.string.business_id)) + " deleted");*/

            Firebase rootRef = new Firebase(getString(R.string.firebase_connection_string));
            Firebase mJobPost = rootRef.child("jobpost");

            JobPost a = new JobPost(getIntent().getExtras().getString(getString(R.string._id)),
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
            rootRef.push().setValue(a);

            startActivity(new Intent(this, MainActivity.class));
        } else {
            /*Timber.v("this should NEVER happen");*/
        }

    }

    public void selectNewPlace(View view) {
        // first call PlacePickerActivity to get the Business details
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), PlacePickerActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
        }
        // PlacePicker Activity will call AddEditJobActivity to enter remaining details about the job post
    }
}
