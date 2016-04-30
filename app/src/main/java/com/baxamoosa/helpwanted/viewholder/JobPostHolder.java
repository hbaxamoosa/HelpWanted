package com.baxamoosa.helpwanted.viewholder;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */
public class JobPostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static TextView mId;
    public final TextView mContent;
    public final CardView mCardView;
    View mView;

    public JobPostHolder(View itemView) {
        super(itemView);
        Timber.v("JobPostHolder(View itemView)");
        mView = itemView;
        mId = (TextView) mView.findViewById(R.id.id);
        mContent = (TextView) mView.findViewById(R.id.content);
        mCardView = (CardView) mView.findViewById(R.id.cardview);
        mCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int position = getLayoutPosition();
        Context context = itemView.getContext();

        Toast.makeText(context, "onClick(View v) from JobPostHolder ViewHolder", Toast.LENGTH_SHORT).show();
        // ((JobPostsAdapter.Callback) context).onItemSelected(position);
    }
}