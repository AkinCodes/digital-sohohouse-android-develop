package com.sohohouse.seven.base.error

import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.viewholders.ErrorStateListener

interface ErrorAdapterStateViewController : ViewController, ErrorStateListener {
    override fun onReloadButtonClicked() {
        presenter.presentAdapterEmptyState()
        presenter.reloadDataAfterError()
    }

    override fun getPresenter(): ErrorAdapterStatePresenter<*>
}