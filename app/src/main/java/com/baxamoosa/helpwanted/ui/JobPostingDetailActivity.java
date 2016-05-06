package com.baxamoosa.helpwanted.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.Toast;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.fragment.JobPostingDetailFragment;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import timber.log.Timber;

/**
 * An activity representing a single JobPosting detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link JobPostingListActivity}.
 */
public class JobPostingDetailActivity extends AppCompatActivity {

    private static final int MENUITEM_SHARE = Menu.FIRST;
    private static final int MENUITEM_DELETE = Menu.FIRST + 1;
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

        final ContentResolver resolver = getContentResolver();

        intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            Timber.v("arguments != null");
            position = intentExtras.getInt(JobPostingDetailFragment.ARG_ITEM_ID);
            businessID = new String[]{intentExtras.getString(getString(R.string.business_id))};
            Timber.v("position: " + position);
            Timber.v("businessID: " + businessID[0].toString());
            Timber.v("_id: " + intentExtras.getString(getString(R.string._id)));
            Timber.v("businessName: " + intentExtras.getString(getString(R.string.business_name)));
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_favorite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite == true) {
                    isFavorite = false;
                    fab.setImageResource(R.drawable.ic_star_border_black_24dp);
                    String deleteSelection = JobPostContract.FavoriteList.COLUMN_ID + "= ?";
                    String[] deleteSelectionArgs = {intentExtras.getString(getString(R.string._id))};
                    resolver.delete(JobPostContract.FavoriteList.CONTENT_URI, deleteSelection, deleteSelectionArgs);

                    // notify user
                    Snackbar.make(view, getString(R.string.remove_favorite), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else { // isFavorite is false
                    isFavorite = true;
                    fab.setImageResource(R.drawable.ic_star_black_24dp);
                    ContentValues[] favoriteArr = new ContentValues[1];
                    favoriteArr[0] = new ContentValues();
                    Timber.v("intentExtras.getString(getString(R.string._id)): " + intentExtras.getString(getString(R.string._id)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_ID, intentExtras.getString(getString(R.string._id)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSID, intentExtras.getString(getString(R.string.business_id)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSNAME, intentExtras.getString(getString(R.string.business_name)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSADDRESS, intentExtras.getString(getString(R.string.business_address)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSPHONE, intentExtras.getString(getString(R.string.business_phone)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSWEBSITE, intentExtras.getString(getString(R.string.business_website)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSLATITUDE, intentExtras.getDouble(getString(R.string.business_latitude)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSLONGITUDE, intentExtras.getDouble(getString(R.string.business_longitude)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_WAGERATE, intentExtras.getInt(getString(R.string.business_wage_rate)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_POSTDATE, intentExtras.getLong(getString(R.string.business_post_date)));
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_OWNER, intentExtras.getString(getString(R.string.business_owner)));

                    resolver.bulkInsert(JobPostContract.FavoriteList.CONTENT_URI, favoriteArr);

                    // notify user
                    Snackbar.make(view, getString(R.string.add_favorite), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        String selection = JobPostContract.FavoriteList.COLUMN_BUSINESSID + "=?";
        String[] selectionArgs = {intentExtras.getString(getString(R.string.business_id))};

        Timber.v("selection: " + selection);
        Timber.v("selectionArgs: " + selectionArgs[0].toString());

        Cursor favoriteCursor = resolver.query(JobPostContract.FavoriteList.CONTENT_URI, Utility.JOBPOST_COLUMNS, selection, selectionArgs, null);

        if (favoriteCursor.getCount() == 0) { // cursor returned 0, therefore is not a favorite
            Timber.v("favoriteCursor == null");
            isFavorite = false;
            fab.setImageResource(R.drawable.ic_star_border_black_24dp);
            // fab.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
        } else { // cursor returns data, then job post is a favorite
            Timber.v("favoriteCursor != null"); // TODO: 5/4/16 check this, because this is always being hit 
            isFavorite = true;
            fab.setImageResource(R.drawable.ic_star_black_24dp);
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
        menuItem.setIntent(createShareIntent());
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

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "something");
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, JobPostingListActivity.class));
                return true;
            case R.id.action_delete_favorite:
                Timber.v("user clicked on delete");
                Toast.makeText(this, "Job Post deleted", Toast.LENGTH_SHORT).show();
                deleteJobPost();
                return true;
            case R.id.action_share:
                Timber.v("user clicked share");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteJobPost() {
        Timber.v("deleteJobPost()");


        // delete job post from Firebase (cloud). See http://www.sitepoint.com/creating-a-cloud-backend-for-your-android-app-using-firebase/
        Utility.mRef
                .orderByChild("_id")
                .equalTo(intentExtras.getString(getString(R.string.business_id)))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            Timber.v("dataSnapshot.hasChildren()");
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            firstChild.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Timber.v("onCancelled(FirebaseError firebaseError)");
                    }
                });

        String selection = JobPostContract.FavoriteList.COLUMN_BUSINESSID + "=?";
        String[] selectionArgs = {intentExtras.getString(getString(R.string.business_id))};

        Timber.v("selection: " + selection);
        Timber.v("selectionArgs: " + selectionArgs[0].toString());

        ContentResolver resolverJobPosts = getContentResolver();
        // delete job post from jobpost table (local)
        resolverJobPosts.delete(JobPostContract.JobPostList.CONTENT_URI, selection, selectionArgs);

        ContentResolver resolverFavorites = getContentResolver();
        // delete job post from favorites table (local)
        resolverFavorites.delete(JobPostContract.FavoriteList.CONTENT_URI, selection, selectionArgs);
        Timber.v("Job post for business ID " + intentExtras.getString(getString(R.string.business_id)) + " deleted");

        startActivity(new Intent(this, JobPostingListActivity.class));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Timber.v("onPrepareOptionsMenu(Menu menu)");
        MenuItem menuItem = menu.findItem(R.id.action_delete_favorite);
        menuItem.setVisible(checkIfOwner());

        /*menu.clear();
        menu.add(0, MENUITEM_DELETE, 0, R.string.action_delete_favorite);*/
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean checkIfOwner() {
        Timber.v("checkIfOwner()");
        boolean isOwner;
        String selection = JobPostContract.FavoriteList.COLUMN_OWNER + "=?";
        String[] selectionArgs = {intentExtras.getString(getString(R.string.business_owner))};

        Timber.v("selection: " + selection);
        Timber.v("selectionArgs: " + selectionArgs[0].toString());

        ContentResolver resolver = getContentResolver();
        Cursor ownerCursor = resolver.query(JobPostContract.FavoriteList.CONTENT_URI, Utility.JOBPOST_COLUMNS, selection, selectionArgs, null);

        if (ownerCursor.getCount() > 0) {
            Timber.v("ownerCursor.getCount() > 0");
            isOwner = false;
        } else {
            Timber.v("ownerCursor.getCount() == 0");
            isOwner = true;
        }
        Timber.v("isOwner: " + isOwner);
        return isOwner;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
