package com.baxamoosa.helpwanted.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.ui.JobPostingDetailActivity;
import com.baxamoosa.helpwanted.ui.JobPostingListActivity;
import com.baxamoosa.helpwanted.utility.Utility;

import timber.log.Timber;

/**
 * A fragment representing a single JobPosting detail screen.
 * This fragment is either contained in a {@link JobPostingListActivity}
 * in two-pane mode (on tablets) or a {@link JobPostingDetailActivity}
 * on handsets.
 */
public class JobPostingDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */

    public static final String ARG_ITEM_ID = "item_id";
    private SharedPreferences sharedPref;
    private Bundle arguments;
    private CollapsingToolbarLayout appBarLayout;
    private int position;

    private TextView mTextView1;
    private TextView mTextView2;
    private String shareContent;

    private String _id;
    private String businessId;
    private String businessName;
    private String businessAddress;
    private String businessPhone;
    private String businessWebsite;
    private double businessLatitude;
    private double businessLongitude;
    private int wageRate;
    private double postDate;
    private String user;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JobPostingDetailFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.v("onActivityCreated(@Nullable Bundle savedInstanceState)");
        }
        getActivity().getSupportLoaderManager().initLoader(Utility.ALL_JOBPOSTS, null, this);
        // getActivity().getSupportLoaderManager().initLoader(Utility.FAVORITE_JOBPOSTS, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        setHasOptionsMenu(true);

        arguments = getArguments();
        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (arguments != null) {
            Timber.v("arguments != null");
            position = arguments.getInt(ARG_ITEM_ID);
            // businessID = new String[]{ arguments.getString(getString(R.string.business_id)) };
            Timber.v("position: " + position);
            // Timber.v("businessID: " + businessID[0]);
        }
        Activity activity = this.getActivity();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (BuildConfig.DEBUG) {
            Timber.v("onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
        }
        View rootView = inflater.inflate(R.layout.jobposting_detail, container, false); // // TODO: 5/3/16 bug here, with the layout

        mTextView1 = (TextView) rootView.findViewById(R.id.jobposting_detail);
        mTextView2 = (TextView) rootView.findViewById(R.id.jobposting_detail2);
        // final FloatingActionButton favorite = (FloatingActionButton) rootView.findViewById(R.id.btn_favorite);

        /*favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite == true){ // job post is a favorite, but now switching it
                    isFavorite = false; // switch boolean value
                    favorite.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                    resolver.delete(JobPostContract.FavoriteList.CONTENT_URI,
                            JobPostContract.FavoriteList.COLUMN_BUSINESSID + "=?",
                            businessID);
                } else { // job post is not a favorite
                    isFavorite = true; // switch boolean value
                    favorite.setBackgroundResource(R.drawable.ic_star_border_black_24dp);

                    // use the arguments from the Fragment to insert into the Content Provider
                    ContentValues[] favoriteArr = new ContentValues[1];
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_ID, _id);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSID, businessId);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSNAME, businessName);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSADDRESS, businessAddress);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSPHONE, businessPhone);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSWEBSITE, businessWebsite);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSLATITUDE, businessLatitude);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_BUSINESSLONGITUDE, businessLongitude);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_WAGERATE, wageRate);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_POSTDATE, postDate);
                    favoriteArr[0].put(JobPostContract.FavoriteList.COLUMN_OWNER, user);

                    resolver.bulkInsert(JobPostContract.FavoriteList.CONTENT_URI, favoriteArr);
                }
            }
        });*/

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (getActivity() instanceof JobPostingDetailActivity) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_detail, menu);
            finishCreatingMenu(menu);
        }
    }

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createJobPostShareIntent());
    }

    private Intent createJobPostShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        return shareIntent;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.v("onCreateLoader(int id, Bundle args)");
        Loader<Cursor> mCursor = null;
        switch (id) {
            case Utility.ALL_JOBPOSTS:
                mCursor = new CursorLoader(
                        getActivity(),
                        JobPostContract.JobPostList.CONTENT_URI,
                        Utility.JOBPOST_COLUMNS,
                        null,
                        null,
                        null
                );
                break;
            case Utility.FAVORITE_JOBPOSTS:
                mCursor = new CursorLoader(
                        getActivity(),
                        JobPostContract.FavoriteList.CONTENT_URI,
                        Utility.JOBPOST_COLUMNS,
                        null,
                        null,
                        null
                );
                break;
        }
        return mCursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.v("onLoadFinished(Loader<Cursor> loader, Cursor data)");
        switch (loader.getId()) {
            case Utility.ALL_JOBPOSTS:
                if (data != null && data.moveToFirst()) {
                    Timber.v("data != null && data.moveToFirst()");
                    data.move(position);

                    _id = data.getString(Utility.COL_ID);
                    Timber.v("_id: " + _id);
                    businessId = data.getString(Utility.COL_BUSINESS_ID);
                    Timber.v("businessID: " + businessId);
                    businessName = data.getString(Utility.COL_BUSINESS_NAME);
                    Timber.v("businessName: " + businessName);
                    businessAddress = data.getString(Utility.COL_BUSINESS_ADDRESS);
                    Timber.v("businessAddress: " + businessAddress);
                    businessPhone = data.getString(Utility.COL_BUSINESS_PHONE);
                    Timber.v("businessPhone: " + businessPhone);
                    businessWebsite = data.getString(Utility.COL_BUSINESS_WEBSITE);
                    Timber.v("businessWebsite: " + businessWebsite);
                    businessLatitude = data.getDouble(Utility.COL_BUSINESS_LONGITUDE);
                    Timber.v("businessLatitude: " + businessLatitude);
                    businessLongitude = data.getDouble(Utility.COL_BUSINESS_LATITUDE);
                    Timber.v("businessLongitude: " + businessLongitude);
                    wageRate = data.getInt(Utility.COL_WAGERATE);
                    Timber.v("wageRate: " + wageRate);
                    postDate = data.getDouble(Utility.COL_POSTDATE);
                    Timber.v("postDate: " + postDate);
                    user = data.getString(Utility.COL_OWNER);
                    Timber.v("user: " + user);

                    if (appBarLayout != null) {
                        appBarLayout.setTitle(businessName);
                    }

                    shareContent = businessName + " has a job posting!";
                    mTextView1.setText(businessName);
                    mTextView2.setText(businessAddress);
                }
                break;
            case Utility.FAVORITE_JOBPOSTS:
                if (data != null && data.moveToFirst()) {
                    // something
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.v("onLoaderReset(Loader<Cursor> loader)");
    }
}
