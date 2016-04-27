package com.baxamoosa.helpwanted.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baxamoosa.helpwanted.BuildConfig;
import com.baxamoosa.helpwanted.R;
import com.baxamoosa.helpwanted.application.HelpWantedApplication;
import com.baxamoosa.helpwanted.model.JobPost;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/18/16.
 */

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class SignInActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    public static JobPost[] mJobPost;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    private boolean signout;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googleaccountexample);

        signout = false;
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate()");
        }
        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]

        Timber.v("creating Firebase ref");
        // Get a reference to our posts
        ref = new Firebase(HelpWantedApplication.getAppContext().getResources().getString(R.string.firebase_connection_string));
        // Attach an listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Timber.v("There are " + snapshot.getChildrenCount() + " job posts");
                int i = 0;
                for (DataSnapshot jobPostSnapshot : snapshot.getChildren()) {
                    JobPost jobPost = jobPostSnapshot.getValue(JobPost.class); // Firebase is returning JobPost objects from the Cloud
                    Timber.v(jobPost.getName() + " - " + jobPost.getAddress());
                    mJobPost = new JobPost[(int) snapshot.getChildrenCount()];
                    if (i < snapshot.getChildrenCount()) {
                        mJobPost[i] = new JobPost();
                        mJobPost[i].id = jobPost.getId();
                        mJobPost[i].name = jobPost.getName();
                        mJobPost[i].address = jobPost.getAddress();
                        mJobPost[i].phone = jobPost.getPhone();
                        mJobPost[i].website = jobPost.getWebsite();
                        mJobPost[i].latitude = jobPost.getLatitude();
                        mJobPost[i].longitude = jobPost.getLongitude();
                        mJobPost[i].date = jobPost.getDate();
                        mJobPost[i].user = jobPost.getUser();
                        Timber.v("mJobPost[i].name: " + mJobPost[i].name);
                        i++;
                    }
                }
                Timber.v("mJobPost.length: " + mJobPost.length);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Timber.v("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) {
            Timber.v("onStart()");
        }

        mGoogleApiClient.connect();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            if (BuildConfig.DEBUG) {
                Timber.v("Got cached sign-in");
            }
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (BuildConfig.DEBUG) {
            Timber.v("onStop()");
        }

        if (mGoogleApiClient.isConnected()) {
            if (BuildConfig.DEBUG) {
                Timber.v("mGoogleApiClient.disconnect()");
            }
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (BuildConfig.DEBUG) {
            Timber.v("onResume()");
        }

        mGoogleApiClient.connect();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            editor = sharedPref.edit();
            editor.putString(getString(R.string.person_name), result.getSignInAccount().getDisplayName());
            editor.putString(getString(R.string.person_email), result.getSignInAccount().getEmail());
            editor.putString(getString(R.string.person_id), result.getSignInAccount().getId());
            editor.putString(getString(R.string.person_photo), String.valueOf(result.getSignInAccount().getPhotoUrl()));
            editor.commit();
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        if (BuildConfig.DEBUG) {
            Timber.v("handleSignInResult: " + result.isSuccess());
        }
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);

            try {
                signout = sharedPref.getBoolean("signout", false);
                Timber.v("signout: " + signout);
            } catch (NullPointerException e) {
                Timber.v("Error: " + e);
                editor = sharedPref.edit();
                editor.putBoolean("signout", false);
                editor.commit();
            }

            if (!signout) { //only send the user to the Job Listing when this is the firstRun
                startActivity(new Intent(this, JobPostingListActivity.class));
            }
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    public void signOutAndRevoke() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (BuildConfig.DEBUG) {
                            Timber.v("onResult status: " + status);
                        }
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
        // TODO: 4/24/16 Also clear out all other SharePrefs from app
        editor = sharedPref.edit();
        // first clear all existing Share Prefs
        editor.clear();
        editor.commit();
        // set the new Share Pref value for signout
        editor.putBoolean("signout", false);
        editor.commit();
    }
    // [END signOut]

    // [START revokeAccess]
    /*private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }*/
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        if (BuildConfig.DEBUG) {
            Timber.v("onConnectionFailed: " + connectionResult);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (BuildConfig.DEBUG) {
            Timber.v("onConnected");
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        mGoogleApiClient.connect();
        if (BuildConfig.DEBUG) {
            Timber.v("onConnectionFailed: " + cause);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOutAndRevoke();
                break;
            case R.id.cancel_button:
                startActivity(new Intent(this, JobPostingListActivity.class));
                break;
        }
    }
}