package com.sohohouse.seven.housepay.payment

import com.sohohouse.seven.base.DiffItem

sealed class ChoosePaymentMethodListItem : DiffItem {
    data class AddNewCard(
        val onClick: () -> Unit
    ) : ChoosePaymentMethodListItem() {
        override val key: Any?
            get() = javaClass
    }

    data class PaymentMethodListItem(
        val isSelected: Boolean,
        val paymentMethod: PaymentMethod,
        var isDefault: Boolean,
        val onClick: (PaymentMethodListItem) -> Unit
    ) : ChoosePaymentMethodListItem() {
        override val key: Any?
            get() = paymentMethod.key
    }
}