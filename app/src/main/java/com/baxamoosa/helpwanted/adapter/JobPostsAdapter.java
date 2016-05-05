package com.baxamoosa.helpwanted.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.model.JobPost;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/25/16.
 */
public class JobPostsAdapter extends RecyclerView.Adapter<JobPostsAdapter.ViewHolder> {

    private final JobPost[] mJobPost;

    public JobPostsAdapter(JobPost[] items) {
        mJobPost = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_jobpost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mJobPost[position];
        holder.mOverlayTextView.setText(mJobPost[position].businessName);
        holder.mIdView.setText(mJobPost[position].businessId);
        holder.mContentView.setText(mJobPost[position].businessAddress);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.v("holder.mView.setOnClickListener(new View.OnClickListener()");
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return mJobPost.length;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * MoviesActivity Callback for when an item has been selected.
         */
        void onItemSelected(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mOverlayTextView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final CardView mCardView;
        public JobPost mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.icon);
            mOverlayTextView = (TextView) view.findViewById(R.id.overlaytext);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mCardView = (CardView) view.findViewById(R.id.cardview);
            mCardView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            Timber.v("onClick(View v) inside JobPostsAdapter");
        }
    }
}