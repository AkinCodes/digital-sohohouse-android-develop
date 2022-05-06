package com.sohohouse.seven.housepay.checkdetail.open

sealed class OpenCheckEvent {

    data class ShowRichDialog(
        val title: String,
        val message: String,
        val confirmCta: String,
        val onConfirm: () -> Unit,
        val cancelCta: String = ""
    ) : OpenCheckEvent()

    object ShowU27DiscountDetailsModal : OpenCheckEvent()

    data class OpenCustomTipInput(
        val initialAmountCents: Int,
        val currencyCode: String,
        val leftToPayCents: Int
    ) : OpenCheckEvent()

    data class OpenHouseCreditInput(
        val initialAmountCents: Int,
        val maxAmountCents: Int,
        val leftToPayCents: Int,
        val currencyCode: String
    ) : OpenCheckEvent()

    object OpenPaymentMethod : OpenCheckEvent()
    object ShowHousePayTerms : OpenCheckEvent()

    object DismissSelf : OpenCheckEvent()

    data class OpenUrlInWebView(
        val url: String,
        val requiresAuth: Boolean
    ) : OpenCheckEvent()

    data class GoToReceipt(val checkId: String) : OpenCheckEvent()
}