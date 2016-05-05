package com.baxamoosa.helpwanted.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by hasnainbaxamoosa on 5/5/16.
 */

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */

public class HelpWantedAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private HelpWantedAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new HelpWantedAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}