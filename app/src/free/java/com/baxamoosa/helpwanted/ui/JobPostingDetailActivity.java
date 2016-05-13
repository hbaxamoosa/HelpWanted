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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.ToastAdListener;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.fragment.JobPostingDetailFragment;
import com.baxamoosa.helpwanted.utility.Utility;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 5/12/16.
 */
public class JobPostingDetailActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;
    private SharedPreferences sharedPref;
    private boolean isFavorite;
    private Bundle intentExtras;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_jobposting_detail);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final ContentResolver resolver = getContentResolver();

        intentExtras = getIntent().getExtras();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_favorite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite == true) {
                    isFavorite = false;
                    fab.setImageResource(R.drawable.ic_star_border_black_24dp);
                    String deleteSelection = JobPostContract.FavoriteList.COLUMN_ID + "=?";
                    String[] deleteSelectionArgs = {intentExtras.getString(getString(R.string._id))};
                    resolver.delete(JobPostContract.FavoriteList.CONTENT_URI, deleteSelection, deleteSelectionArgs);

                    // notify user
                    Snackbar.make(view, getString(R.string.remove_favorite), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else { // isFavorite is false
                    isFavorite = true;
                    fab.setImageResource(R.drawable.ic_star_black_24dp);
                    ContentValues[] favoriteArr = new ContentValues[1];
                    favoriteArr[0] = new ContentValues();
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

        Cursor favoriteCursor = resolver.query(JobPostContract.FavoriteList.CONTENT_URI, Utility.JOBPOST_COLUMNS, selection, selectionArgs, null);

        if (favoriteCursor.getCount() == 0) { // cursor returned 0, therefore is not a favorite
            isFavorite = false;
            fab.setImageResource(R.drawable.ic_star_border_black_24dp);

        } else { // cursor returns data, then job post is a favorite
            isFavorite = true;
            fab.setImageResource(R.drawable.ic_star_black_24dp);
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
            // Create the detail fragment and add it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(JobPostingDetailFragment.ARG_ITEM_ID, getIntent().getExtras().getInt(JobPostingDetailFragment.ARG_ITEM_ID));
            arguments.putString(getString(R.string._id), getIntent().getExtras().getString(getString(R.string._id)));

            JobPostingDetailFragment fragment = new JobPostingDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete_favorite);
        menuItemDelete.setVisible(checkIfOwner());

        MenuItem menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemEdit.setVisible(checkIfOwner());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_favorite:
                Toast.makeText(this, "Job Post deleted", Toast.LENGTH_SHORT).show();
                deleteJobPost();
                return true;
            case R.id.action_edit:
                Intent mIntent = new Intent(this, AddEditJobActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.editJob), getString(R.string.editJob));
                mBundle.putString(getString(R.string._id), intentExtras.getString(getString(R.string._id)));
                mBundle.putString(getString(R.string.business_id), intentExtras.getString(getString(R.string.business_id)));
                mBundle.putString(getString(R.string.business_name), intentExtras.getString(getString(R.string.business_name)));
                mBundle.putString(getString(R.string.business_phone), intentExtras.getString(getString(R.string.business_phone)));
                mBundle.putString(getString(R.string.business_address), intentExtras.getString(getString(R.string.business_address)));
                mBundle.putString(getString(R.string.business_website), intentExtras.getString(getString(R.string.business_website)));
                mBundle.putDouble(getString(R.string.business_latitude), intentExtras.getDouble(getString(R.string.business_latitude)));
                mBundle.putDouble(getString(R.string.business_longitude), intentExtras.getDouble(getString(R.string.business_longitude)));

                mIntent.putExtras(mBundle);
                startActivity(mIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteJobPost() {

        // delete job post from Firebase (cloud). See http://www.sitepoint.com/creating-a-cloud-backend-for-your-android-app-using-firebase/
        Utility.mRef
                .orderByChild("_id")
                .equalTo(intentExtras.getString(getString(R.string._id)))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            firstChild.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Timber.v(String.valueOf(firebaseError));
                    }
                });

        String selection = JobPostContract.FavoriteList.COLUMN_BUSINESSID + "=?";
        String[] selectionArgs = {intentExtras.getString(getString(R.string.business_id))};

        ContentResolver resolverJobPosts = getContentResolver();
        // delete job post from jobpost table (local)
        resolverJobPosts.delete(JobPostContract.JobPostList.CONTENT_URI, selection, selectionArgs);

        ContentResolver resolverFavorites = getContentResolver();
        // delete job post from favorites table (local)
        resolverFavorites.delete(JobPostContract.FavoriteList.CONTENT_URI, selection, selectionArgs);
        /*Timber.v("Job post for business ID " + intentExtras.getString(getString(R.string.business_id)) + " deleted");*/

        startActivity(new Intent(this, MainActivity.class));
    }

    public boolean checkIfOwner() {
        boolean isOwner;
        String selection = JobPostContract.JobPostList.COLUMN_OWNER + "=? AND " + JobPostContract.JobPostList.COLUMN_BUSINESSID + "=?";
        String[] selectionArgs = {sharedPref.getString(getString(R.string.person_email), "no@one.com"), intentExtras.getString(getString(R.string.business_id))};

        ContentResolver resolver = getContentResolver();

        Cursor ownerCursor = resolver.query(JobPostContract.JobPostList.CONTENT_URI, Utility.JOBPOST_COLUMNS, selection, selectionArgs, null);

        isOwner = ownerCursor.getCount() > 0;
        return isOwner;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
