package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "categories")
data class ContentCategory(
    @field:Json(name = "category_name") var categoryName: String = "",
    @field:Json(name = "category_icon") var categoryIcon: CategoryIcon? = null,
) : Resource(), Serializable

data class CategoryIcon(
    @field:Json(name = "png") var png: String = "",
    @field:Json(name = "svg") var svg: String = "",
) : Serializable


