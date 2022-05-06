package com.sohohouse.seven.housepay

import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.housepay.discounts.DiscountType
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.Discount
import com.sohohouse.seven.network.core.models.housepay.Payment

fun Discount.label(stringProvider: StringProvider): String {
    return type
        .asEnumOrDefault<DiscountType>()
        ?.localizedString(stringProvider)
        ?: type
}

fun Check.totalAmountDue(
    tip: Int = 0,
    credit: Int = 0,
): Int {
    return maxOf(
        totalCents + tip - discountsTotal - credit,
        0
    )
}

fun Check.amountPayableByCredit(tip: Int): Int {
    return maxOf(
        totalCents + tip - discountsTotal,
        0
    )
}

val Check.maxCustomTip: Int
    get() = subtotal - amountPaidByOthers

val Check.walkedOut: Boolean
    get() {
        return payments.any { it.walkout == true }
    }

val Check.isClosedOrPaid: Boolean
    get() = status in arrayOf(
        Check.STATUS_CLOSED,
        Check.STATUS_PAID
    )

val Check.totalPaid: Int
    get() {
        return payments.totalPaid + otherPayments.totalPaid
    }

private val List<Payment>.totalPaid: Int
    get() = filter { it.status == Payment.STATUS_PAID }
        .sumOf { it.cents }

