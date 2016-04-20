package com.baxamoosa.helpwanted.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/18/16.
 */
public class PlacePickerExample extends AppCompatActivity {

    private static final int PLACE_PICKER_FLAG = 1;
    protected GoogleApiClient mGoogleApiClient;
    private PlacePicker.IntentBuilder builder;

    private Button pickerBtn;
    private TextView myLocation;
    private ImageView mPhoto;
    private TextView mAttribution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_placepickerexample);
        builder = new PlacePicker.IntentBuilder();

        pickerBtn = (Button) findViewById(R.id.pickerBtn);
        pickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    builder = new PlacePicker.IntentBuilder();
                    Intent intent = builder.build(PlacePickerExample.this);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_FLAG);

                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), PlacePickerExample.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(PlacePickerExample.this, "Google Play Services is not available.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        myLocation = (TextView) findViewById(R.id.myLocation);
        mPhoto = (ImageView) findViewById(R.id.imageView);
        mAttribution = (TextView) findViewById(R.id.attribution);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (BuildConfig.DEBUG) {
            Timber.v("onActivityResult(int requestCode, int resultCode, Intent data)");
        }
        if (resultCode == RESULT_OK) {
            if (BuildConfig.DEBUG) {
                Timber.v("resultCode == RESULT_OK");
            }
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    Place place = PlacePicker.getPlace(data, this);
                    myLocation.setText(
                            "Name: " + place.getName() +
                                    "\n" +
                                    "Address: " + place.getAddress() +
                                    "\n" +
                                    "ID: " + place.getId() +
                                    "\n" +
                                    "Phone: " + place.getPhoneNumber() +
                                    "\n" +
                                    "Website: " + place.getWebsiteUri() +
                                    "\n" +
                                    "LatLang: " + place.getLatLng()
                    );
                    if (BuildConfig.DEBUG) {
                        Timber.v("calling placePhotosTask(place.getId())");
                    }
                    placePhotosTask(place.getId());
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

    private void placePhotosTask(String placeID) {

        if (BuildConfig.DEBUG) {
            Timber.v("placePhotosTask(String placeID)");
        }
        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(mPhoto.getWidth(), mPhoto.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                mPhoto.setImageResource(R.drawable.empty_photo);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (BuildConfig.DEBUG) {
                    Timber.v("onPostExecute(AttributedPhoto attributedPhoto)");
                }
                if (attributedPhoto != null) {
                    if (BuildConfig.DEBUG) {
                        Timber.v("(attributedPhoto != null)");
                    }
                    // Photo has been loaded, display it.
                    mPhoto.setImageBitmap(attributedPhoto.bitmap);

                    // Display the attribution as HTML content if set.
                    if (attributedPhoto.attribution == null) {
                        mAttribution.setVisibility(View.GONE);
                    } else {
                        mAttribution.setVisibility(View.VISIBLE);
                        mAttribution.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                    }

                }
            }
        }.execute(placeID);
    }

    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first (and only) parameter.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        /**
         * Holder for an image and its attribution.
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }

}