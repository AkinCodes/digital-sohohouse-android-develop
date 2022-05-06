package com.sohohouse.seven.home.houseboard.items

import com.sohohouse.seven.base.DiffItem

class HoursDisplayItem constructor(
    val venueName: String,
    val topText: String,
    val bottomText: String,
    val buttonText: String
) : DiffItem {
    override val key: Any?
        get() = HoursDisplayItem::class
}