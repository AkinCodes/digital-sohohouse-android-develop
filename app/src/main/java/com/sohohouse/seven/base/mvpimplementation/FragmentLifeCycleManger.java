package com.sohohouse.seven.base.mvpimplementation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;

public class FragmentLifeCycleManger extends LifeCycleManager<FragmentLifeCycleListener> implements FragmentLifeCycleListener {

    @Override
    public void onViewCreated(Fragment fragment, View view, @Nullable Bundle savedInstanceState) {
        for (FragmentLifeCycleListener fragmentLifeCycleListener : getLifeCycleListenerList()) {
            fragmentLifeCycleListener.onViewCreated(fragment, view, savedInstanceState);
        }
    }
}
