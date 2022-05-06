package com.sohohouse.seven.common.views.locationlist

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ListFilterDescriptionItemBinding

class LocationTextRecyclerViewHolder(private val binding: ListFilterDescriptionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: LocationRecyclerTextItem) = with(binding.text) {
        if (model.headerStringRes != null) {
            setVisible()
            text = getString(model.headerStringRes)
        } else {
            setGone()
        }
    }
}