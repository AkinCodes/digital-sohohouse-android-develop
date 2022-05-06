package com.sohohouse.seven.common.views.locationlist

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.FilterHeaderExtraSpacingBinding
import com.sohohouse.seven.databinding.ListFilterHeaderItemBinding

class LocationComponentViewHolder(vBinding: ViewBinding) :
    RecyclerView.ViewHolder(vBinding.root) {

    val binding = when (vBinding) {
        is ListFilterHeaderItemBinding -> vBinding
        is FilterHeaderExtraSpacingBinding -> vBinding.headerItem
        else -> null
    }

    fun bind(model: LocationRecyclerTextItem) {
        binding?.apply {
            title.text = getString(model.headerStringRes ?: -1)
            title.setVisible(model.headerStringRes != null)

            subtitle.text = getString(model.subtitleStringRes ?: -1)
            subtitle.setVisible(model.subtitleStringRes != null)
        }
    }
}