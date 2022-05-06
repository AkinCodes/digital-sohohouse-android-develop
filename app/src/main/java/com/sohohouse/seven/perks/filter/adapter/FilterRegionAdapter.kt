package com.sohohouse.seven.perks.filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.views.categorylist.*
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerViewHolder
import com.sohohouse.seven.databinding.ComponentFilterCategoryBtnBinding
import com.sohohouse.seven.databinding.ListFilterHeaderItemBinding
import com.sohohouse.seven.perks.filter.PerkFilterDataItem

class FilterRegionAdapter(
    private val selectedItems: MutableList<String>,
    private val allDataItems: MutableList<CategoryAdapterBaseItem>,
    private val listener: CategorySelectedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CategoryAdapterItemType.FILTER_HEADER.ordinal -> {
                CategoryTextRecyclerViewHolder(
                    ListFilterHeaderItemBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            CategoryAdapterItemType.ITEM.ordinal -> {
                LocationRecyclerViewHolder(
                    ComponentFilterCategoryBtnBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            else -> throw IllegalArgumentException("Unexpected ViewHolder : $viewType")
        }
    }

    override fun getItemCount(): Int {
        return allDataItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return allDataItems[position].type.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = allDataItems[position]
        return when (getItemViewType(position)) {
            CategoryAdapterItemType.FILTER_HEADER.ordinal -> {
                (holder as CategoryTextRecyclerViewHolder).bind(item as CategoryAdapterTextItem)
            }
            CategoryAdapterItemType.ITEM.ordinal -> {
                val perkItem = item as PerkFilterDataItem

                val childViewHolder = (holder as LocationRecyclerViewHolder)
                childViewHolder.bind(perkItem.name, perkItem.isSelected, perkItem.isAvailable)
                childViewHolder.setUpOnClick {
                    if (perkItem.isSelected) {
                        selectedItems.remove(perkItem.id)
                        perkItem.isSelected = false
                    } else {
                        selectedItems.add(perkItem.id)
                        perkItem.isSelected = true
                    }

                    notifyItemChanged(position)
                    listener.onCategorySelected(selectedItems)
                }
            }
            else -> throw IllegalArgumentException(
                "Unexpected ViewHolder : ${
                    getItemViewType(
                        position
                    )
                }"
            )
        }
    }

    fun resetSelection(list: List<String>) {
        selectedItems.clear()
        selectedItems.addAll(list)
        for (item in allDataItems) {
            item as CategoryDataItem
            if (item.type == CategoryAdapterItemType.ITEM) {
                item.isSelected = list.contains(item.id)
                notifyItemChanged(allDataItems.indexOf(item))
            }
        }
    }
}