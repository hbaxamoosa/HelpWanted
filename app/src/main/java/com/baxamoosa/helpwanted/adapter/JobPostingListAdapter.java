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

    public static JobPost[] mJobPost;

    public JobPostingListAdapter() {}

    public JobPostingListAdapter(JobPost[] jp) {
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
        holder.mWageRate.setText("$" + Integer.toString(mJobPost[position].getWageRate()));
        holder.mName.setText(mJobPost[position].getbusinessName());
        holder.mAddress.setText(mJobPost[position].getbusinessAddress());
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

        private TextView mWageRate;
        private TextView mName;
        private TextView mAddress;

        public JobPostingListAdapterViewHolder(View itemView) {
            super(itemView);
            mWageRate = (TextView) itemView.findViewById(R.id.wagerate);
            mName = (TextView) itemView.findViewById(R.id.name);
            mAddress = (TextView) itemView.findViewById(R.id.address);
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

            intent.putExtra(v.getContext().getResources().getString(R.string._id), mJobPost[adapterPosition].get_id());
            Timber.v("mJobPost[position].get_id(): " + mJobPost[adapterPosition].get_id());
            intent.putExtra(v.getContext().getResources().getString(R.string.business_id), mJobPost[adapterPosition].getBusinessId());
            Timber.v("mJobPost[position].getBusinessId(): " + mJobPost[adapterPosition].getBusinessId());
            intent.putExtra(v.getContext().getResources().getString(R.string.business_name), mJobPost[adapterPosition].getbusinessName());
            Timber.v("mJobPost[position].getBusinessName(): " + mJobPost[adapterPosition].getbusinessName());
            intent.putExtra(v.getContext().getResources().getString(R.string.business_address), mJobPost[adapterPosition].getbusinessAddress());
            intent.putExtra(v.getContext().getResources().getString(R.string.business_phone), mJobPost[adapterPosition].getbusinessPhone());
            intent.putExtra(v.getContext().getResources().getString(R.string.business_website), mJobPost[adapterPosition].getbusinessWebsite());
            intent.putExtra(v.getContext().getResources().getString(R.string.business_latitude), mJobPost[adapterPosition].getbusinessLatitude()); // double
            intent.putExtra(v.getContext().getResources().getString(R.string.business_longitude), mJobPost[adapterPosition].getbusinessLongitude()); // double
            intent.putExtra(v.getContext().getResources().getString(R.string.business_wage_rate), mJobPost[adapterPosition].getWageRate()); // int
            intent.putExtra(v.getContext().getResources().getString(R.string.business_post_date), mJobPost[adapterPosition].getDate()); // double
            intent.putExtra(v.getContext().getResources().getString(R.string.business_owner), mJobPost[adapterPosition].getUser());

            v.getContext().startActivity(intent);
        }
    }
}
