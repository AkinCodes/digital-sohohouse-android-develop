package com.sohohouse.seven.network.sitecore.models

import com.squareup.moshi.Json

data class Value<T>(@Json(name = "value") val value: T)