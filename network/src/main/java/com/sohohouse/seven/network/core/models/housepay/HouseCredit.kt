package com.sohohouse.seven.network.core.models.housepay

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "soho_house_credits")
data class HouseCredit(
    @field:Json(name = "balance") private val _balance: Int? = null,
    @field:Json(name = "currency") private val _currencyCode: String? = null,
) : Resource() {
    val balance get() = _balance ?: 0
    val currencyCode get() = _currencyCode ?: ""
}
