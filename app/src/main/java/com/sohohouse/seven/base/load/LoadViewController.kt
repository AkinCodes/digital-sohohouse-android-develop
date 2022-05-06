package com.sohohouse.seven.base.load

import androidx.annotation.CallSuper
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.LoadingView

interface LoadViewController : ViewController {
    val loadingView: LoadingView

    @CallSuper
    fun showLoadingState() {
        loadingView.toggleSpinner(true)
    }

    @CallSuper
    fun hideLoadingState() {
        loadingView.toggleSpinner(false)
    }
}