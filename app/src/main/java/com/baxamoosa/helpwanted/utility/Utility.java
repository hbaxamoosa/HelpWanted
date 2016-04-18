package com.baxamoosa.helpwanted.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/14/16.
 */
public class Utility {

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context c) {

        if (BuildConfig.DEBUG) {
            Timber.v("isNetworkAvailable(Context c)");
        }

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isLocationAvailable(Context context) {

        if (BuildConfig.DEBUG) {
            Timber.v("isLocationAvailable(Context context)");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains(context.getString(R.string.pref_location_latitude))
                && prefs.contains(context.getString(R.string.pref_location_longitude));
    }
}
