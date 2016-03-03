package com.tudorluca.sandbox;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.mozilla.javascript.annotations.JSGetter;

/**
 * Created by tudor on 02/03/16.
 */
public class ActivityCallback implements Application.ActivityLifecycleCallbacks {

    public Activity activity;

    @JSGetter
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.activity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
