package com.sohohouse.seven.common.views.locationlist

data class LocationRecyclerChildItem(
    val id: String,
    override val name: String,
    var imageUrl: String,
    var selected: Boolean,
    val enabled: Boolean = true,
    val numberOfPosts: Int = 0,
    val isOpen: Boolean = false,
    val location: String = "",
    val showIcon: Boolean = true
) : LocationRecyclerBaseItem(name, FilterItemType.CHILD) {
    override val key: Any?
        get() = id
}