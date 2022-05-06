package com.sohohouse.seven.base.mvpimplementation;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;


public class ActivityLifeCycleManager extends LifeCycleManager<ActivityLifeCycleListener> implements ActivityLifeCycleListener {

    @Override
    public void onPostCreated(Activity activity, @Nullable Bundle savedInstanceState) {
        for (ActivityLifeCycleListener activityLifeCycleListener : getLifeCycleListenerList()) {
            activityLifeCycleListener.onPostCreated(activity, savedInstanceState);
        }
    }
}
