package com.sohohouse.seven.network.sitecore.models.template

import com.squareup.moshi.Json

interface Template {
    @Json(name = "Id")
    val id: String
    @Json(name = "ParentId")
    val parentId: String
    @Json(name = "Created")
    val created: String
    @Json(name = "DisplayName")
    val displayName: String
    @Json(name = "TemplateId")
    val templateId: String
    @Json(name = "TemplateName")
    val templateName: String
    @Json(name = "IsLatestVersion")
    val isLatestVersion: Boolean
    @Json(name = "Language")
    val language: String
    @Json(name = "Name")
    val name: String
    @Json(name = "Path")
    val path: String
    @Json(name = "Updated")
    val updated: String
    @Json(name = "Url")
    val url: String
    @Json(name = "Version")
    val version: Int
    @Json(name = "FieldValues")
    val fieldValue: FieldValue

    companion object {
        const val TEMPLATE_NAME = "TemplateName"

        // consider to use enum or shield class if more templates are added
        const val TEMPLATE_HOUSE_NOTE_PAGE = "House Note Page"
        const val TEMPLATE_HOUSE_NOTE_PAGE_ID = "E03B0920-01ED-45B4-ABA5-36BA5ED72EEC"
        const val TEMPLATE_EDITORIAL_STORY_PAGE = "Editorial Story Page"
        const val TEMPLATE_EDITORIAL_STORY_PAGE_ID = "689BFB3B-5066-48E1-94EC-4FE62815005D"
    }
}