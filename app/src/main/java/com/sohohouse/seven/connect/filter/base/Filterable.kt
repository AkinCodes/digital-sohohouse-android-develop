package com.sohohouse.seven.connect.filter.base

import androidx.annotation.StringRes
import com.sohohouse.seven.base.DiffItem

sealed class Filterable : DiffItem {
    abstract val id: String
    abstract val title: Any
    open val size: Int = 1
    abstract val type: Int

    companion object {
        const val SECTION_ITEM = 0
        const val FILTER_ITEM = 1
    }
}

data class SectionItem(
    override val id: String,
    @StringRes override val title: Int,
    val items: List<FilterItem>,
    var expanded: Boolean = false
) : Filterable() {
    override val key: String
        get() = id
    override val type: Int = SECTION_ITEM
    override val size: Int
        get() = if (expanded) items.size + 1 else 1

    companion object {
        const val SECTION_FAVOURITES = "favourites"
    }
}

data class FilterItem constructor(
    val filter: Filter,
    var selected: Boolean = false,
    val enabled: Boolean = true,
    val tag: String = "",
    val removable: Boolean = false
) : Filterable(), Comparable<FilterItem> {
    override val id: String
        get() = filter.id

    override val type: Int = FILTER_ITEM

    override val title: String
        get() = filter.title

    override val key: Any
        get() = "$id@$tag"

    override fun compareTo(other: FilterItem): Int {
        if (tag.compareTo(other.tag) == 0)
            return id.compareTo(other.id)
        return tag.compareTo(other.tag)
    }
}