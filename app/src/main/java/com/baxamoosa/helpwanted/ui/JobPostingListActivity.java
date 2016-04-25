package com.baxamoosa.helpwanted.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.application.HelpWantedApplication;
import com.baxamoosa.helpwanted.dummy.DummyContent;
import com.baxamoosa.helpwanted.fragment.JobPostingDetailFragment;
import com.baxamoosa.helpwanted.utility.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;

/**
 * An activity representing a list of JobPostings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link JobPostingDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class JobPostingListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    public static boolean mTwoPane;  // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    public static boolean firstLoad;  // Whether or not the activity is in two-pane mode and whether this is the first load or not.
    private DrawerLayout mDrawerLayout;
    private SharedPreferences sharedPref;
    private TextView profileName;
    private ImageView profilePhoto;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        firstLoad = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set a flag to track whether this is the first time the JobPostingsAdapter is being loaded into the app.
        firstLoad = true;

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }

        boolean isConnected = Utility.isNetworkAvailable(HelpWantedApplication.getAppContext());

        setContentView(R.layout.activity_jobposting_list_drawer);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        // TODO: 4/24/16 set the image and user's name for navigation header

        View recyclerView = findViewById(R.id.jobposting_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.jobposting_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (savedInstanceState != null) {
            // TODO: 4/14/16 do something
        }

        if (savedInstanceState == null) {
            if (isConnected) {
                // TODO: 4/14/16 do something here
            }
        } else {
            if (BuildConfig.DEBUG) {
                Timber.v("(inside else) isConnected: " + isConnected);
            }
            Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            Timber.v("onResume()");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            case R.id.action_signout:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("signout", true);
                editor.commit();
                startActivity(new Intent(this, SignInActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        // Toast.makeText(getApplicationContext(), "menuItem: " + menuItem.getTitle(), Toast.LENGTH_LONG).show();

                        if (menuItem.getTitle() == getString(R.string.job_posting)) {
                            Timber.v("getString(R.string.job_posting): " + getString(R.string.job_posting));
                            startActivity(new Intent(getApplicationContext(), JobPostingListActivity.class));
                        }
                        if (menuItem.getTitle() == getString(R.string.my_jobs)) {
                            Timber.v("getString(R.string.my_jobs): " + getString(R.string.my_jobs));
                            startActivity(new Intent(getApplicationContext(), MyJobsActivity.class));
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_jobpost, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mOverlayTextView.setText(mValues.get(position).id);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(JobPostingDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        JobPostingDetailFragment fragment = new JobPostingDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.jobposting_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, JobPostingDetailActivity.class);
                        intent.putExtra(JobPostingDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mOverlayTextView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mOverlayTextView = (TextView) view.findViewById(R.id.overlaytext);
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
