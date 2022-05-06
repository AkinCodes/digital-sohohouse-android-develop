package com.sohohouse.seven.network.sitecore.models

import com.squareup.moshi.Json

data class SitecoreCTALink(
    val href: String = "",
    val linktype: String = "",
    val text: String = "",
    @Json(name = "querystring") val queryString: String = "",
    val target: String = "",
    val id: String = "",
    @Json(name = "data-hidden-text") val hiddenText: String = "",
    val anchor: String = "",
    val url: String = "",
    val title: String = "",
    val `class`: String = "",
)