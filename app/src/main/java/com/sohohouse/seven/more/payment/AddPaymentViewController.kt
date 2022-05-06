package com.sohohouse.seven.more.payment

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.models.PaymentFormFields

interface AddPaymentViewController : ViewController, LoadViewController, ErrorDialogViewController,
    ErrorViewStateViewController {
    fun addCardSuccess(card: Card)
    fun initLayout(id: String, fields: List<PaymentFormFields>)
}
