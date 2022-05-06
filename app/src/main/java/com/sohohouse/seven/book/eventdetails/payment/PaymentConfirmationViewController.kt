package com.sohohouse.seven.book.eventdetails.payment

import com.sohohouse.seven.payment.CardPaymentItem

interface PaymentConfirmationViewController : BasePaymentController {
    fun onDataReady(card: CardPaymentItem)
    fun bookSuccess(tickets: Int, state: String)
    fun launchCardListActivity(id: String)
    fun openActiveMembershipInfo()
    fun showPsd2Confirmation(transactionAuthHtml: String)
}