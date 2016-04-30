package com.baxamoosa.helpwanted.adapter;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baxamoosa.helpwanted.fragment.MyJobFragment;

/**
 * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
 * representing an object in the collection.
 */
public class MyJobsPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager mFragmentManager;

    public MyJobsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;

    }

    @Override
    public Fragment getItem(int i) {
        /*Fragment fragment = new MyJobFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(MyJobFragment.ARG_OBJECT, SignInActivity.mJobPost);
        fragment.setArguments(args);
        return fragment;*/
        switch (i) {
            case 0: // Active
                Fragment fragmentActive = new MyJobFragment();
                return fragmentActive;
            case 1: // Expired
                Fragment fragmentExpired = new MyJobFragment();
                return fragmentExpired;
            case 2: // Favorites
                Fragment fragmentFavorites = new MyJobFragment();
                return fragmentFavorites;
        }
        return new MyJobFragment(); // execution should never go here!
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Active";
            case 1:
                return "Expired";
            case 2:
                return "Favorite";
        }
        return "No tabs found";
    }
}