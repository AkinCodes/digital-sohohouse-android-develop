package com.sohohouse.seven.book.adapter.model

import com.sohohouse.seven.base.DiffItem

data class DividerBookAdapterItem(
    val titleRes: Int?,
    val hasExtraBottomPadding: Boolean = false,
    val hasExtraTopPadding: Boolean = true
) : DiffItem {
    override val key: Any
        get() = this
}