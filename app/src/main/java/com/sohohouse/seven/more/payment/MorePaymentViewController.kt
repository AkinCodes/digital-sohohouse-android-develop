package com.sohohouse.seven.more.payment

import com.sohohouse.seven.book.eventdetails.payment.BasePaymentController
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.payment.BasePaymentItem

interface MorePaymentViewController : BasePaymentController {
    fun onDataReady(itemList: List<BasePaymentItem>)
    fun paymentDeleted(id: String)
    fun defaultPaymentUpdated(card: Card)
}
