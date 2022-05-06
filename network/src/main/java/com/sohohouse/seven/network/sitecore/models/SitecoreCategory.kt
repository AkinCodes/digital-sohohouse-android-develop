package com.sohohouse.seven.network.sitecore.models

data class SitecoreCategory(
    val id: String = "",
    val fields: Fields = Fields(),
) {
    data class Fields(
        val title: Value<String> = Value(""),
        val icon: Icon = Icon(),
    )

    data class Icon(
        val url: String = "",
        val id: String = "",
        val alt: String? = "",
    )
}