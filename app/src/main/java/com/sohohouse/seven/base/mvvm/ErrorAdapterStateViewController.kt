package com.sohohouse.seven.base.mvvm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent

interface ErrorAdapterStateViewController<Error, Empty> : ViewController {

    fun observeErrorState() {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun observe() {
                viewModel.presentAdapterErrorStateEvent.observe(lifecycleOwner, Observer {
                    presentAdapterEmptyState()
                    reloadAfterError()
                })
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun remove() {
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }

    override val viewModel: ErrorAdapterStateViewModel<Error, Empty>

    fun reloadAfterError()
    fun presentAdapterEmptyState()
}