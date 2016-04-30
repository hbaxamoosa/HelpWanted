package com.baxamoosa.helpwanted.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
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
    private Bundle arguments;
    private CollapsingToolbarLayout appBarLayout;
    private int position;

    private TextView mTextView1;
    private TextView mTextView2;

    /**
     * The dummy content this fragment is presenting.
     */
    // private DummyContent.DummyItem mItem;

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
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        arguments = getArguments();

        if (arguments != null) {
            Timber.v("arguments != null");
            position = arguments.getInt(ARG_ITEM_ID);
            Timber.v("position: " + position);
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
        View rootView = inflater.inflate(R.layout.jobposting_detail, container, false);

        /*ContentResolver resolver = getActivity().getContentResolver();
        // String[] projection = new String[]{JobPostContract.JobPostList.JOBPOST_COLUMNS};
        // Timber.v(TAG + " projection is " + projection);
        Cursor cursor = resolver.query(JobPostContract.JobPostList.CONTENT_URI,
                projection,
                null,
                null,
                null);
        // Timber.v(TAG + " cursor.getCount() is " + cursor.getCount());
        if (cursor.moveToFirst()){
            do {
                String movieID = cursor.getString(0);
                // Timber.v(TAG + " movieID is " + movieID);
                if (movieID.equals(id)){
                    // Timber.v(TAG + " (movieID == id)");
                    favoriteMovie = true;
                }
            } while (cursor.moveToNext());
        }*/

        mTextView1 = (TextView) rootView.findViewById(R.id.jobposting_detail);
        mTextView2 = (TextView) rootView.findViewById(R.id.jobposting_detail2);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.v("onCreateLoader(int id, Bundle args)");
        return new CursorLoader(
                getActivity(),
                JobPostContract.JobPostList.CONTENT_URI,
                Utility.JOBPOST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.v("onLoadFinished(Loader<Cursor> loader, Cursor data)");
        if (data != null && data.moveToFirst()) {
            Timber.v("data != null && data.moveToFirst()");
            data.move(position);
            mTextView1.setText(data.getString(Utility.COL_BUSINESS_NAME));
            mTextView2.setText(data.getString(Utility.COL_BUSINESS_ADDRESS));
            if (appBarLayout != null) {
                appBarLayout.setTitle(data.getString(Utility.COL_BUSINESS_NAME));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.v("onLoaderReset(Loader<Cursor> loader)");
    }
}
