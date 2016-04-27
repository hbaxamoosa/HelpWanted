package com.baxamoosa.helpwanted.adapter;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.fragment.MyJobFragment;
import com.baxamoosa.helpwanted.ui.SignInActivity;

import timber.log.Timber;

/**
 * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
 * representing an object in the collection.
 */
public class MyJobsPagerAdapter extends FragmentStatePagerAdapter {

    public MyJobsPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new MyJobFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(MyJobFragment.ARG_OBJECT, SignInActivity.mJobPost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                if (BuildConfig.DEBUG) {
                    Timber.v("Tab 0");
                }
                return "Active";
            case 1:
                if (BuildConfig.DEBUG) {
                    Timber.v("Tab 1");
                }
                return "Expired";
            case 2:
                if (BuildConfig.DEBUG) {
                    Timber.v("Tab 2");
                }
                return "Favorite";
        }
        return "No tabs found";
    }
}