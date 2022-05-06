/*
  Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
  Unauthorized copying of this file, via any medium is strictly prohibited
  This content is proprietary and confidential
 */

package com.sohohouse.seven.base;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sohohouse.seven.App;
import com.sohohouse.seven.base.mvpimplementation.Presenter;
import com.sohohouse.seven.base.mvpimplementation.ViewController;
import com.sohohouse.seven.common.analytics.AnalyticsManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import timber.log.Timber;

/**
 * Base presenter for the application.
 */
public abstract class BasePresenter<V extends ViewController> implements Presenter<V> {
    private static final long DEFAULT_THROTTLE_MS = 500;
    private final List<OnAttachQueueTask<V>> mQueuedTask = Collections.synchronizedList(new ArrayList<>());
    private boolean mIsFirstAttached = true;
    private boolean mIsRecreated = false;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private WeakReference<V> mRef = new WeakReference<>(null);
    private final AtomicLong lastExecutedIfTimestamp = new AtomicLong(-1);


    /**
     * Attaches view. The view is only attached if it was never attached.
     *
     * @param view the view that being attached
     */
    @Override
    @MainThread
    public final void attach(V view) {
        if (view != null && mRef.get() != view) {
            boolean isFirstAttach = mIsFirstAttached;
            boolean isRecreated = mIsRecreated;
            Timber.d("+attach() called with: view = [" + view + "], isFirstAttach = [" + isFirstAttach + "]" + ", isRecreated=" + isRecreated);
            mRef = new WeakReference<>(view);
            onAttachInternal(view, isFirstAttach, isRecreated);
            List<OnAttachQueueTask<V>> queueTasks = new ArrayList<>(mQueuedTask);
            mQueuedTask.clear();
            int size = queueTasks.size();
            for (int i = 0; i < size; i++) {
                queueTasks.get(i).onAttach(view, isFirstAttach, isRecreated);
            }
        } else {
            Timber.w("view is already attached");
        }
    }

    /**
     * See {@link #onAttach(ViewController, boolean, boolean)}
     *
     * @param view          the view being attached
     * @param isFirstAttach if it is the first time view is being attached
     * @param isRecreated   if the view is recreated
     */
    private void onAttachInternal(V view, boolean isFirstAttach, boolean isRecreated) {
        onAttach(view, isFirstAttach, isRecreated);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @MainThread
    public final void onRecreated() {
        mRef.clear();
        //tasks are cleared because they are no longer relevant
        mQueuedTask.clear();
        mIsFirstAttached = true;
        mIsRecreated = true;
    }


    /**
     * Callback when presenter is attached to a {@link ViewController}.
     * <p>
     * Note that if {@code isFirstAttach} or {@code isRecreated} are true then they are only switched
     * to {@code false} after the presenter has been {@link #detach()} .
     *
     * @param view          the view currently being attached and this is the same view from {@link #getView()}
     * @param isFirstAttach return {@code true} when the view is attached for the first time. This includes
     *                      when the view is recreated due to orientation/configuration change and attached
     *                      for the first time.
     * @param isRecreated   return {@code true} when the view is recreated due to orientation/configuration
     *                      change. Otherwise {@code false}. See {@link #onRecreated()}
     */
    @SuppressWarnings({"EmptyMethod", "WeakerAccess", "unused"})
    @MainThread
    protected void onAttach(@NonNull V view, boolean isFirstAttach, boolean isRecreated) {

    }

    @Override
    public V getView() {
        WeakReference<V> ref = mRef;
        return ref != null ? ref.get() : null;
    }

    /**
     * Detaches the view from the window.
     * If detach was previously called then this will not call any methods.
     */
    @Override
    @MainThread
    public final void detach() {
        WeakReference<V> ref = mRef;
        V view;
        if (ref != null && (view = ref.get()) != null) {
            Timber.d("-detach() called with: view = [" + ref.get() + "]");
            onDetachInternal(view);
            ref.clear();
        } else {
            Timber.w("view is already detached");
        }
        mIsFirstAttached = false;
        mIsRecreated = false;
    }

    /**
     * See {@link #onDetach(ViewController)}
     *
     * @param view the view that is being detached
     */
    private void onDetachInternal(V view) {
        onDetach(view);
    }

    /**
     * This method is called when {@link ViewController} is being detached from the presenter which
     * is during {@link Activity#onPause()} or {@link Fragment#onPause()}. The {@link #getView()}
     * will become null {@code null} after {@link #detach()} is finished.
     *
     * @param view the view being detached
     */
    @SuppressWarnings({"EmptyMethod", "WeakerAccess", "unused"})
    @MainThread
    protected void onDetach(V view) {

    }


    /**
     * This method is called when the presenter is about to be really destroyed. This is not the same callback as {@link Activity#onDestroy()} or {@link Fragment#onDestroy()}
     */
    @Override
    public void onDestroy() {

    }

    private void addTask(OnAttachQueueTask<V> task) {
        mQueuedTask.add(task);
    }

    @Override
    public void executeWhenAvailable(final OnAttachQueueTask<V> task) {
        runOnMainThread(() -> checkAndExecuteWhenAvailable(task));
    }

    @Override
    public void executeWhenAvailable(final OnAttachQueueTask<V> task, long delayMs) {
        mMainHandler.postDelayed(() -> executeWhenAvailable(task), delayMs);
    }

    private void checkAndExecuteWhenAvailable(final OnAttachQueueTask<V> task) {
        V view = getView();
        if (view != null) {
            task.onAttach(view, mIsFirstAttached, mIsRecreated);
        } else {
            addTask(task);
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected final void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            mMainHandler.post(runnable);
        }
    }

    @Override
    public void executeIfAvailableThrottle(final Task<V> task) {
        executeIfAvailableThrottle(task, getThrottleMs());
    }

    @Override
    public void executeIfAvailableThrottle(Task<V> task, long throttleMs) {
        runOnMainThread(() -> checkAndExecuteIfAvailable(task, throttleMs));
    }

    @Override
    public void executeIfAvailable(Task<V> task) {
        executeIfAvailableThrottle(task, 0);
    }

    private void checkAndExecuteIfAvailable(Task<V> task, long throttleMs) {
        long currentTimestampMs = System.currentTimeMillis();
        if (currentTimestampMs - lastExecutedIfTimestamp.get() >= throttleMs) {
            V view = getView();
            if (view != null) {
                lastExecutedIfTimestamp.set(currentTimestampMs);
                task.execute(view);
            } else {
                Timber.d("view not available");
            }
        } else {
            Timber.d("task ignored");
        }
    }

    @Override
    public long getThrottleMs() {
        return DEFAULT_THROTTLE_MS;
    }

    @Override
    public boolean isRetained() {
        return false;
    }

    private boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }


    void logConnected() {
        App.getAppComponent().getAnalyticsManager().logEventAction(AnalyticsManager.Action.BackOnlineSnackBarAppeared, null);
    }

    void logDisconnected() {
        App.getAppComponent().getAnalyticsManager().logEventAction(AnalyticsManager.Action.OfflineSnackBarAppeared, null);
    }

    void setScreenName(String activityName, String screenName) {
        App.getAppComponent().getAnalyticsManager().setScreenName(activityName, screenName);
    }
}