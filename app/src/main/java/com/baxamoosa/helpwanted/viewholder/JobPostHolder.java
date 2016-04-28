package com.baxamoosa.helpwanted.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baxamoosa.helpwanted.R;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */
public class JobPostHolder extends RecyclerView.ViewHolder {
    public static TextView id;
    public TextView content;
    View mView;

    public JobPostHolder(View itemView) {
        super(itemView);
        Timber.v("JobPostHolder(View itemView)");
        mView = itemView;
        id = (TextView) mView.findViewById(R.id.id);
        content = (TextView) mView.findViewById(R.id.content);
    }
}