package com.sohohouse.seven.common.views.toolbar

import com.sohohouse.seven.base.DiffItem

data class Banner constructor(
    val id: String,
    val title: String,
    val subtitle: String,
    val cta: String,
    val listener: (() -> Unit)? = null,
    val checkId: String? = null,
    val isSwipeable: Boolean = false,
) : DiffItem {
    override val key: Any?
        get() = id
}