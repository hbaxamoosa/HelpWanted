package com.baxamoosa.helpwanted.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.adapter.JobPostingListAdapter;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/30/16.
 */
public class MyJobFavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public JobPost[] mJobPost;
    private RecyclerView.Adapter mJobPostingListAdapter;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate(Bundle savedInstanceState)");

        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        getActivity().getSupportLoaderManager().initLoader(Utility.FAVORITE_JOBPOSTS, null, MyJobFavoriteFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Timber.v("onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        View rootView = inflater.inflate(R.layout.activity_content_my_jobs_drawer, container, false);

        emptyView = (TextView) rootView.findViewById(R.id.recyclerview_empty);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mJobPostingListAdapter = new JobPostingListAdapter(mJobPost);
        try {
            mRecyclerView.setAdapter(mJobPostingListAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (BuildConfig.DEBUG) {
            Timber.v("Loader<Cursor> onCreateLoader(int id, Bundle args)");
        }

        return new CursorLoader(getActivity(),
                JobPostContract.FavoriteList.CONTENT_URI,
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

        /*if (data.getCount() == 0) {
            Timber.v("mRecyclerView.setVisibility(View.GONE)");
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility((View.VISIBLE));
            Timber.v("emptyView.setVisibility(View.GONE)");
        }*/

        if (data.getCount() != 0) {
            Cursor mCursor = data;
            mCursor.moveToFirst();
            DatabaseUtils.dumpCursor(data);

            Timber.v("mCursor.getCount(): " + mCursor.getCount());

            // Create JobPost objects array
            JobPost[] jobPosts = new JobPost[mCursor.getCount()];
            for (int i = 0; i < mCursor.getCount(); i++) {
                jobPosts[i] = new JobPost();
                jobPosts[i]._id = mCursor.getString(Utility.COL_ID);
                Timber.v("jobPosts[i].id: " + jobPosts[i]._id);
                jobPosts[i].businessId = mCursor.getString(Utility.COL_BUSINESS_ID);
                jobPosts[i].businessName = mCursor.getString(Utility.COL_BUSINESS_NAME);
                jobPosts[i].businessAddress = mCursor.getString(Utility.COL_BUSINESS_ADDRESS);
                jobPosts[i].businessPhone = mCursor.getString(Utility.COL_BUSINESS_PHONE);
                jobPosts[i].businessWebsite = mCursor.getString(Utility.COL_BUSINESS_WEBSITE);
                jobPosts[i].businessLatitude = mCursor.getDouble(Utility.COL_BUSINESS_LATITUDE);
                jobPosts[i].businessLongitude = mCursor.getDouble(Utility.COL_BUSINESS_LONGITUDE);
                jobPosts[i].wageRate = mCursor.getInt(Utility.COL_WAGERATE);
                jobPosts[i].date = mCursor.getLong(Utility.COL_POSTDATE);
                jobPosts[i].user = mCursor.getString(Utility.COL_OWNER);

                mCursor.moveToNext();
            }

            mJobPostingListAdapter = new JobPostingListAdapter(mJobPost);
            try {
                mRecyclerView.setAdapter(mJobPostingListAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Timber.v("nothing returned");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (BuildConfig.DEBUG) {
            Timber.v("onLoaderReset(Loader<Cursor> loader)");
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * Job Post Callback for when an item has been selected.
         */
        void onItemSelected(int position);
    }
}