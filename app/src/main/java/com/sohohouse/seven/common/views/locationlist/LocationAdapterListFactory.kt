package com.sohohouse.seven.common.views.locationlist

import java.util.ArrayList

class LocationAdapterListFactory {
    fun expandCollapseList(
        itemList: List<LocationRecyclerBaseItem>,
        position: Int
    ): MutableList<LocationRecyclerBaseItem> {
        val newList = ArrayList(itemList)

        val parentItem = newList[position] as LocationRecyclerParentItem

        if (parentItem.expanded) {
            val index = position + 1
            while (index < newList.size && newList[index].filterType == FilterItemType.CHILD) {
                newList.removeAt(index)
            }
        } else {
            var index = position + 1
            for (item in parentItem.childList) {
                newList.add(index, item.copy())
                index += 1
            }
        }

        parentItem.expanded = !parentItem.expanded
        return newList
    }

    fun syncSelection(
        itemList: MutableList<LocationRecyclerBaseItem>,
        id: String,
        selected: Boolean
    ): MutableList<LocationRecyclerBaseItem> {
        val newList = mutableListOf<LocationRecyclerBaseItem>()

        for (item in itemList) {
            if (item.filterType == FilterItemType.CHILD &&
                (item as LocationRecyclerChildItem).id == id
            ) {
                val modifiedItem = item.copy()
                modifiedItem.selected = selected
                newList.add(modifiedItem)
            } else if (item.filterType == FilterItemType.PARENT) {
                val parentItem = item as LocationRecyclerParentItem


                val selectedChild = parentItem.childList.filter { it.id == id }
                if (selectedChild.isNotEmpty()) {
                    val childItem = selectedChild[0]
                    childItem.selected = selected
                    val modifiedParentItem = parentItem.copy()
                    newList.add(modifiedParentItem)
                } else {
                    newList.add(parentItem)
                }
            } else {
                newList.add(item)
            }
        }
        return newList
    }

    fun syncSelectionSingle(
        itemList: MutableList<LocationRecyclerBaseItem>,
        id: String
    ): MutableList<LocationRecyclerBaseItem> {
        val newList = mutableListOf<LocationRecyclerBaseItem>()

        for (item in itemList) {
            if (item.filterType == FilterItemType.CHILD &&
                (item is LocationRecyclerChildItem)
            ) {

                val modifiedItem = item.copy()
                modifiedItem.selected = item.id == id
                newList.add(modifiedItem)
            } else if (item.filterType == FilterItemType.PARENT) {
                val parentItem = item as LocationRecyclerParentItem

                if (parentItem.childList.isNotEmpty()) {
                    val modifiedParentItem = parentItem.copy()
                    modifiedParentItem.childList.forEach { child ->
                        child.selected = child.id == id
                    }
                    newList.add(modifiedParentItem)
                } else {
                    newList.add(parentItem)
                }
            } else {
                newList.add(item)
            }
        }
        return newList
    }
}