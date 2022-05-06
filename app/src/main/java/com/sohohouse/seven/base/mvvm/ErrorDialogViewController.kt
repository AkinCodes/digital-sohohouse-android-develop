package com.sohohouse.seven.base.mvvm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import com.sohohouse.seven.base.error.ErrorDialogHelper

interface ErrorDialogViewController : ViewController {
    fun observeErrorDialogEvents() {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun observe() {
                viewModel.showGenericErrorDialogEvent.observe(lifecycleOwner, Observer { msg ->
                    _context?.let { context ->
                        ErrorDialogHelper.showErrorDialogByErrorCode(context, msg ?: emptyArray())
                    }
                })
                viewModel.showNetworkErrorDialogEvent.observe(lifecycleOwner, Observer {
                    _context?.let { context -> ErrorDialogHelper.showNetworkErrorDialog(context) }
                })
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun remove() {
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }

    override val viewModel: ErrorDialogViewModel
}