package com.sohohouse.seven.network.sitecore.models

data class SitecoreRoute(
    val name: String = "",
    val displayName: String = "",
    val fields: Fields = Fields(),
    val templateName: String = "",
    val databaseName: String = "",
    val deviceID: String = "",
    val itemID: String = "",
    val itemLanguage: String = "",
    val itemVersion: String = "",
    val layoutID: String = "",
    val templateId: String = "",
    val placeholders: SitecorePlaceholders,
) {
    data class Fields(
        val subHeader: Value<String> = Value(""),
        val author: Value<String> = Value(""),
        val region: Value<String> = Value(""),
        val venues: List<SitecoreRelation> = emptyList(),
        val footer: Value<String> = Value(""),
        val publishDate: Value<String> = Value(""),
        val title: Value<String> = Value(""),
        val categories: List<SitecoreCategory> = emptyList(),
        val hidden: Value<Boolean> = Value(false),
        val credits: Value<String> = Value(""),
        val shortDescription: Value<String> = Value(""),
    )
}