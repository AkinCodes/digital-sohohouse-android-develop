package com.sohohouse.seven.perks.filter

import com.sohohouse.seven.common.views.categorylist.CategoryAdapterBaseItem
import com.sohohouse.seven.common.views.categorylist.CategoryAdapterItemType

class PerkFilterDataItem(
    val id: String,
    val name: String,
    val imageUrl: String?,
    var isSelected: Boolean,
    var isAvailable: Boolean
) : CategoryAdapterBaseItem(CategoryAdapterItemType.ITEM)