package com.baxamoosa.helpwanted.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import com.baxamoosa.helpwanted.adapter.JobPostsAdapter;
import com.baxamoosa.helpwanted.application.HelpWantedApplication;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

/**
 * An activity representing a list of JobPostings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link JobPostingDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class JobPostingListActivity extends AppCompatActivity implements /*JobPostingListAdapter.Callback,*/ LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    public boolean mTwoPane;  // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    public boolean firstLoad;  // Whether or not the activity is in two-pane mode and whether this is the first load or not.
    public JobPost[] mJobPost;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences sharedPref;
    private TextView profileName;
    private ImageView profilePhoto;
    private RecyclerView.Adapter mJobPostingListAdapter;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private ValueEventListener jobPostsListener;
    // private Cursor mCursor;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        firstLoad = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set a flag to track whether this is the first time the JobPostingsAdapter is being loaded into the app.
        firstLoad = true;
        mTwoPane = false;  // // TODO: 5/5/16 remove later

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        boolean isConnected = Utility.isNetworkAvailable(HelpWantedApplication.getAppContext());

        setContentView(R.layout.activity_jobposting_list_drawer);

        emptyView = (TextView) findViewById(R.id.recyclerview_empty);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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

        // HelpWantedSyncAdapter.syncImmediately(this);

        getSupportLoaderManager().initLoader(Utility.ALL_JOBPOSTS, null, JobPostingListActivity.this);

        // grabJobPostsFromFireBase();

        mRecyclerView = (RecyclerView) findViewById(R.id.jobposting_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // mRecyclerView.setAdapter(mJobPostingListAdapter);
        Timber.v("setupRecyclerView() is complete");

        if (findViewById(R.id.jobposting_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (savedInstanceState != null) {
            // TODO: 4/14/16 do something
        }

        if (savedInstanceState == null) {
            if (isConnected) {
                // TODO: 4/14/16 do something here
            }
        } else {
            if (BuildConfig.DEBUG) {
                Timber.v("(inside else) isConnected: " + isConnected);
            }
            Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
        }
    }

    private void populateTestData() {

        Firebase mJobPost = Utility.mRef.child("jobpost");

        for (int i = 0; i < 10; i++) {
            String _id = "ChIJkwAWec4yW4YRBWSKMPYdIe0" + i;
            String businessID = "ChIJkwAWec4yW4YRBWSKMPYdIe0" + i + i;
            Calendar calendar = Calendar.getInstance();
            GregorianCalendar postDate = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - i));
            Long postTime = postDate.getTimeInMillis();
            Timber.v("postDate: " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(postTime)));

            JobPost a = new JobPost(_id,
                    businessID,
                    "Starbucks" + i,
                    "13450 Research Blvd #238, Austin, TX 78750, United States",
                    "+1 512-401-6253",
                    "http://www.starbucks.com/" + i,
                    -97.79085309999999,
                    30.4472483,
                    i,
                    postTime,
                    "hbaxamoosa@gmail.com");
            Utility.mRef.push().setValue(a);
            Timber.v("successfully posted " + i + " to Firebase");
            Timber.v("postDate: " + postDate);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) {
            Timber.v("onStart()");
        }
        // populateTestData();  // test data
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) {
            Timber.v("onStop()");
        }

        // remove Firebase event listener
        // Utility.mRef.removeEventListener(jobPostsListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            Timber.v("onResume()");
        }
        // HelpWantedSyncAdapter.syncImmediately(this);
    }

    private void grabJobPostsFromFireBase() {

        // Attach an listener to read the data at our posts reference

        jobPostsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Timber.v("There are " + snapshot.getChildrenCount() + " job posts");
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
                        getContentResolver().insert(JobPostContract.JobPostList.CONTENT_URI, jobPostArr);

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
                        Toast.makeText(getApplicationContext(), "menuItem: " + menuItem.getTitle(), Toast.LENGTH_LONG).show();

                        if (menuItem.getTitle() == getString(R.string.job_posting)) {
                            startActivity(new Intent(getApplicationContext(), JobPostingListActivity.class));
                            Timber.v("menuItem.getTitle() == getString(R.string.job_posting))");
                        }
                        if (menuItem.getTitle() == getString(R.string.my_jobs)) {
                            startActivity(new Intent(getApplicationContext(), MyJobsActivity.class));
                            Timber.v("menuItem.getTitle() == getString(R.string.my_jobs)");
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    /**
     * Callback method from {@link JobPostsAdapter}
     * indicating that the item with the given ID was selected.
     */
    public void JobPostingListAdapter(int position) {
        /*Timber.v("onItemSelected(int position, JobPost[] mJobPost) position is " + position);
        Timber.v("onItemSelected(int position, JobPost[] mJobPost) mJobPost.length is " + mJobPost.length);
        if (mTwoPane) {
            Timber.v("mTwoPane: " + true);
            Bundle arguments = new Bundle();
            arguments.putString(getString(R.string._id), mJobPost[position].get_id());
            arguments.putString(getString(R.string.business_id), mJobPost[position].getBusinessId());
            arguments.putString(getString(R.string.business_name), mJobPost[position].getbusinessName());
            arguments.putString(getString(R.string.business_address), mJobPost[position].getbusinessAddress());
            arguments.putString(getString(R.string.business_phone), mJobPost[position].getbusinessPhone());
            arguments.putString(getString(R.string.business_website), mJobPost[position].getbusinessWebsite());
            arguments.putDouble(getString(R.string.business_latitude), mJobPost[position].getbusinessLatitude());
            arguments.putDouble(getString(R.string.business_longitude), mJobPost[position].getbusinessLongitude());
            arguments.putInt(getString(R.string.business_wage_rate), mJobPost[position].getWageRate());
            arguments.putDouble(getString(R.string.business_post_date), mJobPost[position].getDate());
            arguments.putString(getString(R.string.business_owner), mJobPost[position].getUser());

            JobPostingDetailFragment fragment = new JobPostingDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.jobposting_detail_container, fragment)
                    .commit();
        } else {
            Timber.v("mTwoPane: " + false);
            Intent intent = new Intent(this, JobPostingDetailActivity.class);
            // intent.putExtra(getString(R.string._id), mJobPost[position].get_id());
            intent.putExtra("_id", mJobPost[position].get_id());
            Timber.v("mJobPost[position].get_id(): " +  mJobPost[position].get_id());
            // intent.putExtra(getString(R.string.business_id), mJobPost[position].getBusinessId());
            intent.putExtra("business_id", mJobPost[position].getBusinessId());
            Timber.v("mJobPost[position].getBusinessId(): " +  mJobPost[position].getBusinessId());
            intent.putExtra(getString(R.string.business_name), mJobPost[position].getbusinessName());
            Timber.v("mJobPost[position].getBusinessName(): " +  mJobPost[position].getbusinessName());
            intent.putExtra(getString(R.string.business_address), mJobPost[position].getbusinessAddress());
            intent.putExtra(getString(R.string.business_phone), mJobPost[position].getbusinessPhone());
            intent.putExtra(getString(R.string.business_website), mJobPost[position].getbusinessWebsite());
            intent.putExtra(getString(R.string.business_latitude), mJobPost[position].getbusinessLatitude()); // double
            intent.putExtra(getString(R.string.business_longitude), mJobPost[position].getbusinessLongitude()); // double
            intent.putExtra(getString(R.string.business_wage_rate), mJobPost[position].getWageRate()); // int
            intent.putExtra(getString(R.string.business_post_date), mJobPost[position].getDate()); // double
            intent.putExtra(getString(R.string.business_owner), mJobPost[position].getUser());
            intent.putExtra(JobPostingDetailFragment.ARG_ITEM_ID, position);
            startActivity(intent);
        }*/
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
}
