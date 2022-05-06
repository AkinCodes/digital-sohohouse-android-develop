package com.sohohouse.seven.guests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.house.LocationPickerType
import com.sohohouse.seven.common.views.locationlist.*
import com.sohohouse.seven.databinding.ItemLocationPickerHeaderBinding
import com.sohohouse.seven.databinding.ItemLocationPickerHouseBinding
import com.sohohouse.seven.databinding.ItemLocationPickerRegionBinding
import com.sohohouse.seven.databinding.ListFilterHeaderItemBinding

class LocationPickerAdapter(
    favouriteHouses: List<LocationRecyclerChildItem>,
    allHouses: List<LocationRecyclerParentItem>,
    listener: LocationClickListener,
    type: LocationPickerType
) : BaseLocationRecyclerAdapter(favouriteHouses, allHouses, listener) {

    init {
        if (type == LocationPickerType.HOUSE) {
            addAt(
                0,
                LocationRecyclerTextItem(
                    null,
                    FilterItemType.SUBHEADER,
                    R.string.booking_advance_message
                )
            )
        }
        addMyHouses()
        addAllHouses()
    }

    override fun getChildViewHolder(parent: ViewGroup): LocationViewHolder {
        return LocationPickerHouseViewHolder(
            ItemLocationPickerHouseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LocationComponentViewHolder -> {
                val itemHolder = holder as LocationComponentViewHolder
                itemHolder.bind((currentItems[position] as LocationRecyclerTextItem))
            }
            is LocationPickerHeaderViewHolder -> {
                holder.bind(currentItems[position] as LocationRecyclerTextItem)
            }
            is LargeParentRecyclerViewHolder -> {
                val item = currentItems[position]
                val parentItem = item as LocationRecyclerParentItem
                holder.onBind(parentItem, showSelectedCount = !isSingleSelect)
                holder.setupOnClick(parentItem) {
                    val newList = LocationAdapterListFactory().expandCollapseList(
                        currentItems,
                        holder.adapterPosition
                    )
                    listener.onRegionToggled(parentItem)
                    submitList(newList)
                }
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    fun getSelectedItemPosition() = currentItems.indexOfFirst { locationItem ->
        (locationItem as? LocationRecyclerChildItem)?.selected == true
    }

    override fun getParentViewHolder(parent: ViewGroup): ParentViewHolder {
        return LargeParentRecyclerViewHolder(
            ItemLocationPickerRegionBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LocationPickerHeaderViewHolder(
            ItemLocationPickerHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getSubHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LocationComponentViewHolder(
            ListFilterHeaderItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override val isSingleSelect: Boolean
        get() = true
}

class LocationPickerHouseViewHolder(private val binding: ItemLocationPickerHouseBinding) :
    LocationViewHolder(binding.root) {
    override fun bind(item: LocationRecyclerChildItem) = with(binding) {
        houseName.text = item.name
        houseLocation.text = item.location
        houseIcon.isVisible = item.showIcon
        houseIcon.setImageFromUrl(item.imageUrl, placeholder = 0)
        houseContentContainer.isActivated = item.selected
        houseSelectedCheckmark.visibility = if (item.selected) View.VISIBLE else View.GONE
        houseSelectedCheckmark.contentDescription =
            itemView.context.getString(R.string.content_desc_selected)
    }


    override fun setUpOnClick(onClick: () -> Unit) {
        itemView.clicks { onClick() }
    }
}