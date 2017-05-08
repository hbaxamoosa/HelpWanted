package com.baxamoosa.helpwanted.ui;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;

/**
 * Created by hasnainbaxamoosa on 4/18/16.
 */
public class PlacePickerActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_FLAG = 1;
    protected GoogleApiClient mGoogleApiClient;
    private PlacePicker.IntentBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_placepickerexample);
        builder = new PlacePicker.IntentBuilder();

        try {
            builder = new PlacePicker.IntentBuilder();
            Intent intent = builder.build(PlacePickerActivity.this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, PLACE_PICKER_FLAG);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), PlacePickerActivity.this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(PlacePickerActivity.this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (BuildConfig.DEBUG) {
            Timber.v("onActivityResult(int requestCode, int resultCode, Intent data)");
        }*/
        if (resultCode == RESULT_OK) {
            /*if (BuildConfig.DEBUG) {
                Timber.v("resultCode == RESULT_OK");
            }*/
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    Place place = PlacePicker.getPlace(this, data);
                    Intent mIntent = new Intent(this, AddEditJobActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString(getString(R.string.addJob), getString(R.string.addJob));
                    if (!place.getId().isEmpty()) {
                        mBundle.putString(getString(R.string.business_id), place.getId());
                    }
                    if (!place.getName().toString().isEmpty()) {
                        mBundle.putString(getString(R.string.business_name), (String) place.getName());
                    }
                    try {
                        if (!place.getPhoneNumber().toString().isEmpty()) {
                            mBundle.putString(getString(R.string.business_phone), (String) place.getPhoneNumber());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBundle.putString(getString(R.string.business_phone), "No phone number available."); // make this a string resource
                    }
                    try {
                        if (!place.getAddress().toString().isEmpty()) {
                            mBundle.putString(getString(R.string.business_address), (String) place.getAddress());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBundle.putString(getString(R.string.business_address), "No address available."); // make this a string resource
                    }
                    try {
                        if (!place.getWebsiteUri().toString().isEmpty()) {
                            mBundle.putString(getString(R.string.business_website), place.getWebsiteUri().toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBundle.putString(getString(R.string.business_website), "No website available.");  // make this a string resource
                    }
                    if (!place.getLatLng().toString().isEmpty()) {
                        mBundle.putDouble(getString(R.string.business_latitude), place.getLatLng().latitude);
                    }
                    if (!place.getLatLng().toString().isEmpty()) {
                        mBundle.putDouble(getString(R.string.business_longitude), place.getLatLng().longitude);
                    }
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}