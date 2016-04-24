package com.baxamoosa.helpwanted.ui;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.utility.Utility;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/14/16.
 */
public class Settings extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    protected final static int PLACE_PICKER_REQUEST = 9090;
    private ImageView mAttribution;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));

        // If we are using a PlacePickerActivity location, we need to show attributions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAttribution = new ImageView(this);
            mAttribution.setImageResource(R.drawable.powered_by_google_light);

            if (!Utility.isLocationAvailable(this)) {
                mAttribution.setVisibility(View.GONE);
            }

            setListFooter(mAttribution);
        }
    }

    // Registers a shared preference change listener that gets notified when preferences change
    @Override
    protected void onResume() {

        if (BuildConfig.DEBUG) {
            Timber.v("onResume()");
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    // Unregisters a shared preference change listener
    @Override
    protected void onPause() {

        if (BuildConfig.DEBUG) {
            Timber.v("onPause()");
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void bindPreferenceSummaryToValue(Preference preference) {

        if (BuildConfig.DEBUG) {
            Timber.v("bindPreferenceSummaryToValue(Preference preference)");
        }

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    // This gets called before the preference is changed
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        if (BuildConfig.DEBUG) {
            Timber.v("onPreferenceChange(Preference preference, Object value)");
        }

        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (key.equals(getString(R.string.pref_location_key))) {
            /*@SunshineSyncAdapter.LocationStatus int status = Utility.getLocationStatus(this);
            switch (status) {
                case SunshineSyncAdapter.LOCATION_STATUS_OK:
                    preference.setSummary(stringValue);
                    break;
                case SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN:
                    preference.setSummary(getString(R.string.pref_location_unknown_description, value.toString()));
                    break;
                case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                    preference.setSummary(getString(R.string.pref_location_error_description, value.toString()));
                    break;
                default:
                    // Note --- if the server is down we still assume the value is valid
                    preference.setSummary(stringValue);
            }*/
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    // This gets called after the preference is changed, which is important because we start our synchronization here
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (BuildConfig.DEBUG) {
            Timber.v("onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)");
        }

        if (key.equals(getString(R.string.pref_location_key))) {
            // we've changed the location
            // Wipe out any potential PlacePickerActivity latlng values so that we can use this text entry.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(getString(R.string.pref_location_latitude));
            editor.remove(getString(R.string.pref_location_longitude));
            editor.commit();

            // Remove attributions for our any PlacePickerActivity locations.
            if (mAttribution != null) {
                mAttribution.setVisibility(View.GONE);
            }

            // Utility.resetLocationStatus(this);
            // SunshineSyncAdapter.syncImmediately(this);
        } else if (key.equals(getString(R.string.pref_location_status_key))) {
            // our location status has changed.  Update the summary accordingly
            Preference locationPreference = findPreference(getString(R.string.pref_location_key));
            bindPreferenceSummaryToValue(locationPreference);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (BuildConfig.DEBUG) {
            Timber.v("onActivityResult(int requestCode, int resultCode, Intent data)");
        }

        // TODO: 4/22/16 Replace this Place Picker functionality with a call to determine the user's zipcode 
        // Check to see if the result is from our Place Picker intent
        if (requestCode == PLACE_PICKER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String address = place.getAddress().toString();
                LatLng latLong = place.getLatLng();

                // If the provided place doesn't have an address, we'll form a display-friendly
                // string from the latlng values.
                if (TextUtils.isEmpty(address)) {
                    address = String.format("(%.2f, %.2f)", latLong.latitude, latLong.longitude);
                }

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_location_key), address);

                // Also store the latitude and longitude so that we can use these to get a precise
                // result from our weather service. We cannot expect the weather service to
                // understand addresses that Google formats.
                editor.putFloat(getString(R.string.pref_location_latitude), (float) latLong.latitude);
                editor.putFloat(getString(R.string.pref_location_longitude), (float) latLong.longitude);
                editor.commit();

                // Tell the SyncAdapter that we've changed the location, so that we can update
                // our UI with new values. We need to do this manually because we are respondings
                // to the PlacePickerActivity widget result here instead of allowing the
                // LocationEditTextPreference to handle these changes and invoke our callbacks.
                Preference locationPreference = findPreference(getString(R.string.pref_location_key));
                onPreferenceChange(locationPreference, address);

                // Add attributions for our new PlacePickerActivity location.
                if (mAttribution != null) {
                    mAttribution.setVisibility(View.VISIBLE);
                } else {
                    // For pre-Honeycomb devices, we cannot add a footer, so we will use a snackbar
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, getString(R.string.attribution_text),
                            Snackbar.LENGTH_LONG).show();
                }

                // Utility.resetLocationStatus(this);
                // SunshineSyncAdapter.syncImmediately(this);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
