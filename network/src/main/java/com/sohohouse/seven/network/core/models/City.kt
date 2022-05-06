package com.sohohouse.seven.network.core.models

import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "perks_cities")
data class City(var name: String = "") : Resource() {
    var region: String = ""
}