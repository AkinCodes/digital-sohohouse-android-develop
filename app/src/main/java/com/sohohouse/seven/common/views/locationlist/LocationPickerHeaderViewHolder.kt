package com.sohohouse.seven.common.views.locationlist

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ItemLocationPickerHeaderBinding

class LocationPickerHeaderViewHolder(private val binding: ItemLocationPickerHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: LocationRecyclerTextItem) = with(binding) {
        text.text = getString(model.headerStringRes ?: -1)
        text.setVisible(model.headerStringRes != null)
    }

}