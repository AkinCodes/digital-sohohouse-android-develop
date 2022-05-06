package com.sohohouse.seven.book.eventdetails.payment

import com.sohohouse.seven.payment.BasePaymentItem
import com.sohohouse.seven.payment.CardPaymentItem

interface BookingPaymentViewController : BasePaymentController {
    fun onDataReady(itemList: List<BasePaymentItem>)
    fun initLayout()
    fun setSelectedItem(cardId: String?)
    fun onMethodSelectedResult(model: CardPaymentItem)
}
