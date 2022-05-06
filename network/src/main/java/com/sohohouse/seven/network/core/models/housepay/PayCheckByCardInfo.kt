package com.sohohouse.seven.network.core.models.housepay

import com.google.gson.annotations.SerializedName

data class PayCheckByCardInfo(@SerializedName("data") val data: Data? = null) {
    data class Data(
        @SerializedName("check_id") val checkId: String? = null,
        @SerializedName("amount_cents") val amountCents: Int? = null,
        @SerializedName("card_id") val cardId: String? = null,
        @SerializedName("tip_amount_cents") val tipAmountCents: Int? = null,
        @SerializedName("credit_cents") val creditCents: Int? = null,
    )
}