package com.sohohouse.seven.home.houseboard

import androidx.recyclerview.widget.DiffUtil
import ca.symbilityintersect.rendereradapter.RendererAdapter
import com.sohohouse.seven.base.DefaultDiffCallback
import com.sohohouse.seven.base.DiffItem

open class RendererDiffAdapter : RendererAdapter() {
    override fun setItems(list: List<Any>) {
        @Suppress("UNCHECKED_CAST")
        val typedList = list as? List<DiffItem>
            ?: throw IllegalArgumentException(
                "Items for RendererDiffAdapter must be a subtype of DiffItem"
            )
        DiffUtil.calculateDiff(DefaultDiffCallback(mItems as List<DiffItem>, typedList)).let {
            this.mItems = list
            it.dispatchUpdatesTo(this)
        }
    }

    protected fun modifyList(function: (items: ArrayList<Any>) -> Unit) {
        setItems(ArrayList(mItems).apply {
            function(this)
        })
    }
}