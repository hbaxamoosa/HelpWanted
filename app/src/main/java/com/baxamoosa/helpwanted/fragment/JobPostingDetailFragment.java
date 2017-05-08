package com.baxamoosa.helpwanted.fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.data.JobPostContract;
import com.baxamoosa.helpwanted.ui.JobPostingDetailActivity;
import com.baxamoosa.helpwanted.utility.Utility;

import timber.log.Timber;

public class JobPostingDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */

    public static final String ARG_ITEM_ID = "item_id";
    private SharedPreferences sharedPref;
    private Bundle arguments;
    private CollapsingToolbarLayout appBarLayout;
    private int position;

    private View rootView;
    private TextView phoneOfBusiness;
    private TextView emailOfBusiness;
    private TextView wageRateOfBusiness;
    private TextView addressOfBusiness;

    private String _id;
    private String businessId;
    private String businessName;
    private String businessAddress;
    private String businessPhone;
    private String businessWebsite;
    private double businessLatitude;
    private double businessLongitude;
    private int wageRate;
    private double postDate;
    private String user;

    private MapView mapView;
    private GoogleMap map;
    private boolean tablet;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JobPostingDetailFragment() {
    }

    public static void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareIntent());
    }

    private static Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out HelpWanted to see job posts.");
        return shareIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*if (BuildConfig.DEBUG) {
            Timber.v("onActivityCreated(@Nullable Bundle savedInstanceState)");
        }*/
        getActivity().getSupportLoaderManager().initLoader(Utility.ALL_JOBPOSTS, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }*/

        arguments = getArguments();
        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (arguments != null) {
            /*Timber.v("arguments != null");*/
            position = arguments.getInt(ARG_ITEM_ID);
            // businessID = new String[]{ arguments.getString(getString(R.string.business_id)) };
            /*Timber.v("position: " + position);*/
            // Timber.v("businessID: " + businessID[0]);
        }
        Activity activity = this.getActivity();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.collapsing_toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*if (BuildConfig.DEBUG) {
            Timber.v("onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
        }*/
        rootView = inflater.inflate(R.layout.activity_detail, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        if (mapView != null && mapView.getVisibility() == View.VISIBLE) {  // tablet
            tablet = true;
            mapView.onCreate(savedInstanceState);
            // Gets to GoogleMap from the MapView and does initialization stuff
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    // see https://developers.google.com/android/reference/com/google/android/gms/maps/MapView for reference
                }
            });
            map.getUiSettings().setMyLocationButtonEnabled(false);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());
        }

        phoneOfBusiness = (TextView) rootView.findViewById(R.id.tvPhone);
        emailOfBusiness = (TextView) rootView.findViewById(R.id.tvEmail);
        wageRateOfBusiness = (TextView) rootView.findViewById(R.id.tvWageRate);
        addressOfBusiness = (TextView) rootView.findViewById(R.id.tvAddress);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (getActivity() instanceof JobPostingDetailActivity) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_detail, menu);
            finishCreatingMenu(menu);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*Timber.v("onCreateLoader(int id, Bundle args)");*/
        return new CursorLoader(
                getActivity(),
                JobPostContract.JobPostList.CONTENT_URI,
                Utility.JOBPOST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /*Timber.v("onLoadFinished(Loader<Cursor> loader, Cursor data)");*/

        if (data != null && data.moveToFirst()) {
            Timber.v("data != null && data.moveToFirst()");
            data.move(position);

            _id = data.getString(Utility.COL_ID);
            businessId = data.getString(Utility.COL_BUSINESS_ID);
            businessName = data.getString(Utility.COL_BUSINESS_NAME);
            businessAddress = data.getString(Utility.COL_BUSINESS_ADDRESS);
            businessPhone = data.getString(Utility.COL_BUSINESS_PHONE);
            businessWebsite = data.getString(Utility.COL_BUSINESS_WEBSITE);
            businessLatitude = data.getDouble(Utility.COL_BUSINESS_LATITUDE);
            businessLongitude = data.getDouble(Utility.COL_BUSINESS_LONGITUDE);
            wageRate = data.getInt(Utility.COL_WAGERATE);
            postDate = data.getDouble(Utility.COL_POSTDATE);
            user = data.getString(Utility.COL_OWNER);

            Timber.v("_id: " + _id);
            Timber.v("businessID: " + businessId);
            Timber.v("businessName: " + businessName);
            Timber.v("businessAddress: " + businessAddress);
            Timber.v("businessPhone: " + businessPhone);
            Timber.v("businessWebsite: " + businessWebsite);
            Timber.v("businessLatitude: " + businessLatitude);
            Timber.v("businessLongitude: " + businessLongitude);
            Timber.v("wageRate: " + wageRate);
            Timber.v("postDate: " + postDate);
            Timber.v("user: " + user);

            if (appBarLayout != null) {
                appBarLayout.setTitle(businessName);
            }

            phoneOfBusiness.setText(businessPhone);
            emailOfBusiness.setText(user);
            wageRateOfBusiness.setText("This job pays $" + Integer.toString(wageRate) + " per hour.");
            addressOfBusiness.setText(businessAddress);

            if (tablet) {
                // Updates the location and zoom of the MapView
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(businessLatitude, businessLongitude), 15);
                map.animateCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*Timber.v("onLoaderReset(Loader<Cursor> loader)");*/
    }

    @Override
    public void onResume() {
        if (tablet) {
            mapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tablet) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (tablet) {
            mapView.onLowMemory();
        }
    }
}