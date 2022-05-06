/*
  Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
  Unauthorized copying of this file, via any medium is strictly prohibited
  This content is proprietary and confidential
 */

package com.sohohouse.seven.base.mvpimplementation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class LifeCycleManager<T extends LifeCycleListener> implements LifeCycleListener {

    private final List<T> lifeCycleListenerList = new ArrayList<>();

    @Override
    public void onResume() {
        for (T lifeCycleListener : lifeCycleListenerList) {
            lifeCycleListener.onResume();
        }
    }

    @SuppressWarnings("unused")
    public void add(T lifeCycleListener) {
        lifeCycleListenerList.add(lifeCycleListener);
    }

    public void addAll(List<T> lifeCycleListener) {
        lifeCycleListenerList.addAll(lifeCycleListener);
    }

    List<T> getLifeCycleListenerList() {
        return lifeCycleListenerList;
    }

    @Override
    public void onPause() {
        for (T lifeCycleListener : lifeCycleListenerList) {
            lifeCycleListener.onPause();
        }
    }

    @Override
    public void onDestroy() {
        for (T lifeCycleListener : lifeCycleListenerList) {
            lifeCycleListener.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (T lifeCycleListener : lifeCycleListenerList) {
            lifeCycleListener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        for (T lifeCycleListener : lifeCycleListenerList) {
            lifeCycleListener.onSaveInstanceState(outState);
        }
    }


    public void clearListeners() {
        lifeCycleListenerList.clear();
    }

}
