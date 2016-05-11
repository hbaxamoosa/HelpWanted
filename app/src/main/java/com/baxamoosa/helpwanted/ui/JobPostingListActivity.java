package com.baxamoosa.helpwanted.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.adapter.JobPostingListAdapter;
import com.baxamoosa.helpwanted.application.HelpWantedApplication;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * An activity representing a list of JobPostings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link JobPostingDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class JobPostingListActivity extends AppCompatActivity implements /*JobPostingListAdapter.Callback,*/ LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LOCATION = 1;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    public boolean mTwoPane;  // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    public boolean firstLoad;  // Whether or not the activity is in two-pane mode and whether this is the first load or not.
    public JobPost[] mJobPost;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView profileName;
    private ImageView profilePhoto;
    private RecyclerView.Adapter mJobPostingListAdapter;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private ValueEventListener jobPostsListener;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean mResolvingError;
    private int REQUEST_RESOLVE_ERROR;
    // private Cursor mCursor;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("jobposts", mJobPost);
        super.onSaveInstanceState(outState);
        firstLoad = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set a flag to track whether this is the first time the JobPostingsAdapter is being loaded into the app.
        firstLoad = true;

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        boolean isConnected = Utility.isNetworkAvailable(HelpWantedApplication.getAppContext());

        setContentView(R.layout.activity_jobposting_list_drawer);

        emptyView = (TextView) findViewById(R.id.recyclerview_empty);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        getSupportLoaderManager().initLoader(Utility.ALL_JOBPOSTS, null, JobPostingListActivity.this);

        mRecyclerView = (RecyclerView) findViewById(R.id.jobposting_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Timber.v("setupRecyclerView() is complete");

        if (savedInstanceState != null) {
            Timber.v("savedInstanceState != null");
            mJobPost = (JobPost[]) savedInstanceState.getParcelableArray("jobposts");
        }

        if (savedInstanceState == null) {
            Timber.v("savedInstanceState == null");

            View jobPostDetails = findViewById(R.id.fragment_detail_container);
            if (jobPostDetails != null && jobPostDetails.getVisibility() == View.VISIBLE) {

                Timber.v("if (jobPostDetails != null && jobPostDetails.getVisibility() == View.VISIBLE)");
                // The detail container view will be present only in the
                // large-screen layouts (res/values-large and
                // res/values-sw600dp). If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;

                // In two-pane mode, list items should be given the 'activated' state when touched.
                /*FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
                JobPostingDetailFragment mFragment = new JobPostingDetailFragment();
                mTransaction.replace(R.id.fragment_detail_container, mFragment);
                mTransaction.commit();*/
            }

            // TODO: 5/10/16 verify that this is working
            String s = sharedPref.getString(getString(R.string.range), "no range value");
            Timber.v("this is the range value from sharedPrefs: " + s);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) {
            Timber.v("onStart()");
        }
        // populateTestData();  // test data

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) {
            Timber.v("onStop()");
        }

        if (mGoogleApiClient.isConnected()) {
            if (BuildConfig.DEBUG) {
                Timber.v("mGoogleApiClient.disconnect()");
            }
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            Timber.v("onResume()");
        }
        // HelpWantedSyncAdapter.syncImmediately(this);
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                profileName = (TextView) findViewById(R.id.profileName);
                profileName.setText(sharedPref.getString(getString(R.string.person_name), "no name available"));
                profilePhoto = (ImageView) findViewById(R.id.profileImage);
                Picasso.with(getApplicationContext()).load(sharedPref.getString(getString(R.string.person_photo), "http://square.github.io/picasso/static/sample.png")).into(profilePhoto);
                // profilePhoto.setImageURI(Uri.parse(sharedPref.getString(getString(R.string.person_photo), "no photo available")));
                return true;
            /*case R.id.my_jobs:
                mDrawerLayout.openDrawer(GravityCompat.START);
                profileName = (TextView) findViewById(R.id.profileName);
                profileName.setText(sharedPref.getString(getString(R.string.person_name), "no name available"));
                profilePhoto = (ImageView) findViewById(R.id.profileImage);
                Picasso.with(getApplicationContext()).load(sharedPref.getString(getString(R.string.person_photo), "http://square.github.io/picasso/static/sample.png")).into(profilePhoto);
                // profilePhoto.setImageURI(Uri.parse(sharedPref.getString(getString(R.string.person_photo), "no photo available")));
                return true;*/
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            case R.id.action_signout:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("signout", true);
                editor.commit();
                startActivity(new Intent(this, SignInActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);

                        if (menuItem.getTitle() == getString(R.string.job_posting)) {
                            startActivity(new Intent(getApplicationContext(), JobPostingListActivity.class));
                        }
                        if (menuItem.getTitle() == getString(R.string.my_jobs)) {
                            startActivity(new Intent(getApplicationContext(), MyJobsActivity.class));
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (BuildConfig.DEBUG) {
            Timber.v("Loader<Cursor> onCreateLoader(int id, Bundle args)");
        }

        // start by flushing the existing Content DB
        // getContentResolver().delete(JobPostContract.JobPostList.CONTENT_URI, null, null);
        Timber.v("getContentResolver().delete(JobPostContract.JobPostList.CONTENT_URI, null, null);");

        return new CursorLoader(this,
                JobPostContract.JobPostList.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (BuildConfig.DEBUG) {
            Timber.v("onLoadFinished(Loader<Cursor> loader, Cursor data)");
        }

        mJobPost = Utility.populateJobPostArray(loader, data);

        if (data.getCount() == 0) {
            Timber.v("mRecyclerView.setVisibility(View.GONE)");
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility((View.VISIBLE));
            Timber.v("emptyView.setVisibility(View.GONE)");
        }
        mJobPostingListAdapter = new JobPostingListAdapter(mJobPost);
        mRecyclerView.setAdapter(mJobPostingListAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (BuildConfig.DEBUG) {
            Timber.v("onLoaderReset(Loader<Cursor> loader)");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // see Android RuntimePermissions Sample at https://github.com/googlesamples/android-RuntimePermissions
        Timber.v("onConnected(@Nullable Bundle bundle)");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            Toast.makeText(this, "permissions not available", Toast.LENGTH_LONG).show();
        } else {
            // permission has been granted, continue as usual
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Timber.v("Latitude: " + String.valueOf(mLastLocation.getLatitude()));
                Timber.v("Longtitude: " + String.valueOf(mLastLocation.getLongitude()));
                String location =
                        "Latitude: "
                                + String.valueOf(mLastLocation.getLatitude())
                                + " Longitude: "
                                + String.valueOf(mLastLocation.getLongitude());
                // Toast.makeText(this, location, Toast.LENGTH_LONG).show();
                editor = sharedPref.edit();
                editor.putFloat(getString(R.string.person_latitude), (float) mLastLocation.getLatitude());
                editor.putFloat(getString(R.string.person_longitude), (float) mLastLocation.getLongitude());
                editor.commit();
            } else {
                Toast.makeText(this, "no location available", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.v("onConnectionSuspended(int i)");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Timber.v("onConnectionFailed(ConnectionResult result)");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;

                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            // showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }
}
