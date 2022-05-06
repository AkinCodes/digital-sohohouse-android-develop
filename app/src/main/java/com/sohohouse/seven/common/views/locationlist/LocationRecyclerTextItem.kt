package com.sohohouse.seven.common.views.locationlist

data class LocationRecyclerTextItem(
    val headerStringRes: Int? = null,
    private val itemType: FilterItemType,
    val subtitleStringRes: Int? = null
) : LocationRecyclerBaseItem("", itemType) {
    override val key: Any?
        get() = headerStringRes
}