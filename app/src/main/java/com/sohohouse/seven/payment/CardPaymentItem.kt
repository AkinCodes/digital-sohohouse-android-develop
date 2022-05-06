package com.sohohouse.seven.payment

data class CardPaymentItem(
    val id: String,
    val paymentCardType: PaymentCardType,
    val lastFour: String,
    var isDefault: Boolean,
    val status: PaymentCardStatus,
    var isSelected: Boolean = false
) : BasePaymentItem(PaymentItemType.CARD) {
    override val key: Any
        get() = id
}