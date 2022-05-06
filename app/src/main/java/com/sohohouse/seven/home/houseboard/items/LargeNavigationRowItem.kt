package com.sohohouse.seven.home.houseboard.items

import com.sohohouse.seven.base.DiffItem

class LargeNavigationRowItem(
    val text: String, val contentDescription: String? = text,
    val clickListener: (() -> Unit)? = null
) : DiffItem {
    override val key: Any?
        get() = text
}
