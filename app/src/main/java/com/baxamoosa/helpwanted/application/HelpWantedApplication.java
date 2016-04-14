package com.baxamoosa.helpwanted.application;

import android.app.Application;
import android.content.Context;

import com.baxamoosa.helpwanted.BuildConfig;
import com.facebook.stetho.Stetho;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/14/16.
 */
public class HelpWantedApplication extends Application {

    private static Context context;

    public static Context getAppContext() {
        return HelpWantedApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HelpWantedApplication.context = getApplicationContext();

        //Including Jake Wharton's Timber logging library
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.v("Timber.plant(new Timber.DebugTree());");
        } else {
            // Timber.plant(new CrashReportingTree());
        }

        // Facebook Stetho
        Stetho.initializeWithDefaults(this);
    }
}