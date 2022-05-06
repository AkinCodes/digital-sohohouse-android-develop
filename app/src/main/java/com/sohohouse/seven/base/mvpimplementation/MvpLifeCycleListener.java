/*
  Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
  Unauthorized copying of this file, via any medium is strictly prohibited
  This content is proprietary and confidential
 */

package com.sohohouse.seven.base.mvpimplementation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;

import com.sohohouse.seven.common.utils.ExceptionUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This helps call back the presenter and view controller during activity's or fragment's life cycle
 * <p>
 * This classes uses {@link RetainedFragment} to persist {@link Presenter} instances between configuration changes
 */
public class MvpLifeCycleListener<V extends ViewController, P extends Presenter<V>> implements ActivityLifeCycleListener, FragmentLifeCycleListener {

    private static final String KEY_PRESENTER_ID = "PRESENTER_ID";
    private static final AtomicInteger uniqueId = new AtomicInteger(0);
    private final V mViewController;
    private final RetainedFragment retainedFragment;
    private final String lifeListenerListenerId;
    private P mPresenter;
    private String presenterId;

    public MvpLifeCycleListener(String lifeListenerListenerId, V viewController, RetainedFragment retainedFragment) {
        this.mViewController = viewController;
        this.lifeListenerListenerId = lifeListenerListenerId;
        this.retainedFragment = retainedFragment;
    }

    public P getPresenter() {
        return mPresenter;
    }


    @Override
    public void onViewCreated(Fragment fragment, View view, @Nullable Bundle savedInstanceState) {
        presenterId = null;
        if (savedInstanceState != null) {
            presenterId = savedInstanceState.getString(KEY_PRESENTER_ID);
        }
        if (presenterId == null) {
            presenterId = uniqueId.getAndIncrement() + lifeListenerListenerId;
        }
        onPostCreateOrOnViewCreated(savedInstanceState);
    }

    @Override
    public void onPostCreated(Activity activity, @Nullable Bundle savedInstanceState) {
        presenterId = lifeListenerListenerId;
        onPostCreateOrOnViewCreated(savedInstanceState);
    }

    private void onPostCreateOrOnViewCreated(@Nullable Bundle savedInstanceState) {
        mPresenter = retainedFragment.get(presenterId);
        boolean isRecreated = savedInstanceState != null;
        if (mPresenter == null) {
            //this will try to retrieve a saved mPresenter. We want to do this because we want to handle
            //cases when activities are destroyed and persist some network calls or some data.
            //noinspection unchecked
            mPresenter = (P) mViewController.createPresenter();
            if (mPresenter.isRetained()) {
                registerOnDestroyCallBackOnRetainedFragment();
            }
        }

        mViewController.onCreated();

        if (isRecreated) {
            mPresenter.onRecreated();
        }
    }

    @Override
    public void onResume() {
        mPresenter.attach(mViewController);
    }

    @Override
    public void onPause() {
        mPresenter.detach();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null && !mPresenter.isRetained()) {
            mPresenter.onDestroy();
        }
    }


    private void registerOnDestroyCallBackOnRetainedFragment() {
        Presenter presenter = getPresenter();
        retainedFragment.addOnDestroyListener(presenter::onDestroy);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.attach(mViewController);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Presenter presenter = retainedFragment.get(presenterId);
        if (presenter != null && presenter != this) {
            ExceptionUtils.INSTANCE.throwExceptionDebug(
                    "This presenter id is about to overwrite another presenter. " +
                            "Please use an unique lifecycleListenerId, current id=" + lifeListenerListenerId);
        }
        if (mPresenter.isRetained()) {
            retainedFragment.put(presenterId, mPresenter);
            outState.putString(KEY_PRESENTER_ID, presenterId);
        }
    }

    @SuppressWarnings("unused")
    public V getViewController() {
        return mViewController;
    }

}
