package com.sohohouse.seven.book.adapter.model

import com.sohohouse.seven.base.DiffItem

data class FilterStateHeaderAdapterItem(val titleRes: Int, val isFiltered: Boolean = false) :
    DiffItem {
    override val key: Any
        get() = this
}