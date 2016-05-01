package com.baxamoosa.helpwanted.fragment;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.utility.Utility;
import com.baxamoosa.helpwanted.viewholder.JobPostHolder;
import com.firebase.ui.FirebaseRecyclerAdapter;

import timber.log.Timber;

/**
 * A job post fragment representing a section of the app that displays Active job posts, for the signed in user.
 */
public class MyJobActiveFragment extends Fragment {

    // public static final String ARG_OBJECT = "jobpost";
    private FirebaseRecyclerAdapter<JobPost, JobPostHolder> mRecycleViewAdapter;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate(Bundle savedInstanceState)");

        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mRecycleViewAdapter = new FirebaseRecyclerAdapter<JobPost, JobPostHolder>(JobPost.class, R.layout.cardview_jobpost, JobPostHolder.class, Utility.mRef) {
            @Override
            protected void populateViewHolder(JobPostHolder jobPostHolder, JobPost jobPost, int i) {
                Timber.v("populateViewHolder(JobPostHolder jobPostHolder, JobPost jobPost, int i)");

                /*Timber.v("jobPost.getUser(): " + jobPost.getUser());
                Timber.v("sharedPref.getString(getString(R.string.person_email: " + sharedPref.getString(getString(R.string.person_email), "a"));*/

                String userFromJobPost = jobPost.getUser();
                String userFromSharedPrefs = sharedPref.getString(getString(R.string.person_email), "unknown");

                if (userFromJobPost.equals(userFromSharedPrefs) && Utility.isValid(jobPost.getDate())) {
                    JobPostHolder.mId.setText(jobPost.getbusinessName());
                    jobPostHolder.mContent.setText(jobPost.getbusinessAddress());
                } else {
                    jobPostHolder.mCardView.setVisibility(View.GONE);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Timber.v("onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        View rootView = inflater.inflate(R.layout.activity_content_my_jobs_drawer, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        return rootView;
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