package com.sohohouse.seven.profile.view.model

import com.sohohouse.seven.base.DiffItem

class MessageItem(val message: String) : DiffItem {

    override val key: Any
        get() = message

}