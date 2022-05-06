package com.sohohouse.seven.network.core.models

import com.sohohouse.seven.network.core.common.extensions.asEnumOrDefault
import com.squareup.moshi.Json
import java.io.Serializable


data class DigitalInfo(
    @Json(name = "embed_url") var embedUrl: String? = null,
    @Json(name = "type") private var _type: String? = null,
) : Serializable {

    val type get() = _type?.asEnumOrDefault<Type>()

    enum class Type {
        VIDEO,
        LIVESTREAM
    }
}