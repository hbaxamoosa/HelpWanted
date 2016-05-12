package com.baxamoosa.helpwanted.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.adapter.MyJobsPagerAdapter;
import com.baxamoosa.helpwanted.utility.Utility;
import com.squareup.picasso.Picasso;

public class MyJobsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    MyJobsPagerAdapter mMyJobsPagerAdapter;
    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private SharedPreferences sharedPref;
    private TextView profileName;
    private ImageView profilePhoto;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }*/
        setContentView(R.layout.activity_content_my_jobs_drawer);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Create an adapter that when requested, will return a fragment representing an object in the collection.
        // ViewPager and its adapters use support library fragments, so we must use getSupportFragmentManager.
        mMyJobsPagerAdapter = new MyJobsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMyJobsPagerAdapter);
        // see https://github.com/droanmalik/Movi/blob/master/app%2Fsrc%2Fmain%2Fjava%2Fme%2Fdroan%2Fmovi%2FMoviActivity.java

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // first call PlacePickerActivity to get the Business details
                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    startActivity(new Intent(getApplicationContext(), PlacePickerActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                }
                // PlacePicker Activity will call AddEditJobActivity to enter remaining details about the job post
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                profileName = (TextView) findViewById(R.id.profileName);
                profileName.setText(sharedPref.getString(getString(R.string.person_name), "no name available"));
                profilePhoto = (ImageView) findViewById(R.id.profileImage);
                Picasso.with(getApplicationContext()).load(sharedPref.getString(getString(R.string.person_photo), "http://square.github.io/picasso/static/sample.png")).into(profilePhoto);
                // profilePhoto.setImageURI(Uri.parse(sharedPref.getString(getString(R.string.person_photo), "no photo available")));
                break;
            case R.id.settings:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);

                        if (menuItem.getTitle() == getString(R.string.job_posting)) {
                            startActivity(new Intent(getApplicationContext(), JobPostingListActivity.class));
                        }
                        if (menuItem.getTitle() == getString(R.string.my_jobs)) {
                            startActivity(new Intent(getApplicationContext(), MyJobsActivity.class));
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }
}