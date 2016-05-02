package com.baxamoosa.helpwanted.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.fragment.JobPostingDetailFragment;
import com.baxamoosa.helpwanted.model.JobPost;
import com.baxamoosa.helpwanted.ui.JobPostingDetailActivity;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/29/16.
 */
public class JobPostingListAdapter extends RecyclerView.Adapter<JobPostingListAdapter.JobPostingListAdapterViewHolder> {

    public JobPost[] mJobPost;

    public JobPostingListAdapter() {
        Timber.v("JobPostingListAdapter()");
    }

    public JobPostingListAdapter(JobPost[] jp) {
        // Timber.v("JobPostingListAdapter(JobPost[] jp)");
        mJobPost = jp;
    }

    @Override
    public JobPostingListAdapter.JobPostingListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.v("onCreateViewHolder(ViewGroup parent, int viewType)");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_jobpost, parent, false);
        return new JobPostingListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JobPostingListAdapter.JobPostingListAdapterViewHolder holder, int position) {
        Timber.v("onBindViewHolder(JobPostingListAdapter.JobPostingListAdapterViewHolder holder, int position)");
        holder.mID.setText(mJobPost[position].getbusinessName());
        holder.mContent.setText(mJobPost[position].getbusinessAddress());
    }

    @Override
    public int getItemCount() {
        if (mJobPost != null) {
            Timber.v("getItemCount(): " + mJobPost.length);
        } else {
            Timber.v("mJobPost is null");
        }
        return (null != mJobPost ? mJobPost.length : 0);
    }

    public static class JobPostingListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mID;
        TextView mContent;

        public JobPostingListAdapterViewHolder(View itemView) {
            super(itemView);
            mID = (TextView) itemView.findViewById(R.id.id);
            mContent = (TextView) itemView.findViewById(R.id.content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Timber.v("onClick(View v) inside JobPostingListAdapterViewHolder");
            int adapterPosition = getAdapterPosition();
            int layoutPosition = getLayoutPosition();

            Timber.v("adapterPosition is " + adapterPosition + " and layoutPosition is " + layoutPosition);
            Intent intent = new Intent(v.getContext(), JobPostingDetailActivity.class);
            intent.putExtra(JobPostingDetailFragment.ARG_ITEM_ID, adapterPosition);
            v.getContext().startActivity(intent);
        }
    }
}
