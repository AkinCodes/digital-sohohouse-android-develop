package com.sohohouse.seven.book.eventdetails.payment

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController

interface BasePaymentController : ViewController, LoadViewController, ErrorDialogViewController,
    ErrorViewStateViewController {
    fun showEmptyView()
    fun showFailureView(errorCode: String = "")
}