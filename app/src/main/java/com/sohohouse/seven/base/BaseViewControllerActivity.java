/*
  Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
  Unauthorized copying of this file, via any medium is strictly prohibited
  This content is proprietary and confidential
 */

package com.sohohouse.seven.base;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.sohohouse.seven.base.mvpimplementation.ActivityLifeCycleListener;
import com.sohohouse.seven.base.mvpimplementation.MvpLifeCycleListener;
import com.sohohouse.seven.base.mvpimplementation.ViewController;
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationAdapterItem;
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationDialog;
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Base MVP Activity
 */
public abstract class BaseViewControllerActivity<P extends BasePresenter>
        extends BaseActivity implements ViewController, InAppNotificationListener {

    private static final String RETAINED_KEY_MAIN_PRESENTER = "MAIN_PRESENTER";

    private MvpLifeCycleListener mvpLifeCycleListener;

    /**
     * Presenter is created {@link #onCreate(Bundle)}
     *
     * @return Presenter
     */
    @Override
    abstract public P createPresenter();


    /**
     * Provides the presenter created with {@link #createPresenter()}.
     *
     * @return Presenter instance.
     */
    @SuppressWarnings("unchecked")
    public P getPresenter() {
        return (P) mvpLifeCycleListener.getPresenter();
    }

    @Override
    public List<ActivityLifeCycleListener> createLifeCycleListenerList(
            List<ActivityLifeCycleListener> lifeCycleListenerList
    ) {
        lifeCycleListenerList.add(
                mvpLifeCycleListener = new MvpLifeCycleListener<>(
                        RETAINED_KEY_MAIN_PRESENTER,
                        this,
                        getRetainedFragment()
                )
        );
        return super.createLifeCycleListenerList(lifeCycleListenerList);
    }

    @Override
    public void onCreated() {
        // do nothing by default
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setScreenName(String screenName) {
        getPresenter().setScreenName(this.getLocalClassName(), screenName);
    }

    /**
     * Have to keep two implementations of this because stupid Java won't let us call this from a
     * kotlin interface.
     */
    @Override
    public void onNotificationDataReady(@NotNull Context context, @NotNull InAppNotificationAdapterItem item) {
        InAppNotificationDialog dialog = new InAppNotificationDialog(context, item, this, null, null);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        WindowManager.LayoutParams attribute = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        lp.copyFrom(attribute);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onDialogPrimaryButtonClicked() {
    }

    @Override
    public void onDialogSecondaryButtonClicked() {
    }

    @Override
    protected void onDisconnected() {
        getPresenter().logConnected();
    }

    @Override
    protected void onConnected() {
        getPresenter().logDisconnected();
    }
}

