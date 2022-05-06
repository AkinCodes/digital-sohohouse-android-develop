package com.sohohouse.seven.common.design.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sohohouse.seven.base.DefaultDiffCallback
import com.sohohouse.seven.base.DiffItem

open class RendererDiffAdapter<T : DiffItem> : RendererAdapter<T>() {

    override fun submitItems(items: List<T>) {
        DiffUtil.calculateDiff(DefaultDiffCallback(this.items, items)).let {
            this.items = items.toMutableList()
            it.dispatchUpdatesTo(this)
        }
    }

    protected fun modifyList(function: (items: MutableList<T>) -> Unit) {
        submitItems(items.toMutableList().apply {
            function(this)
        })
    }
}