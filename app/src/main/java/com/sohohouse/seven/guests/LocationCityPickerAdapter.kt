package com.sohohouse.seven.guests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.views.locationlist.*
import com.sohohouse.seven.databinding.ItemLocationPickerHouseBinding
import com.sohohouse.seven.databinding.ListFilterHeaderItemBinding

class LocationCityPickerAdapter(
    items: List<LocationCityItem>,
    private val listener: LocationClickListener
) :
    BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, LocationRecyclerBaseItem>() {

    init {
        addAt(
            0,
            LocationRecyclerTextItem(null, FilterItemType.HEADER, R.string.booking_advance_message)
        )
        addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (FilterItemType.values()[viewType]) {
            FilterItemType.HEADER -> {
                LocationComponentViewHolder(
                    ListFilterHeaderItemBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            FilterItemType.CHILD -> {
                return LocationCityPickerViewHolder(
                    ItemLocationPickerHouseBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw IllegalStateException("Unknown FilterItemType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = currentItems[position]
        when (item.filterType) {
            FilterItemType.HEADER -> {
                holder as LocationComponentViewHolder
                val headerItem = item as LocationRecyclerTextItem
                holder.bind(headerItem)
            }
            FilterItemType.CHILD -> {
                holder as LocationCityPickerViewHolder
                holder.bind(currentItems[position] as LocationCityItem, listener)
                holder.onClick = { name ->
                    markSelectedCity(name)
                }
            }
            else -> {
                throw IllegalStateException("Unknown FilterItemType")
            }
        }

    }

    fun markSelectedCity(name: String) {
        submitList(currentItems.map {
            if (it is LocationCityItem) {
                if (name == it.name) it.copy(selected = true) else it.copy(
                    selected = false
                )
            } else {
                it
            }
        }, performDiffing = false)
    }

    override fun getItemViewType(position: Int): Int {
        return currentItems[position].filterType.ordinal
    }

}
