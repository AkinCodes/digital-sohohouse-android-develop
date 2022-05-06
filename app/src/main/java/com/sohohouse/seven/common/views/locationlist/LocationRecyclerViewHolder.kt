package com.sohohouse.seven.common.views.locationlist

import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ComponentFilterCategoryBtnBinding

class LocationRecyclerViewHolder(private val binding: ComponentFilterCategoryBtnBinding) :
    LocationViewHolder(binding.root) {

    fun bind(text: String, selected: Boolean, enabled: Boolean = true, showX: Boolean = false) =
        with(binding) {
            regionFilterPill.label = text
            regionFilterPill.isEnabled = enabled
            regionFilterPill.isActivated = selected
            regionFilterPill.setActionButton(if (showX) R.drawable.ic_close else 0)
        }

    override fun setUpOnClick(onClickListener: () -> (Unit)) {
        binding.regionFilterPill.setOnClickListener { onClickListener() }
    }

    override fun bind(childItem: LocationRecyclerChildItem) {
        bind(childItem.name, childItem.selected, childItem.enabled)
    }
}