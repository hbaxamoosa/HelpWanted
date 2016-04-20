package com.baxamoosa.helpwanted.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baxamoosa.helpwanted.BuildConfig;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/18/16.
 */
public class GoogleAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.v("onCreate");
        }
    }
}
