package com.baxamoosa.helpwanted.fragment;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.model.JobPost;

import timber.log.Timber;

/**
 * A job post fragment representing a section of the app.
 */
public class MyJobFragment extends Fragment {

    public static final String ARG_OBJECT = "jobpost";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.cardview_jobpost, container, false);
        Bundle args = getArguments();
        Parcelable[] allParcelables = args.getParcelableArray(ARG_OBJECT);

        Timber.v("allParcelables array size is " + allParcelables.length);

        JobPost[] mJobPosts = new JobPost[allParcelables.length];
        Timber.v("mJobPosts array size is " + mJobPosts.length);
        for (int i = 0; i < mJobPosts.length; i++) {
            mJobPosts[i] = (JobPost) allParcelables[i];
        }
        // Timber.v("mJobPosts[0].getName(): " + mJobPosts[0].getName());
        Timber.v("mJobPosts[1].getName(): " + mJobPosts[1].getName());

        TextView businessName = (TextView) rootView.findViewById(R.id.id);
        businessName.setText(mJobPosts[1].getName());

        TextView businessAddress = (TextView) rootView.findViewById(R.id.content);
        businessAddress.setText(mJobPosts[1].getAddress());

        return rootView;
    }
}