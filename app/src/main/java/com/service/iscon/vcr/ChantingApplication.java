package com.service.iscon.vcr;

import android.app.Application;

import com.service.iscon.vcr.Utils.ChantingPrefSettings;

/**
 * Created by root on 13/9/18.
 */

public class ChantingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        ChantingPrefSettings.init(this);
    }
}
