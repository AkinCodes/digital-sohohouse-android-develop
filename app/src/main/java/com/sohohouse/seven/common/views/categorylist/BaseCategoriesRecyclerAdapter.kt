package com.sohohouse.seven.common.views.categorylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerViewHolder
import com.sohohouse.seven.databinding.ComponentFilterCategoryBtnBinding

enum class CategoryAdapterItemType {
    FILTER_HEADER,
    HEADER,
    DESCRIPTION,
    ITEM
}

interface CategorySelectedListener {
    fun onCategorySelected(selectedItems: List<String>)
}

abstract class BaseCategoriesRecyclerAdapter(
    private val selectedItems: MutableList<String>,
    private val listener: CategorySelectedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val itemList: MutableList<CategoryAdapterBaseItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocationRecyclerViewHolder(
            ComponentFilterCategoryBtnBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return itemList[position].type.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position] as CategoryDataItem
        val pillHolder = holder as LocationRecyclerViewHolder

        pillHolder.bind(item.name, item.isSelected)
        pillHolder.setUpOnClick {
            if (item.isSelected) {
                selectedItems.remove(item.id)
                item.isSelected = false
            } else {
                selectedItems.add(item.id)
                item.isSelected = true
            }
            notifyItemChanged(position)
            listener.onCategorySelected(selectedItems)
        }
    }

    fun clearSelection() {
        selectedItems.removeAll { true }
        for (item in itemList) {
            if (item.type == CategoryAdapterItemType.ITEM && (item as CategoryDataItem).isSelected) {
                item.isSelected = false
                notifyItemChanged(itemList.indexOf(item))
            }
        }
    }

    fun resetSelection(list: List<String>) {
        selectedItems.clear()
        selectedItems.addAll(list)
        for (item in itemList) {
            if (item.type == CategoryAdapterItemType.ITEM) {
                (item as CategoryDataItem).isSelected = list.contains(item.id)
                notifyItemChanged(itemList.indexOf(item))
            }
        }
    }
}