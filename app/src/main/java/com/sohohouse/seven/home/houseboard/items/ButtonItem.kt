package com.sohohouse.seven.home.houseboard.items

import com.sohohouse.seven.base.DiffItem

open class ButtonItem(val text: String, val action: String? = null) : DiffItem {
    override val key: Any?
        get() = text
}

class DarkButtonItem(text: String, action: String? = null) : ButtonItem(text, action)

class SecondaryButtonItem(text: String, action: String? = null) : ButtonItem(text, action)