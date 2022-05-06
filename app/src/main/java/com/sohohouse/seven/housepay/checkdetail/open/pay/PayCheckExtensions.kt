package com.sohohouse.seven.housepay.checkdetail.open.pay

import com.sohohouse.seven.housepay.payment.PaymentMethod

fun PaymentMethod.toPayCheckPaymentInfo(): PayCheckPaymentInfo {
    return when (this) {
        PaymentMethod.GooglePay -> TODO()
        is PaymentMethod.PaymentCard -> {
            PayCheckPaymentInfo.Card(card.id)
        }
    }
}