package com.baxamoosa.helpwanted.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    public JobPost[] mFavorites;
    private RecyclerView.Adapter mFavoriteAdapter;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Timber.v("onCreate(Bundle savedInstanceState)");*/

        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        getActivity().getSupportLoaderManager().initLoader(Utility.FAVORITE_JOBPOSTS, null, MyJobFavoriteFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*Timber.v("onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");*/

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_jobs, container, false);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);

        try {
            mRecyclerView.setAdapter(mFavoriteAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mRecyclerView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*if (BuildConfig.DEBUG) {
            Timber.v("Loader<Cursor> onCreateLoader(int id, Bundle args)");
        }*/

        return new CursorLoader(getActivity(),
                JobPostContract.FavoriteList.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /*if (BuildConfig.DEBUG) {
            Timber.v("onLoadFinished(Loader<Cursor> loader, Cursor data)");
        }*/

        if (data.getCount() != 0) {
            mFavorites = Utility.populateJobPostArray(loader, data);

            mFavoriteAdapter = new JobPostingListAdapter(mFavorites);
            data.close();
            try {
                mRecyclerView.setAdapter(mFavoriteAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Timber.v("nothing returned");
            // nothing returned, so close cursor
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*if (BuildConfig.DEBUG) {
            Timber.v("onLoaderReset(Loader<Cursor> loader)");
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(Utility.FAVORITE_JOBPOSTS, null, MyJobFavoriteFragment.this);
        if (mFavoriteAdapter != null) {
            mFavoriteAdapter.notifyDataSetChanged();
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