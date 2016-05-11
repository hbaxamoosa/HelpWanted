package com.baxamoosa.helpwanted.ui;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/14/16.
 */

// http://developer.android.com/reference/android/preference/PreferenceScreen.html
// http://developer.android.com/guide/topics/ui/settings.html
public class Settings extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);
    }

    // Registers a shared preference change listener that gets notified when preferences change
    @Override
    public void onResume() {
        super.onResume();

        if (BuildConfig.DEBUG) {
            Timber.v("onResume()");
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    // Unregisters a shared preference change listener
    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) {
            Timber.v("onPause()");
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) {
            Timber.v("onStart()");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (BuildConfig.DEBUG) {
            Timber.v("onStop()");
        }
    }

    // This gets called before the preference is changed
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        return true;
    }

    // This gets called after the preference is changed, which is important because we start our synchronization here
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (BuildConfig.DEBUG) {
            Timber.v("onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)");
        }

        if (key.equals(getString(R.string.range))) {
            Preference rangePref = findPreference(getString(R.string.range));
            rangePref.setSummary(sharedPreferences.getString(getString(R.string.range), ""));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
