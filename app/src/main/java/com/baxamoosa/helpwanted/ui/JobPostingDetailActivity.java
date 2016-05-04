package com.baxamoosa.helpwanted.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.fragment.JobPostingDetailFragment;
import com.baxamoosa.helpwanted.utility.Utility;

import timber.log.Timber;

/**
 * An activity representing a single JobPosting detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link JobPostingListActivity}.
 */
public class JobPostingDetailActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;
    private SharedPreferences sharedPref;
    private String[] businessID;
    private boolean isFavorite;
    private Bundle intentExtras;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        setContentView(R.layout.activity_jobposting_detail);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            Timber.v("arguments != null");
            position = intentExtras.getInt(JobPostingDetailFragment.ARG_ITEM_ID);
            /*businessID = new String[]{ intentExtras.getString(getString(R.string.business_id)) };
            Timber.v("position: " + position);
            Timber.v("businessID: " + businessID[0]);
            Timber.v("_id: " + intentExtras.getString(getString(R.string._id)));
            Timber.v("businessName: " + intentExtras.getString(getString(R.string.business_name)));*/
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_favorite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (isFavorite == true) {
                    isFavorite = false;
                    fab.setImageResource(R.drawable.ic_star_border_black_24dp);
                    // TODO: 5/4/16 use the Cursor Resolver to delete the job post from the Favorite table
                } else {
                    isFavorite = true;
                    fab.setImageResource(R.drawable.ic_star_black_24dp);
                    // TODO: 5/4/16 use the Cursor Resolver to insert the job post from the Favorite table
                }
            }
        });

        String selection = JobPostContract.JobPostList.COLUMN_POSTDATE + "=?";
        String[] selectionArgs = {sharedPref.getString(getString(R.string.person_email), "no name available")};

        Timber.v("selection: " + selection);
        Timber.v("selectionArgs: " + selectionArgs[0].toString());

        final ContentResolver resolver = getContentResolver();
        Cursor favoriteCursor = resolver.query(JobPostContract.FavoriteList.CONTENT_URI, Utility.JOBPOST_COLUMNS, selection, selectionArgs, null);
        if (favoriteCursor == null) { // cursor returned NULL, therefore is not a favorite
            Timber.v("favoriteCursor == null");
            isFavorite = false;
            fab.setImageResource(R.drawable.ic_star_border_black_24dp);
            // fab.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
        } else { // is cursor is not NULL, then job post is a favorite
            Timber.v("isFavorite != null");
            isFavorite = true;
            fab.setImageResource(R.drawable.ic_star_black_24dp);
            // fab.setBackgroundResource(R.drawable.ic_star_black_24dp);
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            Timber.v("getIntent().getExtras().getInt(JobPostingDetailFragment.ARG_ITEM_ID): " + getIntent().getExtras().getInt(JobPostingDetailFragment.ARG_ITEM_ID));
            arguments.putInt(JobPostingDetailFragment.ARG_ITEM_ID, getIntent().getExtras().getInt(JobPostingDetailFragment.ARG_ITEM_ID));
            arguments.putString(getString(R.string._id), getIntent().getExtras().getString(getString(R.string._id)));



            /*arguments.putString(getString(R.string.business_id), mJobPost[position].getBusinessId());
            arguments.putString(getString(R.string.business_name), mJobPost[position].getbusinessName());
            arguments.putString(getString(R.string.business_address), mJobPost[position].getbusinessAddress());
            arguments.putString(getString(R.string.business_phone), mJobPost[position].getbusinessPhone());
            arguments.putString(getString(R.string.business_website), mJobPost[position].getbusinessWebsite());
            arguments.putDouble(getString(R.string.business_latitude), mJobPost[position].getbusinessLatitude());
            arguments.putDouble(getString(R.string.business_longitude), mJobPost[position].getbusinessLongitude());
            arguments.putInt(getString(R.string.business_wage_rate), mJobPost[position].getWageRate());
            arguments.putDouble(getString(R.string.business_post_date), mJobPost[position].getDate());
            arguments.putString(getString(R.string.business_owner), mJobPost[position].getUser());*/

            JobPostingDetailFragment fragment = new JobPostingDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.jobposting_detail_container, fragment)
                    .commit();
        }
    }

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        String email = (sharedPref.getString(getString(R.string.person_email), "no email"));
        String userEmail = "hbaxamoosa@gmail.com";

        if (email.equals(userEmail)) {
            inflater.inflate(R.menu.menu_detail_signedin, menu);
        } else {
            inflater.inflate(R.menu.menu_detail, menu);
        }
        finishCreatingMenu(menu);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "something");
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, JobPostingListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
        // see https://thedevelopersinfo.wordpress.com/2009/10/20/dynamically-change-options-menu-items-in-android/
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
