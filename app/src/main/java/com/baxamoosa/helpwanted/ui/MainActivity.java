package com.baxamoosa.helpwanted.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.fragment.AllJobsFragment;
import com.baxamoosa.helpwanted.fragment.MyJobActiveFragment;
import com.baxamoosa.helpwanted.fragment.MyJobExpiredFragment;
import com.baxamoosa.helpwanted.fragment.MyJobFavoriteFragment;
import com.baxamoosa.helpwanted.utility.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 5/11/16.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TextView profileName;
    private ImageView profilePhoto;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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
        Timber.v("onOptionsItemSelected(MenuItem item): " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                profileName = (TextView) findViewById(R.id.profileName);
                profileName.setText(sharedPref.getString(getString(R.string.person_name), "no name available"));
                profilePhoto = (ImageView) findViewById(R.id.profileImage);
                Picasso.with(getApplicationContext()).load(sharedPref.getString(getString(R.string.person_photo), "http://square.github.io/picasso/static/sample.png")).into(profilePhoto);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new AllJobsFragment(), "All Jobs");
        adapter.addFragment(new MyJobActiveFragment(), "Active");
        adapter.addFragment(new MyJobExpiredFragment(), "Expired");
        adapter.addFragment(new MyJobFavoriteFragment(), "Favorite");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (menuItem.getTitle() == getString(R.string.action_settings)) {
                            startActivity(new Intent(getApplicationContext(), Settings.class));
                        }
                        if (menuItem.getTitle() == getString(R.string.action_signout)) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("signout", true);
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}