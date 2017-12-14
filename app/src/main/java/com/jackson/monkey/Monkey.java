package com.jackson.monkey;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

/**
 * author: hd
 * since: 2017/12/15
 */
public class Monkey {

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    private Monkey(Activity activity) {
        this(activity,null);
    }

    private Monkey(Fragment fragment){
        this(null,fragment);
    }

    public Monkey(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<Activity>(activity);
        mFragment = new WeakReference<Fragment>(fragment);
    }

    public static Monkey from(Activity activity){
        return new Monkey(activity);
    }

    public static Monkey from(Fragment fragment){
        return new Monkey(fragment);
    }



}
