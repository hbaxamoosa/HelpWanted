package com.baxamoosa.helpwanted.fragment;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.application.HelpWantedApplication;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.viewholder.JobPostHolder;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import timber.log.Timber;

/**
 * A job post fragment representing a section of the app.
 */
public class MyJobFragment extends Fragment {

    public static final String ARG_OBJECT = "jobpost";
    private Firebase mRef;
    private FirebaseRecyclerAdapter<JobPost, JobPostHolder> mRecycleViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate(Bundle savedInstanceState)");
        mRef = new Firebase(HelpWantedApplication.getAppContext().getResources().getString(R.string.firebase_connection_string));

        mRecycleViewAdapter = new FirebaseRecyclerAdapter<JobPost, JobPostHolder>(JobPost.class, R.layout.cardview_jobpost, JobPostHolder.class, mRef) {
            @Override
            protected void populateViewHolder(JobPostHolder jobPostHolder, JobPost jobPost, int i) {
                Timber.v("populateViewHolder(JobPostHolder jobPostHolder, JobPost jobPost, int i)");
                JobPostHolder.id.setText(jobPost.getName());
                jobPostHolder.content.setText(jobPost.getAddress());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Timber.v("onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        View rootView = inflater.inflate(R.layout.activity_content_my_jobs_drawer, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(false);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        return rootView;
    }


}