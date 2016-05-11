package com.baxamoosa.helpwanted.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by hasnainbaxamoosa on 5/5/16.
 */
public class HelpWantedSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static HelpWantedSyncAdapter sHelpWantedSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sHelpWantedSyncAdapter == null) {
                sHelpWantedSyncAdapter = new HelpWantedSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sHelpWantedSyncAdapter.getSyncAdapterBinder();
    }
}