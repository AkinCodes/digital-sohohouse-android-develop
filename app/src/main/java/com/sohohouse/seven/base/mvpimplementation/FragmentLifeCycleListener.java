package com.sohohouse.seven.base.mvpimplementation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;

/**
 * Callbacks specifically for a fragment.
 */
public interface FragmentLifeCycleListener extends LifeCycleListener {

    void onViewCreated(Fragment fragment, View view, @Nullable Bundle savedInstanceState);
}
