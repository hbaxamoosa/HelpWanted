package com.baxamoosa.helpwanted.fragment;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */

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

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.adapter.JobPostingListAdapter;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

/**
 * A job post fragment representing a section of the app that displays Active job posts, for the signed in user.
 */
public class MyJobActiveFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public JobPost[] mJobPost;
    // public static final String ARG_OBJECT = "jobpost";
    // private FirebaseRecyclerAdapter<JobPost, JobPostHolder> mRecycleViewAdapter;
    private RecyclerView.Adapter mJobPostingListAdapter;
    private RecyclerView mRecyclerView;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate(Bundle savedInstanceState)");

        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        getActivity().getSupportLoaderManager().initLoader(Utility.ACTIVE_JOBPOSTS, null, MyJobActiveFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Timber.v("onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        View rootView = inflater.inflate(R.layout.activity_content_my_jobs_drawer, container, false);

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

        Calendar calendar = Calendar.getInstance();
        GregorianCalendar validDate = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - Utility.LENGTH_OF_VALIDITY));
        Long validTime = validDate.getTimeInMillis();
        Timber.v("validTime: " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(validTime)));

        Timber.v("validTime: " + validTime);
        Timber.v("validTime String: " + validTime);

        String selection = JobPostContract.JobPostList.COLUMN_POSTDATE + "<?";
        String[] selectionArgs = {validTime.toString()};

        Timber.v("selection: " + selection);
        Timber.v("selectionArgs: " + selectionArgs[0]);

        return new CursorLoader(getActivity(),
                JobPostContract.JobPostList.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (BuildConfig.DEBUG) {
            Timber.v("onLoadFinished(Loader<Cursor> loader, Cursor data)");
        }

        if (data.getCount() != 0) {
            mJobPost = Utility.populateJobPostArray(loader, data);

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