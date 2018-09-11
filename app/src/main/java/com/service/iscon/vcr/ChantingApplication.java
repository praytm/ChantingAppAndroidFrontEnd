package com.service.iscon.vcr;

import android.app.Application;

import com.service.iscon.vcr.db.AppDataBase;
import com.service.iscon.vcr.db.DatabaseManager;

/**
 * Created by Aabhas_Jain on 9/11/2018.
 */

public class ChantingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        DatabaseManager.initializeInstance(AppDataBase.getInstance(getApplicationContext()));
        // Log.d("Debug Database Address" ,DebugDB.getAddressLog());
    }
}
