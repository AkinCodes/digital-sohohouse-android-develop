package com.sohohouse.seven.common.design.textblock

import com.sohohouse.seven.base.DiffItem

interface TextBlock : DiffItem {

    val titleRes: Int

    override val key: Any
        get() = titleRes
}