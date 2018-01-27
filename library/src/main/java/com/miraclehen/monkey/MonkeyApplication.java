package com.miraclehen.monkey;

import android.app.Application;


/**
 * author: miraclehen
 * since: 2018/1/23
 */
public class MonkeyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }
}
