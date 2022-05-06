package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "forms")
data class NewForm(
    @field:Json(name = "form_type") var formType: String = "",
    @field:Json(name = "venue") var venue: HasOne<Venue> = HasOne(),
) : Resource()


