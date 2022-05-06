package com.sohohouse.seven.base.load

import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.securewebview.SecureWebViewListener

interface WebViewLoadViewController : ViewController, SecureWebViewListener, LoadViewController {
    override fun onLoadingStarted() {
        showLoadingState()
    }

    override fun onLoadingFinished() {
        hideLoadingState()
    }
}