package com.sohohouse.seven.common.views.locationlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.databinding.ComponentFilterCategoryBtnBinding
import com.sohohouse.seven.databinding.FilterSecondHeaderBinding
import com.sohohouse.seven.databinding.ListFilterHeaderItemBinding
import com.sohohouse.seven.databinding.ListFilterParentItemBinding
import com.sohohouse.seven.houseboard.filter.recycler.FilterSecondHeaderViewHolder

enum class FilterItemType {
    PARENT,
    CHILD,
    HEADER,
    SUBHEADER,
    DESCRIPTION
}

interface LocationClickListener {
    fun onLocationClicked(selectedLocations: List<String>)
    fun onRegionToggled(parentItem: LocationRecyclerParentItem) {}
}

abstract class BaseLocationRecyclerAdapter(
    protected val favouriteHouses: List<LocationRecyclerChildItem>,
    protected val allHouses: List<LocationRecyclerParentItem>,
    protected val listener: LocationClickListener
) : BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, LocationRecyclerBaseItem>() {

    var selectedItemList: MutableList<String> = mutableListOf()

    protected open val isSingleSelect = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (FilterItemType.values()[viewType]) {
            FilterItemType.PARENT -> {
                getParentViewHolder(parent)
            }
            FilterItemType.CHILD -> {
                getChildViewHolder(parent)
            }
            FilterItemType.HEADER -> {
                getHeaderViewHolder(parent)
            }
            FilterItemType.SUBHEADER -> {
                getSubHeaderViewHolder(parent)
            }
            else -> {
                throw IllegalStateException("Unknown FilterItemType")
            }
        }
    }

    protected open fun getHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        LocationComponentViewHolder(
            ListFilterHeaderItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    protected open fun getParentViewHolder(parent: ViewGroup): ParentViewHolder =
        ParentRecyclerViewHolder(
            ListFilterParentItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    protected open fun getChildViewHolder(parent: ViewGroup): LocationViewHolder =
        LocationRecyclerViewHolder(
            ComponentFilterCategoryBtnBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    protected open fun getSubHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        FilterSecondHeaderViewHolder(
            FilterSecondHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = currentItems[position]
        when (item.filterType) {
            FilterItemType.PARENT -> {
                val itemHolder = holder as ParentViewHolder
                val parentItem = item as LocationRecyclerParentItem
                itemHolder.onBind(parentItem, showSelectedCount = !isSingleSelect)
                itemHolder.setupOnClick(parentItem) {
                    val newList = LocationAdapterListFactory().expandCollapseList(
                        currentItems,
                        itemHolder.adapterPosition
                    )
                    listener.onRegionToggled(parentItem)
                    submitList(newList)
                }
            }
            FilterItemType.CHILD -> {
                val childItem = item as LocationRecyclerChildItem
                val pillHolder = holder as LocationViewHolder

                pillHolder.bind(childItem)
                pillHolder.setUpOnClick {
                    onLocationClick(childItem)
                }
            }
            FilterItemType.HEADER -> {
                val itemHolder = holder as LocationComponentViewHolder
                val headerItem = item as LocationRecyclerTextItem
                itemHolder.bind(headerItem)
            }
            FilterItemType.SUBHEADER -> {
                val itemHolder = holder as FilterSecondHeaderViewHolder
                itemHolder.bind((item as LocationRecyclerTextItem).headerStringRes)
            }
            else -> {
                throw IllegalStateException("Unknown FilterItemType")
            }
        }
    }

    protected open fun onLocationClick(childItem: LocationRecyclerChildItem) {
        val newList = if (selectedItemList.contains(childItem.id)) {
            if (isSingleSelect) return  //cannot remove selection in single select mode
            selectedItemList.remove(childItem.id)
            LocationAdapterListFactory().syncSelection(
                currentItems.toMutableList(),
                childItem.id,
                false
            )
        } else {
            if (isSingleSelect) {
                selectedItemList.clear()
            }
            selectedItemList.add(childItem.id)
            if (isSingleSelect) {
                LocationAdapterListFactory().syncSelectionSingle(
                    currentItems.toMutableList(),
                    childItem.id
                )
            } else {
                LocationAdapterListFactory().syncSelection(
                    currentItems.toMutableList(),
                    childItem.id,
                    selected = true
                )
            }
        }
        submitList(newList, performDiffing = false)
        listener.onLocationClicked(selectedItemList)
    }

    override fun getItemViewType(position: Int): Int {
        return currentItems[position].filterType.ordinal
    }

    fun resetSelection(selectedLocations: List<String>) {
        for (item in selectedItemList) {
            val newList = LocationAdapterListFactory().syncSelection(
                currentItems.toMutableList(),
                item,
                false
            )
            submitList(newList)
        }
        for (item in selectedLocations) {
            val newList =
                LocationAdapterListFactory().syncSelection(currentItems.toMutableList(), item, true)
            submitList(newList, performDiffing = false)
        }
        selectedItemList = selectedLocations.toMutableList()
    }

    fun addMyHouses() {
        for (item in favouriteHouses) {
            add(item)
            if (item.selected) {
                selectedItemList.add(item.id)
            }
        }
    }

    fun addAllHouses() {
        for (item in allHouses) {
            add(item)
            var selectedChildCount = 0
            for (childItem in item.childList) {
                if (item.expanded) {
                    add(childItem)
                }
                if (childItem.selected) {
                    if (!selectedItemList.contains(childItem.id)) {
                        selectedItemList.add(childItem.id)
                    }
                    selectedChildCount++
                }
            }
        }
    }
}

abstract class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: LocationRecyclerChildItem)
    abstract fun setUpOnClick(onClick: () -> Unit)
}