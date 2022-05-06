package com.sohohouse.seven.network.sitecore.models

data class ContentColor(
    val id: String = "",
    val url: String = "",
    val fields: Fields = Fields(),
) {
    data class Fields(val hexCode: String = "")
}