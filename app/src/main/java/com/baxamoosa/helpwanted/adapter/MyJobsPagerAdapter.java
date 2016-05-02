package com.baxamoosa.helpwanted.adapter;

/**
 * Created by hasnainbaxamoosa on 4/27/16.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baxamoosa.helpwanted.fragment.MyJobActiveFragment;
import com.baxamoosa.helpwanted.fragment.MyJobExpiredFragment;
import com.baxamoosa.helpwanted.fragment.MyJobFavoriteFragment;

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
        Fragment returnFragment = null;
        switch (i) {
            case 0: // Active
                Fragment fragmentActive = new MyJobActiveFragment();
                returnFragment = fragmentActive;
                break;
            case 1: // Expired
                Fragment fragmentExpired = new MyJobExpiredFragment();
                returnFragment = fragmentExpired;
                break;
            case 2: // Favorites
                Fragment fragmentFavorites = new MyJobFavoriteFragment();
                returnFragment = fragmentFavorites;
                break;
        }
        return returnFragment; // execution should never go here!
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