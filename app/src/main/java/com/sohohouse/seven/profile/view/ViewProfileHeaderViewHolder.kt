package com.sohohouse.seven.profile.view

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.views.CircleOutlineProvider
import com.sohohouse.seven.databinding.ItemViewProfileHeaderBinding

class ViewProfileHeaderViewHolder(private val binding: ItemViewProfileHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.profileAvatar.outlineProvider = CircleOutlineProvider()
        binding.profileAvatar.clipToOutline = true
    }

    fun bind(item: ViewProfileAdapterItem.Header) {
        with(binding) {
            profileAvatar.setImageUrl(item.imageUrl, placeholder = item.placeholder)
            profileName.text = item.name
            profileOccupation.text = item.occupation
            profileCity.text = item.city

            profileCity.isVisible = !item.city.isNullOrBlank()
            profileOccupation.isVisible = !item.occupation.isNullOrBlank()
        }
    }
}