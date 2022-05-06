package com.sohohouse.seven.network.core.models.housepay

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "locations")
data class Location(
    @field:Json(name = "name") val name: String? = null,
    @field:Json(name = "code") val code: String? = null,
    @field:Json(name = "variable_tips") val variableTips: Boolean? = null,
    @field:Json(name = "variable_tips_values") val variableTipsValues: List<String>? = null,
    @field:Json(name = "variable_tips_default") val variableTipsDefault: String? = null,
    @field:Json(name = "display_net_total") val displayNetTotal: Boolean? = null,
    @field:Json(name = "service_charge_percentage") val serviceChargePercentage: Float? = null,
    @field:Json(name = "subtotal_split_food_beverage") val subtotalSplitFoodBeverage: Boolean? = null,
    @field:Json(name = "discount_priorities") val discountPriorities: Map<String, List<String>>? = null,
) : Resource(), Serializable
