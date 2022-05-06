package com.sohohouse.seven.home.houseboard.items

import com.sohohouse.seven.base.DiffItem

class NavigationRowItem(val text: String) : DiffItem {
    override val key: Any?
        get() = text
}