package com.sohohouse.seven.base.error

import com.sohohouse.seven.base.mvpimplementation.ViewController

interface ErrorDialogViewController : ViewController {

    fun showGenericErrorDialog(vararg errorCodes: String = emptyArray()) {
        context?.let {
            ErrorDialogHelper.showErrorDialogByErrorCode(it, errorCodes)
        }
    }

    fun showNetworkErrorDialog() {
        context?.let {
            ErrorDialogHelper.showNetworkErrorDialog(it)
        }
    }
}