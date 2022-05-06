package com.sohohouse.seven.housepay.payment

import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.StringProviderImpl
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.payment.PaymentCardType

sealed class PaymentMethod : DiffItem {

    data class PaymentCard(
        val card: Card
    ) : PaymentMethod()

    object GooglePay : PaymentMethod()   //TODO

    fun getLabel(stringProvider: StringProvider): String {
        return when (this) {
            is PaymentCard -> {
                stringProvider.getString(
                    R.string.payment_card_number_label
                ).replaceBraces(card.lastFour)
            }
            GooglePay -> stringProvider.getString(R.string.label_google_pay)
        }
    }

    val icon: Int?
        get() = when (this) {
            is PaymentCard -> {
                card.cardType.asEnumOrDefault<PaymentCardType>(null)
                    ?.resDrawable
            }
            GooglePay -> null //TODO
        }

    override val key: Any?
        get() = when (this) {
            GooglePay -> javaClass
            is PaymentCard -> card.id
        }
}