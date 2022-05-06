package com.sohohouse.seven.housepay.tips

sealed class Tip {
    data class CustomAmount(
        val amountInCents: Int
    ) : Tip()

    data class Percentage(
        val percentage: Float
    ) : Tip()

    object NoTip : Tip()
}