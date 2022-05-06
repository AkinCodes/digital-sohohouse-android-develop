package com.sohohouse.seven.common.views.locationlist

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.databinding.ItemLocationPickerHouseBinding

class LocationCityPickerViewHolder(private val binding: ItemLocationPickerHouseBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var onClick: ((String) -> Unit)? = null

    fun bind(item: LocationCityItem, listener: LocationClickListener) = with(binding) {
        houseName.text = item.name
        houseLocation.text = getString(item.region.stringRes)
        houseIcon.setGone()
        houseContentContainer.isActivated = item.selected
        houseSelectedCheckmark.isVisible = item.selected
        houseSelectedCheckmark.contentDescription =
            itemView.context.getString(R.string.content_desc_selected)
        root.clicks {
            listener.onLocationClicked(item.venueIdsInCity)
            onClick?.invoke(item.name)
        }
    }
}