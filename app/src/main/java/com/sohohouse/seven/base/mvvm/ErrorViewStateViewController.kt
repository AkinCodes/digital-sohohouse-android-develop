package com.sohohouse.seven.base.mvvm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.annotation.CallSuper
import android.view.View
import com.sohohouse.seven.common.views.ReloadableErrorStateView

interface ErrorViewStateViewController : ViewController {

    fun observeErrorViewEvents() {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun observe() {
                viewModel.errorViewState.observe(lifecycleOwner, Observer {
                    showReloadableErrorState()
                })
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun remove() {
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }

    @CallSuper
    fun showReloadableErrorState() {
        getErrorStateView().visibility = View.VISIBLE
        getErrorStateView().reloadClicks {
            hideReloadableErrorState()
            viewModel.reloadDataAfterError()
        }
    }

    @CallSuper
    fun hideReloadableErrorState() {
        getErrorStateView().visibility = View.GONE
    }

    fun getErrorStateView(): ReloadableErrorStateView

    override val viewModel: ErrorViewStateViewModel

}