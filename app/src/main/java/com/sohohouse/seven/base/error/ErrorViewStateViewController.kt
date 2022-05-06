package com.sohohouse.seven.base.error

import androidx.annotation.CallSuper
import android.view.View
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.ReloadableErrorStateView

interface ErrorViewStateViewController : ViewController {

    @CallSuper
    fun showReloadableErrorState() {
        getErrorStateView().visibility = View.VISIBLE
        getErrorStateView().reloadClicks {
            hideReloadableErrorState()
            presenter.reloadDataAfterError()
        }
    }

    @CallSuper
    fun hideReloadableErrorState() {
        getErrorStateView().visibility = View.GONE
    }

    fun getErrorStateView(): ReloadableErrorStateView

    override fun getPresenter(): ErrorViewStatePresenter<*>
}