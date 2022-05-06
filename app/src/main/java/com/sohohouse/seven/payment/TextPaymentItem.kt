package com.sohohouse.seven.payment

data class TextPaymentItem(val stringRes: Int) : BasePaymentItem(PaymentItemType.DESCRIPTION)