package com.sohohouse.seven.payment

import androidx.annotation.DrawableRes
import com.sohohouse.seven.R

enum class PaymentCardType(@DrawableRes var resDrawable: Int, var resAltString: Int) {
    VISA(R.drawable.ic_visa_card, R.string.alt_payment_methods_visa_credit_label),
    MASTERCARD(R.drawable.ic_master_card, R.string.alt_payment_methods_mastercard_label),
    AMEX(R.drawable.ic_default_card, R.string.alt_payment_methods_amex_label),
    OTHER(R.drawable.ic_default_card, R.string.alt_payment_methods_card_label);
}