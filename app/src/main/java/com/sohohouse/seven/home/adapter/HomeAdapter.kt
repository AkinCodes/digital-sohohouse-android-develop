package com.sohohouse.seven.home.adapter

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.views.toolbar.Banner
import com.sohohouse.seven.home.houseboard.SwipeCallback
import com.sohohouse.seven.home.houseboard.items.NotificationItem
import com.sohohouse.seven.network.core.models.Event

class HomeAdapter : RendererDiffAdapter<DiffItem>(), SwipeCallback.SwipeHelper<Banner> {

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
        items.forEach { item ->
            if (item is BaseAdapterItem.HappeningNowItem.Container)
                item.updateNestedItem(event)
        }
    }

    override fun canSwipe(position: Int): Boolean {
        return getItem(position)?.isSwipeable == true
    }

    override fun getItem(position: Int): Banner? {
        return try {
            items[position].takeIf { it is Banner } as? Banner
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

}