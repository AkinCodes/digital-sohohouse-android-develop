package com.sohohouse.seven.network.sitecore.models.template

import com.squareup.moshi.Json

class EditorialStoryPage(
    @Json(name = "Id") override val id: String = "",
    @Json(name = "TemplateId") override val templateId: String = "",
    @Json(name = "TemplateName") override val templateName: String = "",
    @Json(name = "ParentId") override val parentId: String = "",
    @Json(name = "Created") override val created: String = "",
    @Json(name = "DisplayName") override val displayName: String = "",
    @Json(name = "IsLatestVersion") override val isLatestVersion: Boolean = true,
    @Json(name = "Language") override val language: String = "",
    @Json(name = "Name") override val name: String = "",
    @Json(name = "Path") override val path: String = "",
    @Json(name = "Updated") override val updated: String = "",
    @Json(name = "Url") override val url: String = "",
    @Json(name = "Version") override val version: Int = 0,
    @Json(name = "FieldValues") override val fieldValue: FieldValue = FieldValue(),
) : Template