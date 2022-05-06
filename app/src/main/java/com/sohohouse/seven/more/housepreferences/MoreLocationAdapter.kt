package com.sohohouse.seven.more.housepreferences

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.locationlist.*
import com.sohohouse.seven.databinding.ListFilterDescriptionItemBinding

class MoreLocationAdapter(
    allHouses: List<LocationRecyclerParentItem>,
    favouriteHouses: List<LocationRecyclerChildItem>,
    selectionChangedListener: SelectedLocationListener
) : BaseLocationRecyclerAdapter(favouriteHouses, allHouses,

    object : LocationClickListener {
        override fun onLocationClicked(selectedLocations: List<String>) {
            selectionChangedListener.onSelectedLocationsChanged(selectedLocations)
        }

        override fun onRegionToggled(parentItem: LocationRecyclerParentItem) {

        }
    }
) {

    init {
        add(
            LocationRecyclerTextItem(
                R.string.more_house_settings_supporting,
                FilterItemType.DESCRIPTION
            )
        )

        add(
            LocationRecyclerTextItem(
                R.string.more_house_settings_my_label,
                FilterItemType.SUBHEADER
            )
        )
        addMyHouses()

        add(
            LocationRecyclerTextItem(
                R.string.more_house_settings_all_label,
                FilterItemType.SUBHEADER
            )
        )
        addAllHouses()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (FilterItemType.values()[viewType] == FilterItemType.DESCRIPTION) {
            return LocationTextRecyclerViewHolder(
                ListFilterDescriptionItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = currentItems[position]
        when (item.filterType) {
            FilterItemType.DESCRIPTION -> {
                val itemHolder = holder as LocationTextRecyclerViewHolder
                val descriptionItem = item as LocationRecyclerTextItem
                itemHolder.bind(descriptionItem)
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }

}