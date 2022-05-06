package com.sohohouse.seven.discover.houses.adapter

import com.sohohouse.seven.base.DiffItem

abstract class BaseHouseItem : DiffItem {

    abstract val type: Int

    companion object {
        const val HEADER_ID = "houses_header_item"

        const val TYPE_HEADER = 0
        const val TYPE_REGION_ITEM = 1
    }
}