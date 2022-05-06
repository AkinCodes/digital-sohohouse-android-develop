package com.sohohouse.seven.common.views.categorylist

class CategoryDataItem(
    val id: String,
    val name: String,
    val imageUrl: String?,
    var isSelected: Boolean
) : CategoryAdapterBaseItem(CategoryAdapterItemType.ITEM)