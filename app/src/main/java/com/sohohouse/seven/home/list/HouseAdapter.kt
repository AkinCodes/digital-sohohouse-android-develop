package com.sohohouse.seven.home.list

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.network.core.models.Event

abstract class HouseAdapter : BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, BaseAdapterItem>() {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).resLayout
    }

    fun updateHouseNotes(newHouseNotecurrentItems: List<BaseAdapterItem.HouseNoteItem>) {
        modifyList { items ->
            items.filter { it is BaseAdapterItem.HouseNoteItem }.let { currentHouseNoteItems ->
                val index = items.indexOf(currentHouseNoteItems.firstOrNull()).takeIf { it != -1 }
                    ?: return@modifyList
                items.removeAll(currentHouseNoteItems)
                items.addAll(index, newHouseNotecurrentItems)
            }
        }
    }

    fun updateEventItem(event: Event) {
        currentItems.forEach { item ->
            if (item is BaseAdapterItem.HappeningNowItem.Container)
                item.updateNestedItem(event)
        }
    }

}
